import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        keywords.put("print", TokenType.KEYWORD);
    }

    public Scanner(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.position = 0;
    }

    public List<Token> scanTokens() {
        while (position < input.length()) {
            char current = input.charAt(position);
            if (Character.isDigit(current)) {
                scanNumber();
            } else if (Character.isLetter(current)) {
                scanIdentifierOrKeyword();
            } else if (current == '"') {
                scanString();
            } else if (isOperator(current)) {
                scanOperator();
            } else if (current == '/') {
                if (match('/')) {
                    scanSingleLineComment();
                } else if (match('*')) {
                    scanMultiLineComment();
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
        while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
            position++;
        }

        String value = input.substring(start, position);
        if (value.contains(".")) {
            addToken(TokenType.FLOAT, value);
        } else {
            addToken(TokenType.INT, value);
        }
    }

    private void scanIdentifierOrKeyword() {
        int start = position;
        while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            position++;
        }
        String value = input.substring(start, position);

        // Check if the value is a keyword
        TokenType type = keywords.getOrDefault(value, TokenType.IDENTIFIER);
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
    char current = input.charAt(position);

    // Check for single-line comment
    if (current == '/' && position + 1 < input.length() && input.charAt(position + 1) == '/') {
        scanSingleLineComment();
        return;
    }

    switch (current) {
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
        case '=':
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


private void scanSingleLineComment() {
    int start = position;
    while (position < input.length() && input.charAt(position) != '\n') {
        position++;
    }
    addToken(TokenType.COMMENT, input.substring(start, position));
}

private void scanMultiLineComment() {
    int start = position;
    position += 2; // Skip the opening '/*'
    
    while (position + 1 < input.length()) {
        if (input.charAt(position) == '*' && input.charAt(position + 1) == '/') {
            position += 2; // Skip the closing '*/'
            addToken(TokenType.COMMENT, input.substring(start, position));
            return;
        }
        position++;
    }

    // Handle unclosed multi-line comment error
       // You might want to report an error or handle it in your way

    // Reset the position to the start of the comment to avoid incorrect tokenizing
    position = start;
    // Or you could choose to add an incomplete comment token here if needed
    // addToken(TokenType.COMMENT, input.substring(start, position));


    position += 2; // Skip the opening '/*'
    
    while (position + 1 < input.length()) {
        if (input.charAt(position) == '*' && input.charAt(position + 1) == '/') {
            position += 2; // Skip the closing '*/'
            return;
        }
        position++;
    }

    // Handle unclosed multi-line comment error
    // You might want to report an error or handle it in your way

    // Reset the position to the start of the comment to avoid incorrect tokenizing
    position -= 2;
}



    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
    }

private boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '&' || c == '|' || c == '^' ||
           c == '~' || c == '<' || c == '>' || c == '=' || c == '!' || c == ':' || c == '?' || c == '.' ||
           c == ',' || c == ';' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
           c == '@' || c == '#' || c == '`' || c == '/';
}



    private boolean match(char expected) {
        // Implementation for matching characters
        if (position >= input.length() || input.charAt(position) != expected) {
            return false;
        }
        position++;
        return true;
    }
}


class Parser {

    
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Entry point for parsing
    public void parse() {
        try {
            // Implementation of parsing logic
            // Start by calling the expression() method
            expression();
            System.out.println("Parsing successful!");
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    // Grammar rules

    private void expression() {
        term();
        while (match(TokenType.OPERATOR)) {
            System.out.println("Operator: " + tokens.get(current - 1).getValue());
            term();
        }
    }

    private void term() {
        if (match(TokenType.INT) || match(TokenType.FLOAT) || match(TokenType.IDENTIFIER)) {
            System.out.println("Literal or Identifier: " + tokens.get(current - 1).getValue());
        } else if (match(TokenType.OPERATOR) && tokens.get(current - 1).getValue().equals("(")) {
            System.out.println("Open Parenthesis");
            // Handle parentheses
            expression();
            consume(TokenType.OPERATOR, ")");
            System.out.println("Close Parenthesis");
        } else {
            // Handle syntax error
            throw new ParseException("Syntax error at position " + tokens.get(current).getValue());
        }
    }

    // Helper methods

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return tokens.get(current).getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void consume(TokenType type, String errorMessage) {
        if (check(type)) {
            advance();
        } else {
            // Handle syntax error
            throw new ParseException(errorMessage);
        }
    }

    private static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        String input = "x=5";
        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        parser.parse();
    }
}
