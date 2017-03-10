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
        String[] partes = pergunta.split(" ");
        
        String answer = "";
        if(partes.length == 2){
            ArrayList<String> result = o.executaPropriedade(partes[0], partes[1]);
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
