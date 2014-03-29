/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bartsidee.nb.ejs.lexer.api;

import bartsidee.nb.javascript2.lexer.api.JsTokenId;
import bartsidee.nb.ejs.lexer.EJSLexer;
import org.netbeans.api.html.lexer.HTMLTokenId;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author bartvandenende
 */
public enum EJSTokenId implements TokenId {

    HTML("html"),
    /** Contents inside <%# %> */
    JSCOMMENT("comment"),
    /** Contents inside <%= %> <%- %>*/
    JS_EXPR("js"),
    /** Contents inside <% %> */
    JS("js"),
    /** <% or %> */
    DELIMITER("ejs-delimiter"); 

    public static final String MIME_TYPE = "application/x-ejs"; // NOI18N
    
    private final String primaryCategory;
    
    public static boolean isJS(TokenId id) {
        return id == JS || id == JS_EXPR || id == JSCOMMENT;
    }

    EJSTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    // Token ids declaration
    private static final Language<EJSTokenId> language = new LanguageHierarchy<EJSTokenId>() {
        @Override
        protected Collection<EJSTokenId> createTokenIds() {
            return EnumSet.allOf(EJSTokenId.class);
        }
        
        @Override
        protected Map<String,Collection<EJSTokenId>> createTokenCategories() {
            return null;
        }
        
        @Override
        public Lexer<EJSTokenId> createLexer(LexerRestartInfo<EJSTokenId> info) {
            return new EJSLexer(info);
        }
        
        @Override
        protected LanguageEmbedding<? extends TokenId> embedding(Token<EJSTokenId> token,
                                  LanguagePath languagePath, InputAttributes inputAttributes) {
            switch(token.id()) {
                case HTML:
                    return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
                case JS_EXPR:
                case JS:
                    return LanguageEmbedding.create(JsTokenId.javascriptLanguage(), 0, 0, false);
                default:
                    return null;
            }
        }
        
        @Override
        public String mimeType() {
            return EJSTokenId.MIME_TYPE;
        }
    }.language();
    
    public static Language<EJSTokenId> language() {
        return language;
    }
    
}
