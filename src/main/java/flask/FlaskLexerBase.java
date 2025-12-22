package flask;

import grammar.flask.FlaskLexer;
import org.antlr.v4.runtime.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Base class for Flask Lexer with Python indentation support
 */
//public abstract class FlaskLexerBase extends Lexer {
//
//    private LinkedList<Token> tokens = new LinkedList<>();
//    private Deque<Integer> indents = new ArrayDeque<>();
//    private int opened = 0;
//    private Token lastToken = null;
//
//    protected FlaskLexerBase(CharStream input) {
//        super(input);
//        // Initialize with base indentation level 0
//        indents.push(0);
//    }
//
//    @Override
//    public void emit(Token t) {
//        super.setToken(t);
//        tokens.offer(t);
//    }
//
//    @Override
//    public Token nextToken() {
//        // Handle EOF with pending DEDENTs
//        if (_input.LA(1) == EOF && indents.size() > 1) {
//            // Remove any trailing EOF tokens
//            for (int i = tokens.size() - 1; i >= 0; i--) {
//                if (tokens.get(i).getType() == EOF) {
//                    tokens.remove(i);
//                }
//            }
//
//            // Emit final NEWLINE
//            this.emit(createToken(FlaskLexer.NEWLINE, "\n"));
//
//            // Emit DEDENT for each remaining indentation level
//            while (indents.size() > 1) {
//                this.emit(createDedent());
//                indents.pop();
//            }
//
//            // Emit EOF
//            this.emit(createToken(FlaskLexer.EOF, "<EOF>"));
//        }
//
//        Token next = super.nextToken();
//
//        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
//            this.lastToken = next;
//        }
//
//        return tokens.isEmpty() ? next : tokens.poll();
//    }
//
//    private Token createDedent() {
//        CommonToken dedent = createToken(FlaskLexer.DEDENT, "");
//        if (this.lastToken != null) {
//            dedent.setLine(this.lastToken.getLine());
//        }
//        return dedent;
//    }
//
//    private CommonToken createToken(int type, String text) {
//        CommonToken token = new CommonToken(type, text);
//        token.setLine(this.getLine());
//
//        // Calculate char position based on token type
//        int charPos = this.getCharPositionInLine();
//        if (type == FlaskLexer.NEWLINE) {
//            charPos = Math.max(0, charPos - 1);
//        } else if (type == FlaskLexer.INDENT || type == FlaskLexer.DEDENT) {
//            charPos = 0;
//        }
//
//        token.setCharPositionInLine(charPos);
//        token.setStartIndex(this.getCharIndex());
//        token.setStopIndex(this.getCharIndex() + text.length() - 1);
//        return token;
//    }
//
//    static int getIndentationCount(String spaces) {
//        int count = 0;
//        for (char ch : spaces.toCharArray()) {
//            switch (ch) {
//                case '\t':
//                    count += 8 - (count % 8);  // Python standard: tab to multiple of 8
//                    break;
//                default:
//                    count++;
//            }
//        }
//        return count;
//    }
//
//    protected boolean atStartOfInput() {
//        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
//    }
//
//    protected void openBrace() {
//        this.opened++;
//    }
//
//    protected void closeBrace() {
//        this.opened--;
//    }
//
//    protected void onNewLine() {
//        String fullText = getText();
//        String newLine = fullText.replaceAll("[^\r\n\f]+", "");
//        String spaces = fullText.replaceAll("[\r\n\f]+", "");
//
//        int next = _input.LA(1);
//        int nextnext = _input.LA(2);
//
//        // Skip newlines inside parentheses/brackets/braces or blank lines
//        if (opened > 0 || (nextnext != -1 && (next == '\r' || next == '\n' || next == '\f' || next == '#'))) {
//            skip();
//        } else {
//            // Emit NEWLINE token
//            emit(createToken(FlaskLexer.NEWLINE, newLine));
//
//            // Calculate indentation
//            int indent = getIndentationCount(spaces);
//            int previous = indents.isEmpty() ? 0 : indents.peek();
//
//            if (indent == previous) {
//                // Same level - skip (no INDENT/DEDENT needed)
//                skip();
//            } else if (indent > previous) {
//                // Increased indentation - INDENT
//                indents.push(indent);
//                emit(createToken(FlaskLexer.INDENT, spaces));
//            } else {
//                // ========================================
//                // Decreased indentation - DEDENT(s)
//                // ========================================
//
//                // Emit DEDENT for each level we're dedenting
//                while (indents.size() > 1 && indents.peek() > indent) {
//                    this.emit(createDedent());
//                    indents.pop();
//                }
//
//                // ========================================
//                // CRITICAL: Validate indentation
//                // ========================================
//                // After all DEDENTs, the current indent MUST match the top of stack
//                // Otherwise: IndentationError
//                if (indents.peek() != indent) {
//                    // Construct helpful error message
//                    StringBuilder validIndents = new StringBuilder();
//                    for (Integer i : indents) {
//                        if (validIndents.length() > 0) validIndents.append(", ");
//                        validIndents.append(i);
//                    }
//
//                    throw new RuntimeException(
//                            String.format(
//                                    "IndentationError: unindent does not match any outer indentation level\n" +
//                                            "  Line %d: found %d spaces, expected one of [%s]",
//                                    getLine(), indent, validIndents.toString()
//                            )
//                    );
//                }
//            }
//        }
//    }
//
//    @Override
//    public void reset() {
//        tokens = new LinkedList<>();
//        indents = new ArrayDeque<>();
//        indents.push(0);  // Reset base level
//        opened = 0;
//        lastToken = null;
//        super.reset();
//    }
//}

import org.antlr.v4.runtime.*;
import java.util.*;

public abstract class FlaskLexerBase extends Lexer {

    private LinkedList<Token> tokens = new LinkedList<>();
    private Deque<Integer> indents = new ArrayDeque<>();
    private int opened = 0;
    private Token lastToken = null;
    private boolean expectIndent = false;

    protected FlaskLexerBase(CharStream input) {
        super(input);
        indents.push(0);
    }

    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);

        if (t.getChannel() == Token.DEFAULT_CHANNEL) {
            if (t.getType() == FlaskLexer.COLON) {
                expectIndent = true;
            } else if (t.getType() != FlaskLexer.NEWLINE &&
                    t.getType() != FlaskLexer.INDENT &&
                    t.getType() != FlaskLexer.DEDENT) {
                expectIndent = false;
            }
        }
    }

    @Override
    public Token nextToken() {
        if (_input.LA(1) == EOF && indents.size() > 1) {
            for (int i = tokens.size() - 1; i >= 0; i--) {
                if (tokens.get(i).getType() == EOF) {
                    tokens.remove(i);
                }
            }

            this.emit(createToken(FlaskLexer.NEWLINE, "\n"));

            while (indents.size() > 1) {
                this.emit(createDedent());
                indents.pop();
            }

            this.emit(createToken(FlaskLexer.EOF, "<EOF>"));
        }

        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            this.lastToken = next;
        }

        return tokens.isEmpty() ? next : tokens.poll();
    }

    private Token createDedent() {
        CommonToken dedent = createToken(FlaskLexer.DEDENT, "");
        if (this.lastToken != null) {
            dedent.setLine(this.lastToken.getLine());
        }
        return dedent;
    }

    private CommonToken createToken(int type, String text) {
        CommonToken token = new CommonToken(type, text);
        token.setLine(this.getLine());

        int charPos = this.getCharPositionInLine();
        if (type == FlaskLexer.NEWLINE) {
            charPos = Math.max(0, charPos - 1);
        } else if (type == FlaskLexer.INDENT || type == FlaskLexer.DEDENT) {
            charPos = 0;
        }

        token.setCharPositionInLine(charPos);
        token.setStartIndex(this.getCharIndex());
        token.setStopIndex(this.getCharIndex() + text.length() - 1);
        return token;
    }

    public static int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            switch (ch) {
                case '\t':
                    count += 8 - (count % 8);
                    break;
                default:
                    count++;
            }
        }
        return count;
    }

    protected boolean atStartOfInput() {
        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
    }

    protected void openBrace() {
        this.opened++;
    }

    protected void closeBrace() {
        this.opened--;
    }

    protected void onNewLine() {
        String fullText = getText();
        String newLine = fullText.replaceAll("[^\r\n\f]+", "");
        String spaces = fullText.replaceAll("[\r\n\f]+", "");

        int next = _input.LA(1);
        int nextnext = _input.LA(2);

        if (opened > 0 || (nextnext != -1 && (next == '\r' || next == '\n' || next == '\f' || next == '#'))) {
            skip();
        } else {
            emit(createToken(FlaskLexer.NEWLINE, newLine));

            int indent = getIndentationCount(spaces);
            int previous = indents.isEmpty() ? 0 : indents.peek();

            if (indent == previous) {
                skip();
                expectIndent = false;
            } else if (indent > previous) {
                if (!expectIndent) {
                    throw new RuntimeException(
                            String.format(
                                    "IndentationError: unexpected indent at line %d\n" +
                                            "  Previous indentation: %d spaces\n" +
                                            "  Current indentation: %d spaces\n" +
                                            "  Hint: Indentation can only increase after ':', 'if:', 'for:', 'def:', etc.",
                                    getLine(), previous, indent
                            )
                    );
                }

                indents.push(indent);
                emit(createToken(FlaskLexer.INDENT, spaces));
                expectIndent = false;

            } else {
                while (indents.size() > 1 && indents.peek() > indent) {
                    this.emit(createDedent());
                    indents.pop();
                }

                if (indents.peek() != indent) {
                    StringBuilder validIndents = new StringBuilder();
                    for (Integer i : indents) {
                        if (validIndents.length() > 0) validIndents.append(", ");
                        validIndents.append(i);
                    }

                    throw new RuntimeException(
                            String.format(
                                    "IndentationError: unindent does not match any outer indentation level\n" +
                                            "  Line %d: found %d spaces, expected one of [%s]",
                                    getLine(), indent, validIndents.toString()
                            )
                    );
                }

                expectIndent = false;  // Reset
            }
        }
    }

    @Override
    public void reset() {
        tokens = new LinkedList<>();
        indents = new ArrayDeque<>();
        indents.push(0);
        opened = 0;
        lastToken = null;
        expectIndent = false;  // ← NEW: Reset
        super.reset();
    }
}