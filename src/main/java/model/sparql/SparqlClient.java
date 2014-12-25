package model.sparql;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Class to establish the connection to the fuseki server.
 * To better understand the format of the responses, you
 * can check the documentation here:
 * http://www.w3.org/TR/2013/REC-rdf-sparql-XMLres-20130321/
 */
public class SparqlClient {

    private static final Logger LOGGER = LogManager.getLogger();

    // Address of the server
    private String endpointUri;

    // Default query to know if server is up
    private final String ASK_QUERY = "ASK WHERE { ?s ?p ?o }";

    public SparqlClient(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public boolean isServerUp(){
        return ask(ASK_QUERY);
    }

    /*
     * Run a SPARQL select on the server
     */
    public SparqlResult select(String queryString) {

        LOGGER.entry(queryString);

        Document document = getXMLFromServer(queryString);
        NodeList resultNodes = document.getElementsByTagName("result");
        return LOGGER.exit(getResults(resultNodes));
    }


    /*
     * Store the information of the XML result nodes in a SPARQL
     * result structure.
     */
    private SparqlResult getResults(NodeList resultNodes) {

        SparqlResult results = new SparqlResult();

        for (int i = 0; i < resultNodes.getLength(); ++i) {

            Node resultNode = resultNodes.item(i);
            getBindings(resultNode, results);
        }

        return results;
    }

    /*
     * Take a Result node and bind the values.
     */
    private void getBindings(Node resultNode, SparqlResult results) {

        results.addRow();
        NodeList bindingNodes = resultNode.getChildNodes();

        for (int i = 0; i < bindingNodes.getLength(); ++i) {

            Node bindingNode = bindingNodes.item(i);
            bindValue(results, bindingNode);
        }
    }

    /*
     * Bind a single value.
     */
    private void bindValue(SparqlResult results, Node bindingNode) {

        // To avoid text nodes
        if (notTextNode(bindingNode)) {

            NamedNodeMap attrMap = bindingNode.getAttributes();
            Node attrName = attrMap.getNamedItem("name");
            String varName = attrName.getTextContent();
            String value = getValue(bindingNode);
            results.addResult(varName, value);
        }
    }

    /*
     * Get the value from the XML node.
     */
    private String getValue(Node bindingNode) {

        NodeList bindingChildren = bindingNode.getChildNodes();

        for (int i = 0; i < bindingChildren.getLength(); ++i) {

            Node bindingChild = bindingChildren.item(i);

            // To avoid text nodes
            if (notTextNode(bindingChild)) {
                return bindingChild.getTextContent();
            }
        }

        return "";
    }

    /*
     * To check if a node is not a Text Node.
     * The parser takes into account the spaces outside the tags,
     * we can avoid them using this.
     */
    private boolean notTextNode(Node bindingNode) {
        return bindingNode.getNodeType() == Node.ELEMENT_NODE;
    }

    /*
     * Run a SPARQL ask on the remote server
     */
    public boolean ask(String queryString) {

        LOGGER.entry(queryString);

        Document document = getXMLFromServer(queryString);
        NodeList list = document.getElementsByTagName("boolean");
        Node xmlNode = list.item(0);

        return xmlNode != null && xmlNode.getTextContent().equals("true");
    }

    /*
     * Connects to the server using the query and stores the reply in an XML
     * Document.
     */
    private Document getXMLFromServer(String queryString) {

        try {

            URI uri = buildHTTPRequest(queryString);
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return parser.parse(uri.toString());

        } catch (SAXException ex) {
            LOGGER.fatal("A problem was found parsing the file from server.", ex);
        } catch (IOException ex) {
            LOGGER.fatal("There were problems to connect to the server", ex);
        } catch (ParserConfigurationException ex) {
            LOGGER.fatal("XML/HTML Parser could not be created.", ex);
        } catch (URISyntaxException ex) {
            LOGGER.fatal("Found a malformed URI when trying to connect to server.", ex);
        }

        return newEmptyDocument();
    }

    /*
     * Builds the URI to send queries to the server.
     */
    private URI buildHTTPRequest(String queryString) throws URISyntaxException {

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(this.endpointUri);
        builder.setPath("/sparql");
        builder.setParameter("query", queryString);
        builder.setParameter("output", "xml");
        URI uri = builder.build();
        return uri;
    }

    /*
     * Run a SPARQL update on the remote server.
     */
    public void update(String queryString) {
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost request = buildPostRequest(queryString);
            HttpResponse response = httpClient.execute(request);

            try {
                HttpEntity entity = response.getEntity();
                // Closes the stream
                EntityUtils.consume(entity);
            } finally {
                request.releaseConnection();
            }
        } catch (IOException ex) {
            LOGGER.error("There were problems to connect to the server", ex);
        }
    }

    /*
     * Builds post request to send updates to the server.
     */
    private HttpPost buildPostRequest(String queryString)
            throws UnsupportedEncodingException {

        HttpPost httpPost = new HttpPost("http://" + endpointUri + "/update");

        //Creates update attribute
        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        pairList.add(new BasicNameValuePair("update", queryString));

        // Adds update pair to the post request
        httpPost.setEntity(new UrlEncodedFormEntity(pairList));
        return httpPost;
    }

    private static Document newEmptyDocument() {

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document doc;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("There were problems creating an empty document", e);
        }

        doc = builder.newDocument();

        return doc;
      }
}
