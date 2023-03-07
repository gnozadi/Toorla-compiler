package Compiler;

import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class MyListener implements ToorlaListener {
    int currentScopeNumber = 0;
    private static final String program = "program";
    private int parameterIndex = 0;
    HashMap<String, Element> parameters;
    private boolean inMethod = false;
    private Stack<SymbolTable> symbolTablesStack = new Stack<>();
    private ArrayList<SymbolTable> symbolTableArrayList = new ArrayList<>();
    private ArrayList<String> definedClasses = new ArrayList<>();
    private Map<String, String> variables = new HashMap<>();
    private ArrayList<String> fieldsDefined = new ArrayList<>();
    private ArrayList<String> privateMethods = new ArrayList<>();
    private ErrorHandler errorHandler;
    static HashMap<String, String> classFields = new HashMap<>();
    private ArrayList<Integer> allScopeNumbers = new ArrayList<>();
    private HashMap<String, String> classParent = new HashMap<>();
    private boolean inClass = true;
    String[] types = {"int", "string", "bool", "ClassType"};

    @Override
    public void enterProgram(ToorlaParser.ProgramContext ctx) {
        //TODO: name of elements change
        startNewScope(program, ctx.start.getLine() - 1);
        symbolTablesStack.peek().setScopeType("Global");
        errorHandler = new ErrorHandler();
    }

    @Override
    public void exitProgram(ToorlaParser.ProgramContext ctx) {
        endScope();
        printAllSymbolTables();

        errorHandler.setSymbolTableArrayList(symbolTableArrayList);

        errorHandler.checkForError101_102_104();
        errorHandler.checkForError105();
        errorHandler.checkForError106(variables);
        errorHandler.printAllErrors();
    }

    @Override
    public void enterClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        String name = ctx.className.getText();
        if (!name.equals("MainClass")) {
            String key = getKey("Class", name);
            Element value = Element.classElement(name, ctx);
            value.setLine(ctx.start.getLine());
            value.setColumn(ctx.stop.getCharPositionInLine());
            classParent.put(name, ctx.classParent.getText());
            addToCurrentScope(key, value);
            startNewScope(name, ctx.start.getLine());
            symbolTablesStack.peek().setScopeType("Class");
            definedClasses.add(value.getOriginalType());
        }

    }

    @Override
    public void exitClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        errorHandler.checkForError410(classParent, ctx.start.getLine(), ctx.stop.getCharPositionInLine());
        endScope();
    }

    @Override
    public void enterEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {
        String name1 = ctx.classDeclaration().className.getText();
        String key = getKey("Class", name1);
        Element value = Element.classElement(name1, ctx.classDeclaration());
        value.setLine(ctx.start.getLine());
        value.setColumn(ctx.stop.getCharPositionInLine());
        addToCurrentScope(key, value);
        startNewScope(name1, ctx.start.getLine());
        symbolTablesStack.peek().setScopeType("Class");

        definedClasses.add(value.getOriginalType());

    }

    @Override
    public void exitEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void enterFieldDeclaration(ToorlaParser.FieldDeclarationContext ctx) {
        String name = ctx.fieldName.getText();
        String key = getKey("Field", name);


        String key2 = name + "_" + ctx.start.getLine() + "_" + ctx.stop.getCharPositionInLine();

        variables.put(key2, String.valueOf(currentScopeNumber));
        String type = ctx.fieldType.getText();
        classFields.put(name, type);
        Element value = Element.fieldElement(name, "ClassField", type, true, inMethod);
        value.setLine(ctx.start.getLine());
        value.setColumn(ctx.stop.getCharPositionInLine());
        addToCurrentScope(key, value);

    }

    @Override
    public void exitFieldDeclaration(ToorlaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void enterAccess_modifier(ToorlaParser.Access_modifierContext ctx) {

    }

    @Override
    public void exitAccess_modifier(ToorlaParser.Access_modifierContext ctx) {

    }

    @Override
    public void enterMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {

        String name = ctx.methodName.getText();
        String isMethod = "Method";
        if (definedClasses.contains(name))
            isMethod = "Constructor";
        String key = getKey(isMethod, name);
        Element value;
        inMethod = true;

        value = Element.primitiveMethodElement(name, isMethod, ctx.t.start.getText(), ctx);
        value.setLine(ctx.start.getLine());
        value.setColumn(ctx.stop.getCharPositionInLine());
        addToCurrentScope(key, value);
        parameters = baseParamElement(ctx);

        startNewScope(name, ctx.start.getLine());
        symbolTablesStack.peek().setScopeType(isMethod);

        if (ctx.getChild(0).getText().equals("private")) {
            privateMethods.add(name);
        }
    }

    @Override
    public void exitMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        if (parameters != null) {
            for (HashMap.Entry<String, Element> entry : parameters.entrySet()) {
                String key = getKey("Field", entry.getKey());
                String key2 = entry.getKey() + "_" + ctx.start.getLine() + "_" + ctx.stop.getCharPositionInLine();

                variables.put(key2, String.valueOf(currentScopeNumber));
                Element value = entry.getValue();
                addToCurrentScope(key, value);
            }
        }
        inMethod = false;
        endScope();
    }

    @Override
    public void enterClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void exitClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void enterClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {
        startNewScope("if", ctx.start.getLine());
    }

    @Override
    public void exitClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {
        endScope();
    }

    @Override
    public void enterOpenConditional(ToorlaParser.OpenConditionalContext ctx) {

    }

    @Override
    public void exitOpenConditional(ToorlaParser.OpenConditionalContext ctx) {

    }

    @Override
    public void enterOpenStatement(ToorlaParser.OpenStatementContext ctx) {

    }

    @Override
    public void exitOpenStatement(ToorlaParser.OpenStatementContext ctx) {

    }

    @Override
    public void enterStatement(ToorlaParser.StatementContext ctx) {
        if (ctx.s1 != null && ctx.s1.statementAssignment() != null) {
            String name = ctx.s1.statementAssignment().left.getText();
            // for ignoring variables that start with self. and the ones that weren't defined before
            if (!name.contains("self") && !fieldsDefined.contains(name)) {
                String key = getKey("Field", name);
                String key2 = name + "_" + ctx.start.getLine() + "_" + ctx.stop.getCharPositionInLine();
                variables.put(key2, String.valueOf(currentScopeNumber));
                String type;
                Element value;

                type = getType(ctx.s1.statementAssignment().right);

                value = Element.fieldElement(name, "MethodVar", type, true, inMethod);

                value.setLine(ctx.start.getLine());
                value.setColumn(ctx.stop.getCharPositionInLine());
                addToCurrentScope(key, value);
            }
        }
    }

    @Override
    public void exitStatement(ToorlaParser.StatementContext ctx) {

    }

    @Override
    public void enterStatementVarDef(ToorlaParser.StatementVarDefContext ctx) {
        String name = ctx.i1.getText();
        fieldsDefined.add(name);

        String key = getKey("Field", name);
        String type;
        Element value;
        type = getTypePrimitive(ctx);
        value = Element.fieldElement(name, "MethodVar", type, true, inMethod);
        value.setLine(ctx.start.getLine());
        value.setColumn(ctx.stop.getCharPositionInLine());
        addToCurrentScope(key, value);

    }

    @Override
    public void exitStatementVarDef(ToorlaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void enterStatementBlock(ToorlaParser.StatementBlockContext ctx) {

    }

    @Override
    public void exitStatementBlock(ToorlaParser.StatementBlockContext ctx) {

    }

    @Override
    public void enterStatementContinue(ToorlaParser.StatementContinueContext ctx) {

    }

    @Override
    public void exitStatementContinue(ToorlaParser.StatementContinueContext ctx) {

    }

    @Override
    public void enterStatementBreak(ToorlaParser.StatementBreakContext ctx) {

    }

    @Override
    public void exitStatementBreak(ToorlaParser.StatementBreakContext ctx) {

    }

    @Override
    public void enterStatementReturn(ToorlaParser.StatementReturnContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        SymbolTable currentTable = symbolTablesStack.peek();
        SymbolTable parentTable = getCertainTable(currentTable.getParentScopeNumber());
        errorHandler.checkForError210(currentTable, parentTable, ctx.getChild(1).getText(), line, column);
    }

    @Override
    public void exitStatementReturn(ToorlaParser.StatementReturnContext ctx) {

    }

    @Override
    public void enterStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {
        startNewScope("while", ctx.start.getLine());
    }

    @Override
    public void exitStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {
        endScope();
    }

    @Override
    public void enterStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {

    }

    @Override
    public void exitStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {

    }

    @Override
    public void enterStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void exitStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void enterStatementAssignment(ToorlaParser.StatementAssignmentContext ctx) {
    }

    @Override
    public void exitStatementAssignment(ToorlaParser.StatementAssignmentContext ctx) {

    }

    @Override
    public void enterStatementInc(ToorlaParser.StatementIncContext ctx) {

    }

    @Override
    public void exitStatementInc(ToorlaParser.StatementIncContext ctx) {

    }

    @Override
    public void enterStatementDec(ToorlaParser.StatementDecContext ctx) {

    }

    @Override
    public void exitStatementDec(ToorlaParser.StatementDecContext ctx) {

    }

    @Override
    public void enterExpression(ToorlaParser.ExpressionContext ctx) {
    }

    @Override
    public void exitExpression(ToorlaParser.ExpressionContext ctx) {

    }

    @Override
    public void enterExpressionOr(ToorlaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void exitExpressionOr(ToorlaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void enterExpressionOrTemp(ToorlaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void exitExpressionOrTemp(ToorlaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void enterExpressionAnd(ToorlaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void exitExpressionAnd(ToorlaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void enterExpressionAndTemp(ToorlaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void exitExpressionAndTemp(ToorlaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void enterExpressionEq(ToorlaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void exitExpressionEq(ToorlaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void enterExpressionEqTemp(ToorlaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void exitExpressionEqTemp(ToorlaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void enterExpressionCmp(ToorlaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void exitExpressionCmp(ToorlaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void enterExpressionCmpTemp(ToorlaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void exitExpressionCmpTemp(ToorlaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void enterExpressionAdd(ToorlaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void exitExpressionAdd(ToorlaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void enterExpressionAddTemp(ToorlaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void exitExpressionAddTemp(ToorlaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void enterExpressionMultMod(ToorlaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void exitExpressionMultMod(ToorlaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void enterExpressionMultModTemp(ToorlaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void exitExpressionMultModTemp(ToorlaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void enterExpressionUnary(ToorlaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void exitExpressionUnary(ToorlaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void enterExpressionMethods(ToorlaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void exitExpressionMethods(ToorlaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void enterExpressionMethodsTemp(ToorlaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void exitExpressionMethodsTemp(ToorlaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void enterExpressionOther(ToorlaParser.ExpressionOtherContext ctx) {

    }

    @Override
    public void exitExpressionOther(ToorlaParser.ExpressionOtherContext ctx) {

    }

    @Override
    public void enterToorlaType(ToorlaParser.ToorlaTypeContext ctx) {

    }

    @Override
    public void exitToorlaType(ToorlaParser.ToorlaTypeContext ctx) {

    }

    @Override
    public void enterSingleType(ToorlaParser.SingleTypeContext ctx) {

    }

    @Override
    public void exitSingleType(ToorlaParser.SingleTypeContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }


    private void startNewScope(String name, int scopeNumber) {
        currentScopeNumber = scopeNumber;
        allScopeNumbers.add(scopeNumber);
        SymbolTable symbolTable = new SymbolTable(name, scopeNumber);
        if (symbolTablesStack.size() >= 1) {
            int parentScopeNumber = symbolTablesStack.peek().getScopeNumber();
            symbolTable.setParentScopeNumber(parentScopeNumber);
        }
        symbolTablesStack.push(symbolTable);
    }

    private void addToCurrentScope(String key, Element value) {
        SymbolTable current = symbolTablesStack.peek();
        String finalKey = checkIfRepeated(current, key, value);
        current.insert(finalKey, value);
    }

    private void endScope() {
        symbolTableArrayList.add(symbolTablesStack.pop());
    }

    private String getKey(String keyword, String name) {
        return keyword + "_" + name;
    }

    private void printAllSymbolTables() {
        for (int i = 0; i < symbolTablesStack.size() && !symbolTablesStack.isEmpty(); i++) {
            symbolTableArrayList.add(symbolTablesStack.get(i));
        }
        symbolTableArrayList.sort(Comparator.comparing(SymbolTable::getScopeNumber));
        for (int i = 0; i < symbolTableArrayList.size(); i++) {
            String symbolTable = symbolTableArrayList.get(i).toString();
            System.out.println(symbolTable);
        }
    }

    private String checkIfRepeated(SymbolTable symbolTable, String key, Element value) {
        int line = value.getLine();
        int column = value.getColumn();
        if (symbolTable.lookup(key) != null) {
            key += "_" + line + "_" + column;
            value.setRepeated(true);
        }
        return key;
    }


    private SymbolTable getCertainTable(int scopeNumber) {
        Stack<SymbolTable> tempStack = new Stack<>();
        SymbolTable found = null;
        while (!symbolTablesStack.isEmpty()) {
            SymbolTable temp = symbolTablesStack.pop();
            if (temp.getScopeNumber() == scopeNumber) {
                found = temp;
                symbolTablesStack.push(temp);
                break;
            }
            tempStack.push(temp);
        }
        while (!tempStack.isEmpty())
            symbolTablesStack.push(tempStack.pop());

        return found;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getTypePrimitive(ToorlaParser.StatementVarDefContext ctx) {
        for (int i = 0; i < ctx.children.size(); i++) {
            if (ctx.getChild(i).getText().equals("=")) {
                String v = ctx.getChild(i + 1).getText();
                if (isNumeric(v)) {
                    return types[0];
                } else if (v.contains("\""))
                    return types[1];
                else if (v.equals("true") || v.equals("false")) {
                    return types[2];
                } else if (v.contains("new")) {
                    int index = v.indexOf('w');
                    return types[3] + ": [" + v.substring(index + 1, v.length() - 2) + "]";
                }
            }
        }
        return null;
    }

    public String getType(ToorlaParser.ExpressionContext right) {
        for (int i = 0; i < right.children.size(); i++) {
            String v = right.getChild(i).getText();
            if (v.contains("new")) {
                if (right.getText().contains(types[0]))
                    return types[0] + "[]";
                else if (right.getText().contains(types[1]))
                    return types[1] + "[]";
                else if (right.getText().contains(types[2]))
                    return types[2] + "[]";
                else {
                    int index = v.indexOf('w');
                    return types[3] + ": [" + v.substring(index + 1, v.length() - 2) + "]";
                }

            } else if (v.contains("("))
                return types[3];
        }
        return null;
    }

    private HashMap<String, Element> baseParamElement(ToorlaParser.MethodDeclarationContext ctx) {
        HashMap<String, Element> elements = new HashMap<>();
        int start = 0;
        int finish = 0;
        String name = "";
        String type = "";
        Boolean isDefined = true;

        for (int i = 0; i < ctx.children.size(); i++) {
            if (ctx.getChild(i).getText().equals("("))
                start = i;
            else if (ctx.getChild(i).getText().equals(")")) {
                finish = i;
            }
            if (finish - start == 1)
                return null;

        }
        for (int i = start; i < finish; i += 4) {
            name = ctx.getChild(i + 1).getText();
            type = ctx.getChild(i + 3).getText();
            Element temp = Element.paramFieldElement(name, type, isDefined);
            elements.put(name, temp);
        }
        return elements;
    }

}

