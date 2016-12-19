package usp.lar.lara.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.*;
import javax.servlet.http.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.parser.*;
import opennlp.tools.util.Span;
import usp.lar.lara.ontologia.Ontologia;

/**
 * @brief Recebe a entrada da barra de pesquisa e à envia
 * para ser processada.
 * 
 * @author tarcisio
 */
public class Search extends HttpServlet{
    private Ontologia o;

    public Search(){
        o = new Ontologia();
    }
    /**
     * @brief Recebe um envio GET do AJAX em search.js.
     * @param request Conteúdo da mensagem.
     * @param response Resposta a ser enviada de volta ao script.
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        doPost(request, response);
    }

    /**
     * @brief Recebe um envio POST do AJAX em search.js.
     * @param request Conteúdo da mensagem.
     * @param response Resposta a ser enviada de volta ao script.
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String pergunta = request.getParameter("content");
        
        Tokenizer _tokenizer = null;

        InputStream modelIn = null;
        String[] token = null;
        String tag = null;
        try {
            // Loading tokenizer model
            modelIn = getClass().getResourceAsStream("/pt-pos-maxent.bin");
            final POSModel posModel = new POSModel(modelIn);
            modelIn.close();

            modelIn = getClass().getResourceAsStream("/pt-token.bin");
            final TokenizerModel tokenModel = new TokenizerModel(modelIn);
            modelIn.close();
 
            _tokenizer = new TokenizerME(tokenModel);
            POSTaggerME _posTagger = new POSTaggerME(posModel);

            token = _tokenizer.tokenize(pergunta);
            tag = _posTagger.tag(pergunta);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        };
        String[] tag_tokens = tag.split(" ");
        String propriedade = null;
        String individuo = null;
        for(int i = 0; i < tag_tokens.length; ++i){
            if(tag_tokens[i].contains("/n")){
                if(propriedade == null){
                    propriedade = token[i];
                } else if(token[i] == "professor" ||
                          token[i] == "professora"){
                    ++i;
                    individuo = token[i];
                }
            }
        }
        if(individuo == null){
            for(int i = 0; i < tag_tokens.length; ++i){
                if(tag_tokens[i].contains("/prop") ||
                   tag_tokens[i].contains("/prp")){
                    individuo = token[i];
                    break;
                }
            }
            if(individuo != null){
                ArrayList<String> nome_completo = o.executaPropriedade(individuo, "chave");
                Iterator<String> it = nome_completo.iterator();
                if(it.hasNext()){
                    individuo = it.next();
                    for (; it.hasNext();) {
                        individuo += " " + it.next();
                    }
                    if(individuo != ""){
                        individuo = individuo.substring(individuo.length()-1);
                    }
                }
            }
        }

        if(individuo == null || propriedade == null){
            return;
        }
        
        ArrayList<String> result = o.executaPropriedade(individuo, propriedade);
        String answer = "";
        for (Iterator<String> it = result.iterator(); it.hasNext();) {
            String t = it.next();
            answer += token + " ";
        }
        response.getOutputStream().write(answer.getBytes("UTF-8"));
    }
}
