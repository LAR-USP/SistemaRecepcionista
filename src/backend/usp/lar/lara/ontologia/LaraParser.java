package usp.lar.lara.ontologia;

import java.util.ArrayList;

/**
 *
 * @author Híkaro, Raphael e Tarcísio.
 */
public class LaraParser {

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
        ArrayList<String> results = input.get( 2 );

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
