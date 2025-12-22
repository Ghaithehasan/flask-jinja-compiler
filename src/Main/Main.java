package Main;

import antlr.TemplateLexer;
import antlr.TemplateParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import visitor.BaseVisitor;
import ast.ASTNode;
import ast.DocumentNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main driver class for the Flask-Jinja template compiler.
 * Reads an input template file, parses it, builds an AST, and prints the AST structure.
 */
public class Main {

    /**
     * The root AST node, stored for persistence throughout execution.
     */
    private static DocumentNode astRoot = null;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: java Main.Main <input_file>");
            System.err.println("Example: java Main.Main templates/example.html");
            System.exit(1);
        }

        String inputFile = args[0];
        System.out.println("Flask-Jinja Template Compiler");
        System.out.println("=============================");
        System.out.println("Input file: " + inputFile);
        System.out.println();

        try {
            // Read input file
            InputStream inputStream = new FileInputStream(inputFile);
            CharStream charStream = CharStreams.fromStream(inputStream);

            // Create lexer
            TemplateLexer lexer = new TemplateLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Create parser
            TemplateParser parser = new TemplateParser(tokens);

            // Ensure parse tree is fully built
            parser.setBuildParseTree(true);

            // Enable basic syntax error reporting
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg, RecognitionException e) {
                    System.err.println("Syntax error at line " + line + ":" + charPositionInLine + " - " + msg);
                }
            });

            // Parse the input file
            System.out.println("Parsing input file...");
            ParseTree tree = parser.htmlDocument();

            // Build AST using visitor
            System.out.println("Building AST...");
            BaseVisitor astBuilder = new BaseVisitor();
            ASTNode astNode = astBuilder.visit(tree);

            // Validate root node
            if (astNode instanceof DocumentNode) {
                astRoot = (DocumentNode) astNode;
                System.out.println("AST built successfully!");
                System.out.println();
            } else {
                System.err.println("Warning: Root node is not a DocumentNode. Got: "
                        + (astNode != null ? astNode.getClass().getSimpleName() : "null"));

                if (astNode != null) {
                    astRoot = new DocumentNode(1);
                    astRoot.addChild(astNode);
                }
            }

            // Print the AST tree
            System.out.println("AST Tree Structure:");
            System.out.println("==================");

            if (astRoot != null) {
                System.out.println(astRoot.printTree());
            } else {
                System.out.println("(Empty AST)");
            }

            System.out.println();
            System.out.println("Compilation completed successfully.");

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error during compilation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Gets the root AST node.
     *
     * @return the root DocumentNode, or null if parsing hasn't completed yet
     */
    public static DocumentNode getAstRoot() {
        return astRoot;
    }
}
