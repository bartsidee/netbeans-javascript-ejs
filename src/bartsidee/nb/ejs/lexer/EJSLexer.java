/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bartsidee.nb.ejs.lexer;

import bartsidee.nb.ejs.lexer.api.EJSTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author bartvandenende
 */
public class EJSLexer implements Lexer<EJSTokenId> {
    
    private static final int EOF = LexerInput.EOF;
    
    private final LexerInput input;
    
    private final TokenFactory<EJSTokenId> tokenFactory;
    
    @Override
    public Object state() {
        return state;
    }
    
    //main internal lexer state
    private int state = INIT;
    
    // Internal analyzer states
    private static final int INIT                     = 0;  // initial lexer state = content language
    private static final int ISA_LT                   = 1; // after '<' char
    private static final int ISA_LT_PC                = 2; // after '<%' - comment or directive or scriptlet
    private static final int ISI_SCRIPTLET            = 3; // inside JS scriptlet
    private static final int ISI_SCRIPTLET_PC         = 4; // just after % in scriptlet
    private static final int ISI_COMMENT_SCRIPTLET    = 5; // Inside a JS comment scriptlet
    private static final int ISI_COMMENT_SCRIPTLET_PC = 6; // just after % in a JS comment scriptlet
    private static final int ISI_EXPR_SCRIPTLET       = 7; // inside JS expression scriptlet
    private static final int ISI_EXPR_SCRIPTLET_PC    = 8; // just after % in an expression scriptlet
    private static final int ISI_JS_LINE              = 9; // just after % in an %-line
    
    public EJSLexer(LexerRestartInfo<EJSTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        if (info.state() == null) {
            this.state = INIT;
        } else {
            state = (Integer) info.state();
        }
    }
    
    private Token<EJSTokenId> token(EJSTokenId id) {
        if(input.readLength() == 0) {
            new Exception("Error - token length is zero!; state = " + state).printStackTrace();
        }
        Token<EJSTokenId> t = tokenFactory.createToken(id);
        return t;
    }
    
    @Override
    public Token<EJSTokenId> nextToken() {
        int actChar;
        while (true) {
            actChar = input.read();
            
            if (actChar == EOF) {
                if(input.readLengthEOF() == 1) {
                    return null; //just EOL is read
                } else {
                    //there is something else in the buffer except EOL
                    //we will return last token now
                    input.backup(1); //backup the EOL, we will return null in next nextToken() call
                    break;
                }
            }
            
            switch (state) {
                case INIT:
                    switch (actChar) {
//                        case '\n':
//                            return token(EJSTokenId.EOL);
                        case '<':
                            state = ISA_LT;
                            break;
                            
                        case '%': {
                            int peek = input.read();
                            if (peek == '%') {
                                // %% means just %
                                break;
                            }
                            if (peek != LexerInput.EOF) {
                                input.backup(1);
                            }
                            
                            // See if we're in a line prefix
                            if (input.readLength() == 1) {
                                state = ISI_JS_LINE;
                                return token(EJSTokenId.DELIMITER);
                            }
                            CharSequence cs = input.readText();
                            // -2: skip the final %
                            for (int i = cs.length()-2; i >= 0; i--) {
                                char c = cs.charAt(i);
                                if (c == '\n') {
                                    // We're in a new line: Finish this token as HTML.
                                    input.backup(1);
                                    // When we come back we'll just process the line as a delimiter
                                    return token(EJSTokenId.HTML);
                                } else if (!Character.isWhitespace(c)) {
                                    // The % is not the beginning of a line
                                    break;
                                }
                            }
                            break;
                    }
                }
                break;
                    
                case ISA_LT:
                    switch (actChar) {
                        case '%':
                            state = ISA_LT_PC;
                            break;
                        default:
                            state = INIT; //just content
//                            state = ISI_TAG_ERROR;
//                            break;
                    }
                    break;
                    
                case ISA_LT_PC:
                    switch (actChar) {
                        case '=': 
                            if(input.readLength() == 3) {
                                // just <%! or <%= read
                                state = ISI_EXPR_SCRIPTLET;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                // EJS symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%=
                                state = INIT;
                                return token(EJSTokenId.HTML); //return CL token
                            }
                        case '%': {
                            int peek = input.read();
                            if (peek != LexerInput.EOF) {
                                input.backup(1);
                            }
                            if (peek != '>') {
                                // Handle <%% == <%
                                if(input.readLength() == 3) {
                                    // <%% is just an escape for <% in HTML...
                                    state = INIT;
                                    break;
                                } else {
                                    // EJS symbol, but we also have content language in the buffer
                                    input.backup(3); //backup <%@
                                    state = INIT;
                                    return token(EJSTokenId.HTML); //return CL token
                                }
                            } else if (input.readLength() == 3) {
                                // We have <%%> - it's just a <% opener followed by a %> closer;
                                // digest the open delimiter now
                                input.backup(1);
                                state = ISI_SCRIPTLET;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                state = INIT;
                                input.backup(3);
                                return token(EJSTokenId.HTML);
                            }
                        }
                            
                        case '#':
                            if(input.readLength() == 3) {
                                // just <%! or <%= read
                                state = ISI_COMMENT_SCRIPTLET;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                //jsp symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%! or <%=
                                state = INIT;
                                return token(EJSTokenId.HTML); //return CL token
                            }
                        case '-':
                            if(input.readLength() == 3) {
                                // just read <%-
                                state = ISI_SCRIPTLET;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                // EJS symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%-
                                state = INIT;
                                return token(EJSTokenId.HTML); //return CL token
                            }
                        default:  // EJS scriptlet delimiter '<%'
                            if(input.readLength() == 3) {
                                // just <% + something != [=,#] read
                                state = ISI_SCRIPTLET;
                                input.backup(1); //backup the third character, it is a part of the JS scriptlet
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                // EJS symbol, but we also have content language in the buffer
                                input.backup(3); //backup <%@
                                state = INIT;
                                return token(EJSTokenId.HTML); //return CL token
                            }
                    }
                    break;
                    
                case ISI_COMMENT_SCRIPTLET:
                    switch(actChar) {
                        case '%':
                            state = ISI_COMMENT_SCRIPTLET_PC;
                            break;
                    }
                    break;
                    
                    
                case ISI_SCRIPTLET:
                    switch(actChar) {
                        case '%':
                            state = ISI_SCRIPTLET_PC;
                            break;
                    }
                    break;
                    

                case ISI_SCRIPTLET_PC:
                    switch(actChar) {
                        case '>':
                            if(input.readLength() == 2) {
                                //just the '%>' symbol read
                                state = INIT;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                //return the scriptlet content
                                input.backup(2); // backup '%>' we will read JUST them again
                                state = ISI_SCRIPTLET;
                                return token(EJSTokenId.JS);
                            }
                        default:
                            state = ISI_SCRIPTLET;
                            break;
                    }
                    break;

                case ISI_JS_LINE:
                    while (actChar != '\n') {
                        actChar = input.read();
                        if (actChar == LexerInput.EOF) {
                            break;
                        }
                    }
                    if (actChar == '\n') {
                        input.backup(1);
                    }
                    state = INIT;
                    if (input.readLength() > 0) {
                        return token(EJSTokenId.JS);
                    }
                    break;

                case ISI_EXPR_SCRIPTLET:
                    switch(actChar) {
                        case '%':
                            state = ISI_EXPR_SCRIPTLET_PC;
                            break;
                    }
                    break;
                    

                case ISI_EXPR_SCRIPTLET_PC:
                    switch(actChar) {
                        case '>':
                            if(input.readLength() == 2) {
                                //just the '%>' symbol read
                                state = INIT;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                //return the scriptlet content
                                input.backup(2); // backup '%>' we will read JUST them again
                                state = ISI_EXPR_SCRIPTLET;
                                return token(EJSTokenId.JS_EXPR);
                            }
                        default:
                            state = ISI_EXPR_SCRIPTLET;
                            break;
                    }
                    break;
                    
                case ISI_COMMENT_SCRIPTLET_PC:
                    switch(actChar) {
                        case '>':
                            if(input.readLength() == 2) {
                                //just the '%>' symbol read
                                state = INIT;
                                return token(EJSTokenId.DELIMITER);
                            } else {
                                //return the scriptlet content
                                input.backup(2); // backup '%>' we will read JUST them again
                                state = ISI_COMMENT_SCRIPTLET;
                                return token(EJSTokenId.JSCOMMENT);
                            }
                        default:
                            state = ISI_COMMENT_SCRIPTLET;
                            break;
                    }
                    break;
            }
        }
        
        // At this stage there's no more text in the scanned buffer.
        // Scanner first checks whether this is completely the last
        // available buffer.
        
        switch(state) {
            case INIT:
                if (input.readLength() == 0) {
                    return null;
                } else {
                    return token(EJSTokenId.HTML);
                }
            case ISA_LT:
                state = INIT;
                return token(EJSTokenId.DELIMITER);
            case ISA_LT_PC:
                state = INIT;
                return token(EJSTokenId.DELIMITER);
            case ISI_SCRIPTLET_PC:
                state = INIT;
                return token(EJSTokenId.DELIMITER);
            case ISI_SCRIPTLET:
                state = INIT;
                return token(EJSTokenId.JS);
            case ISI_EXPR_SCRIPTLET_PC:
                state = INIT;
                return token(EJSTokenId.DELIMITER);
            case ISI_EXPR_SCRIPTLET:
                state = INIT;
                return token(EJSTokenId.JS_EXPR);
            case ISI_COMMENT_SCRIPTLET_PC:
                state = INIT;
                return token(EJSTokenId.DELIMITER);
            case ISI_COMMENT_SCRIPTLET:
                state = INIT;
                return token(EJSTokenId.JSCOMMENT);
                
                
            default:
                System.out.println("EJSLexer - unhandled state : " + state);   // NOI18N
        }
        
        return null;
        
    }
    
    @Override
    public void release() {
    }
    
}
