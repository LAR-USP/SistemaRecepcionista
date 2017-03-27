package usp.lar.lara.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

        Gson g = new Gson();

        String[] propriedades = null;
        String pergunta = request.getParameter("content");
        String entidade = request.getParameter("entity");
        String props_temp = request.getParameter("properties");
        if(props_temp != null && props_temp.length() > 2){
            propriedades = props_temp.substring(2, props_temp.length()-2).split("\", \"");
        }
        JsonArray novas_props = new JsonArray();

        ArrayList<ArrayList<String>> parsed = LaraParser.parse( pergunta, this.o );
        if(parsed.get(0).isEmpty()){
            if(entidade != null && entidade != ""){
                parsed.get(0).add(entidade);
            }
        } else if(parsed.get(0).size() == 1){
            entidade = parsed.get(0).get(0);
        } else {
            entidade = "";
        }
        if (parsed.get(1).isEmpty()){
            if(propriedades != null && propriedades.length > 0){
                for(int i = 0; i < propriedades.length; ++i){
                    novas_props.add(propriedades[i]);
                }
                parsed.get(1).addAll(Arrays.asList(propriedades));
            }
        } else {
            parsed.get(1).forEach(novas_props::add);
        }

        String answer = LaraParser.formatResponse( parsed, o );
        
        JsonObject rv = new JsonObject();
        rv.addProperty("answer", answer);
        rv.addProperty("entity", entidade);
        rv.add("properties", novas_props);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write( g.toJson(rv) );//.getBytes( "UTF-8" ) );
    }

}
