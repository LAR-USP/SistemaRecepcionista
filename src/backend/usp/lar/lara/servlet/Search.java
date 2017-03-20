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
import usp.lar.lara.ontologia.LaraParser;
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

        ArrayList<ArrayList<String>> parsed = LaraParser.parse(pergunta, this.o);
        
        String answer = LaraParser.formatResponse(parsed);
        // if(parsed.get(0).size() > 0 && parsed.get(1).size() > 0){
        //     for(Iterator<String> i = parsed.get(0).iterator(); i.hasNext();){
        //         String indiv = i.next();
        //         answer = answer.concat(indiv+":\n");
        //         for(Iterator<String> j = parsed.get(1).iterator(); j.hasNext();){
        //             String prop = j.next();
        //             ArrayList<String> results = o.executaPropriedade(indiv, prop);
        //             if(results.size() > 0){
        //                 answer = answer.concat(prop+":\n");
        //                 for(Iterator<String> k = results.iterator(); k.hasNext();){
        //                     answer = answer.concat(k.next()+"\n");
        //                 }
        //             }
        //         }
        //         answer = answer.concat("\n");
        //     }
        // }
        
        response.getOutputStream().write(answer.getBytes("UTF-8"));
    }
}
