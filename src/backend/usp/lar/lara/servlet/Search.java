package usp.lar.lara.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.Collator;
import java.util.Locale;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import usp.lar.lara.ontology.LaraParser;
import usp.lar.lara.calendar.CalendarSearch;
import usp.lar.lara.ontology.OntologySearch;

/**
 * @brief Recebe a entrada da barra de pesquisa e à envia
 * para ser processada.
 * 
 * @author Tarcisio
 */
public class Search extends HttpServlet{
    private final OntologySearch os = new OntologySearch();

    public JsonObject introspection(String pergunta){
        JsonObject rv= new JsonObject();
        String answer = "";
        String voice = "";

        Collator compare = Collator.getInstance(Locale.getDefault());
        compare.setStrength(Collator.PRIMARY);
        compare.setDecomposition(Collator.NO_DECOMPOSITION);
        if(compare.compare(pergunta, "Quem é você") == 0 ||
           compare.compare(pergunta, "Quem é você?") == 0){
            answer = "Olá! <br />Eu sou a Assistente do Laboratório de Aprendizado de Robôs, mas você pode me chamar de LARA. <br />Eu sou uma inteligência artificial que visa prover informações sobre o Centro de Robótica de São Carlos, como professores que o compõem, <br />próximos eventos, dentre outros. <br /><br />Se precisar de algo, é só pedir! Você pode saber mais escrevendo ou dizendo \"Ajuda\".";
            voice = answer.replaceAll("<br />", "");
        } else if(compare.compare(pergunta, "Ajuda") == 0){
            answer = "Você pode procurar sobre professores inserindo seu nome e o que deseja saber. Eu conheço a área de pesquisa, o cargo, a sala, o email, <br />o ramal e o Lattes deles. <br /> Quanto aos eventos do Centro de Robótica, eu sei sobre os eventos de hoje, amanhã, da semana, do mês e do ano, tanto na parte da <br />manhã quanto da tarde e da noite.";
        }
        
        if(!answer.isEmpty()){
            rv.addProperty("answer", answer);
            rv.addProperty("voice", voice);
            rv.addProperty("entity", "");
            rv.add("properties", new JsonArray());
            return rv;
        } else {
            return null;
        }
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

        JsonObject rv = this.introspection(pergunta);
        if (rv == null) {
            if(LaraParser.requestType(pergunta) == LaraParser.Type.ONTOLOGY){
                rv = this.os.search(pergunta, entidade, propriedades);
            } else {
                rv = CalendarSearch.getList(pergunta);
            }
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write( g.toJson(rv) );//.getBytes( "UTF-8" ) );
    }

}
