package usp.lar.lara.ontologia;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


/**
 *
 * @author Ewerton Wantroba
 */
public class Ontologia {
    private OntologyWordNetBr ontologiaWordNet;
    private OntologyICMC ontologiaICMC;
    
    public Ontologia() {
        this.ontologiaWordNet = new OntologyWordNetBr();
        this.ontologiaICMC = new OntologyICMC();


        //        try {
        //            ontologia.createOntology(new File("Palavras.owl"));
        //            //ontologia.loadWordNetBrOntology(new File("Palavras.owl"));
        //            criaTodasPalavras(ontologia);
        //            ontologia.verificaConsistencia();
        //            ontologia.showClasses();
        //            //ontologia.ontologyWalker();
        //            ontologia.saveOntology();
        //            System.out.println("Sinionimos");
        //            ontologia.getSinonimos("esquema");
        //
        //        } catch (Exception e) {
        //            Logger.getLogger(Ontologia.class.getName()).log(Level.SEVERE, null, e);
        //        }
        try {
            System.out.println("\n\nICMC\n\n");
            //Icmc.owl
            ontologiaICMC.load(new File(getClass().getResource("/ontologia.owl").getPath()));
            
            ontologiaICMC.verificaConsistencia();
            ontologiaICMC.showClasses();
            
            //System.out.println("\n\nWord Net\n\n");
            //ontologiaWordNet.loadWordNetBrOntology(new File(getClass().getResource("/Palavras.owl").getPath()));
            //ontologiaWordNet.verificaConsistencia();
            //ontologiaWordNet.showClasses();
            
        } catch (OWLOntologyCreationException e) {
            Logger.getLogger(Ontologia.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public ArrayList<String> executaPropriedade(String individuo, String propriedade){
        return ontologiaICMC.executeProperty(individuo, propriedade);
    }
    
    public ArrayList<String> getObjetcProperties(String dominio, String range){
        return ontologiaICMC.getOntologyProperties(dominio, range);
    }
    
    public ArrayList<String> getSinonimos(String palavra){
        return ontologiaWordNet.getSinonimos(palavra);
    }
    public ArrayList<String[]> getSuperClasses(ArrayList<String> palavras){
        return this.ontologiaICMC.getOntologySuperClasses(palavras);
        
    }
    public ArrayList<String[]> getSubClasses(ArrayList<String> palavras){
        return this.ontologiaICMC.getOntologySubClasses(palavras);
        
    }
    
    public ArrayList<String> getOntologyIndividualClasses(ArrayList<String> palavras){
        return this.ontologiaICMC.getOntologyIndividualClasses(palavras);
    }
    
    public String getOntologyIndividualClasses(String palavra){
        return this.ontologiaICMC.getOntologyIndividualClasses(palavra);
    }

    public boolean éEntidade(String entitdade){
        return ontologiaICMC.éEntidade(entitdade);
    }
    
    public boolean éClasse(String palavra){
        return ontologiaICMC.éClasse(palavra);
    }
    
    public String getClasse(String individuo){
        return ontologiaICMC.getClasse(individuo);
    }

    public static String preProcessaString(String str) {

        ArrayList<String> caracteresProibidos = new ArrayList();
        caracteresProibidos.add("");
        caracteresProibidos.add("");
        caracteresProibidos.add("");
        caracteresProibidos.add("");
        caracteresProibidos.add("");
        caracteresProibidos.add("\"");
        caracteresProibidos.add("\'");
        caracteresProibidos.add(":");
        caracteresProibidos.add("\\{");
        caracteresProibidos.add("\\}");
        caracteresProibidos.add(Pattern.quote("\\"));
        for (int i = 0; i < caracteresProibidos.size(); i++) {
            str = str.replaceAll(caracteresProibidos.get(i), "");

        }
        str = str.trim().replaceAll(" ", "_").replaceAll("\n", "_").replaceAll("%", " porcento");
        try {
            return new String(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Ontologia.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
