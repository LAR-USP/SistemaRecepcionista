/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usp.lar.lara.ontologia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.reasoner.*;

/**
 *
 * @author ewerton
 */
public class OntologyICMC {

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private OWLOntologyManager manager;
    private IRI ontologyIRI;
    private File arquivo;
    private OWLOntologyID ontologyID;
    private ReasonerFactory reasonerFactory;
    private OWLReasoner reasoner;
    private Set<OWLObjectProperty> propriedades;

    public OntologyICMC() {
    }

    public void createOntology(File arquivo) throws OWLOntologyCreationException, IOException {
        // We first need to create an OWLOntologyManager, which will provide a
        // point for creating, loading and saving ontologies. We can create a
        // default ontology manager with the OWLManager class. This provides a
        // common setup of an ontology manager. It registers parsers etc. for
        // loading ontologies in a variety of syntaxes
        this.manager = OWLManager.createOWLOntologyManager();
        // In OWL 2, an ontology may be named with an IRI (Internationalised
        // Resource Identifier) We can create an instance of the IRI class as
        // follows:
        this.arquivo = arquivo;
        this.ontologyIRI = IRI.create(arquivo);
        // Here we have decided to call our ontology
        // "http://www.semanticweb.org/ontologies/myontology" If we publish our
        // ontology then we should make the location coincide with the ontology
        // IRI Now we have an IRI we can create an ontology using the manager
        this.ontology = manager.createOntology(ontologyIRI);
        System.out.println("Created ontology: " + ontology);
        // In OWL 2 if an ontology has an ontology IRI it may also have a
        // version IRI The OWL API encapsulates ontology IRI and possible
        // version IRI information in an OWLOntologyID Each ontology knows about
        // its ID
        this.ontologyID = ontology.getOntologyID();
        // In this case our ontology has an IRI but does not have a version IRI
        System.out.println("Ontology IRI: " + ontologyID.getOntologyIRI());
        // Our version IRI will be null to indicate that we don't have a version
        // IRI
        System.out.println("Ontology Version IRI: " + ontologyID.getVersionIRI());
        // An ontology may not have a version IRI - in this case, we count the
        // ontology as an anonymous ontology. Our ontology does have an IRI so
        // it is not anonymous:
        System.out.println("Anonymous Ontology: " + ontologyID.isAnonymous());
        this.factory = manager.getOWLDataFactory();

        this.reasonerFactory = new ReasonerFactory();
        this.reasoner = reasonerFactory.createReasoner(ontology);
        //createClasses();
        getObjectProperties();

    }

    public String[] getSuperClasses(String palavra, boolean diretas) {
        palavra = palavra.replaceAll(" ", "_");
        OWLClass classe = factory.getOWLClass(IRI.create(ontologyIRI + "#" + palavra));



        NodeSet<OWLClass> superClassesOWL = reasoner.getSuperClasses(classe, diretas);
        String[] superClasses = new String[superClassesOWL.getNodes().size()];
        Iterator<Node<OWLClass>> t = superClassesOWL.iterator();
        int i = 0;
        while (t.hasNext()) {
            OWLClass c = t.next().getRepresentativeElement();

            superClasses[i] = (c.getIRI().getFragment().replaceAll("_", " "));
            i++;

        }
        //getSuperClasses(classe,superClasses);
        return superClasses;

    }

    public String[] getSubClasses(String palavra, boolean classesDiretas) {
        palavra = palavra.replaceAll(" ", "_");
        OWLClass classe = factory.getOWLClass(IRI.create(ontologyIRI + "#" + palavra));



        NodeSet<OWLClass> subClassesOWL = reasoner.getSubClasses(classe, classesDiretas);
        String subClasses[] = new String[subClassesOWL.getNodes().size()];
        Iterator<Node<OWLClass>> t = subClassesOWL.iterator();

        int i = 0;
        while (t.hasNext()) {
            OWLClass c = t.next().getRepresentativeElement();

            subClasses[i] = (c.getIRI().getFragment().replaceAll("_", " "));
            i++;

        }


        return subClasses;
    }

    public ArrayList<String> getInstancesOf(String palavra, boolean classesDiretas) {
        palavra = palavra.replaceAll(" ", "_");
        ArrayList<String> instancias = new ArrayList<String>();
        OWLClass classe = factory.getOWLClass(IRI.create(ontologyIRI + "#" + palavra));

        NodeSet<OWLNamedIndividual> individuos = reasoner.getInstances(classe, classesDiretas);
        Iterator<Node<OWLNamedIndividual>> i = individuos.iterator();
        while (i.hasNext()) {
            OWLNamedIndividual individuo = i.next().getRepresentativeElement();
            instancias.add(individuo.getIRI().getFragment().replaceAll("_", " "));
        }
        return instancias;
    }

    public ArrayList<String> executeProperty(String indivíduo, String propriedade) {
        indivíduo = toCamelCase(indivíduo);
        ArrayList<String> instancias = new ArrayList<String>();
        OWLNamedIndividual indivíduoOWL = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + indivíduo));


        OWLObjectProperty propriedadeOWL = factory.getOWLObjectProperty(IRI.create(ontologyIRI
                + "#" + propriedade));

        NodeSet<OWLNamedIndividual> individuos = this.reasoner.getObjectPropertyValues(indivíduoOWL, propriedadeOWL);
        Iterator<Node<OWLNamedIndividual>> i = individuos.iterator();
        while (i.hasNext()) {
            OWLNamedIndividual individuo = i.next().getRepresentativeElement();

            instancias.add(individuo.getIRI().getFragment().replaceAll("_", " "));
        }

        return instancias;
    }

    public ArrayList<String[]> getOntologySuperClasses(ArrayList<String> palavras) {
        ArrayList<String[]> superClasses = new ArrayList<String[]>();
        for (int i = 0; i < palavras.size(); i++) {
            String palavra = toCamelCase(palavras.get(i));


            if (ontology.containsClassInSignature(IRI.create(ontologyIRI + "#" + palavra))) {

                superClasses.add(getSuperClasses(palavra, false));
            } else {
                superClasses.add(null);
            }
        }
        return superClasses;
    }

    public ArrayList<String[]> getOntologySubClasses(ArrayList<String> palavras) {

        ArrayList<String[]> subClasses = new ArrayList<String[]>();
        for (int i = 0; i < palavras.size(); i++) {
            String palavra = toCamelCase(palavras.get(i));


            if (ontology.containsClassInSignature(IRI.create(ontologyIRI + "#" + palavra))) {

                subClasses.add(getSubClasses(palavra, false));
            } else {
                subClasses.add(null);
            }
        }
        return subClasses;
    }

    private void getObjectProperties() {
        propriedades = ontology.getObjectPropertiesInSignature();
        Iterator<OWLObjectProperty> i = propriedades.iterator();
        System.out.println("Propriedades:");
        while (i.hasNext()) {
            OWLObjectProperty propriedade = i.next();
            System.out.println(propriedade.getIRI().getFragment() + ": ");
            System.out.println("Domínios: ");
            Iterator<OWLClassExpression> j = propriedade.getDomains(ontology).iterator();
            while (j.hasNext()) {
                OWLClass dominio = j.next().asOWLClass();
                System.out.println(dominio.getIRI().getFragment());
            }
            System.out.println("");
            System.out.println("Ranges: ");
            Iterator<OWLClassExpression> k = propriedade.getRanges(ontology).iterator();
            while (k.hasNext()) {
                OWLClass range = k.next().asOWLClass();
                System.out.println(range.getIRI().getFragment());
            }
            System.out.println("----------------");
        }
    }

    public ArrayList<String> getOntologyProperties(String dominio, String range) {
        dominio = toCamelCase(dominio);
        range = toCamelCase(range);
        ArrayList<String> properties = new ArrayList<String>();
        Iterator<OWLObjectProperty> i = propriedades.iterator();
        
        
        while (i.hasNext()) {
            OWLObjectProperty propriedade = i.next();
            
            Set<OWLClassExpression> dominios = propriedade.getDomains(ontology);
            Set<OWLClassExpression> ranges = propriedade.getRanges(ontology);
            if(dominios.contains(factory.getOWLClass(IRI.create(ontologyIRI+"#"+dominio))) && ranges.contains(factory.getOWLClass(IRI.create(ontologyIRI+"#"+range)))){
                properties.add(propriedade.getIRI().getFragment());
            }
                    
        }
        return properties;
    }

    public boolean éClasse(String palavra){
        palavra = toCamelCase(palavra);
        return ontology.containsClassInSignature(IRI.create(ontologyIRI + "#" + palavra));
    }
    
    public String getClasse(String individuo){
        individuo = toCamelCase(individuo);
        if (ontology.containsIndividualInSignature(IRI.create(ontologyIRI + "#" + individuo))) {
            OWLIndividual individuoOWL = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + individuo));
                Set<OWLClassAssertionAxiom> classes = ontology.getClassAssertionAxioms(individuoOWL);
                return classes.iterator().next().getClassExpression().asOWLClass().getIRI().getFragment();
        }
        
        return null;
    }
    public ArrayList<String> getOntologyIndividualClasses(ArrayList<String> palavras) {
        ArrayList<String> individualClasses = new ArrayList<String>();
        for (int i = 0; i < palavras.size(); i++) {
            String palavra = toCamelCase(palavras.get(i));
            if (ontology.containsIndividualInSignature(IRI.create(ontologyIRI + "#" + palavra))) {
                OWLIndividual individuo = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra));
                Set<OWLClassAssertionAxiom> classes = ontology.getClassAssertionAxioms(individuo);
                individualClasses.add(classes.iterator().next().getClassExpression().asOWLClass().getIRI().getFragment());

            } else {

                individualClasses.add(null);
            }

        }

        return individualClasses;
    }

    public String getOntologyIndividualClasses(String palavra) {
        palavra = toCamelCase(palavra);
        //if (ontology.containsIndividualInSignature(IRI.create(ontologyIRI + "#" + palavra))) {
                OWLIndividual individuo = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra));
                Set<OWLClassAssertionAxiom> classes = ontology.getClassAssertionAxioms(individuo);
                if(classes.size()>=1)
                    return classes.iterator().next().getClassExpression().asOWLClass().getIRI().getFragment();
                else
                    return "";
//        }
//        else
//            return "";
    }
    private static String toCamelCase(String s) {
        String[] parts = s.split(" ");
        String camelCaseString = "";
        for (String part : parts) {
            if(part.length()>1)
                camelCaseString = camelCaseString + "_" + toProperCase(part);
            else
                camelCaseString = camelCaseString + "_" + part.toUpperCase();
        }
        return camelCaseString.replaceFirst("_", "");
    }

    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase()
                + s.substring(1).toLowerCase();
    }

    public void verificaConsistencia() {
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory. Create a reasoner factory. In this case, we will use HermiT,
        // but we could also use FaCT++ (http://code.google.com/p/factplusplus/)
        // or Pellet(http://clarkparsia.com/pellet) Note that (as of 03 Feb
        // 2010) FaCT++ and Pellet OWL API 3.0.0 compatible libraries are
        // expected to be available in the near future). For now, we'll use
        // HermiT HermiT can be downloaded from http://hermit-reasoner.com Make
        // sure you get the HermiT library and add it to your class path. You
        // can then instantiate the HermiT reasoner factory: Comment out the
        // first line below and uncomment the second line below to instantiate
        // the HermiT reasoner factory. You'll also need to import the
        // org.semanticweb.HermiT.Reasoner package.
        reasonerFactory = new ReasonerFactory();
        // OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        // We'll now create an instance of an OWLReasoner (the implementation
        // being provided by HermiT as we're using the HermiT reasoner factory).
        // The are two categories of reasoner, Buffering and NonBuffering. In
        // our case, we'll create the buffering reasoner, which is the default
        // kind of reasoner. We'll also attach a progress monitor to the
        // reasoner. To do this we set up a configuration that knows about a
        // progress monitor. Create a console progress monitor. This will print
        // the reasoner progress out to the console.
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        // Specify the progress monitor via a configuration. We could also
        // specify other setup parameters in the configuration, and different
        // reasoners may accept their own defined parameters this way.
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        // Create a reasoner that will reason over our ontology and its imports
        // closure. Pass in the configuration.
        reasoner = reasonerFactory.createReasoner(ontology, config);
        // Ask the reasoner to do all the necessary work now
        reasoner.precomputeInferences();
        // We can determine if the ontology is actually consistent (in this
        // case, it should be).

        boolean consistent = reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        System.out.println("\n");
        // We can easily get a list of unsatisfiable classes. (A class is
        // unsatisfiable if it can't possibly have any instances). Note that the
        // getUnsatisfiableClasses method is really just a convenience method
        // for obtaining the classes that are equivalent to owl:Nothing. In our
        // case there should be just one unsatisfiable class - "mad_cow" We ask
        // the reasoner for the unsatisfiable classes, which returns the bottom
        // node in the class hierarchy (an unsatisfiable class is a subclass of
        // every class).
        Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
        // This node contains owl:Nothing and all the classes that are
        // equivalent to owl:Nothing - i.e. the unsatisfiable classes. We just
        // want to print out the unsatisfiable classes excluding owl:Nothing,
        // and we can used a convenience method on the node to get these
        Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
        if (!unsatisfiable.isEmpty()) {
            System.out.println("The following classes are unsatisfiable: ");
            for (OWLClass cls : unsatisfiable) {
                System.out.println("    " + cls);
            }
        } else {
            System.out.println("There are no unsatisfiable classes");
        }
        System.out.println("\n");
    }

    public void load(File arquivo) throws OWLOntologyCreationException {
        this.arquivo = arquivo;

        this.manager = OWLManager.createOWLOntologyManager();

        this.ontology = this.manager.loadOntologyFromOntologyDocument(arquivo);
        this.ontologyID = ontology.getOntologyID();
        this.ontologyIRI = this.ontologyID.getOntologyIRI();
        this.factory = manager.getOWLDataFactory();
        this.reasonerFactory = new ReasonerFactory();
        this.reasoner = reasonerFactory.createReasoner(ontology);
        this.reasoner.precomputeInferences();
        getObjectProperties();
        System.out.println("Loaded ontology: " + ontology);
        // We can always obtain the location where an ontology was loaded from

        System.out.println("from: " + ontologyIRI);
    }

    public void showClasses() {
        // The ontology will now contain references to class A and class B -
        // that is, class A and class B are contained within the SIGNATURE of
        // the ontology let's print them out
        for (OWLClass cls : ontology.getClassesInSignature()) {
            System.out.println("Referenced class: " + cls);
        }

    }
}
