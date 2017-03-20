package usp.lar.lara.ontologia;

import java.util.ArrayList;
import java.util.Arrays;

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

    // Bloco de inicialização de variáveis estáticas.
    static {
        Investiga = new ArrayList(Arrays.asList("investiga", "pesquisa"));
        FicaEm = new ArrayList(Arrays.asList("sala", "escritório", "escritorio"));
        PossuiRamal = new ArrayList(Arrays.asList("ramal", "telefone"));
        PossuiEmail = new ArrayList(Arrays.asList("email", "e-mail"));
        Exerce = new ArrayList(Arrays.asList("exerce", "cargo"));
        PossuiCurriculo = new ArrayList(Arrays.asList("currículo", "curriculo", "lattes"));
    }

    public static ArrayList<String> obterPropriedades(String palavra) {
        ArrayList<String> propriedades = new ArrayList();

        if (investiga.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("investiga");
        } else if (ficaEm.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("ficaEm");
        } else if (possuiRamal.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiRamal");
        } else if (possuiEmail.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiEmail");
        } else if (exerce.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("exerce");
        } else if (palavra.equalsIgnoreCase("contato")) {
            propriedades.add("possuiRamal");
            propriedades.add("possuiEmail");
        } else if (possuiCurriculo.stream().anyMatch(palavra::equalsIgnoreCase)) {
            propriedades.add("possuiCurrículo");
        }
        return propriedades;
    }

    /**
     * @param frase Phrase to be analysed.
     * @param o Ontology to be accessed.
     * @return List of entities and properties.
     */
    public static ArrayList<ArrayList<String>> parse( String frase, Ontologia o ) {
        String[] tokens = frase.split(" ");
        ArrayList<String> individuos = new ArrayList();
        ArrayList<String> propriedades = new ArrayList();
        for ( String token : tokens ) {
            ArrayList<String> prop = obterPropriedades( token );
            if ( prop.size() > 0 && !prop.get(0).equals("") ) {
                System.out.println( prop.toString() );
                propriedades.addAll( prop );
            } else if ( o.éEntidade( token ) ) {
                individuos.addAll( o.executaPropriedade( token, "éChaveDe" ) );
                System.out.println( individuos.toString() );
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
