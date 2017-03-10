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

    public ArrayList<ArrayList<String>> parse(String frase){
        String[] tokens = frase.split(" ");
        ArrayList<String> individuos = null;
        ArrayList<String> propriedades = new ArrayList();
        propriedades.add("investiga");
        for(int i = 0; i < tokens.length; ++i){
            if(this.o.éEntitdade(tokens[i])){
                individuos = o.executaPropriedade(tokens[i], "éChaveDe");
            }
        }
        ArrayList<ArrayList<String>> result = new ArrayList();
        result.add(individuos);
        result.add(propriedades);
        return result;
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

        String[] parsed = this.parse(pergunta);
        
        String answer = "";
        if(parsed[0] != "" && parsed[1] != ""){
            ArrayList<String> result = o.executaPropriedade(parsed[0], parsed[1]);
            if(result != null){
                for (Iterator<String> it = result.iterator(); it.hasNext();) {
                    String t = it.next();
                    answer += t + " ";
                }
            }
        }
        response.getOutputStream().write(answer.getBytes("UTF-8"));
    }
}
