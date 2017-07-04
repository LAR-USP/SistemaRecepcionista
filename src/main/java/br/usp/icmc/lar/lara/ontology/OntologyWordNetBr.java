package br.usp.icmc.lar.lara.ontology;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;

/**
 *
 * @author Ewerton Wantroba
 */
public class OntologyWordNetBr {

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private OWLOntologyManager manager;
    private IRI ontologyIRI;
    private File arquivo;
    private OWLOntologyID ontologyID;
    private ReasonerFactory reasonerFactory;
    private OWLReasoner reasoner;
    /**
     * Cria uma nova Ontologia.
     */
    public OntologyWordNetBr() {
        
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
        factory = manager.getOWLDataFactory();
        reasonerFactory = new ReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
        createClasses();
        
        
    }
    
    private void createClasses(){
        
        //Criam-se todas as classes de Palavras
        ArrayList<OWLClass> classes = new ArrayList();
        
        OWLClass classPalavra = factory.getOWLClass(IRI.create(ontologyIRI + "#Palavra")); classes.add(classPalavra);
        OWLClass classSubstantivo = factory.getOWLClass(IRI.create(ontologyIRI + "#Substantivo")); classes.add(classSubstantivo);
        OWLClass classOutraCategoria = factory.getOWLClass(IRI.create(ontologyIRI + "#OutraCategoria")); classes.add(classOutraCategoria);
        OWLClass classVerbo = factory.getOWLClass(IRI.create(ontologyIRI + "#Verbo")); classes.add(classVerbo);
        OWLClass classAdvérbio = factory.getOWLClass(IRI.create(ontologyIRI + "#Advérbio")); classes.add(classAdvérbio);
        OWLClass classAdjetivo = factory.getOWLClass(IRI.create(ontologyIRI + "#Adjetivo")); classes.add(classAdjetivo);
        
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(classSubstantivo,classPalavra);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        
        ax = factory.getOWLSubClassOfAxiom(classOutraCategoria,classPalavra);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        
        ax = factory.getOWLSubClassOfAxiom(classVerbo,classPalavra);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        
        ax = factory.getOWLSubClassOfAxiom(classAdvérbio,classPalavra);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        
        ax = factory.getOWLSubClassOfAxiom(classAdjetivo,classPalavra);
        // Add our axiom to the ontology
        manager.applyChange(new AddAxiom(ontology, ax));
        
        //Faz com que as classes sejam disjuntas
//        OWLDisjointClassesAxiom disjointClassesAxiom = factory.getOWLDisjointClassesAxiom(classAdjetivo,classAdvérbio,classOutraCategoria,classSubstantivo,classVerbo);
//        manager.addAxiom(ontology, disjointClassesAxiom);
        createProperties(classes);
    }
    
    private void createProperties(ArrayList<OWLClass> classes){
        OWLObjectProperty sinonimo_de = factory.getOWLObjectProperty(IRI.create(ontologyIRI
                + "#sinonimo_de"));
        OWLObjectProperty antonimo_de = factory.getOWLObjectProperty(IRI.create(ontologyIRI
                + "#antonimo_de"));
        manager.addAxiom(ontology,factory.getOWLInverseObjectPropertiesAxiom(sinonimo_de, sinonimo_de));
        //manager.addAxiom(ontology,factory.getOWLInverseObjectPropertiesAxiom(antonimo_de, antonimo_de));
        
        //sinonimos e antonimos são propriedades transitivas
        manager.addAxiom(ontology, factory.getOWLTransitiveObjectPropertyAxiom(sinonimo_de));
        manager.addAxiom(ontology, factory.getOWLTransitiveObjectPropertyAxiom(antonimo_de));
        
        // cria os domínios e escopos das propriedades
        Set<OWLAxiom> domainsAndRanges = new HashSet();
        
        //for (int i = 0; i < classes.size(); i++) {
            domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(sinonimo_de, classes.get(0)));
            domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(sinonimo_de, classes.get(0)));
            
//            domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(antonimo_de, classes.get(i)));
//            domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(antonimo_de, classes.get(i)));
       // }
        //manager.addAxiom(ontology, factory.getOWLEquivalentObjectPropertiesAxiom(sinonimo_de,antonimo_de));
        manager.addAxioms(ontology, domainsAndRanges);
        
    }
    
    public void saveOntology() throws OWLOntologyCreationException, OWLOntologyStorageException{
        
        
        manager.saveOntology(ontology);
        System.out.println("Saved ontology: "+ontology);
    }

    
    public void loadWordNetBrOntology(File arquivo) throws OWLOntologyCreationException{
        this.arquivo = arquivo;
        
        this.manager = OWLManager.createOWLOntologyManager();
        
        this.ontology = this.manager.loadOntologyFromOntologyDocument(arquivo);    
        this.ontologyID = ontology.getOntologyID();
        this.ontologyIRI = this.ontologyID.getOntologyIRI();
        this.factory = manager.getOWLDataFactory();
        reasonerFactory = new ReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
        System.out.println("Loaded ontology: " + ontology);
        // We can always obtain the location where an ontology was loaded from
        
        System.out.println("from: "+ontologyIRI);
    }
    public void addAxiomSubclass(String classeA, String classeB){
    // Now we want to specify that A is a subclass of B. To do this, we add
        // a subclass axiom. A subclass axiom is simply an object that specifies
        // that one class is a subclass of another class. We need a data factory
        // to create various object from. Each manager has a reference to a data
        // factory that we can use.
        
        
        // Get hold of references to class A and class B. Note that the ontology
        // does not contain class A or classB, we simply get references to
        // objects from a data factory that represent class A and class B
        OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + classeA));
        OWLClass clsB = factory.getOWLClass(IRI.create(ontologyIRI + classeB));
        // Now create the axiom
        OWLAxiom axiom = factory.getOWLSubClassOfAxiom(clsA, clsB);
        
        // We now add the axiom to the ontology, so that the ontology states
        // that A is a subclass of B. To do this we create an AddAxiom change
        // object. At this stage neither classes A or B, or the axiom are
        // contained in the ontology. We have to add the axiom to the ontology.
        AddAxiom addAxiom = new AddAxiom(ontology, axiom);
        // We now use the manager to apply the change
        manager.applyChange(addAxiom);
        // We should also find that B is an ASSERTED superclass of A
        Set<OWLClassExpression> superClasses = clsA.getSuperClasses(ontology);
        System.out.println("Asserted superclasses of " + clsA + ":");
        superClasses.forEach((desc) -> {
            System.out.println(desc);
        });
        
        
    }
    public void showClasses(){
        // The ontology will now contain references to class A and class B -
        // that is, class A and class B are contained within the SIGNATURE of
        // the ontology let's print them out
        ontology.getClassesInSignature().forEach((cls) -> {
            System.out.println("Referenced class: " + cls);
        });
        
    }
    
    public void createPalavra(String palavra,String categoria) throws OWLOntologyStorageException{
        
        
        OWLClass classe = factory.getOWLClass(IRI.create(ontologyIRI + "#" + categoria));
        
        OWLIndividual individuo = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra));
        OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(classe, individuo);
        
        manager.addAxiom(ontology, axiom);
        //manager.saveOntology(ontology);
    }
    
    public void createSynonymous(String palavra1, String palavra2) throws OWLOntologyStorageException{
        
        OWLIndividual individuo1 = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra1));
        OWLIndividual individuo2 = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra2));
        OWLObjectProperty sinonimo_de = factory.getOWLObjectProperty(IRI.create(ontologyIRI
                + "#sinonimoDe"));
        // Now we need to create the assertion that John hasWife Mary. To do
        // this we need an axiom, in this case an object property assertion
        // axiom. This can be thought of as a "triple" that has a subject, john,
        // a predicate, hasWife and an object Mary
        OWLObjectPropertyAssertionAxiom axiom1 = factory
                .getOWLObjectPropertyAssertionAxiom(sinonimo_de, individuo1, individuo2);
        
        AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
        // Now we apply the change using the manager.
        manager.applyChange(addAxiom1);
        
    }
    
    public void createAntonym(String palavra1, String palavra2) throws OWLOntologyStorageException{
        
        OWLIndividual individuo1 = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra1));
        OWLIndividual individuo2 = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + palavra2));
        OWLObjectProperty sinonimo_de = factory.getOWLObjectProperty(IRI.create(ontologyIRI
                + "#antonimo_de"));
        // Now we need to create the assertion that John hasWife Mary. To do
        // this we need an axiom, in this case an object property assertion
        // axiom. This can be thought of as a "triple" that has a subject, john,
        // a predicate, hasWife and an object Mary
        OWLObjectPropertyAssertionAxiom axiom1 = factory
                .getOWLObjectPropertyAssertionAxiom(sinonimo_de, individuo1, individuo2);
        
        AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
        // Now we apply the change using the manager.
        manager.applyChange(addAxiom1);
        
    }
    
    
    public void ontologyWalker(){
        System.out.println("Walker: ");
        // Create the walker. Pass in the pizza ontology - we need to put it
        // into a set though, so we just create a singleton set in this case.
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(ontology));
        // Now ask our walker to walk over the ontology. We specify a visitor
        // who gets visited by the various objects as the walker encounters
        // them. We need to create out visitor. This can be any ordinary
        // visitor, but we will extend the OWLOntologyWalkerVisitor because it
        // provides a convenience method to get the current axiom being visited
        // as we go. Create an instance and override the
        // visit(OWLObjectSomeValuesFrom) method, because we are interested in
        // some values from restrictions.
        
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(
                walker) {
            @Override
            public Object visit(OWLObjectSomeValuesFrom desc) {
                // Print out the restriction
                System.out.println(desc);
                // Print out the axiom where the restriction is used
                System.out.println("         " + getCurrentAxiom());
                System.out.println();
                // We don't need to return anything here.
                return null;
            }
            
        };
        // Now ask the walker to walk over the ontology structure using our
        // visitor instance.
        walker.walkStructure(visitor);
    }
    
    public ArrayList<String> getSinonimos(String palavra){
        
        ArrayList<String> sinonimos = new ArrayList();
      
        OWLNamedIndividual indivíduoOWL = factory.getOWLNamedIndividual(IRI.create(ontologyIRI+ "#"+ palavra));
        OWLObjectProperty propriedadeOWL = factory.getOWLObjectProperty(IRI.create(ontologyIRI+ "#sinonimoDe"));
        NodeSet<OWLNamedIndividual> sinonimosIndividuos = reasoner.getObjectPropertyValues(indivíduoOWL, propriedadeOWL);
        Iterator<Node<OWLNamedIndividual>> i = sinonimosIndividuos.iterator();
        while(i.hasNext()){
            OWLNamedIndividual individuo = i.next().getRepresentativeElement();
            
                sinonimos.add(individuo.getIRI().getFragment().replaceAll("_", " "));
        }
        
        return sinonimos;
    }
    
 
    
    public void verificaConsistencia(){
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
            unsatisfiable.forEach((cls) -> {
                System.out.println("    " + cls);
            });
        } else {
            System.out.println("There are no unsatisfiable classes");
        }
        System.out.println("\n");
    }
}
