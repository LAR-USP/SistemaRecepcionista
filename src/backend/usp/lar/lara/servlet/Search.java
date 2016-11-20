package usp.lar.lara.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @brief Recebe a entrada da barra de pesquisa e à envia
 * para ser processada.
 * 
 * @author tarcisio
 */
public class Search extends HttpServlet{
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

        String name = request.getParameter("content");
        PrintWriter out = response.getWriter();
        out.println(name);
    }
}
