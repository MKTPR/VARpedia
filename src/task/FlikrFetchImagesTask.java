package task;
import javafx.concurrent.Task;
//TODO: Filter header
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class FlikrFetchImagesTask extends Task<Void> {
    //http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg


    private URL _searchURL;
    private String _query;
    private String _apiKey;
    private String _secretKey;

    public FlikrFetchImagesTask(String search) {
        _apiKey ="d992bd4d77d8ae432b48683a2e7f25fa";
        try {
            _searchURL = new URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        _secretKey = "adc0d05f57d0e88a";
        _query = "api_key={api_key}&tags={search_term}&format=rest"
                .replace("{search_term}", search)
                .replace("{api_key}", _apiKey);
    }

    @Override
    protected Void call() throws Exception {
        // Fetch response for search term
        String response = fetchXMLResponse();

        //parse XML fetching attribute data
        parseXML(response);

        // Download photos from endpoint
        return null;
    }

    private String fetchXMLResponse() throws IOException {

        URLConnection urlc = _searchURL.openConnection();

        // Make POST request
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        // Send query
        PrintStream ps = new PrintStream(urlc.getOutputStream());
        ps.print(_query);
        ps.close();

        // Read query result from buffer and close buffer when finished
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc
                .getInputStream()));
        String response = "";
        String l = "";
        while ((l = br.readLine()) != null) {
            response += l;
        }
        br.close();
        //System.out.println(response);
        return response;
    }

    private String[] parseXML(String response) {
        // Read DOM model
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputSource is;
        try {
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(response));
            Document doc = builder.parse(is);

            // Fetch all nodes attributed with "photo"
            NodeList nodeList = doc.getElementsByTagName("photo");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                NamedNodeMap photo = nodeList.item(i).getAttributes();
                String endpoint = "http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg"
                        .replace("{farm-id}", photo.getNamedItem("farm").getNodeValue())
                        .replace("{server-id}", photo.getNamedItem("server").getNodeValue())
                        .replace("{id}", photo.getNamedItem("id").getNodeValue())
                        .replace("{secret}", photo.getNamedItem("secret").getNodeValue());
                System.out.println(endpoint);
                downloadPhoto("" +i, endpoint);
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void downloadPhoto(String name, String endpoint) throws IOException {
        URL url = new URL(endpoint);
        InputStream in = new BufferedInputStream(url.openStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream("./temp/{name}.jpg".replace("{name}", name)));
        for ( int i; (i = in.read()) != -1; ) {
            out.write(i);
        }
        in.close();
        out.close();
    }
}
