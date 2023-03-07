package Compiler;

public class MyColor {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String makeRed(String string){
        return ANSI_RED + string + ANSI_RESET;
    }

    public static String makeGreen(String string){
        return ANSI_GREEN + string + ANSI_RESET;
    }

    public static String makeYellow(String string){
        return ANSI_YELLOW + string + ANSI_RESET;
    }

    public static String makeBlue(String string){
        return ANSI_BLUE + string + ANSI_RESET;
    }

    public static String makePurple(String string){
        return ANSI_PURPLE + string + ANSI_RESET;
    }

    public static String makeCyan(String string){
        return ANSI_CYAN + string + ANSI_RESET;
    }
}

