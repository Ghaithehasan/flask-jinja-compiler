lexer grammar TemplateLexer;

// The Lexer is based on a standard ANTLR HTML Lexer, modified to include Jinja2 templating.

// ----------------------------------------------------------------------
// DEFAULT MODE (DEFAULT_MODE) - For parsing outside of tags
// ----------------------------------------------------------------------

// Jinja Directives - Transition to Jinja modes
JINJA_BLOCK_OPEN : '{%' -> pushMode(JINJA_BLOCK_MODE);
JINJA_EXPR_OPEN  : '{{' -> pushMode(JINJA_EXPR_MODE);
JINJA_COMMENT_OPEN : '{#' -> pushMode(JINJA_COMMENT_MODE);

// HTML Structure
TAG_OPEN         : '<' -> pushMode(TAG_MODE);
HTML_COMMENT     : '<!--' .*? '-->' ;
HTML_CONDITIONAL_COMMENT : '<!' .*? '>' ;
CDATA            : '<![CDATA[' .*? ']]>' ;
DTD              : '<!' .*? '>' ;
XML              : '<?xml' .*? '?>' ;

// Script and Style tags - Transition to specialized modes
SCRIPT_OPEN      : '<script' -> pushMode(SCRIPT_MODE);
STYLE_OPEN       : '<style' -> pushMode(STYLE_MODE);

// Whitespace and Text
SEA_WS           : [ \t\r\n]+ ;
HTML_TEXT        : ~('<' | '{')+ ; // Text that is not a tag or Jinja delimiter
SCRIPTLET        : '<%' .*? '%>' ; // ASP/JSP style scriptlets

// ----------------------------------------------------------------------
// TAG MODE - For parsing inside an HTML tag (e.g., <div class="foo">)
// ----------------------------------------------------------------------

mode TAG_MODE;

TAG_SLASH_CLOSE  : '/>' -> popMode;
TAG_CLOSE        : '>' -> popMode;
TAG_SLASH        : '/';
TAG_EQUALS       : '=';
TAG_NAME         : [a-zA-Z][a-zA-Z0-9\-_]*;

// Attribute values - Modified to allow embedded Jinja expressions
ATTVALUE_VALUE
    : '"' (ATTVALUE_CHAR_DQ | JINJA_EXPR_INSIDE_DQ)* '"'
    | '\'' (ATTVALUE_CHAR_SQ | JINJA_EXPR_INSIDE_SQ)* '\''
    ;

// Whitespace inside tag is skipped
TAG_WHITESPACE   : [ \t\r\n]+ -> skip;

// Helper fragments for attribute values
fragment ATTVALUE_CHAR_DQ : ~["] ;
fragment ATTVALUE_CHAR_SQ : ~['] ;

// Jinja Expression inside attribute value (matched as a single token)
fragment JINJA_EXPR_INSIDE_DQ : '{{' .*? '}}';
fragment JINJA_EXPR_INSIDE_SQ : '{{' .*? '}}';

// ----------------------------------------------------------------------
// JINJA MODES - For parsing inside Jinja delimiters
// ----------------------------------------------------------------------

mode JINJA_BLOCK_MODE;
JINJA_BLOCK_CLOSE: '%}' -> popMode;
JINJA_BLOCK_CONTENT: . ;

mode JINJA_EXPR_MODE;
JINJA_EXPR_CLOSE : '}}' -> popMode;
JINJA_EXPR_CONTENT: . ;

mode JINJA_COMMENT_MODE;
JINJA_COMMENT_CLOSE: '#}' -> popMode;
JINJA_COMMENT_CONTENT: . ;

// ----------------------------------------------------------------------
// SCRIPT MODE - For parsing inside <script>...</script>
// ----------------------------------------------------------------------

mode SCRIPT_MODE;

SCRIPT_BODY      : ( ~('<' | '{') | '<' ~('/') | '<' '/' ~('s' | 'S') | '<' '/' ('s' | 'S') ~('c' | 'C') | '{' ~('%' | '#') )+ ;
SCRIPT_SHORT_BODY: . ; // Fallback for short script content
SCRIPT_CLOSE     : '</script' [ \t\r\n]* '>' -> popMode;

// ----------------------------------------------------------------------
// STYLE MODE - For parsing inside <style>...</style>
// ----------------------------------------------------------------------

mode STYLE_MODE;

STYLE_BODY       : ( ~('<' | '{') | '<' ~('/') | '<' '/' ~('s' | 'S') | '<' '/' ('s' | 'S') ~('t' | 'T') | '{' ~('%' | '#') )+ ;
STYLE_SHORT_BODY : . ; // Fallback for short style content
STYLE_CLOSE      : '</style' [ \t\r\n]* '>' -> popMode;
