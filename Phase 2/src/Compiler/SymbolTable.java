package Compiler;

import java.util.*;


public class SymbolTable {
    private String name;
    private int scopeNumber;
    private HashMap<String, Element> table;
    // To keep keys in the order
    private Queue<String> keys;
    private int parentScopeNumber = 0;
    private String scopeType;

    public SymbolTable(String name, int scopeNumber) {
        this.name = name;
        this.scopeNumber = scopeNumber;
        this.table = new HashMap<>();
        this.keys = new LinkedList<>();
    }

    public void insert(String idefname, Element element) {
        table.put(idefname, element);
        keys.add(idefname);
    }

    public Element lookup(String idefname) {
        for (HashMap.Entry<String , Element> entry : table.entrySet()) {
            if (entry.getKey().equals(idefname) && !entry.getValue().isRepeated())
                return table.get(idefname);
        }
        return null;
    }

    public String toString() {
        return MyColor.makeBlue("------------- " + name + " : " + scopeNumber + " -------------\n" ) +
                printItems() +
                MyColor.makeBlue("-----------------------------------------\n");
    }

        public String printItems(){
            StringBuilder itemsString = new StringBuilder();
            for (HashMap.Entry<String,Element> entry : table.entrySet()) {
                updateName(entry);
                itemsString.append(MyColor.makeGreen("Key = ")).append(entry.getKey()).append(" | "+MyColor.makeGreen("Value = ")).append(entry.getValue()).append("\n");
            }
            return itemsString.toString();

    }

    public void updateName(Map.Entry<String, Element> entry){
        String name = entry.getKey().split("_")[1];
        entry.getValue().setElementName(name);
    }
    public int getScopeNumber() {
        return scopeNumber;
    }

    public HashMap<String, Element> getTable() {
        return table;
    }

    public int getParentScopeNumber() {
        return parentScopeNumber;
    }

    public void setParentScopeNumber(int parentScopeNumber) {
        this.parentScopeNumber = parentScopeNumber;
    }

    public String getName() {
        return name;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }
}

