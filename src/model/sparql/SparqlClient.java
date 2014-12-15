package model.sparql;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class SparqlClient {

    private static final Logger LOGGER = Logger.getLogger(SparqlClient.class.getName());
    private String endpointUri;
    private final String ASK_QUERY = "ASK WHERE { ?s ?p ?o }";

    public SparqlClient(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public boolean isServerUp(){
        return ask(ASK_QUERY);
    }

    /**
     * run a SPARQL query (select) on the remote server
     * @param queryString
     */
    public SparqlResult select(String queryString) {

        Document document = getXMLFromServer(queryString);
        NodeList resultNodes = document.getElementsByTagName("result");
        return getResults(resultNodes);
    }


    private SparqlResult getResults(NodeList resultNodes) {

        SparqlResult results = new SparqlResult();

        for (int i = 0; i < resultNodes.getLength(); ++i) {

            Node resultNode = resultNodes.item(i);
            getBindings(resultNode, results);
        }

        return results;
    }

    private void getBindings(Node resultNode, SparqlResult results) {

        results.addRow();
        NodeList bindingNodes = resultNode.getChildNodes();

        for (int i = 0; i < bindingNodes.getLength(); ++i) {

            Node bindingNode = bindingNodes.item(i);
            bindValue(results, bindingNode);
        }
    }

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

    private boolean notTextNode(Node bindingNode) {
        return bindingNode.getNodeType() == Node.ELEMENT_NODE;
    }

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

    /**
     * run a SPARQL query (ask) on the remote server
     * @param queryString
     */
    public boolean ask(String queryString) {

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
            LOGGER.log(Level.SEVERE,
                    "A problem was found parsing the file from server.", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "There were problems to connect to the server", ex);
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, "XML/HTML Parser could not be created.", ex);
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE,
                    "Found a malformed URI when trying to connect to server.", ex);
        }

        return null;
    }

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

    /**
     * run a SPARQL update on the remote server
     * @param queryString
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
            LOGGER.log(Level.SEVERE,
                    "There were problems to connect to the server", ex);
        }
    }

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
}
