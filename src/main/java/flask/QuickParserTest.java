package flask;

import grammar.flask.FlaskLexer;
import grammar.flask.FlaskParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * اختبار سريع للتأكد من أن Parser الجديد يعمل بشكل صحيح
 */
public class QuickParserTest {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         Quick Test for New Parser Structure           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Test 1: الكود الأصلي (بدون سطر فارغ)
        test1_Original();

        // Test 2: الكود الأصلي (مع سطر فارغ)
        test2_OriginalWithBlankLine();

        // Test 3: Flask app كامل
        test3_CompleteApp();

        // Test 4: Edge cases
        test4_EdgeCases();
    }

    // ═══════════════════════════════════════════════════════════
    // Test 1: الكود الأصلي (بدون سطر فارغ)
    // ═══════════════════════════════════════════════════════════
    private static void test1_Original() {
        System.out.println("🧪 Test 1: Original Code (No Blank Line)");
        System.out.println("──────────────────────────────────────────────────────────────────");

        String code =
                "@app.route('about')\n" +
                        "def about():\n" +
                        "    def ali():\n"+
                        "        message = 'This is Flask app'\n";

        TestResult result = parseCode(code);
        result.print();

        // Validations
        result.shouldContain("decoratedDef", "decorator", "functionDef", "suite");
        result.shouldNotHaveErrors();
        result.printValidation();
    }

    // ═══════════════════════════════════════════════════════════
    // Test 2: الكود الأصلي (مع سطر فارغ)
    // ═══════════════════════════════════════════════════════════
    private static void test2_OriginalWithBlankLine() {
        System.out.println("\n🧪 Test 2: Original Code (With Blank Line)");
        System.out.println("─────────────────────────────────────────────────────────────────v");

        String code =
                "@app.route('about')\n" +
                        "def about():\n" +
                        "    message = 'This is Flask app'\n" +
                        "\n";  // ← السطر الفارغ

        TestResult result = parseCode(code);
        result.print();

        result.shouldContain("decoratedDef", "decorator", "functionDef");
        result.shouldNotHaveErrors();
        result.printValidation();
    }

    // ═══════════════════════════════════════════════════════════
    // Test 3: Flask app كامل
    // ═══════════════════════════════════════════════════════════
    private static void test3_CompleteApp() {
        System.out.println("\n🧪 Test 3: Complete Flask App");
        System.out.println("───────────────────────────────────────────────────────────────");

        String code =
                "from flask import Flask\n" +
                        "\n" +
                        "app = Flask(__name__)\n" +
                        "\n" +
                        "@app.route('/')\n" +
                        "def home():\n" +
                        "    x= 'Hello'\n" +
                        "\n" +
                        "@app.route('/about')\n" +
                        "def about():\n" +
                        "    message = 'This is Flask app'\n" +
                        "    ii= message\n";

        TestResult result = parseCode(code);
        result.print();

        result.shouldContain("importStatement", "decoratedDef", "assignmentStatement");
        result.shouldNotHaveErrors();
        result.printValidation();
    }

    // ═══════════════════════════════════════════════════════════
    // Test 4: Edge Cases
    // ═══════════════════════════════════════════════════════════
    private static void test4_EdgeCases() {
        System.out.println("\n🧪 Test 4: Edge Cases");
        System.out.println("─-----------------------------------------------------------------");

        String[] testCases = {
                "Empty File: |\n|",
                "Only Comment: |# Comment\n|",
                "Multiple Blank Lines: |\n\n\n|",
                "One-liner Function: |def f(): return 5\n|"
        };

        String[] codes = {
                "\n",
                "# Comment\n",
                "\n\n\n",
                "def f(): return 5\n"
        };

        for (int i = 0; i < testCases.length; i++) {
            System.out.printf("  %-40s ", testCases[i]);
            TestResult result = parseCode(codes[i]);
            if (result.hasErrors) {
                System.out.println("❌");
            } else {
                System.out.println("✅");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Helper: Parse Code and Return Result
    // ═══════════════════════════════════════════════════════════
    private static TestResult parseCode(String code) {
        TestResult result = new TestResult();
        result.code = code;

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FlaskParser parser = new FlaskParser(tokens);

            // Error listener
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    result.hasErrors = true;
                    result.errorMessage = "Line " + line + ":" + charPositionInLine + " - " + msg;
                }
            });

            // Parse
            ParseTree tree = parser.program();
            result.tree = tree.toStringTree(parser);
            result.parser = parser;

            // Collect tokens
            tokens.fill();
            result.tokenCount = 0;
            for (Token token : tokens.getTokens()) {
                if (token.getType() != Token.EOF) {
                    result.tokenCount++;
                }
            }

            // Check INDENT/DEDENT balance
            tokens.seek(0);
            tokens.fill();
            int indents = 0, dedents = 0;
            for (Token token : tokens.getTokens()) {
                if (token.getType() == FlaskLexer.INDENT) indents++;
                if (token.getType() == FlaskLexer.DEDENT) dedents++;
            }
            result.indentBalance = (indents == dedents);

        } catch (Exception e) {
            result.hasErrors = true;
            result.errorMessage = "Exception: " + e.getMessage();
        }

        return result;
    }

    // ═══════════════════════════════════════════════════════════
    // TestResult Class
    // ═══════════════════════════════════════════════════════════
    static class TestResult {
        String code;
        String tree;
        FlaskParser parser;
        boolean hasErrors = false;
        String errorMessage = "";
        int tokenCount = 0;
        boolean indentBalance = true;

        java.util.List<String> expectedNodes = new java.util.ArrayList<>();
        java.util.List<String> validations = new java.util.ArrayList<>();

        void print() {
            System.out.println("Input:");
            System.out.println(code.replace("\n", "\\n\n"));

            if (hasErrors) {
                System.out.println("\n❌ Parsing FAILED");
                System.out.println("Error: " + errorMessage);
            } else {
                System.out.println("\n✅ Parsing SUCCESSFUL");
                System.out.println("Tokens: " + tokenCount);
                System.out.println("INDENT/DEDENT Balanced: " + (indentBalance ? "✅" : "❌"));
            }
        }

        void shouldContain(String... nodes) {
            for (String node : nodes) {
                expectedNodes.add(node);
            }
        }

        void shouldNotHaveErrors() {
            if (hasErrors) {
                validations.add("❌ Should not have errors, but got: " + errorMessage);
            } else {
                validations.add("✅ No errors");
            }
        }

        void printValidation() {
            System.out.println("\nValidations:");

            // Check expected nodes
            for (String node : expectedNodes) {
                if (tree != null && tree.contains(node)) {
                    System.out.println("  ✅ Contains: " + node);
                } else {
                    System.out.println("  ❌ Missing: " + node);
                }
            }

            // Check balance
            if (!indentBalance) {
                System.out.println("  ❌ INDENT/DEDENT imbalance");
            }

            // Other validations
            for (String validation : validations) {
                System.out.println("  " + validation);
            }
        }
    }
}