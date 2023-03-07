package Compiler;

import java.util.ArrayList;

import gen.ToorlaParser.*;
import org.antlr.v4.runtime.tree.ParseTree;

//Symbol Table element
public class Element {

    private int line;
    private int column;
    private boolean repeated;
    private String key;
    private static String elementName;
    private String originalType = null;
    private String classType = null;
    private String returnType = null;
    private int index = -1;
    boolean isDefined = true;
    static boolean isEntry = false;
    String value = "";

    FieldDeclarationContext parameters = null;
    MethodDeclarationContext arguments = null;
    ClassDeclarationContext parentList = null;
    ArrayList<String> parameterList = null;

    public Element(String key, String name){
        this.elementName = name;
        this.key = key;
    }


    public static Element classElement (String name, ClassDeclarationContext parentList){
        Element e = new Element("Class", name);
        isEntry();
        e.parentList = parentList;

        e.value = makeClassString(parentList);
        e.setOriginalType(name);
        return e;
    }


    public static Element fieldElement(String name,String key, String classType, boolean isDefined, boolean inMethod) {
        return baseFieldElement(key, name, classType, isDefined, classType, inMethod);
    }


    public static Element baseFieldElement(String key, String name, String classType, boolean isDefined,
                                                   String originalType, boolean isInMethod) {
        if (isInMethod) {
            key = "MethodVar";
        }

        Element element = new Element(key, name);

        element.classType = classType;
        element.isDefined = isDefined;

        element.value = " ( type: " + classType + ", " + generateIsDefined(isDefined) + ")";
        element.setOriginalType(originalType);
        return element;
    }

    /*
    when a method returns a primitive variable
     */
    public static Element primitiveMethodElement(String name,String isMethod, String returnType, MethodDeclarationContext arguments) {
        return baseMethodElement(name,isMethod, returnType, arguments, returnType);
    }

    public static Element baseMethodElement(String name, String isMethod, String returnType, MethodDeclarationContext arguments,
                                            String originalType) {
        Element element = new Element(isMethod, name);

        element.returnType = returnType;
        element.arguments = arguments;

        element.value = generateReturnType(returnType) + generateParameterList(arguments);


        element.setOriginalType(originalType);

        return element;
    }
    public static Element paramFieldElement(String name, String type, Boolean isDefined) {

        Element element = new Element("ParamField", name);

        element.value = "(type: "+type +", "+ generateIsDefined(isDefined);

        return element;
    }



    public String toString() {
        return generateName(key, elementName) + value;
    }

    public static String getParameterString(MethodDeclarationContext ctx) {
        String parametersString = "";
        int bracketOpenIndex = 3;
        int bracketCloseIndex = 0;
        int i = 0;
        for (ParseTree parseTree : ctx.children) {
            String child = parseTree.getText();
            if (child.equals("("))
                bracketOpenIndex = i;
            if (child.equals(")"))
                bracketCloseIndex = i;
            i++;
        }

        if(bracketCloseIndex - bracketOpenIndex == 1)
            parametersString = "[]";
        else {
            int index = 1;
            parametersString = "[ ";
            for (int j = bracketOpenIndex + 1; j < bracketCloseIndex; j += 4) {

                parametersString += "elementName: " + ctx.children.get(j).getText() + ", type:"
                        + ctx.children.get(j + 2).getText() + ", Index: " + index + " - ";
                index ++;
            }
            parametersString = parametersString.substring(0,parametersString.length()-2);
        }

        return parametersString;
    }


    public static String makeClassString(ClassDeclarationContext parents){
        return generateParent(parents) + makeEntryString();
    }

    public static String generateName(String key, String name) {
        return key + "(Name: " + name + ")";
    }


    public static String generateParameterList(MethodDeclarationContext parameterList) {
        return  "( Parameter List: " + getParameterString(parameterList) + ")";
    }

    public static String generateParent(ClassDeclarationContext parent) {
        if (parent.classParent == null)
            return "(Parent: [])";
        else
            return "( Parent: " + parent.classParent.getText() + ")";
    }

    public static String makeEntryString(){
        return "(isEntry: " + isEntry + ")";
    }

    public static void isEntry(){
        if (getElementName().equals("MainClass"))
                isEntry = true;
    }

    public boolean isDefined() {
        return isDefined;
    }
    public static String generateReturnType(String returnType) {
        return   "( Return: " + returnType + ")";
    }

    public static void setElementName(String elementName) {
        Element.elementName = elementName;
    }

    public static String generateIsDefined(boolean isDefined) {
        return "IsDefined: " + isDefined + ")";
    }

    public String getKey() {
        return key;
    }

    public static String getElementName() {
        return elementName;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public String getOriginalType() {
        return originalType;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }


    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}
