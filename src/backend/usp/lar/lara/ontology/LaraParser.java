package usp.lar.lara.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Classe para traduzir as perguntas do usuário para a ontologia.
 * @author Híkaro, Raphael e Tarcísio.
 */
public class LaraParser {

    private static final ArrayList<List<String>> KEYWORDS;
    private static final ArrayList<String> PROPERTIES;
    private static final ArrayList<String> OUTPUT_PROPERTIES;

    public static enum Type{ ONTOLOGY, CALENDAR };

    // Bloco de inicialização de variáveis estáticas.
    static {
        PROPERTIES = new ArrayList(Arrays.asList("investiga", "ficaEm", "possuiRamal", "possuiEmail", "exerce", "possuiCurrículo", "possuiDescrição", "possuiRobô", "possuiMembro", "possuiLaboratório", "possuiImagem", "fazParteDe", "possuiProjeto", "éInvestigadoPor"));
        KEYWORDS = new ArrayList(Arrays.asList(
            Arrays.asList("pesquisa", "investiga"),
            Arrays.asList("sala", "escritório", "escritorio", "endereço", "endereco", "local"),
            Arrays.asList("ramal", "telefone"),
            Arrays.asList("email", "e-mail"),
            Arrays.asList("cargo", "exerce"),
            Arrays.asList("currículo", "curriculo", "lattes"),
            Arrays.asList("resumo", "descrição", "descricao"),
            Arrays.asList("robô", "robôs", "robo", "robos"),
            Arrays.asList("membro", "membros", "professor", "professores"),
            Arrays.asList("laboratório", "laboratórios", "laboratorio", "laboratorios"),
            Arrays.asList("imagem", "imagens", "foto", "fotos", "figura", "figuras"),
            Arrays.asList("participa", "parte", "usado"),
            Arrays.asList("desenvolve", "faz"),
            Arrays.asList("investigado", "desenvolvido", "pesquisado")
        ));
        OUTPUT_PROPERTIES = new ArrayList(Arrays.asList("pesquisa", "local", "ramal", "email", "cargo", "currículo", "descrição", "robô", "membro", "laboratório", "imagem", "participação", "projeto", "por quem é investigado"));
    }

    public static Type requestType(String pergunta){
        /*Arrays.asList retorna uma lista com os arrays passados*/
        ArrayList<String> CalendarKeywords = new ArrayList(Arrays.asList("dia", "quando", "semana", "hora", "horário", "horario", "mês", "mes", "ano", "evento", "eventos", "manhã", "manha", "tarde", "noite", "hoje", "amanha", "amanhã"));
        /*Analisa a pergunta e troca . , e ; por espaço*/
        pergunta = pergunta.replace("."," ");
        pergunta = pergunta.replace(","," ");
        pergunta = pergunta.replace(";"," ");
        /*Pega a string pergunta, divide ela com os delimitadores "espaço" e cada
        corte configura um elemento no array list tokens.
        */
        ArrayList<String> tokens = new ArrayList(Arrays.asList(pergunta.split(" ")));
        /*Cria um objeto iterator para percorrer o array tokens*/
        Iterator<String> t = tokens.iterator();
        while(t.hasNext()){
            /*Compara se alguma palavra da string recebida tem correspondência com o
            array CalendarKeywords, se sim, retorna o tipo Calendar, declarado por enum
            */
            if(CalendarKeywords.stream().anyMatch(t.next()::equalsIgnoreCase)){
                return Type.CALENDAR;
            }
        }
        /*Se não houver correspondência, retorna tipo Ontology*/
        return Type.ONTOLOGY;
    }

    public static ArrayList<String> obterPropriedades(String palavra) {
        ArrayList<String> propriedades = new ArrayList();
        
        /*Analisa uma palavra e compara com uma propriedade, se der match coloca no
        arraylist propriedades, então retorna este
        */

        for(int i = 0; i < PROPERTIES.size(); ++i){
            if(KEYWORDS.get(i).stream().anyMatch(palavra::equalsIgnoreCase)){
                propriedades.add(PROPERTIES.get(i));
            }
        }

        return propriedades;
    }

    /**
     * @param frase Phrase to be analysed.
     * @param o Ontology to be accessed.
     * @return List of entities and properties.
     */
    public static ArrayList<ArrayList<String>> parseOntology( String frase, Ontologia o ) {
        /*Troca qualquer dos itens citados por espaço*/
        frase = frase.replaceAll("\\.|,|;|:|\\?|!|\"|\\(|\\)|\\*|'", " ");
        /*cada palavra separada por espaço é considerada um token que é alocada em um array*/
        String[] tokens = frase.split(" ");
        /*declara um Arraylist para indivíduos e propriedades, para assim que ir analisando
        todos os tokens, ir achando correspondências e colocando no Arraylist adequado
        */
        ArrayList<String> individuos = new ArrayList();
        ArrayList<String> propriedades = new ArrayList();
        for ( String token : tokens ) {
            ArrayList<String> prop = obterPropriedades( token );
            /*Se prop não estiver vazia, entra no if, ou seja, se achar uma correspondência
            para propriedades.
            */
            if ( prop.size() > 0 && !prop.get(0).equals("") ) {
                propriedades.addAll( prop );
            }
            /*Se não houver correspondência com propriedades, verifica se tem com
            indivíduos.
            */
            else {
                /*Para verificar o indivíduo tem que analisar a classe Ontologia*/
                ArrayList<String> new_indiv = o.executaPropriedade(OntologyICMC.toCamelCase(token), "éChaveDe");
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
        Iterator<String> prop_it = properties.iterator();
        while(prop_it.hasNext()){
            String token = prop_it.next();
            for(int i = 0; i < PROPERTIES.size(); ++i){
                if(PROPERTIES.get(i).equalsIgnoreCase(token)){
                    output_properties.add(OUTPUT_PROPERTIES.get(i));
                    break;
                }
            }
        }
        if ( entities.isEmpty()  || (entities.size() == 1 && entities.get(0).equals("") ) ) {
            if ( output_properties.size() == 1 ) {
                // Not found.
                response = "Sobre o quê ou quem gostaria de saber o(a)(s) " + output_properties.get( 0 ) + "?";
            }
            else if ( !output_properties.isEmpty() ){ // More than one property about something.
                response = "Sobre o quê ou quem gostaria de saber o(a)(s) " + output_properties.get( 0 );
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
