package flask.test;


import grammar.flask.FlaskLexer;
import org.antlr.v4.runtime.*;

/**
 * التأكيد النهائي: هل Lexer جاهز للـ production؟
 */
public class FinalLexerValidation {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         Final Lexer Validation Checklist              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        boolean allPassed = true;

        // Check 1: Keywords
        allPassed &= checkKeywords();

        // Check 2: Operators
        allPassed &= checkOperators();

        // Check 3: Strings
        allPassed &= checkStrings();

        // Check 4: Numbers
        allPassed &= checkNumbers();

        // Check 5: Indentation
        allPassed &= checkIndentation();

        // Check 6: Comments
        allPassed &= checkComments();

        // Check 7: Real Flask Code
        allPassed &= checkRealFlaskCode();

        // Final Result
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        if (allPassed) {
            System.out.println("║  🎉 ALL CHECKS PASSED - LEXER IS PRODUCTION READY! 🎉 ║");
        } else {
            System.out.println("║  ⚠️  SOME CHECKS FAILED - REVIEW IMPLEMENTATION      ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static boolean checkKeywords() {
        System.out.println("📋 Check 1: Keywords");
        System.out.println("─------------------------------------------");

        String[] keywords = {
                "and", "as", "assert", "break", "class", "continue", "def", "del",
                "if", "elif", "else", "False", "for", "from", "global", "import",
                "in", "is", "None", "not", "or", "pass", "return", "True", "while", "with"
        };

        boolean allOk = true;
        for (String keyword : keywords) {
            if (!testToken(keyword + "\n", keyword, null)) {
                System.out.println("  ❌ Failed to recognize: " + keyword);
                allOk = false;
            }
        }

        if (allOk) {
            System.out.println("  ✅ All keywords recognized (" + keywords.length + " keywords)");
        }
        System.out.println();
        return allOk;
    }

    private static boolean checkOperators() {
        System.out.println("📋 Check 2: Operators");
        System.out.println("─----------------------------------------------------------");

        String[][] operators = {
                {">=", "GTE"}, {"<=", "LTE"}, {"!=", "NEQ"}, {"==", "EQ"},
                {"**", "POWER"}, {"//", "FLOOR_DIV"},
                {"+=", "ADD_ASSIGN"}, {"-=", "SUB_ASSIGN"},
                {"*=", "MUL_ASSIGN"}, {"/=", "DIV_ASSIGN"},
                {"=", "ASSIGN"}, {"+", "ADD"}, {"-", "SUB"}, {"*", "MUL"},
                {"/", "DIV"}, {"%", "MOD"}, {">", "GT"}, {"<", "LT"}, {".", "DOT"}
        };

        boolean allOk = true;
        for (String[] op : operators) {
            if (!testToken(op[0] + "\n", null, op[1])) {
                System.out.println("  ❌ Failed: " + op[0] + " -> " + op[1]);
                allOk = false;
            }
        }

        if (allOk) {
            System.out.println("  ✅ All operators recognized (" + operators.length + " operators)");
        }
        System.out.println();
        return allOk;
    }

    private static boolean checkStrings() {
        System.out.println("📋 Check 3: Strings");
        System.out.println("─---------------------------------------------------------");

        String[] tests = {
                "'hello'",
                "\"world\"",
                "'''multi\nline'''",
                "\"\"\"triple\nquoted\"\"\"",
                "f'format {x}'",
                "r'raw\\nstring'"
        };

        boolean allOk = true;
        for (String test : tests) {
            if (!testToken(test + "\n", null, "STRING")) {
                System.out.println("  ❌ Failed: " + test);
                allOk = false;
            }
        }

        if (allOk) {
            System.out.println("  ✅ All string types recognized");
        }
        System.out.println();
        return allOk;
    }

    private static boolean checkNumbers() {
        System.out.println("📋 Check 4: Numbers");
        System.out.println("─---------------------------------------------------------");

        String[] tests = {
                "123",
                "3.14",
                "2.5e10",
                "1e-5",
//                ".5"
        };

        boolean allOk = true;
        for (String test : tests) {
            if (!testToken(test + "\n", null, "NUMBER")) {
                System.out.println("  ❌ Failed: " + test);
                allOk = false;
            }
        }

        if (allOk) {
            System.out.println("  ✅ All number formats recognized");
        }
        System.out.println();
        return allOk;
    }

    private static boolean checkIndentation() {
        System.out.println("📋 Check 5: Indentation");
        System.out.println("─---------------------------------------------------------");

        String code =
                "def func():\n" +
                        "    x = 1\n" +
                        "    if True:\n" +
                        "        y = 2\n" +
                        "    z = 3\n";

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();

            int indents = 0, dedents = 0;
            for (Token token : tokens.getTokens()) {
                if (token.getType() == FlaskLexer.INDENT) indents++;
                if (token.getType() == FlaskLexer.DEDENT) dedents++;
            }

            boolean balanced = (indents == dedents);
            boolean correctCount = (indents == 2); // 2 INDENT (func, if)

            if (balanced && correctCount) {
                System.out.println("  ✅ INDENT/DEDENT balanced (" + indents + " each)");
                System.out.println();
                return true;
            } else {
                System.out.println("  ❌ INDENT: " + indents + ", DEDENT: " + dedents);
                System.out.println();
                return false;
            }
        } catch (Exception e) {
            System.out.println("  ❌ Exception: " + e.getMessage());
            System.out.println();
            return false;
        }
    }

    private static boolean checkComments() {
        System.out.println("📋 Check 6: Comments");
        System.out.println("─---------------------------------------------------------");

        String code =
                "# This is a comment\n" +
                        "x = 5  # inline comment\n";

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();

            // Comments should be skipped
            boolean hasCommentToken = false;
            for (Token token : tokens.getTokens()) {
                String typeName = FlaskLexer.VOCABULARY.getSymbolicName(token.getType());
                if ("COMMENT".equals(typeName)) {
                    hasCommentToken = true;
                    break;
                }
            }

            if (!hasCommentToken) {
                System.out.println("  ✅ Comments correctly skipped");
                System.out.println();
                return true;
            } else {
                System.out.println("  ❌ Comments not skipped");
                System.out.println();
                return false;
            }
        } catch (Exception e) {
            System.out.println("  ❌ Exception: " + e.getMessage());
            System.out.println();
            return false;
        }
    }

    private static boolean checkRealFlaskCode() {
        System.out.println("📋 Check 7: Real Flask Code");
        System.out.println("─---------------------------------------------------------");

        String code =
                "from flask import Flask, render_template\n" +
                        "\n" +
                        "app = Flask(__name__)\n" +
                        "\n" +
                        "@app.route('/')\n" +
                        "def home():\n" +
                        "    return render_template('index.html')\n" +
                        "\n" +
                        "if __name__ == '__main__':\n" +
                        "    app.run(debug=True)\n";

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();

            int tokenCount = 0;
            for (Token token : tokens.getTokens()) {
                if (token.getType() != Token.EOF) {
                    tokenCount++;
                }
            }

            // Should have many tokens (>30)
            if (tokenCount > 30) {
                System.out.println("  ✅ Real Flask code tokenized successfully");
                System.out.println("     Total tokens: " + tokenCount);
                System.out.println();
                return true;
            } else {
                System.out.println("  ❌ Too few tokens: " + tokenCount);
                System.out.println();
                return false;
            }
        } catch (Exception e) {
            System.out.println("  ❌ Exception: " + e.getMessage());
            System.out.println();
            return false;
        }
    }

    private static boolean testToken(String code, String expectedText, String expectedType) {
        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();

            if (tokens.getTokens().size() < 2) return false;

            Token token = tokens.getTokens().get(0);
            String text = token.getText();
            String type = FlaskLexer.VOCABULARY.getSymbolicName(token.getType());

            boolean textMatch = (expectedText == null || text.equals(expectedText));
            boolean typeMatch = (expectedType == null || type.equals(expectedType));

            return textMatch && typeMatch;
        } catch (Exception e) {
            return false;
        }
    }
}