package ADL.Compile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pakapon on 3/16/2017 AD.
 */
public class Tokenizer {
    private static final Pattern REGEX_PATTERN = Pattern.compile("(?<Keyword>Agent|State|Seq|GroupAction)|(?<OpenBracket>\\()|(?<CloseBracket>\\))|(?<OpenCurlyBracket>\\{)|(?<CloseCurlyBracket>})|(?<Comma>,)|(?<Double>-?\\d+\\.\\d+)|(?<Integer>-?\\d+)|(?<Boolean>true|false)|(?<String>(?<Quote>['\"])[^']*\\k<Quote>)|(?<PlusOPR>\\+)|(?<MinusOPR>-)|(?<MultiplyOPR>\\*)|(?<DivideOPR>/)|(?<Identifier>\\w+)");
    private static final String[] REGEX_GROUP = {"Keyword", "Identifier", "OpenBracket", "CloseBracket", "OpenCurlyBracket", "CloseCurlyBracket", "Comma", "Double", "Integer", "Boolean", "String", "PlusOPD", "MinusOPD", "MultiplyOPD", "DivideOPD"};

    private static Tokenizer ourInstance = new Tokenizer();

    public static Tokenizer getInstance() {
        return ourInstance;
    }

    private Tokenizer() {
    }

    public List<Token> tokenize(String context){
        List<Token> tokens = new ArrayList<>();

        Matcher matcher = REGEX_PATTERN.matcher(context);

        while(matcher.find())
        {
            String lexeme = matcher.group();
            if (isGroupPresented(matcher, "Keyword"))
            {
                tokens.add(new Token("Keyword", lexeme));
            }
            else if(isGroupPresented(matcher, "Identifier"))
            {
                tokens.add(new Token("Identifier", lexeme));
            }
            else if(isGroupPresented(matcher, "Double"))
            {
                tokens.add(new Token("Double", lexeme));
            }
            else if(isGroupPresented(matcher, "Integer"))
            {
                tokens.add(new Token("Integer", lexeme));
            }
        }

        return tokens;
    }

    private static boolean isGroupPresented(Matcher matcher, String groupName){
        Optional<String> lexeme = Optional.ofNullable(matcher.group(groupName));
        return lexeme.isPresent();
    }
}
