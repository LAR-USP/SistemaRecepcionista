/*
 * Realiza uma busca na ontologia.
 */
package usp.lar.lara.ontology;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author tarcisio
 */
public class OntologySearch {

    private final Ontologia o = new Ontologia();
    
    public JsonObject search(String pergunta, String entidade, String[] propriedades){
        JsonArray novas_props = new JsonArray();
        
        /* Parsed é uma lista de uma lista de strings, que  guarda algo chamando parseOntology*/
        ArrayList<ArrayList<String>> parsed = LaraParser.parseOntology( pergunta, this.o );
        if(parsed.get(0).isEmpty()){
            if(entidade != null && entidade.isEmpty()){
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

        String answer = "";
        if ( !pergunta.isEmpty() ) {
            answer = LaraParser.formatResponse( parsed, this.o );
        }
        
        /*Aproveita o parsed para poder pegar as informações referentes a sala, telefone e 
        email vinculado a entidade.
        */
        ArrayList<String> entities = parsed.get( 0 );
        ArrayList<String> sala = o.executaPropriedade(entities.get(0), "ficaEm");
        ArrayList<String> telefone = o.executaPropriedade(entities.get(0), "possuiRamal");
        ArrayList<String> endereco_email = o.executaPropriedade(entities.get(0), "possuiEmail");
        
        String room = "Sala: " + sala.get(0);
        String telephone = "Telephone: " + telefone.get(0);
        String email = "Email: " + endereco_email.get(0);
        
        /*Associa toda informação resgatada a um JsonObject rv*/
        JsonObject rv = new JsonObject();
        rv.addProperty("answer", answer);
        String voice = answer.replaceAll("\\<br\\>|\\<ul\\>|\\</ul\\>|\\<li\\>", "").replaceAll("\\</li\\>", ", ");
        rv.addProperty("voice", voice);
        rv.addProperty("entity", entidade);
        rv.addProperty("room", room);
        rv.addProperty("telephone", telephone);
        rv.addProperty("email", email);
        rv.add("properties", novas_props);

        return rv;
    }
}
