package Compiler;

import java.util.*;

public class ErrorHandler {
    private ArrayList<SymbolTable> symbolTableArrayList;
    private ArrayList<Error> allErrors;

    public ErrorHandler() {
        this.allErrors = new ArrayList<>();
    }

    public void printAllErrors() {
        allErrors.sort(Comparator.comparing(Error::getLine));
        for (Error e : allErrors) {
            System.out.println(e.toString());
        }
    }

    // repeated declaration of method and field
    public void checkForError101_102_104() {
        for (SymbolTable symbolTable : symbolTableArrayList) {
            Map<String, Element> tableMap = symbolTable.getTable();
            for (Map.Entry<String, Element> entry : tableMap.entrySet()) {
                if (entry.getValue().isRepeated()) {
                    String type = "";
                    String errMessage = "";
                    int lineOfError = entry.getValue().getLine();
                    int colOfError = entry.getValue().getColumn();

                    if (entry.getValue().getKey().equals("Method")) {
                        errMessage = " method " + entry.getValue().getElementName() + " has been defined already";
                        type = "102";
                    } else if (entry.getValue().getKey().equals("Field") ||
                            entry.getValue().getKey().equals("ClassField")) {
                        errMessage = " field " + entry.getValue().getElementName() + " has been defined already";
                        type = "101";
                    }else if(entry.getValue().getKey().equals("MethodVar")){
                        errMessage = " Var " + entry.getValue().getElementName() + " has been defined already";
                        type = "104";
                    }
                    Error error = new Error(type, errMessage, lineOfError, colOfError);
                    allErrors.add(error);
                }
            }
        }
    }

    // using an undefined class
    public void checkForError105() {
        for (SymbolTable symbolTable : symbolTableArrayList) {
            Map<String, Element> tableMap = symbolTable.getTable();
            for (Map.Entry<String, Element> entry : tableMap.entrySet()) {
                if (!entry.getValue().isDefined()) {
                    int lineOfError = entry.getValue().getLine();
                    int colOfError = entry.getValue().getColumn();
                    String errMessage = "cannot find class " + entry.getValue().getOriginalType();
                    Error error = new Error("105", errMessage, lineOfError, colOfError);
                    allErrors.add(error);
                }
            }
        }
    }

    // using an undefined variable
    public void checkForError106(Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String[] str = entry.getKey().split("_");
//            System.out.println(Arrays.toString(str));
            if (!checkVariableDefinition(str[0], entry.getValue())) {
                String errMessage = "Can not find Variable " + str[0];
                int lineOfError = Integer.parseInt(str[1]);
                int colOfError = Integer.parseInt(str[2]);
                Error error = new Error("106", errMessage, lineOfError, colOfError);
                allErrors.add(error);
            }
        }

    }

    // Error 210 - ReturnType mismatch
    public void checkForError210(SymbolTable currentTable, SymbolTable parentTable, String returnValue,
                                 int line, int column) {

        // Finding Return Type
        String returnType = "";
        if (!MyListener.isNumeric(returnValue) && !returnValue.contains("\"")) {
            if(returnValue.contains("self")) {
                returnValue = returnValue.substring(5);

            }
            Element fieldValue = currentTable.lookup("Field_" + returnValue);
            if (fieldValue != null)
                returnType = fieldValue.getOriginalType();
            else {
                if (!MyListener.classFields.containsKey(returnValue)) {
                    String message = "Can not find Variable " + returnValue;
                    Error error = new Error("106", message, line, column);
                    allErrors.add(error);
                    return;
                }
            }
        }

        // Finding Method ReturnType
        String methodName = currentTable.getName();
        String methodReturnType = "";
        Element methodValue = parentTable.lookup("Method_" + methodName);
        Element constructorValue = parentTable.lookup("Constructor_" + methodName);
        if (methodValue != null)
            methodReturnType = methodValue.getOriginalType();
        else if(constructorValue!=null){
            methodReturnType = constructorValue.getOriginalType();
        }
        else {
                String message = "Can not find method " + methodName;
                Error error = new Error("107", message, line, column);
                allErrors.add(error);
                return;

        }
        // Compare the two
        if ( (MyListener.isNumeric(returnValue) && methodReturnType.equals("int")) ||
                ( returnValue.contains("\"") && methodReturnType.equals("string"))
        )
            return;
            if (!returnType.equals(methodReturnType)
                    && !MyListener.classFields.get(returnValue).equals(methodReturnType)) {
                String message = "";
                if (!(MyListener.isNumeric(returnType) && methodReturnType.equals("int"))) {
                    message = "ReturnType of this method must be " + methodReturnType;
                    Error error = new Error("210", message, line, column);
                    allErrors.add(error);
                }
            }

    }


    public void checkForError410(HashMap<String, String> classParent,int line, int column) {
        String message = "";
        if(loopExists(classParent)){
            message = "There is a inheritance loop!";
            Error error = new Error("410",message,line,column);
            allErrors.add(error);
        }

    }

    public boolean loopExists(HashMap<String, String> classParent){
        for (HashMap.Entry<String , String> entry: classParent.entrySet()) {
            HashMap<String, String > temp = classParent;
            String parent = entry.getKey();
            String child = entry.getValue();
            for(;temp.containsKey(child);){
                if (child.equals(parent))
                    return true;
                child = temp.get(child);
            }
        }
        return false;
    }

    private SymbolTable getCertainSymbolTable(int scopeNumber) {
        for (SymbolTable symbolTable : symbolTableArrayList) {
            if (symbolTable.getScopeNumber() == scopeNumber)
                return symbolTable;
        }
        return null;
    }

    private SymbolTable getCertainScope(String scopeType) {
        for (SymbolTable symbolTable : symbolTableArrayList) {
            if (symbolTable.getScopeType().equals(scopeType))
                return symbolTable;
        }
        return null;
    }

    private boolean checkVariableDefinition(String key, String scopeNumber) {
        int scopeNum = Integer.parseInt(scopeNumber);
        while (scopeNum >= 1) {
            SymbolTable st = getCertainSymbolTable(scopeNum);
            Element ba = st.lookup("Field_"+key);
            //if can't find var in current scope.. look up in higher scopes
            if (ba == null) {
                scopeNum = st.getParentScopeNumber();
            }
            else{
                return true;
            }
        }
        return false;
    }




    public ArrayList<SymbolTable> getSymbolTableArrayList() {
        return symbolTableArrayList;
    }

    public void setSymbolTableArrayList(ArrayList<SymbolTable> symbolTableArrayList) {
        this.symbolTableArrayList = symbolTableArrayList;
    }

    public ArrayList<Error> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(ArrayList<Error> allErrors) {
        this.allErrors = allErrors;
    }


}
