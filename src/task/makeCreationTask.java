package task;


import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * This class handles the merging of final Audio and the Video file,
 * to create the final creation.
 */
public class makeCreationTask extends Task<Void> {

    private String _searchword;
    private ObservableList<String> _audioChosen;
    private String _creationName;
    private String directory;
    private int _exitStatus;
    private int _number;
    private String audio=null;
    private String _music=null;

    // Assigns constructor inputs to a field.
    public makeCreationTask(String searchWord, int number, String creationName, String music) {
        _searchword=searchWord;
        _number= number;
        _creationName=creationName;
        _music=music;
        directory= "./Files/keywords/"+_creationName;
    }

    //All three methods needed for creation.
    @Override
    protected Void call() throws Exception {
        this.flickrSearch();
        this.makeCreation();
        this.mergeAV();
        return null;
    }

    /**
     * Uses the pre-downloaded apiKey from flickr to downloaded
     * the specified number of images to use in the video slideshow.
     */

    private void flickrSearch() {

        String apiKey = "45c83e45dac4125359b3f971c3b8eee0";
        String sharedSecret = "cc02c993f0254d2c";
        try {
            //New flickr object
            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            int page = 0;

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(_searchword);

            PhotoList<Photo> results = photos.search(params, _number, page);

            //makes the basic directory needed to store the downloaded photos.
            try {
                String cmd="rm -rf " + directory +"; mkdir " +directory +"; mkdir "+directory + "/Video";
                Process process = new ProcessBuilder("/bin/bash","-c",cmd).start();
                process.waitFor();
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            int i = 0;
            //Download and name the image files, and place the inside the folders created above.
            for (Photo photo: results) {
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);
                    String filename = "image"+i+".jpg";
                    File outputfile = new File(directory+"/Video",filename);
                    ImageIO.write(image, "jpg", outputfile);
                    i++;
                } catch (FlickrException | IOException fe) { }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for creating the slideshow with the downloaded images.
     * The duration of the slideshow is retrieved from the audio file that will be used.
     */
    private void makeCreation() {

        String vidirectory=directory+"/Video/";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./Files/shortened.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Obtain duration from the final audio file.
        double duration=getDuration();
        duration=duration/_number;

        //Use PrintWriter to create a new txt file with repeating commands in it - one for each photo.
        for (int i = 0; i < _number; i++) {
            if (i==_number-1) {
                writer.println("file ./keywords/"+_creationName+"/Video/image"+i+".jpg");
                writer.println("duration "+duration);
                break;
            }
            writer.println("file ./keywords/"+_creationName+"/Video/image"+i+".jpg");
            writer.println("duration "+duration);
        }
        writer.close();

        //Make a slideshow by merging all the photos and the search term as a subtitle on top.
        try {
            String cmd = "ffmpeg -y -f concat -safe 0 -i ./Files/shortened.txt -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' " +vidirectory+"video.mp4;ffmpeg -y -i "+vidirectory+"video.mp4 "+ "-vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_searchword+"'\" "+"-r 25 "+vidirectory+_creationName+".mp4";
            Process process = new ProcessBuilder("/bin/bash","-c",cmd).start();
            _exitStatus = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for merging the final video and the audio
     * files, which has been created in previous tasks.
     */
    private void mergeAV() {
        //merge the video and audio
        try {
            String cmd = "ffmpeg -y -i "+directory+ "/Video/" + _creationName + ".mp4 -i ./Files/temp/finaloutput.wav -r 25 ./Files/creations/" + _creationName + ".mp4; rm ./Files/temp/finaloutput.wav ./Files/temp/finaloutput2.wav";
            Process process = new ProcessBuilder("bash", "-c", cmd).start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the duration of the final audio file in double value,
     * in order to inform how long each photo should take.
     */
    private double getDuration(){

        String cmd=null;
        //Uses the finaloutput.wav file
        try {

            if (_music=="No Music"){
                cmd = "soxi -D ./Files/temp/finaloutput2.wav; mv ./Files/temp/finaloutput2.wav ./Files/temp/finaloutput.wav";
            }
            else {
                cmd = "soxi -D ./Files/temp/finaloutput2.wav";
            }
                Process process = new ProcessBuilder("bash", "-c", cmd).start();
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                _exitStatus = process.waitFor();
                audio = stdout.readLine();
                stdout.close();
            return Double.parseDouble(audio);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //Returns exit status of each bash command
    public int get_exitStatus() {
        return _exitStatus;
    }
}
