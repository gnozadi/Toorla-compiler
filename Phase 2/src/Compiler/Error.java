package Compiler;

import static Compiler.MyColor.*;
import java.awt.*;

public class Error {
    private String type;
    private int line;
    private int column;
    private String text;

    public Error(String type, String text, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.text = text;
    }

    public String toString(){
        return ANSI_RED + "Error" + type + " : " + ANSI_RESET+
                "in line " + "[" + line + ":" + column + "] ," +
                text   ;
    }


    public int getLine() {
        return line;
    }

}
