import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.StringWriter;

public class RestrictionExample {

    public static void main(String[] args) {
        // Create a new, empty Model object.
        Model model = new TreeModel();

        // We want to reuse this namespace when creating several building blocks.
        String ex = "http://example.org/";

        // We can add also the namespace to the model
        model.setNamespace("ex", ex);
        model.setNamespace("rdfs", RDFS.NAMESPACE);
        model.setNamespace("owl", OWL.NAMESPACE);

        // Create IRIs for the resources we want to add.
        IRI Entity = Values.iri(ex, "Entity");
        IRI Person = Values.iri(ex, "Person");
        IRI friendOf = Values.iri(ex, "friendOf");

        // add our simple schema: two classes and one object property
        model.add(Entity, RDF.TYPE, OWL.CLASS);
        model.add(Person, RDF.TYPE, OWL.CLASS);
        model.add(friendOf, RDF.TYPE, OWL.OBJECTPROPERTY);

        // Create a blank node for the restriction
        BNode restriction = Values.bnode();

        // Add triples to represent the someValuesFrom restriction
        // All people have some friend who is Person

        // Person subclass of the restriction
        model.add(Person, RDFS.SUBCLASSOF, restriction);
        // Define it as an OWL restriction
        model.add(restriction, RDF.TYPE, OWL.RESTRICTION);
        // Specify the property
        model.add(restriction, OWL.ONPROPERTY, friendOf);
        // Specify the target class
        model.add(restriction, OWL.SOMEVALUESFROM, Person);

        StringWriter writer = new StringWriter();
        Rio.write(model, writer, RDFFormat.TURTLE);
        System.out.println(writer.toString());

        // this code can be used to print blank nodes inline
//        StringWriter writer2 = new StringWriter();
//        RDFWriter rdfWriter2 = Rio.createWriter(RDFFormat.TURTLE, writer2);
//        rdfWriter2.getWriterConfig().set(BasicWriterSettings.INLINE_BLANK_NODES, true);
//        Rio.write(model, rdfWriter2);
//        System.out.println(writer2.toString());


    }
}
