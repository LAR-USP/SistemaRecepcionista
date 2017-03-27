package usp.lar.lara.servlet;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import usp.lar.lara.ontologia.LaraParser;
import usp.lar.lara.ontologia.Ontologia;

/**
 * @brief Recebe a entrada da barra de pesquisa e à envia
 * para ser processada.
 * 
 * @author Tarcisio
 */
public class Search extends HttpServlet{

    private final Ontologia o;

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
        if ( pergunta.isEmpty() ) return;

        ArrayList<ArrayList<String>> parsed = LaraParser.parse( pergunta, this.o );

        String answer = LaraParser.formatResponse( parsed, o );

        response.getOutputStream().write(answer.getBytes( "UTF-8" ) );
    }

}
