package flask.test;

import flask.FlaskLexerBase;
import grammar.flask.FlaskLexer;
import org.antlr.v4.runtime.*;


public class IndentationTestSuite {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     Flask Indentation Comprehensive Test Suite        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Category 1: Valid Indentation
        testValidIndentation();

        // Category 2: Invalid Indentation
        testInvalidIndentation();

        // Category 3: Edge Cases
        testEdgeCases();

        // Summary
        printSummary();
    }

    // ═══════════════════════════════════════════════════════════
    // Valid Indentation Tests (يجب أن تنجح)
    // ═══════════════════════════════════════════════════════════

    private static void testValidIndentation() {
        System.out.println("✅ Category 1: Valid Indentation (Should Pass)");
        System.out.println("══════════════════════════════════════════════════════");

        // Test 1.1: Basic function
        testValid("Basic Function",
                "def hello():\n" +
                        "    return 'Hi'\n",
                new int[]{0, 4}
        );

        // Test 1.2: Nested if
        testValid("Nested If",
                "def test():\n" +
                        "    if True:\n" +
                        "        x = 1\n",
                new int[]{0, 4, 8}
        );

        // Test 1.3: Multiple levels
        testValid("Multiple Levels",
                "def func():\n" +
                        "    if True:\n" +
                        "        for i in range(10):\n" +
                        "            print(i)\n",
                new int[]{0, 4, 8, 12}
        );

        // Test 1.4: Consistent spacing
        testValid("Consistent 2 Spaces",
                "def func():\n" +
                        "  if True:\n" +
                        "    x = 1\n",
                new int[]{0, 2, 4}
        );

        // Test 1.5: Dedent to multiple levels
        testValid("Dedent Multiple Levels",
                "def func():\n" +
                        "    if True:\n" +
                        "        x = 1\n" +
                        "    y = 2\n",
                new int[]{0, 4, 8, 4}
        );

        // Test 1.6: Decorator
        testValid("Decorator",
                "@app.route('/')\n" +
                        "def home():\n" +
                        "    return 'Home'\n",
                new int[]{0, 4}
        );
    }

    // ═══════════════════════════════════════════════════════════
    // Invalid Indentation Tests (يجب أن تفشل)
    // ═══════════════════════════════════════════════════════════

    private static void testInvalidIndentation() {
        System.out.println("\n❌ Category 2: Invalid Indentation (Should Fail)");
        System.out.println("════════════════════════════════════════════════════════════v");

        // Test 2.1: Unexpected indent (الحالة الأصلية)
        testInvalid("Unexpected Indent",
                "def hello():\n" +
                        "    mes = 'ahmad'\n" +
                        "      return 'Hello'\n",  // ← 6 spaces بدون سبب!
                "unexpected indent"
        );

        // Test 2.2: Random indentation increase
        testInvalid("Random Indent Increase",
                "x = 1\n" +
                        "    y = 2\n",  // ← indent بدون colon!
                "unexpected indent"
        );

        // Test 2.3: Unmatched dedent
        testInvalid("Unmatched Dedent",
                "def func():\n" +
                        "    if True:\n" +
                        "        x = 1\n" +
                        "   y = 2\n",  // ← 3 spaces (لا يوجد مستوى 3)
                "unindent does not match"
        );

        // Test 2.4: Inconsistent indentation
        testInvalid("Inconsistent Indent",
                "def func():\n" +
                        "    x = 1\n" +
                        "  y = 2\n",  // ← 2 spaces (لا يوجد مستوى 2)
                "unindent does not match"
        );

        // Test 2.5: Indent in middle of block
        testInvalid("Indent in Middle",
                "def func():\n" +
                        "    x = 1\n" +
                        "        y = 2\n",  // ← زيادة بدون colon
                "unexpected indent"
        );
    }

    // ═══════════════════════════════════════════════════════════
    // Edge Cases
    // ═══════════════════════════════════════════════════════════

    private static void testEdgeCases() {
        System.out.println("\n⚠️  Category 3: Edge Cases");
        System.out.println("════════════════════════════════════════════");

        // Test 3.1: Empty lines
        testValid("Empty Lines",
                "def func():\n" +
                        "    x = 1\n" +
                        "\n" +
                        "    y = 2\n",
                new int[]{0, 4, 4}
        );

        // Test 3.2: Comments
        testValid("Comments",
                "def func():\n" +
                        "    # Comment\n" +
                        "    x = 1\n",
                new int[]{0, 4}
        );

        // Test 3.3: Multi-line expressions
        testValid("Multi-line Expression",
                "x = (1 + 2 +\n" +
                        "     3 + 4)\n",
                new int[]{0}
        );

        // Test 3.4: Tabs vs Spaces (consistent)
        testValid("Tabs",
                "def func():\n" +
                        "\tx = 1\n",
                new int[]{0, 8}  // tab = 8 spaces
        );
    }

    // ═══════════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════════

    private static void testValid(String testName, String code, int[] expectedIndents) {
        System.out.printf("  %-35s ", testName + "...");

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            tokens.fill();

            // ✅ FIX: Track INDENT/DEDENT balance instead of just INDENT tokens
            int indentCount = 0;
            int dedentCount = 0;
            java.util.Set<Integer> seenLevels = new java.util.HashSet<>();
            seenLevels.add(0);  // Base level

            for (Token token : tokens.getTokens()) {
                if (token.getType() == FlaskLexer.INDENT) {
                    indentCount++;
                    String text = token.getText();
                    int spaces = FlaskLexerBase.getIndentationCount(text);
                    seenLevels.add(spaces);
                } else if (token.getType() == FlaskLexer.DEDENT) {
                    dedentCount++;
                }
            }

            // Validate: INDENT and DEDENT must be balanced
            boolean balanced = (indentCount == dedentCount);

            // Validate: All expected indent levels should be seen
            boolean levelsMatch = true;
            for (int expected : expectedIndents) {
                if (!seenLevels.contains(expected)) {
                    levelsMatch = false;
                    break;
                }
            }

            if (balanced && levelsMatch) {
                System.out.println("✅ PASS");
                passedTests++;
            } else {
                System.out.println("❌ FAIL");
                if (!balanced) {
                    System.out.println("    INDENT/DEDENT imbalance: " + indentCount + " INDENT, " + dedentCount + " DEDENT");
                }
                if (!levelsMatch) {
                    System.out.println("    Expected levels: " + java.util.Arrays.toString(expectedIndents));
                    System.out.println("    Seen levels:     " + seenLevels);
                }
                failedTests++;
            }

        } catch (Exception e) {
            System.out.println("❌ FAIL (Exception)");
            System.out.println("    " + e.getMessage());
            failedTests++;
        }
    }

    private static void testInvalid(String testName, String code, String expectedError) {
        System.out.printf("  %-35s ", testName + "...");

        try {
            CharStream input = CharStreams.fromString(code);
            FlaskLexer lexer = new FlaskLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            tokens.fill();  // This should throw exception

            // If we reach here, test failed (should have thrown error)
            System.out.println("❌ FAIL (No error thrown)");
            failedTests++;

        } catch (RuntimeException e) {
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains(expectedError.toLowerCase())) {
                System.out.println("✅ PASS (Correct error)");
                passedTests++;
            } else {
                System.out.println("❌ FAIL (Wrong error)");
                System.out.println("    Expected error containing: " + expectedError);
                System.out.println("    Got: " + e.getMessage());
                failedTests++;
            }
        }
    }

    private static void printSummary() {
        int total = passedTests + failedTests;
        double percentage = (double) passedTests / total * 100;

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    Test Summary                        ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests:      %-35d║%n", total);
        System.out.printf("║  ✅ Passed:        %-35d║%n", passedTests);
        System.out.printf("║  ❌ Failed:        %-35d║%n", failedTests);
        System.out.printf("║  Success Rate:     %.1f%%                              ║%n", percentage);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        if (failedTests == 0) {
            System.out.println("\n🎉 Perfect! All indentation tests passed!");
            System.out.println("   Lexer correctly handles INDENT/DEDENT in all cases.");
        } else {
            System.out.println("\n⚠️  Some tests failed. Review the implementation.");
        }
    }
}