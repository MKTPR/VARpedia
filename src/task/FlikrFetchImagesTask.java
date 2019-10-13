package task;
import javafx.concurrent.Task;
//TODO: Filter header
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class FlikrFetchImagesTask extends Task<Void> {

    //TODO: Create API requests to flikr
    //http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
    //farm-id
    //server-id
    //id
    //secret

    @Override
    protected Void call() throws Exception {
        //TODO: Remove secretid from this file and make the search dynamic
        URL url = new URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&");
        String query = "api_key=d992bd4d77d8ae432b48683a2e7f25fa&tags=apple&format=rest";

        URLConnection urlc = url.openConnection();

        // Make POST request
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        // Send query
        PrintStream ps = new PrintStream(urlc.getOutputStream());
        ps.print(query);
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
                // Print Id of photo
                System.out.println(nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue());
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
}
