import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.IOException;

public class GraphDBExample {

    /**
     * How to connect to a graphdb repository, load a file to a repository, add some data through the API and perform a
     * sparql query
     */
    public static void main(String[] args) {

        String repositoryName = "ProjectB";

        HTTPRepository repository = new HTTPRepository("http://localhost:7200/repositories/" + repositoryName);
        RepositoryConnection connection = repository.getConnection();

        // Clear the repository before we start
        connection.clear();

        // load a simple ontology from a file
        connection.begin();
        // Adding the family ontology
        try {
            connection.add(GraphDBExample.class.getResourceAsStream("/rdf_examples/simple_ontology.ttl"), "urn:base",
                    RDFFormat.TURTLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Committing the transaction persists the data
        connection.commit();

        Model model = new TreeModel();
        String ex = "http://www.semanticweb.org/simple_ontology#";
        model.setNamespace("ex", ex);

        IRI george = Values.iri(ex, "george");
        IRI Professor = Values.iri(ex, "Professor");

        model.add(george, RDF.TYPE, Professor);

        // add our data using a transaction
        connection.begin();
        connection.add(model);
        connection.commit();

        // We do a simple SPARQL SELECT-query that retrieves all resources of type `ex:Artist`,
        // and their first names.
        String queryString = "PREFIX ex: <http://www.semanticweb.org/simple_ontology#> \n";
        queryString += "SELECT ?p \n";
        queryString += "WHERE { \n";
        queryString += "    ?p a ex:Person. \n";
        queryString += "}";

        TupleQuery query = connection.prepareTupleQuery(queryString);

        // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
        try (TupleQueryResult result = query.evaluate()) {
            // we just iterate over all solutions in the result...
            for (BindingSet solution : result) {
                // ... and print out the value of the variable binding for ?s and ?n
                System.out.println("?p = " + solution.getValue("p"));
            }
        }

        // Create a model with all triples from the remote repository
        connection.begin();
        RepositoryResult<Statement> statements = connection.getStatements(null, null, null);
        model.clear();
        for (Statement statement : statements) {
            model.add(statement);
        }
        System.out.println(model.size());
        connection.commit();

        connection.close();
        repository.shutDown();
    }
}
