package usp.lar.lara.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Classe para traduzir as perguntas do usuário para a ontologia.
 * @author Híkaro, Raphael e Tarcísio.
 */
public class LaraParser {

    private static final ArrayList<String> Investiga;
    private static final ArrayList<String> FicaEm;
    private static final ArrayList<String> PossuiRamal;
    private static final ArrayList<String> PossuiEmail;
    private static final ArrayList<String> Exerce;
    private static final ArrayList<String> PossuiCurriculo;

    public static enum Type{ ONTOLOGY, CALENDAR };

    // Bloco de inicialização de variáveis estáticas.
    static {
        Investiga = new ArrayList(Arrays.asList("investiga", "pesquisa"));
        FicaEm = new ArrayList(Arrays.asList("sala", "escritório", "escritorio"));
        PossuiRamal = new ArrayList(Arrays.asList("ramal", "telefone"));
        PossuiEmail = new ArrayList(Arrays.asList("email", "e-mail"));
        Exerce = new ArrayList(Arrays.asList("exerce", "cargo"));
        PossuiCurriculo = new ArrayList(Arrays.asList("currículo", "curriculo", "lattes"));
    }

    public static Type requestType(String pergunta){
        ArrayList<String> CalendarKeywords = new ArrayList(Arrays.asList("dia", "quando", "semana", "hora", "horário", "horario", "mês", "mes", "ano", "evento", "eventos", "manhã", "manha", "tarde", "noite", "hoje", "amanha", "amanhã"));
        pergunta = pergunta.replace("."," ");
        pergunta = pergunta.replace(","," ");
        pergunta = pergunta.replace(";"," ");
        ArrayList<String> tokens = new ArrayList(Arrays.asList(pergunta.split(" ")));
        Iterator<String> t = tokens.iterator();
        while(t.hasNext()){
            if(CalendarKeywords.stream().anyMatch(t.next()::equalsIgnoreCase)){
                return Type.CALENDAR;
            }
        }
        return Type.ONTOLOGY;
    }

    public static ArrayList<String> obterPropriedades(String palavra) {
        ArrayList<String> propriedades = new ArrayList();

        if (Investiga.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("investiga");
        } else if ( FicaEm.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("ficaEm");
        } else if (PossuiRamal.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiRamal");
        } else if (PossuiEmail.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiEmail");
        } else if (Exerce.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("exerce");
        } else if (palavra.equalsIgnoreCase("contato")) {
            propriedades.add("possuiRamal");
            propriedades.add("possuiEmail");
        } else if (PossuiCurriculo.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiCurrículo");
        }
        return propriedades;
    }

    /**
     * @param frase Phrase to be analysed.
     * @param o Ontology to be accessed.
     * @return List of entities and properties.
     */
    public static ArrayList<ArrayList<String>> parseOntology( String frase, Ontologia o ) {
        frase = frase.replaceAll("\\.|,|;|:|\\?|!|\"|\\(|\\)|\\*|'", " ");
        String[] tokens = frase.split(" ");
        ArrayList<String> individuos = new ArrayList();
        ArrayList<String> propriedades = new ArrayList();
        for ( String token : tokens ) {
            ArrayList<String> prop = obterPropriedades( token );
            if ( prop.size() > 0 && !prop.get(0).equals("") ) {
                propriedades.addAll( prop );
            } else {
                ArrayList<String> new_indiv = o.executaPropriedade(token, "éChaveDe");
                if(!new_indiv.isEmpty()){
                    if ( individuos.isEmpty() ) {
                        individuos.addAll( new_indiv );
                    } else {
                        individuos.retainAll( new_indiv );
                    }
                }
            }
        }
        ArrayList<ArrayList<String>> result = new ArrayList();
        result.add( individuos );
        result.add( propriedades );
        return( result );
    }

    /**
     * @param input Resposta do Parser. Contém duas listas
     * A 1ª lista contém as entidades e a 2ª lista contém as propriedades.
     * @param o Ontologia utilizada.
     * @return Resposta formatada.
     */
    public static String formatResponse( ArrayList<ArrayList<String>> input, Ontologia o ) {
        String response = "";

        // 1ª lista: entidades.
        ArrayList<String> entities = input.get( 0 );
        // 2ª lista: propriedades.
        ArrayList<String> properties = input.get( 1 );

        ArrayList<String> output_properties = new ArrayList();
        for(int i = 0; i < properties.size(); ++i){
            if("investiga".equals(properties.get(i))){
                output_properties.add("pesquisa");
            } else if("ficaEm".equals(properties.get(i))){
                output_properties.add("sala");
            } else if("possuiRamal".equals(properties.get(i))){
                output_properties.add("ramal");
            } else if("possuiEmail".equals(properties.get(i))){
                output_properties.add("email");
            } else if("possuiCurrículo".equals(properties.get(i))){
                output_properties.add("currículo");
            } else if("exerce".equals(properties.get(i))){
                output_properties.add("cargo");
            }
        }
        if ( entities.isEmpty()  || (entities.size() == 1 && entities.get(0) == "")) {
            if ( output_properties.size() == 1 ) {
                // Not found.
                response = "Sobre o quê ou quem gostaria de saber o(a) " + output_properties.get( 0 ) + "?";
            }
            else if ( !output_properties.isEmpty() ){ // More than one property about something.
                response = "Sobre o quê ou quem gostaria de saber o(a) " + output_properties.get( 0 );
                for ( int i = 1; i < output_properties.size() - 1; i++ ) {
                    response = response + ", " + output_properties.get( i );
                }
                response = response + " e " + output_properties.get( output_properties.size() - 1 ) + "?";
            }
        }
        else if ( entities.size() == 1 ) {
            if ( output_properties.isEmpty() ) {
                // Not found.
                response = "O que gostaria de saber sobre " + entities.get(0) + "?";
            }
            else if ( output_properties.size() == 1 ) {
                //p.ex. "A sala de Roseli é xxx.";
                ArrayList<String> results = o.executaPropriedade(entities.get(0), properties.get(0));
                if ( results.isEmpty() ) {
                    response = entities.get(0) + " não tem " + output_properties.get(0) + ".";
                }
                else if( results.size() == 1 ) {
                    response = capitalize(output_properties.get( 0 )) + " de " + entities.get( 0 ) + " é " + results.get( 0 ) + ".";   
                } else {
                    response = capitalize(output_properties.get(0)) + " de " + entities.get(0) + " são ";
                    response += results.get(0);
                    for(int i = 1; i < results.size()-1; ++i){
                       response += ", " + results.get(i);
                    }
                    response += " e " + results.get(results.size() - 1);
                }
            }
            else { // More than one property about something.
                //p.ex. "A sala, telefone e curriculo de Roseli é xxx, yyy e zzz.";
                ArrayList<String> results = new ArrayList();
                for(Iterator<String> i = properties.iterator(); i.hasNext();){
                    results.addAll(o.executaPropriedade(entities.get(0), i.next()));
                }

                response = capitalize(output_properties.get( 0 ));
                for ( int i = 1; i < output_properties.size() - 1; i++ ) {
                    response += ", " + output_properties.get( i );
                }
                response += " e " + output_properties.get( output_properties.size() - 1) + " de " + entities.get( 0 ) + " são: " + results.get( 0 );
                for ( int i = 1; i < results.size() - 1; i++ ) {
                    response += ", " + results.get( i );
                }
                response += " e " + results.get( results.size() - 1) + ".";
            }
        }
        else { // Ambiguous entities.
            response = "Sobre o quê ou quem gostaria de saber?<br><ul>";
            for ( int i = 0; i < entities.size(); i++ ) {
                response += "<li>" + entities.get( i ) + "</li>";
            }
            response += "</ul>";
        }
        return( response );
    }

    public static String capitalize( String original ) {
        if ( original == null || original.length() == 0 ) {
            return original;
        }
        return original.substring( 0, 1 ).toUpperCase() + original.substring( 1 );
    }    

}
