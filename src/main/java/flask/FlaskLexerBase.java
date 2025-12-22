package flask;

import grammar.flask.FlaskLexer;
import org.antlr.v4.runtime.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public abstract class FlaskLexerBase extends Lexer {

    private LinkedList<Token> tokens = new LinkedList<>();
    private Deque<Integer> indents = new ArrayDeque<>();
    private int opened = 0;
    private Token lastToken = null;

    protected FlaskLexerBase(CharStream input) {
        super(input);
    }

    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);
    }

    @Override
    public Token nextToken() {
        if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
            for (int i = tokens.size() - 1; i >= 0; i--) {
                if (tokens.get(i).getType() == EOF) {
                    tokens.remove(i);
                }
            }
            this.emit(createToken(FlaskLexer.NEWLINE, "\n"));
            while (!indents.isEmpty()) {
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
        dedent.setLine(this.lastToken.getLine());
        return dedent;
    }

    private CommonToken createToken(int type, String text) {
        CommonToken token = new CommonToken(type, text);
        token.setLine(this.getLine());

        // Calculate char position based on token type
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

    static int getIndentationCount(String spaces) {
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
            } else if (indent > previous) {
                indents.push(indent);
                emit(createToken(FlaskLexer.INDENT, spaces));
            } else {
                while (!indents.isEmpty() && indents.peek() > indent) {
                    this.emit(createDedent());
                    indents.pop();
                }
            }
        }
    }

    @Override
    public void reset() {
        tokens = new LinkedList<>();
        indents = new ArrayDeque<>();
        opened = 0;
        lastToken = null;
        super.reset();
    }
}