package flask;

import grammar.flask.FlaskLexer;
import org.antlr.v4.runtime.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Base class للـ Flask Lexer
 * يتعامل مع Python indentation و brace tracking
 */
public abstract class FlaskLexerBase extends Lexer {

    // ========================================
    // STATE VARIABLES
    // ========================================

    /**
     * Queue للـ tokens الإضافية (INDENT, DEDENT)
     * لأن token واحد (NEWLINE) قد يولد عدة tokens
     */
    private LinkedList<Token> tokens = new LinkedList<>();

    /**
     * Stack لتتبع مستويات الـ indentation
     * مثال: [0, 4, 8] يعني المستويات 0 → 4 spaces → 8 spaces
     */
    private Deque<Integer> indents = new ArrayDeque<>();

    /**
     * عدد الأقواس/الأقواس المربعة/الأقواس المعقوفة المفتوحة
     * لتجاهل newlines داخلها
     */
    private int opened = 0;

    /**
     * آخر token تم إنتاجه على default channel
     * مفيد لتحديد line number للـ DEDENT tokens
     */
    private Token lastToken = null;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    protected FlaskLexerBase(CharStream input) {
        super(input);
    }

    // ========================================
    // TOKEN EMISSION
    // ========================================

    /**
     * إضافة token للـ queue
     */
    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);  // أضف للـ queue
    }

    /**
     * الحصول على Token التالي
     * يتعامل مع:
     * 1. EOF مع pending DEDENTS
     * 2. Queue من pending tokens
     */
    @Override
    public Token nextToken() {
        // ========================================
        // حالة خاصة: EOF مع indentation متبقية
        // ========================================
        if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
            // 1. احذف أي EOF tokens موجودة في الـ queue
            for (int i = tokens.size() - 1; i >= 0; i--) {
                if (tokens.get(i).getType() == EOF) {
                    tokens.remove(i);
                }
            }

            // 2. أضف NEWLINE نهائي
            this.emit(commonToken(FlaskLexer.NEWLINE, "\n"));

            // 3. أضف كل الـ DEDENTS المطلوبة
            while (!indents.isEmpty()) {
                this.emit(createDedent());
                indents.pop();
            }

            // 4. أضف EOF مرة أخرى
            this.emit(commonToken(FlaskLexer.EOF, "<EOF>"));
        }

        // ========================================
        // الحصول على Token التالي
        // ========================================
        Token next = super.nextToken();

        // تتبع آخر token على default channel
        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            this.lastToken = next;
        }

        // إذا يوجد tokens في الـ queue، أرجعهم أولاً
        return tokens.isEmpty() ? next : tokens.poll();
    }

    /**
     * إنشاء DEDENT token
     * Line number = نفس line الـ lastToken
     */
    private Token createDedent() {
        CommonToken dedent = commonToken(FlaskLexer.DEDENT, "");
        dedent.setLine(this.lastToken.getLine());
        return dedent;
    }

    /**
     * إنشاء CommonToken
     */
    private CommonToken commonToken(int type, String text) {
        int stop = this.getCharIndex() - 1;
        int start = text.isEmpty() ? stop : stop - text.length() + 1;
        return new CommonToken(
                this._tokenFactorySourcePair,
                type,
                DEFAULT_TOKEN_CHANNEL,
                start,
                stop
        );
    }

    // ========================================
    // INDENTATION HELPERS
    // ========================================

    /**
     * حساب عدد الـ indentation
     * Tab = 8 spaces (Python standard)
     *
     * من Python docs:
     * "Tabs are replaced (from left to right) by one to eight spaces
     *  such that the total number of characters up to and including
     *  the replacement is a multiple of eight"
     */
    static int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            switch (ch) {
                case '\t':
                    // Tab = عدد spaces ليصبح المجموع multiple of 8
                    count += 8 - (count % 8);
                    break;
                default:
                    // Space عادي
                    count++;
            }
        }
        return count;
    }

    /**
     * هل نحن في بداية الملف؟
     */
    protected boolean atStartOfInput() {
        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
    }

    // ========================================
    // BRACE TRACKING
    // ========================================

    /**
     * عند فتح (, [, {
     */
    protected void openBrace() {
        this.opened++;
    }

    /**
     * عند إغلاق ), ], }
     */
    protected void closeBrace() {
        this.opened--;
    }

    // ========================================
    // NEWLINE HANDLING (يُستدعى من Grammar)
    // ========================================

    /**if (spaces.trim().isEmpty() && (opened > 0 || next == '\r' || next == '\n' || next == '\f' || next == '#')) {
     skip();  // Skip blank lines with spaces
     }
     * معالجة NEWLINE
     * هذا يُستدعى من grammar في NEWLINE rule
     */
    protected void onNewLine() {
        // 1. فصل newline characters عن spaces
        String newLine = getText().replaceAll("[^\r\n\f]+", "");
        String spaces = getText().replaceAll("[\r\n\f]+", "");

        // 2. تحقق من وجود newline character فعلي
        // إذا لم يكن هناك newline character (فقط spaces)، يجب skip
        if (newLine.isEmpty()) {
            skip();  // فقط spaces بدون newline → skip
            return;
        }

        // 3. تحقق من الـ character التالي
        int next = _input.LA(1);

        // 4. تجاهل newlines في هذه الحالات:
        // - داخل (), [], {} (opened > 0)
        // - سطر فارغ (التالي newline أو comment)
        if (opened > 0 || next == '\r' || next == '\n' || next == '\f' || next == '#') {
            skip();  // Skip blank lines or newlines inside braces
            return;
        }

        // 5. أضف NEWLINE token (يوجد newline character فعلي)
        emit(commonToken(FlaskLexer.NEWLINE, newLine));

        // 6. حساب indentation
        int indent = getIndentationCount(spaces);
        int previous = indents.isEmpty() ? 0 : indents.peek();

        // 7. قارن مع المستوى السابق
        if (indent == previous) {
            // نفس المستوى → skip (لا indent ولا dedent)
            skip();
        }
        else if (indent > previous) {
            // زيادة indentation → INDENT
            indents.push(indent);
            emit(commonToken(FlaskLexer.INDENT, spaces));
        }
        else {
            // نقصان indentation → DEDENT(s)
            // قد نحتاج عدة DEDENTs (إذا نزلنا عدة مستويات)
            while (!indents.isEmpty() && indents.peek() > indent) {
                this.emit(createDedent());
                indents.pop();
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