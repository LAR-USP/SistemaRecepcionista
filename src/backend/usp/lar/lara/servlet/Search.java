package usp.lar.lara.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.*;
import javax.servlet.http.*;
import usp.lar.lara.ontology.LaraParser;
import usp.lar.lara.ontology.OntologySearch;
import usp.lar.lara.calendar.CalendarSearch;

/**
 * @brief Recebe a entrada da barra de pesquisa e à envia
 * para ser processada.
 * 
 * @author Tarcisio
 */
public class Search extends HttpServlet{
    private OntologySearch os = new OntologySearch();

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

        Gson g = new Gson();

        String[] propriedades = null;
        String pergunta = request.getParameter("content");

        String entidade = request.getParameter("entity");
        String props_temp = request.getParameter("properties");
        if(props_temp != null && props_temp.length() > 2){
            propriedades = props_temp.substring(2, props_temp.length()-2).split("\", \"");
        }

        JsonObject rv = null;
        if(LaraParser.requestType(pergunta) == LaraParser.Type.ONTOLOGY){
            rv = this.os.search(pergunta, entidade, propriedades);
        } else {
            rv = CalendarSearch.getList(pergunta);
        }
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write( g.toJson(rv) );//.getBytes( "UTF-8" ) );
    }

}
