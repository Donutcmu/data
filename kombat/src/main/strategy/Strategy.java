package main.strategy;

import java.util.List;

public class Strategy {
    private String strategyCode;
    private ASTNode parsedAST;

    public Strategy(String strategyCode) {
        this.strategyCode = strategyCode;
        parseStrategy();
    }

    private void parseStrategy() {
        Lexer lexer = new Lexer(strategyCode);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        parsedAST = parser.parse();
    }

    public ASTNode getAST() {
        return parsedAST;
    }
}
