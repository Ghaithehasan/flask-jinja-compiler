parser grammar TemplateParser;

@header {
package antlr;
}

options {
    tokenVocab = TemplateLexer;
}

// ----------------------------------------------------------------------
// Root rule: represents the full HTML document
// ----------------------------------------------------------------------
htmlDocument
    : htmlElements* EOF
      #Document
    ;

// ----------------------------------------------------------------------
// Top-level HTML elements
// ----------------------------------------------------------------------
htmlElements
    : htmlMisc* htmlElement htmlMisc*
      #Elements
    ;

// ----------------------------------------------------------------------
// Single HTML element
// ----------------------------------------------------------------------
htmlElement
    : TAG_OPEN TAG_NAME htmlAttribute* (
        TAG_CLOSE (htmlContent TAG_OPEN TAG_SLASH TAG_NAME TAG_CLOSE)?
        | TAG_SLASH_CLOSE
      )                       #TagElement
    | style                   #StyleElement
    | jinjaBlock              #JinjaBlockElement
    | jinjaExpr               #JinjaExprElement
    | jinjaComment            #JinjaCommentElement
    ;

// ----------------------------------------------------------------------
// Content inside an HTML element
// ----------------------------------------------------------------------
htmlContent
    : htmlChardata?
      (
        ( htmlElement
        | htmlComment
        | jinjaBlock
        | jinjaExpr
        | jinjaComment
        )
        htmlChardata?
      )*
      #Content
    ;

// ----------------------------------------------------------------------
// HTML attribute
// ----------------------------------------------------------------------
htmlAttribute
    : TAG_NAME (TAG_EQUALS ATTVALUE_VALUE)?
      #Attribute
    ;

// ----------------------------------------------------------------------
// Character data
// ----------------------------------------------------------------------
htmlChardata
    : HTML_TEXT   #TextContent
    | SEA_WS      #WhitespaceContent
    ;

// ----------------------------------------------------------------------
// Misc content between elements
// ----------------------------------------------------------------------
htmlMisc
    : htmlComment #MiscComment
    | SEA_WS      #MiscWhitespace
    ;

// ----------------------------------------------------------------------
// HTML comments
// ----------------------------------------------------------------------
htmlComment
    : HTML_COMMENT               #StandardComment
    | HTML_CONDITIONAL_COMMENT   #ConditionalComment
    ;

// ----------------------------------------------------------------------
// Style block
// ----------------------------------------------------------------------
style
    : STYLE_OPEN (STYLE_CONTENT | STYLE_CHAR_FALLBACK)* STYLE_CLOSE
      #StyleBlock
    ;

// ----------------------------------------------------------------------
// Jinja block
// ----------------------------------------------------------------------
jinjaBlock
    : JINJA_BLOCK_OPEN JINJA_BLOCK_CONTENT* JINJA_BLOCK_CLOSE
      #JinjaBlockRule
    ;

// ----------------------------------------------------------------------
// Jinja expression
// ----------------------------------------------------------------------
jinjaExpr
    : JINJA_EXPR_OPEN JINJA_EXPR_CONTENT* JINJA_EXPR_CLOSE
      #JinjaExpression
    ;

// ----------------------------------------------------------------------
// Jinja comment
// ----------------------------------------------------------------------
jinjaComment
    : JINJA_COMMENT_OPEN JINJA_COMMENT_CONTENT* JINJA_COMMENT_CLOSE
      #JinjaCommentRule
    ;



