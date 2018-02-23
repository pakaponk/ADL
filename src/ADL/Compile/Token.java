package ADL.Compile;

/**
 * Created by Pakapon on 3/16/2017 AD.
 */
public class Token {
    private String category;
    private String lexeme;

    public Token(String category, String lexeme) {
        this.category = category;
        this.lexeme = lexeme;
    }

    public String getCategory() {
        return category;
    }

    public String getLexeme() {
        return lexeme;
    }
}
