package comp;

import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;



public class MyListener implements ToorlaListener {
    private final String indentation1="    ";
    private final String indentation2="        ";
    private ArrayList<String> ClassNames=new ArrayList<>();
    ArrayList<String> classNames=new ArrayList<>();


    @Override
    // can print the whole thing
    public void enterProgram(ToorlaParser.ProgramContext ctx) {
       System.out.println(MyColor.ANSI_GREEN+"Program Start{"+MyColor.ANSI_RESET);
    }

    @Override
    public void exitProgram(ToorlaParser.ProgramContext ctx) {
        System.out.println("}");
    }

    @Override
    // class name
    public void enterClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        String name = ctx.className.getText();
        classNames.add(name);
        if (!name.equals("MainClass")) {
           ifNotMain(ctx,name);
        }
    }
    @Override
    public void exitClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        System.out.println(indentation1+"}");
    }

    @Override

    public void enterEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {
       System.out.print(indentation1+MyColor.ANSI_CYAN+"Class: "+MyColor.ANSI_RESET+ctx.classDeclaration().className.getText()+" / ");
       String parentName = "none";
       if (ctx.classDeclaration().classParent!=null)
           parentName = ctx.classDeclaration().classParent.getText();
       System.out.print(MyColor.ANSI_CYAN+"Class Parent: "+MyColor.ANSI_RESET+parentName+MyColor.ANSI_CYAN+"/ isEntry:"+MyColor.ANSI_RESET+ "True{\n");

    }

    @Override
    public void exitEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {

    }

    @Override
    public void enterFieldDeclaration(ToorlaParser.FieldDeclarationContext ctx) {
        System.out.println(indentation2+
                MyColor.ANSI_BLUE+"field: " +MyColor.ANSI_RESET
                +ctx.fieldName.getText()+
                MyColor.ANSI_BLUE+ " / type: "+MyColor.ANSI_RESET
                +ctx.fieldType.getText());

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
    // 0 ->Access Level
    // 1 -> function
    // 2 -> name

    public void enterMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        String name = ctx.methodName.getText();
        String returnType = ctx.t.start.getText();
        String type = ctx.children.get(0).getText();
        if(name.equals("main")) {
            name = MyColor.ANSI_GREEN+"Main Method"+MyColor.ANSI_RESET;
            System.out.println(indentation2 + name + " / "+
                    MyColor.ANSI_GREEN+"Type: "+MyColor.ANSI_RESET
                    + returnType +"{");
        }
        else {
            name =
                    MyColor.ANSI_GREEN+"Class "+MyColor.ANSI_RESET
                            + isConstructor(ctx) + ": " + name;
            System.out.println(indentation2 + name + " /"+
                    MyColor.ANSI_GREEN+" Return Type: "+MyColor.ANSI_RESET
                    + returnType + " /"+
                    MyColor.ANSI_GREEN+" Type: "+MyColor.ANSI_RESET
                    + type + "{");
        }
        if(!name.contains("main"))
            System.out.println(indentation2+indentation1+MyColor.ANSI_YELLOW+"Parameter List:"+MyColor.ANSI_RESET+" ["+argument(ctx)+"]");
    }

    @Override
    public void exitMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        System.out.println(indentation2+"}");
    }

    @Override
    public void enterClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void exitClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void enterClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {

    }

    @Override
    public void exitClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {

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
        if (ctx.s1!=null)
            if(ctx.s1.getText().contains("newint"))
                System.out.println(indentation2+indentation1+"field: "+ctx.s1.statementAssignment().left.getText()+" / type: input[]");
    }

    @Override
    public void exitStatement(ToorlaParser.StatementContext ctx) {

    }

    @Override
    public void enterStatementVarDef(ToorlaParser.StatementVarDefContext ctx) { //local variables
        if (!ctx.parent.parent.parent.getChild(0).getText().equals("begin"))
            System.out.println(indentation2+indentation1+"field: "+ctx.i1.getText()+" / type: local "+ctx.children.get(0));

    }

    @Override
    public void exitStatementVarDef(ToorlaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void enterStatementBlock(ToorlaParser.StatementBlockContext ctx) {
        System.out.println(indentation2+indentation1+
                MyColor.ANSI_RED+"nested"+MyColor.ANSI_RESET+" {");
        nested(ctx);
    }

    @Override
    public void exitStatementBlock(ToorlaParser.StatementBlockContext ctx) {
            System.out.println(indentation2+indentation1+"}");
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

    }

    @Override
    public void exitStatementReturn(ToorlaParser.StatementReturnContext ctx) {

    }

    @Override
    public void enterStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {

    }

    @Override
    public void exitStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {

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
//        System.out.println(ctx.right.getText());
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
    public void exitExpressionMethods(ToorlaParser.ExpressionMethodsContext ctx) { //other class used here?

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

    public String argument(ToorlaParser.MethodDeclarationContext ctx){
        String returnString="";
        if (ctx.typeP1 != null) {
            returnString = "Type: "+ctx.typeP1.getText()+" / name: "+ctx.param1.getText();
            if (ctx.typeP2 != null)
                returnString= returnString+ ", Type:" + ctx.typeP2.getText()+" / name: "+ctx.param2.getText();

        }
        return returnString;
    }

    public String isConstructor(ToorlaParser.MethodDeclarationContext ctx){

        String name = ctx.children.get(2).getText();
        if(classNames.contains(name))
            return "Constructor";
        else
            return "Method";
    }

    public void ifNotMain(ToorlaParser.ClassDeclarationContext ctx, String name){
        System.out.print(indentation1 +MyColor.ANSI_CYAN+ "Class: "+MyColor.ANSI_RESET+name+" / ");
        if (ctx.classParent != null)
            System.out.print(MyColor.ANSI_CYAN+"Class Parent: "+MyColor.ANSI_RESET + ctx.classParent.getText() + " / ");
        else
            System.out.print(MyColor.ANSI_CYAN+"Class Parent"+MyColor.ANSI_RESET+": none / ");
        System.out.print(MyColor.ANSI_CYAN+"isEntry"+MyColor.ANSI_RESET+": False{\n");
    }

    public void nested(ToorlaParser.StatementBlockContext ctx){
        for(int i=0;i<ctx.statement().size();i++){
            if(ctx.statement().get(i).getText().contains("var")){
                int index = ctx.statement().get(i).getText().indexOf("=");
                String name = ctx.statement().get(i).getText().substring(3,index);
                System.out.println(indentation2+indentation2+"field: "+name+ " / type: local var");
            }
        }
    }
}
