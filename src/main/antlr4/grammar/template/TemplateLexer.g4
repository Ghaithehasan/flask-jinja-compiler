lexer grammar TemplateLexer;

// ----------------------------------------------------------------------
// DEFAULT MODE (DEFAULT_MODE)
// ----------------------------------------------------------------------

// --- Jinja Delimiters ---
JINJA_BLOCK_OPEN   : '{%' -> pushMode(JINJA_BLOCK_MODE);   // Matches '{%' and enters Jinja block mode.
JINJA_EXPR_OPEN    : '{{' -> pushMode(JINJA_EXPR_MODE);    // Matches '{{' and enters Jinja expression mode.
JINJA_COMMENT_OPEN : '{#' -> pushMode(JINJA_COMMENT_MODE); // Matches '{#' and enters Jinja comment mode.

// --- HTML Tags and Comments ---
TAG_OPEN           : '<' -> pushMode(TAG_MODE);            // Matches '<' and enters tag parsing mode.
HTML_COMMENT       : '<!--' .*? '-->' ;                    // Matches standard HTML comments, e.g., <!-- ... -->.
HTML_CONDITIONAL_COMMENT
                   : '<!' .*? '>' ;                        // Matches IE conditional comments or declarations.


STYLE_OPEN         : '<' [sS][tT][yY][lL][eE] ( ~'>' )* '>'
                     -> pushMode(STYLE_MODE);              // Matches a <style> opening tag and enters style mode.

// --- Whitespace and Text ---
SEA_WS             : [ \t\r\n]+ ;                           // Matches one or more whitespace characters.
HTML_TEXT          : ~('<' | '{')+ ;                        // Matches raw HTML text outside tags and Jinja.

// ----------------------------------------------------------------------
// TAG MODE - For parsing inside an HTML tag (e.g., <div class="..">).
// ----------------------------------------------------------------------

mode TAG_MODE;

// --- Tag Delimiters ---
TAG_SLASH_CLOSE    : '/>' -> popMode;                       // Matches '/>' for self-closing tags.
TAG_CLOSE          : '>' -> popMode;                        // Matches '>' and exits tag mode.
TAG_SLASH          : '/';                                   // Matches '/' used in closing tags.
TAG_EQUALS         : '=';                                   // Matches '=' in attribute assignments.

// --- Tag Content ---
TAG_NAME           : [a-zA-Z][a-zA-Z0-9\-_]*;               // Matches an HTML tag or attribute name.

// --- Attribute Values (allows embedded Jinja expressions) ---
ATTVALUE_VALUE
    : '"' (ATTVALUE_CHAR_DQ | JINJA_EXPR_INSIDE_DQ)* '"'    // Double-quoted attribute value.
    | '\'' (ATTVALUE_CHAR_SQ | JINJA_EXPR_INSIDE_SQ)* '\''  // Single-quoted attribute value.
    ;

TAG_WHITESPACE     : [ \t\r\n]+ -> skip;                    // Skips whitespace inside tag declarations.

// --- Fragments for Attribute Values ---
fragment ATTVALUE_CHAR_DQ
    : ~["] ;                                                // Matches any character except double quote.

fragment ATTVALUE_CHAR_SQ
    : ~['] ;                                                // Matches any character except single quote.

// --- Fragments for Embedded Jinja ---
fragment JINJA_EXPR_INSIDE_DQ
    : '{{' .*? '}}' ;                                       // Matches Jinja expression inside double quotes.

fragment JINJA_EXPR_INSIDE_SQ
    : '{{' .*? '}}' ;                                       // Matches Jinja expression inside single quotes.

// ----------------------------------------------------------------------
// JINJA MODES - For parsing content within Jinja delimiters.
// ----------------------------------------------------------------------

// --- Jinja Block Mode ---
mode JINJA_BLOCK_MODE;

JINJA_BLOCK_CLOSE
    : '%}' -> popMode;                                      // Closes Jinja block and returns to previous mode.

JINJA_BLOCK_CONTENT
    : . ;                                                   // Matches any character inside a Jinja block.

// --- Jinja Expression Mode ---
mode JINJA_EXPR_MODE;

JINJA_EXPR_CLOSE
    : '}}' -> popMode;                                      // Closes Jinja expression and returns to previous mode.

JINJA_EXPR_CONTENT
    : . ;                                                   // Matches any character inside a Jinja expression.

// --- Jinja Comment Mode ---
mode JINJA_COMMENT_MODE;

JINJA_COMMENT_CLOSE
    : '#}' -> popMode;                                      // Closes Jinja comment and returns to previous mode.

JINJA_COMMENT_CONTENT
    : . ;                                                   // Matches any character inside a Jinja comment.

// ----------------------------------------------------------------------
// STYLE MODE - For parsing content inside a <style> tag.
// ----------------------------------------------------------------------

mode STYLE_MODE;

// --- Embedded Jinja Delimiters ---
JINJA_BLOCK_OPEN_IN_STYLE
    : '{%' -> pushMode(JINJA_BLOCK_MODE);                   // Allows Jinja blocks inside <style>.

JINJA_EXPR_OPEN_IN_STYLE
    : '{{' -> pushMode(JINJA_EXPR_MODE);                    // Allows Jinja expressions inside <style>.

JINJA_COMMENT_OPEN_IN_STYLE
    : '{#' -> pushMode(JINJA_COMMENT_MODE);                 // Allows Jinja comments inside <style>.

// --- Style Closing Tag ---
STYLE_CLOSE
    : '</' [sS][tT][yY][lL][eE] [ \t\r\n]* '>'
      -> popMode;                                          // Matches closing </style> tag.

// --- Style Content ---
STYLE_CONTENT
    : ( ~('<') | '<' ~'/' )+ ;                              // Matches CSS content inside <style>.

// --- Fallback ---
STYLE_CHAR_FALLBACK
    : . ;                                                   // Fallback rule to consume any remaining character.


