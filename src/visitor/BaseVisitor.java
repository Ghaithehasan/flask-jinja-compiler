package visitor;

import antlr.TemplateParser;
import antlr.TemplateParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import ast.*;


public class BaseVisitor extends TemplateParserBaseVisitor<ASTNode> {

    private final CssParser cssParser;

    public BaseVisitor() {
        this.cssParser = new CssParser();
    }



    private int getLineNumber(ParserRuleContext ctx) {
        if (ctx != null && ctx.start != null) {
            return ctx.start.getLine();
        }
        return 1;
    }

    /* ------------------ Root / Top-level ------------------ */

    @Override
    public ASTNode visitDocument(TemplateParser.DocumentContext ctx) {
        DocumentNode documentNode = new DocumentNode(getLineNumber(ctx));
        if (ctx.htmlElements() != null) {
            for (TemplateParser.HtmlElementsContext elCtx : ctx.htmlElements()) {
                ASTNode n = visit(elCtx); // dispatch to visitElements
                if (n != null) documentNode.addChild(n);
            }
        }
        return documentNode;
    }

    @Override
    public ASTNode visitElements(TemplateParser.ElementsContext ctx) {
        // The htmlElement child will be a labeled alternative (TagElementContext, ...)
        if (ctx.htmlElement() != null) {
            return visit(ctx.htmlElement()); // dispatch to the specific visitor
        }
        return null;
    }

    /* ------------------ htmlElement alternatives ------------------ */

    @Override
    public ASTNode visitTagElement(TemplateParser.TagElementContext ctx) {
        // TAG_NAME(0) is the opening tag name
        String tagName = "";
        if (ctx.TAG_NAME() != null && !ctx.TAG_NAME().isEmpty()) {
            tagName = ctx.TAG_NAME(0).getText();
        }

        boolean selfClosing = ctx.TAG_SLASH_CLOSE() != null;
        HtmlElementNode elementNode = new HtmlElementNode(tagName, getLineNumber(ctx), selfClosing);

        // attributes
        if (ctx.htmlAttribute() != null) {
            for (TemplateParser.HtmlAttributeContext attrCtx : ctx.htmlAttribute()) {
                ASTNode attrNode = visit(attrCtx); // dispatch to visitAttribute
                if (attrNode != null) elementNode.addChild(attrNode);
            }
        }

        // content (if not self-closing)
        if (!selfClosing && ctx.htmlContent() != null) {
            ASTNode content = visit(ctx.htmlContent()); // dispatch to visitContent
            if (content != null) elementNode.addChild(content);
        }

        return elementNode;
    }

    @Override
    public ASTNode visitStyleElement(TemplateParser.StyleElementContext ctx) {
        // delegated to style rule's visitor
        if (ctx.style() != null) {
            return visit(ctx.style()); // dispatch to visitStyleBlock
        }
        return null;
    }

    @Override
    public ASTNode visitJinjaBlockElement(TemplateParser.JinjaBlockElementContext ctx) {
        if (ctx.jinjaBlock() != null) return visit(ctx.jinjaBlock()); // visitJinjaBlockRule
        return null;
    }

    @Override
    public ASTNode visitJinjaExprElement(TemplateParser.JinjaExprElementContext ctx) {
        if (ctx.jinjaExpr() != null) return visit(ctx.jinjaExpr()); // visitJinjaExpression
        return null;
    }

    @Override
    public ASTNode visitJinjaCommentElement(TemplateParser.JinjaCommentElementContext ctx) {
        if (ctx.jinjaComment() != null) return visit(ctx.jinjaComment()); // visitJinjaCommentRule
        return null;
    }

    /* ------------------ Content ------------------ */

    @Override
    public ASTNode visitContent(TemplateParser.ContentContext ctx) {
        ContentNode container = new ContentNode(getLineNumber(ctx));

        // htmlChardata children (these are labeled alternatives: TextContent/WhitespaceContent)
        if (ctx.htmlChardata() != null) {
            for (TemplateParser.HtmlChardataContext ch : ctx.htmlChardata()) {
                ASTNode tn = visit(ch); // dispatch to visitTextContent / visitWhitespaceContent
                if (tn != null) container.addChild(tn);
            }
        }

        // child elements
        if (ctx.htmlElement() != null) {
            for (TemplateParser.HtmlElementContext el : ctx.htmlElement()) {
                ASTNode en = visit(el); // dispatch to the specific htmlElement alternative
                if (en != null) container.addChild(en);
            }
        }

        // htmlComment children
        if (ctx.htmlComment() != null) {
            for (TemplateParser.HtmlCommentContext c : ctx.htmlComment()) {
                ASTNode commentNode = visit(c); // dispatch to StandardComment/ConditionalComment
                if (commentNode != null) container.addChild(commentNode);
            }
        }

        // jinjaBlock children
        if (ctx.jinjaBlock() != null) {
            for (TemplateParser.JinjaBlockContext jb : ctx.jinjaBlock()) {
                ASTNode jn = visit(jb); // visitJinjaBlockRule
                if (jn != null) container.addChild(jn);
            }
        }

        // jinjaExpr children
        if (ctx.jinjaExpr() != null) {
            for (TemplateParser.JinjaExprContext je : ctx.jinjaExpr()) {
                ASTNode jn = visit(je); // visitJinjaExpression
                if (jn != null) container.addChild(jn);
            }
        }

        // jinjaComment children
        if (ctx.jinjaComment() != null) {
            for (TemplateParser.JinjaCommentContext jc : ctx.jinjaComment()) {
                ASTNode jn = visit(jc); // visitJinjaCommentRule
                if (jn != null) container.addChild(jn);
            }
        }

        return container;
    }

    /* ------------------ Attributes / chardata ------------------ */

    @Override
    public ASTNode visitAttribute(TemplateParser.AttributeContext ctx) {
        String attrName = "";
        String attrValue = null;

        if (ctx.TAG_NAME() != null) {
            attrName = ctx.TAG_NAME().getText();
        }

        if (ctx.ATTVALUE_VALUE() != null) {
            String value = ctx.ATTVALUE_VALUE().getText();
            if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'")))) {
                attrValue = value.substring(1, value.length() - 1);
            } else {
                attrValue = value;
            }
        }

        return new AttributeNode(attrName, attrValue, getLineNumber(ctx));
    }

    @Override
    public ASTNode visitTextContent(TemplateParser.TextContentContext ctx) {
        String text = ctx.HTML_TEXT() != null ? ctx.HTML_TEXT().getText() : "";
        if (text == null) text = "";
        if (!text.trim().isEmpty()) {
            return new TextNode(text, getLineNumber(ctx));
        }
        // if it's only whitespace, let whitespace visitor handle it
        return null;
    }

    @Override
    public ASTNode visitWhitespaceContent(TemplateParser.WhitespaceContentContext ctx) {
        String ws = ctx.SEA_WS() != null ? ctx.SEA_WS().getText() : "";
        if (ws == null) ws = "";
        // preserve whitespace nodes if needed (you may choose to skip)
        return new TextNode(ws, getLineNumber(ctx));
    }

    /* ------------------ htmlMisc / comments ------------------ */

    @Override
    public ASTNode visitMiscComment(TemplateParser.MiscCommentContext ctx) {
        if (ctx.htmlComment() != null) return visit(ctx.htmlComment());
        return null;
    }

    @Override
    public ASTNode visitMiscWhitespace(TemplateParser.MiscWhitespaceContext ctx) {
        // intentionally skip misc whitespace
        return null;
    }

    @Override
    public ASTNode visitStandardComment(TemplateParser.StandardCommentContext ctx) {
        String comment = ctx.HTML_COMMENT() != null ? ctx.HTML_COMMENT().getText() : "";
        if (comment.startsWith("<!--") && comment.endsWith("-->")) {
            comment = comment.substring(4, comment.length() - 3);
        }
        return new HtmlCommentNode(comment, getLineNumber(ctx));
    }

    @Override
    public ASTNode visitConditionalComment(TemplateParser.ConditionalCommentContext ctx) {
        String comment = ctx.HTML_CONDITIONAL_COMMENT() != null ? ctx.HTML_CONDITIONAL_COMMENT().getText() : "";
        if (comment.startsWith("<!") && comment.endsWith(">")) {
            comment = comment.substring(2, comment.length() - 1);
        }
        return new HtmlCommentNode(comment, getLineNumber(ctx));
    }

    /* ------------------ Style rule ------------------ */

    @Override
    public ASTNode visitStyleBlock(TemplateParser.StyleBlockContext ctx) {
        StyleNode styleNode = new StyleNode(getLineNumber(ctx));
        StringBuilder cssContent = new StringBuilder();

        // STYLE_CONTENT tokens
        for (TerminalNode tn : ctx.getTokens(TemplateParser.STYLE_CONTENT)) {
            cssContent.append(tn.getText());
        }

        // STYLE_CHAR_FALLBACK tokens
        for (TerminalNode tn : ctx.getTokens(TemplateParser.STYLE_CHAR_FALLBACK)) {
            cssContent.append(tn.getText());
        }

        if (cssContent.length() > 0) {
            String rawCss = cssContent.toString();
            int lineNumber = getLineNumber(ctx);
            CssNode cssNode = new CssNode(rawCss, lineNumber);
            
            // Parse CSS and build structured AST using CssParser
            CssStylesheetNode stylesheetNode = cssParser.parse(rawCss, lineNumber);
            if (stylesheetNode != null) {
                cssNode.addChild(stylesheetNode);
            }
            
            styleNode.addChild(cssNode);
        }
        return styleNode;
    }

    /* ------------------ Jinja rules ------------------ */

    @Override
    public ASTNode visitJinjaBlockRule(TemplateParser.JinjaBlockRuleContext ctx) {
        StringBuilder blockContent = new StringBuilder();
        for (TerminalNode tn : ctx.getTokens(TemplateParser.JINJA_BLOCK_CONTENT)) {
            blockContent.append(tn.getText());
        }
        return new JinjaBlockNode(blockContent.toString(), getLineNumber(ctx));
    }

    @Override
    public ASTNode visitJinjaExpression(TemplateParser.JinjaExpressionContext ctx) {
        StringBuilder expr = new StringBuilder();
        for (TerminalNode tn : ctx.getTokens(TemplateParser.JINJA_EXPR_CONTENT)) {
            expr.append(tn.getText());
        }
        return new JinjaExpressionNode(expr.toString(), getLineNumber(ctx));
    }

    @Override
    public ASTNode visitJinjaCommentRule(TemplateParser.JinjaCommentRuleContext ctx) {
        StringBuilder comment = new StringBuilder();
        for (TerminalNode tn : ctx.getTokens(TemplateParser.JINJA_COMMENT_CONTENT)) {
            comment.append(tn.getText());
        }
        return new JinjaCommentNode(comment.toString(), getLineNumber(ctx));
    }

    /* ------------------ htmlComment / jinjaElement delegations ------------------ */

    public ASTNode visitJinjaBlock(TemplateParser.JinjaBlockContext ctx) {
        // fallback: if generic jinjaBlock context reached, delegate to children
        return visitChildren(ctx);
    }

    public ASTNode visitJinjaExpr(TemplateParser.JinjaExprContext ctx) {
        return visitChildren(ctx);
    }
    public ASTNode visitJinjaComment(TemplateParser.JinjaCommentContext ctx) {
        return visitChildren(ctx);
    }


    public ASTNode visitHtmlMisc(TemplateParser.HtmlMiscContext ctx) {
        return null;
    }


    public ASTNode visitHtmlElements(TemplateParser.HtmlElementsContext ctx) {
        return null;
    }


    public ASTNode visitHtmlElement(TemplateParser.HtmlElementContext ctx) {
        return null;
    }


    public ASTNode visitHtmlContent(TemplateParser.HtmlContentContext ctx) {
        return null;
    }


    public ASTNode visitHtmlAttribute(TemplateParser.HtmlAttributeContext ctx) {
        return null;
    }


    public ASTNode visitHtmlChardata(TemplateParser.HtmlChardataContext ctx
    ) {
        return null;
    }

    public ASTNode visitHtmlComment(TemplateParser.HtmlCommentContext ctx) {
        return null;
    }

    public ASTNode visitStyle(TemplateParser.StyleContext ctx) {
        return null;
    }
    public ASTNode visitHtmlDocument(TemplateParser.HtmlDocumentContext ctx) {
        return visitChildren(ctx);
    }

}
