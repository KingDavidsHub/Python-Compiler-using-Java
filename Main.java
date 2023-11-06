import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}

enum TokenType {
    INT, FLOAT, IDENTIFIER, KEYWORD, OPERATOR, STRING, COMMENT
}

class Scanner {
    private final String input;
    private final List<Token> tokens;
    private int position;

        private static final Map<String, TokenType> keywords = new HashMap<>();

 static {
        keywords.put("False", TokenType.KEYWORD);
        keywords.put("None", TokenType.KEYWORD);
        keywords.put("True", TokenType.KEYWORD);
        keywords.put("and", TokenType.KEYWORD);
        keywords.put("as", TokenType.KEYWORD);
        keywords.put("assert", TokenType.KEYWORD);
        keywords.put("async", TokenType.KEYWORD);
        keywords.put("await", TokenType.KEYWORD);
        keywords.put("break", TokenType.KEYWORD);
        keywords.put("class", TokenType.KEYWORD);
        keywords.put("continue", TokenType.KEYWORD);
        keywords.put("def", TokenType.KEYWORD);
        keywords.put("del", TokenType.KEYWORD);
        keywords.put("elif", TokenType.KEYWORD);
        keywords.put("else", TokenType.KEYWORD);
        keywords.put("except", TokenType.KEYWORD);
        keywords.put("finally", TokenType.KEYWORD);
        keywords.put("for", TokenType.KEYWORD);
        keywords.put("from", TokenType.KEYWORD);
        keywords.put("global", TokenType.KEYWORD);
        keywords.put("if", TokenType.KEYWORD);
        keywords.put("import", TokenType.KEYWORD);
        keywords.put("in", TokenType.KEYWORD);
        keywords.put("is", TokenType.KEYWORD);
        keywords.put("lambda", TokenType.KEYWORD);
        keywords.put("nonlocal", TokenType.KEYWORD);
        keywords.put("not", TokenType.KEYWORD);
        keywords.put("or", TokenType.KEYWORD);
        keywords.put("pass", TokenType.KEYWORD);
        keywords.put("raise", TokenType.KEYWORD);
        keywords.put("return", TokenType.KEYWORD);
        keywords.put("try", TokenType.KEYWORD);
        keywords.put("while", TokenType.KEYWORD);
        keywords.put("with", TokenType.KEYWORD);
        keywords.put("yield", TokenType.KEYWORD);
    }
    public Scanner(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.position = 0;
    }
     private boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '&' || c == '|' || c == '^' ||
           c == '~' || c == '<' || c == '>' || c == '=' || c == '!' || c == ':' || c == '?' || c == '.' ||
           c == ',' || c == ';' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
           c == '@' || c == '#' || c == '`';
}



    public List<Token> scanTokens() {
        while (position < input.length()) {
            char current = input.charAt(position);
            if (Character.isDigit(current)) {
                scanNumber();
            } else if (Character.isLetter(current)) {
                //scanIdentifierOrKeyword();
                scanString();
                System.out.println("idnt");
            } else if (current == '"') {
                //scanString();
                scanIdentifierOrKeyword();
                System.out.println("string");

            } else if (isOperator(current)) {
                scanOperator();
            } else if (current == '/') {
                if (match('/')) {
                    scanComment();
                } else {
                    addToken(TokenType.OPERATOR, "/");
                    position++;
                }
            } else if (Character.isWhitespace(current)) {
                position++;
            } else {
                // Handle other cases
                position++;
            }
        }
        return tokens;
    }

private void scanNumber() {
    int start = position;
    while (position < input.length() && Character.isDigit(input.charAt(position))) {
        position++;
    }
    // Check for decimal part
    if (position < input.length() && input.charAt(position) == '.') {
        position++;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        addToken(TokenType.FLOAT, input.substring(start, position));
    } else {
        addToken(TokenType.INT, input.substring(start, position));
    }
}


private void scanIdentifierOrKeyword() {
    int start = position;
    while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
        position++;
    }
    String value = input.substring(start, position);
    TokenType type = keywords.containsKey(value) ? keywords.get(value) : TokenType.IDENTIFIER;
    addToken(type, value);
}





private void scanString() {
    int start = position;
    position++; // Move past the opening quote
    while (position < input.length() && input.charAt(position) != '"') {
        // Allow for escape sequences
        if (input.charAt(position) == '\\' && position + 1 < input.length()) {
            position += 2;
        } else {
            position++;
        }
    }
    if (position >= input.length()) {
        // Unterminated string
        // Handle the error accordingly
        return;
    }
    // Include the closing quote
    position++;
    String value = input.substring(start, position);
    addToken(TokenType.STRING, value);
}


private void scanOperator() {
    switch (input.charAt(position)) {
        case '+':
        case '-':
        case '*':
        case '/':
        case '%':
        case '&':
        case '|':
        case '^':
        case '~':
        case '<':
        case '>':
            addToken(TokenType.OPERATOR, Character.toString(input.charAt(position)));
            position++;
            break;
        default:
            if (position + 1 < input.length()) {
                String twoCharOperator = input.substring(position, position + 2);
                switch (twoCharOperator) {
                    case "==":
                    case "!=":
                    case "<=":
                    case ">=":
                    case "//":
                    case "<<":
                    case ">>":
                    case "**":
                    case "+=":
                    case "-=":
                    case "*=":
                    case "/=":
                    case "%=":
                    case "&=":
                    case "|=":
                    case "^=":
                    case "<<=":
                    case ">>=":
                    case "and":
                    case "or":
                    case "not":
                    case "is":
                    case "in":
                        addToken(TokenType.OPERATOR, twoCharOperator);
                        position += 2;
                        break;
                    default:
                        // Handle unrecognized operators
                        position++;
                        break;
                }
            } else {
                // Handle unrecognized operators
                position++;
                break;
            }
    }
}




private void scanComment() {
    int start = position;
    if (position + 1 < input.length() && input.charAt(position + 1) == '/') {
        // Single-line comment
        while (position < input.length() && input.charAt(position) != '\n') {
            position++;
        }
    } else if (position + 2 < input.length() &&
            (input.substring(position, position + 3).equals("'''") ||
                    input.substring(position, position + 3).equals("\"\"\""))) {
        // Multi-line comment
        position += 3; // Skip the opening triple quotes
        while (position + 2 < input.length() &&
                !(input.substring(position, position + 3).equals("'''") ||
                        input.substring(position, position + 3).equals("\"\"\""))) {
            position++;
        }
        if (position + 2 < input.length()) {
            position += 3; // Skip the closing triple quotes
        } else {
            // Handle unclosed multi-line comment error
            position = start;
        }
    }
    // You can choose to include the comment token in the output if needed
    addToken(TokenType.COMMENT, input.substring(start, position));
}





    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
    }

    private boolean match(char expected) {
        if (position >= input.length() || input.charAt(position) != expected) {
            return false;
        }
        position++;
        return true;
    }
}

public class Main {
        public static void main(String[] args) {
            String input = "'David'";
            String inputString = input.toString(); // Convert Integer to String
            Scanner scanner = new Scanner(input);
            List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
