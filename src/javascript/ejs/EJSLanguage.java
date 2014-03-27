/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javascript.ejs;

//import java.util.Collections;
//import java.util.Set;
import org.netbeans.api.lexer.Language;
//import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
//import org.netbeans.modules.parsing.spi.indexing.PathRecognizerRegistration;
//import org.netbeans.modules.javascript2.editor.JsLanguage;
//import org.netbeans.modules.javascript2.editor.Utils;

import javascript.ejs.lexer.api.EJSTokenId;


@LanguageRegistration(mimeType="application/x-ejs")
//@PathRecognizerRegistration(mimeTypes="application/x-ejs", sourcePathIds=JsLanguage.SOURCE, libraryPathIds=JsLanguage.BOOT, binaryLibraryPathIds={}) //NOI18N
public class EJSLanguage extends DefaultLanguageConfig {
    
//    public EJSLanguage() {
//    }

    @Override
    public Language getLexerLanguage() {
        return EJSTokenId.language();
    }
    
    @Override
    public String getDisplayName() {
        return "EJS";
    }
    
//    @Override
//    public String getPreferredExtension() {
//        return "ejs"; // NOI18N
//    }
    
//    @Override
//    public boolean isUsingCustomEditorKit() {
//        return true;
//    }

//    @Override
//    public Parser getParser() {
//        return new RhtmlParser();
//    }
//
//    @Override
//    public boolean hasStructureScanner() {
//        return true;
//    }

//    @Override
//    public StructureScanner getStructureScanner() {
//        return new RhtmlScanner();
//    }
//
//    private class RhtmlScanner extends RubyStructureAnalyzer {
//        @Override
//        public Configuration getConfiguration() {
//            return new Configuration(false, false, 0);
//        }
//    }
//
//    @Override
//    public EmbeddingIndexerFactory getIndexerFactory() {
//        return new RubyIndexer.Factory();
//    }
//
//    @Override
//    public Set<String> getSourcePathIds() {
//        return Collections.singleton(RubyLanguage.SOURCE);
//    }
//
//    @Override
//    public Set<String> getLibraryPathIds() {
//        return Collections.singleton(RubyLanguage.BOOT);
//    }
//
//    @Override
//    public String getLineCommentPrefix() {
//        return RubyUtils.getLineCommentPrefix();
//    }
//
//    @Override
//    public boolean isIdentifierChar(char c) {
//        return RubyUtils.isIdentifierChar(c);
//    }

}
