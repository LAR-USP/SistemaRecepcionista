package usp.lar.lara.ontologia;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Híkaro, Raphael e Tarcísio.
 */
public class LaraParser {

    public static ArrayList<String> obterPropriedades(String palavra){
        ArrayList<String> propriedades = new ArrayList();
        ArrayList<String> investiga = new ArrayList(Arrays.asList("investiga", "pesquisa"));
        ArrayList<String> ficaEm = new ArrayList(Arrays.asList("sala", "escritório", "escritorio"));
        ArrayList<String> possuiRamal = new ArrayList(Arrays.asList("ramal", "telefone"));
        ArrayList<String> possuiEmail = new ArrayList(Arrays.asList("email", "e-mail"));
        ArrayList<String> exerce = new ArrayList(Arrays.asList("exerce", "cargo"));
        ArrayList<String> possuiCurriculo = new ArrayList(Arrays.asList("currículo", "curriculo", "lattes"));

        if(investiga.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("investiga");
        }else if(ficaEm.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("ficaEm");
        }else if(possuiRamal.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("possuiRamal");
        }else if(possuiEmail.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("possuiEmail");
        }else if(exerce.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("exerce");
        }else if(palavra.equalsIgnoreCase("contato")){
            propriedades.add("possuiRamal");
            propriedades.add("possuiEmail");
        }else if(possuiCurriculo.stream().anyMatch(palavra::equalsIgnoreCase)){
            propriedades.add("possuiCurrículo");
        }
        return propriedades;
    }
    public static ArrayList<ArrayList<String>> parse(String frase, Ontologia o){
        String[] tokens = frase.split(" ");
        ArrayList<String> individuos = new ArrayList();
        ArrayList<String> propriedades = new ArrayList();
        for(int i = 0; i < tokens.length; ++i){
            ArrayList<String> prop = obterPropriedades(tokens[i]);
            if(prop.size() > 0 && !prop.get(0).equals("")){
                System.out.println(prop.toString());
                propriedades.addAll(prop);
            }else if(o.éEntidade(tokens[i])){
                individuos.addAll(o.executaPropriedade(tokens[i], "éChaveDe"));
                System.out.println(individuos.toString());
            }
        }
        ArrayList<ArrayList<String>> result = new ArrayList();
        result.add(individuos);
        result.add(propriedades);
        return result;
    }
    /**
     * @param input Resposta do Parser.
     * @return Resposta formatada.
     */
    public static String formatResponse( ArrayList<ArrayList<String>> input ) {
        String response = null;

        // 1ª lista: entidades.
        ArrayList<String> entities = input.get( 0 );
        // 2ª lista: propriedades.
        ArrayList<String> properties = input.get( 1 );
        // 3ª lista: respostas.
        ArrayList<String> results = new ArrayList();//input.get( 2 );

        if ( entities.isEmpty() ) {
            if ( properties.isEmpty() ) {
                // Not found.
                response = "Desculpe, não sei sobre este assunto.";
            }
            else if ( properties.size() == 1 ) {
                // Not found.
                response = "Sobre o quê ou quem gostaria de saber o(a) " + properties.get( 0 ) + "?";
            }
            else { // More than one property about something.
                response = "Sobre o quê ou quem gostaria de saber o(a) " + properties.get( 0 );
                for ( int i = 1; i < properties.size() - 1; i++ ) {
                    response = response + ", " + properties.get( i );
                }
                response = response + " e " + properties.get( properties.size() ) + "?";
            }
        }
        else if ( entities.size() == 1 ) {
            if ( properties.isEmpty() ) {
                // Not found.
                response = "O que gostaria de saber sobre " + entities.get(0) + "?";
            }
            else if ( properties.size() == 1 ) {
                //p.ex. "A sala de Roseli é xxx.";
                response = "A " + properties.get( 0 ) + " de " + entities.get( 0 ) + " é " + properties.get( 0 ) + ".";
            }
            else { // More than one property about something.
                //p.ex. "A sala, telefone e curriculo de Roseli é xxx, yyy e zzz.";
                response = "A " + properties.get( 0 );
                for ( int i = 1; i < properties.size() - 1; i++ ) {
                    response = response + ", " + properties.get( i );
                }
                response = response + " e " + properties.get( properties.size() ) + " de " + entities.get( 0 ) + " são: " + properties.get( 0 );
                for ( int i = 1; i < properties.size() - 1; i++ ) {
                    response = response + ", " + properties.get( i );
                }
                response = response + " e " + properties.get( properties.size() ) + ".";
            }
        }
        else { // Ambiguous entities.
            response = "Sobre o quê ou quem gostaria de saber? " + entities.get( 0 );
            for ( int i = 1; i < entities.size() - 1; i++ ) {
                response = response + ", " + entities.get( i );
            }
            response = response + " ou " + entities.get( entities.size() ) + "?";
        }
        return( response );
    }
    
}
