parser grammar TemplateParser;

options {
    tokenVocab = TemplateLexer;
}

// ----------------------------------------------------------------------
// Main Document Structure
// ----------------------------------------------------------------------

htmlDocument
    : scriptletOrSeaWs* XML? scriptletOrSeaWs* DTD? scriptletOrSeaWs* htmlElements* EOF
    ;

scriptletOrSeaWs
    : SCRIPTLET
    | SEA_WS
    ;

htmlElements
    : htmlMisc* htmlElement htmlMisc*
    ;

// ----------------------------------------------------------------------
// HTML Element Rules
// ----------------------------------------------------------------------

htmlElement
    : TAG_OPEN TAG_NAME htmlAttribute* (
        TAG_CLOSE (htmlContent TAG_OPEN TAG_SLASH TAG_NAME TAG_CLOSE)?
        | TAG_SLASH_CLOSE
    )
    | SCRIPTLET
    | script
    | style
    | jinjaBlock // Allow Jinja blocks at the top level
    | jinjaExpr  // Allow Jinja expressions at the top level
    | jinjaComment // Allow Jinja comments at the top level
    ;

htmlContent
    : htmlChardata? ((htmlElement | CDATA | htmlComment | jinjaBlock | jinjaExpr | jinjaComment) htmlChardata?)*
    ;

htmlAttribute
    : TAG_NAME (TAG_EQUALS ATTVALUE_VALUE)?
    ;

htmlChardata
    : HTML_TEXT
    | SEA_WS
    ;

htmlMisc
    : htmlComment
    | SEA_WS
    ;

htmlComment
    : HTML_COMMENT
    | HTML_CONDITIONAL_COMMENT
    ;

script
    : SCRIPT_OPEN (SCRIPT_BODY | SCRIPT_SHORT_BODY) SCRIPT_CLOSE
    ;

style
    : STYLE_OPEN (STYLE_BODY | STYLE_SHORT_BODY) STYLE_CLOSE
    ;

// ----------------------------------------------------------------------
// Jinja2 Templating Rules
// ----------------------------------------------------------------------

jinjaBlock
    : JINJA_BLOCK_OPEN JINJA_BLOCK_CONTENT* JINJA_BLOCK_CLOSE
    ;

jinjaExpr
    : JINJA_EXPR_OPEN JINJA_EXPR_CONTENT* JINJA_EXPR_CLOSE
    ;

jinjaComment
    : JINJA_COMMENT_OPEN JINJA_COMMENT_CONTENT* JINJA_COMMENT_CLOSE
    ;
