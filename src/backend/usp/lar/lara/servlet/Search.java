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
    
    /*Json Object é um objeto que guarda dados em formato Json, portanto, retorna
    um Json Object*/

    public JsonObject introspection(String pergunta){
        JsonObject rv= new JsonObject();
        String answer = "";
        String voice = "";
        
        /*Essa parte do código pega entrada do usuário e verifica se é uma pergunta
        básica, se for, retorna JsonObject rv com os valores dos campos answer,
        voice, entity e properties*/
        /* Collator é uma classe que manipula strings, determina diferença entre elas,
        verifica diferença, entre outras funções. Compara strings em âmbito local
         e case-sensitive*/
        /*Locale é um objeto que representa uma região cultural, política ou geográfica.
        Nesse caso, precisamos saber qual convenção de palavras, por região. getInstance
        obtêm um objeto Collator específico para certo local*/
        Collator compare = Collator.getInstance(Locale.getDefault());
        /*seta um fator de comparação, base para tal. Primary depende do local setado
        por getInstance, mas geralmente difere letras completamente diferentes, não
        senso case-sensitive*/
        compare.setStrength(Collator.PRIMARY);
        /*setDecomposition determina como caracteres compostos são manuseados. Com
        NO_DECOMPOSITION, os caracteres acentuados não são decompostos, principal efeito.
        */
        compare.setDecomposition(Collator.NO_DECOMPOSITION);
        if(compare.compare(pergunta, "Quem é você") == 0 ||
           compare.compare(pergunta, "Quem é você?") == 0){
            answer = "Olá! <br />Eu sou a Assistente do Laboratório de Aprendizado de Robôs, mas você pode me chamar de LARA. <br />Eu sou uma inteligência artificial que visa prover informações sobre o Centro de Robótica de São Carlos, como professores que o compõem, <br />próximos eventos, dentre outros. <br /><br />Se precisar de algo, é só pedir! Você pode saber mais escrevendo ou dizendo \"Ajuda\".";
            voice = answer.replaceAll("<br />", "");
        } else if(compare.compare(pergunta, "Ajuda") == 0){
            answer = "Você pode procurar sobre professores inserindo seu nome e o que deseja saber. Eu conheço a área de pesquisa, o cargo, a sala, o email, <br />o ramal e o Lattes deles. <br /> Quanto aos eventos do Centro de Robótica, eu sei sobre os eventos de hoje, amanhã, da semana, do mês e do ano, tanto na parte da <br />manhã quanto da tarde e da noite.";
        }
        
        if(!answer.isEmpty()){
            /*Vai adicionando propriedades ao objeto json, dependedo do que foi coletado*/
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
     * @brief Recebe um envio GET do AJAX em search.js, recebe envio GET mas apenas
     * é um intermédio, pois logo que recebe, envia por POST.
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
        
        /*Cria um objeto Json seguindo os padões da biblioteca Gson, da Google,
        que facilita a leitura e conversão de dados Json para classes Java, e vice-versa*/
        Gson g = new Gson();

        String[] propriedades = null;
        /*getParameter é utilizado para recuperar valores passados como parâmetro*/
        String pergunta = request.getParameter("content");

        String entidade = request.getParameter("entity");
        String props_temp = request.getParameter("properties");
        if(props_temp != null && props_temp.length() > 2){
            /*Pega a substring de props_temp do segundo caracter até o tamanho total
            da string menos 2. Depois corta a string com o fator de separação \
            */
            propriedades = props_temp.substring(2, props_temp.length()-2).split("\", \"");
        }

        JsonObject rv = null;
        /*chama novamente o método introspection dentro dessa classe, e guarda o
        Json Object em rv*/
        rv = this.introspection(pergunta);
        /*basicamente vemos se já temos alguma resposta, como as já tabeladas em
        instrospection. Caso não temos, entra na parte do código, e tenta pegar uma resposta
        retornando o Json Object rv.
        */
        if(rv == null){
            /*chama métodos em LaraParser, para comparação. Basicamente, analisa a pergunta
            e vê se esta tem alguma correlaçãoc com algum dado que pode ser recuperado
            via calendário. Se não, entra no if*/

            if(LaraParser.requestType(pergunta) == LaraParser.Type.ONTOLOGY){
                /*chama método em OntologySearch*/
                rv = this.os.search(pergunta, entidade, propriedades);
            } else {
                rv = CalendarSearch.getList(pergunta);
            }
        }
        
        /*Coloca conteúdo na response e padroniza*/
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write( g.toJson(rv) );//.getBytes( "UTF-8" ) );
    }

}
