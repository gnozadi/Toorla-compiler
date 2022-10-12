package comp;

import gen.ToorlaLexer;
import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


import java.io.IOException;


public class Compiler {
    public static void main(String[] args) throws IOException {
       ToorlaLexer lexer = new ToorlaLexer(CharStreams.fromFileName("src/comp/test1.trl"));
       TokenStream tokenStream = new CommonTokenStream(lexer);
        ToorlaParser parser = new ToorlaParser(tokenStream);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        ToorlaListener listener = new MyListener();

        walker.walk(listener, tree);
    }
}
