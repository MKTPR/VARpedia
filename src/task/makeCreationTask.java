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

public class makeCreationTask extends Task<Void> {

    private String _searchword;
    private ObservableList<String> _audioChosen;
    private String _creationName;
    private String directory;

    private int _exitStatus;
    private int _number;
    private String audio=null;


    public makeCreationTask(String searchWord, int number, String creationName) {
        _searchword=searchWord;
        _number= number;
        _creationName=creationName;
        directory= "./Files/keywords/"+_creationName;
    }

    @Override
    protected Void call() throws Exception {
        this.flickrSearch();
        this.makeCreation();
        this.mergeAV();
        return null;
    }

    private void mergeAV() {

        try {
            String cmd = "ffmpeg -y -i "+directory+ "/Video/" + _creationName + ".mp4 -i ./Files/temp/finaloutput.wav -r 25 ./Files/creations/" + _creationName + ".mp4; rm ./Files/temp/finaloutput.wav ./Files/temp/finaloutput2.wav";
            Process process = new ProcessBuilder("bash", "-c", cmd).start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private double getDuration(){

        try {
                String cmd = "soxi -D ./Files/temp/finaloutput2.wav";
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

        double duration=getDuration();
        duration=duration/_number;

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

        try {
            String cmd = "ffmpeg -y -f concat -safe 0 -i ./Files/shortened.txt -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' " +vidirectory+"video.mp4;ffmpeg -y -i "+vidirectory+"video.mp4 "+ "-vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_searchword+"'\" "+"-r 25 "+vidirectory+_creationName+".mp4";
            Process process = new ProcessBuilder("/bin/bash","-c",cmd).start();
            _exitStatus = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void flickrSearch() {

        String apiKey = "45c83e45dac4125359b3f971c3b8eee0";
        String sharedSecret = "cc02c993f0254d2c";

        try {

            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            int page = 0;

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(_searchword);

            PhotoList<Photo> results = photos.search(params, _number, page);

            try {
                String cmd="rm -rf " + directory +"; mkdir " +directory +"; mkdir "+directory + "/Video";
                Process process = new ProcessBuilder("/bin/bash","-c",cmd).start();
                process.waitFor();
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            int i = 0;
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

    public int get_exitStatus() {
        return _exitStatus;
    }
}
