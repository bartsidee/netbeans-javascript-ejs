/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bartsidee.nb.ejs;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;

import bartsidee.nb.ejs.lexer.api.EJSTokenId;


@LanguageRegistration(mimeType="application/x-ejs")
public class EJSLanguage extends DefaultLanguageConfig {
    
    public EJSLanguage() {
    }

    @Override
    public Language getLexerLanguage() {
        return EJSTokenId.language();
    }
    
    @Override
    public String getDisplayName() {
        return "EJS";
    }
    
    @Override
    public String getPreferredExtension() {
        return "ejs"; // NOI18N
    }

}
