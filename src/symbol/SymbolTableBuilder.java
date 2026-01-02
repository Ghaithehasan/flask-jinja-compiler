package symbol;

import ast.*;

/**
 * Symbol table builder that traverses the AST and populates a symbol table.
 * Uses recursive traversal to visit all nodes and register symbols appropriately,
 * respecting scopes (e.g., HTML elements introduce nested scopes, style blocks introduce CSS scope).
 */
public class SymbolTableBuilder {
    private SymbolTable symbolTable;

    /**
     * Constructs a symbol table builder with a new symbol table.
     */
    public SymbolTableBuilder() {
        this.symbolTable = new SymbolTable();
    }

    /**
     * Constructs a symbol table builder with the given symbol table.
     *
     * @param symbolTable the symbol table to populate
     */
    public SymbolTableBuilder(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * Builds the symbol table by traversing the AST starting from the root node.
     *
     * @param root the root AST node
     * @return the populated symbol table
     */
    public SymbolTable build(ASTNode root) {
        if (root != null) {
            visit(root);
        }
        return symbolTable;
    }

    /**
     * Gets the symbol table.
     *
     * @return the symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * Recursively visits an AST node and processes it based on its type.
     *
     * @param node the AST node to visit
     */
    private void visit(ASTNode node) {
        if (node == null) {
            return;
        }

        // Process node based on its type
        if (node instanceof DocumentNode) {
            visitDocumentNode((DocumentNode) node);
        } else if (node instanceof HtmlElementNode) {
            visitHtmlElementNode((HtmlElementNode) node);
            // Children are visited inside visitHtmlElementNode
            return; // Return early to avoid double visiting
        } else if (node instanceof AttributeNode) {
            visitAttributeNode((AttributeNode) node);
        } else if (node instanceof StyleNode) {
            visitStyleNode((StyleNode) node);
            // Children are visited inside visitStyleNode
            return; // Return early to avoid double visiting
        } else if (node instanceof CssRuleNode) {
            visitCssRuleNode((CssRuleNode) node);
            // Children are visited inside visitCssRuleNode
            return; // Return early to avoid double visiting
        } else if (node instanceof CssSelectorNode) {
            visitCssSelectorNode((CssSelectorNode) node);
        } else if (node instanceof CssDeclarationNode) {
            visitCssDeclarationNode((CssDeclarationNode) node);
        } else if (node instanceof JinjaBlockNode) {
            visitJinjaBlockNode((JinjaBlockNode) node);
            // Children are visited inside visitJinjaBlockNode
            return; // Return early to avoid double visiting
        } else if (node instanceof JinjaExpressionNode) {
            visitJinjaExpressionNode((JinjaExpressionNode) node);
        } else if (node instanceof CssStylesheetNode) {
            visitCssStylesheetNode((CssStylesheetNode) node);
        }

        // Recursively visit children (for nodes that don't handle children themselves)
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }

    /**
     * Visits a DocumentNode (root of the document).
     *
     * @param node the document node
     */
    private void visitDocumentNode(DocumentNode node) {
        // Document is the root, already in global scope
        // No symbol to register, just traverse children
    }

    /**
     * Visits an HtmlElementNode (HTML tag).
     * Registers the tag as a symbol and enters a new element-level scope.
     *
     * @param node the HTML element node
     */
    private void visitHtmlElementNode(HtmlElementNode node) {
        String tagName = node.getTagName();
        if (tagName != null && !tagName.isEmpty()) {
            Symbol symbol = new Symbol(tagName, SymbolType.HTML_TAG, 
                                       symbolTable.getCurrentScopeName(), 
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }

        // Enter element-level scope for nested content (only if not self-closing)
        if (!node.isSelfClosing()) {
            String elementScope = "element:" + (tagName != null ? tagName : "unknown");
            symbolTable.enterScope(elementScope);
        }

        // Process children
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }

        // Exit element-level scope after processing children
        if (!node.isSelfClosing()) {
            symbolTable.exitScope();
        }
    }

    /**
     * Visits an AttributeNode (HTML attribute).
     * Registers the attribute as a symbol.
     *
     * @param node the attribute node
     */
    private void visitAttributeNode(AttributeNode node) {
        String attrName = node.getAttributeName();
        if (attrName != null && !attrName.isEmpty()) {
            Symbol symbol = new Symbol(attrName, SymbolType.HTML_ATTRIBUTE,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }
    }

    /**
     * Visits a StyleNode (<style> block).
     * Enters a CSS scope for the style content.
     *
     * @param node the style node
     */
    private void visitStyleNode(StyleNode node) {
        // Enter CSS scope
        symbolTable.enterScope("style");
        
        // Process children (which will include CSS rules)
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
        
        // Exit CSS scope after processing style content
        symbolTable.exitScope();
    }

    /**
     * Visits a CssStylesheetNode.
     * The stylesheet itself doesn't need to be registered, but we track it.
     *
     * @param node the CSS stylesheet node
     */
    private void visitCssStylesheetNode(CssStylesheetNode node) {
        // Stylesheet is a container, no symbol to register
        // Just process children
    }

    /**
     * Visits a CssRuleNode (CSS rule with selector).
     * Registers the rule and enters a rule-level scope.
     *
     * @param node the CSS rule node
     */
    private void visitCssRuleNode(CssRuleNode node) {
        String selector = node.getSelector();
        if (selector != null && !selector.isEmpty()) {
            Symbol symbol = new Symbol(selector, SymbolType.CSS_RULE,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }
        
        // Enter rule-level scope for declarations
        symbolTable.enterScope("rule:" + (selector != null ? selector : "unknown"));
        
        // Process children (selectors and declarations)
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
        
        // Exit rule scope
        symbolTable.exitScope();
    }

    /**
     * Visits a CssSelectorNode (CSS selector).
     * Registers the selector as a symbol.
     *
     * @param node the CSS selector node
     */
    private void visitCssSelectorNode(CssSelectorNode node) {
        String selector = node.getSelector();
        if (selector != null && !selector.isEmpty()) {
            Symbol symbol = new Symbol(selector, SymbolType.CSS_SELECTOR,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }
    }

    /**
     * Visits a CssDeclarationNode (CSS property:value declaration).
     * Registers the property as a symbol.
     *
     * @param node the CSS declaration node
     */
    private void visitCssDeclarationNode(CssDeclarationNode node) {
        String property = node.getProperty();
        if (property != null && !property.isEmpty()) {
            Symbol symbol = new Symbol(property, SymbolType.CSS_PROPERTY,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }
    }



    /**
     * Visits a JinjaBlockNode (Jinja block {% ... %}).
     * Registers the block and enters a block-level scope.
     *
     * @param node the Jinja block node
     */
    private void visitJinjaBlockNode(JinjaBlockNode node) {
        String blockContent = node.getBlockContent();
        if (blockContent != null && !blockContent.trim().isEmpty()) {
            // Extract block name (e.g., "if", "for", "block", etc.)
            String blockName = extractBlockName(blockContent);
            Symbol symbol = new Symbol(blockName, SymbolType.JINJA_BLOCK,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
        }

        // Enter block-level scope
        symbolTable.enterScope("jinja-block");
        
        // Process children
        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
        
        // Exit block scope
        symbolTable.exitScope();
    }

    /**
     * Visits a JinjaExpressionNode (Jinja expression {{ ... }}).
     * Registers the expression and attempts to extract variable names.
     *
     * @param node the Jinja expression node
     */
    private void visitJinjaExpressionNode(JinjaExpressionNode node) {
        String expression = node.getExpressionContent();
        if (expression != null && !expression.trim().isEmpty()) {
            Symbol symbol = new Symbol(expression.trim(), SymbolType.JINJA_EXPRESSION,
                                       symbolTable.getCurrentScopeName(),
                                       node.getLineNumber());
            symbolTable.define(symbol);
            
            // Try to extract variable names from expression
            // This is a simple extraction - in a real compiler, you'd parse the expression properly
            String[] tokens = expression.trim().split("[\\s.()\\[\\]|]+");
            for (String token : tokens) {
                token = token.trim();
                if (!token.isEmpty() && isPotentialVariable(token)) {
                    Symbol varSymbol = new Symbol(token, SymbolType.JINJA_VARIABLE,
                                                   symbolTable.getCurrentScopeName(),
                                                   node.getLineNumber());
                    symbolTable.define(varSymbol);
                }
            }
        }
    }

    /**
     * Extracts the block name from Jinja block content.
     * For example, "if condition" -> "if", "for item in items" -> "for".
     *
     * @param blockContent the full block content
     * @return the block name (first word)
     */
    private String extractBlockName(String blockContent) {
        if (blockContent == null || blockContent.trim().isEmpty()) {
            return "block";
        }
        String trimmed = blockContent.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex > 0) {
            return trimmed.substring(0, spaceIndex);
        }
        return trimmed;
    }

    /**
     * Checks if a token could be a variable name.
     * Filters out common operators, keywords, and non-variable tokens.
     *
     * @param token the token to check
     * @return true if the token looks like a variable name
     */
    private boolean isPotentialVariable(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // Filter out common operators and keywords
        String[] keywords = {"and", "or", "not", "in", "is", "if", "else", "for", "with"};
        for (String keyword : keywords) {
            if (token.equalsIgnoreCase(keyword)) {
                return false;
            }
        }
        
        // Filter out operators and special characters
        if (token.matches("^[+\\-*/%=<>!]+$")) {
            return false;
        }
        
        // Filter out numbers
        if (token.matches("^\\d+(\\.\\d+)?$")) {
            return false;
        }
        
        // Filter out strings (quoted)
        if ((token.startsWith("\"") && token.endsWith("\"")) ||
            (token.startsWith("'") && token.endsWith("'"))) {
            return false;
        }
        
        // Should start with a letter or underscore
        return token.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
}

