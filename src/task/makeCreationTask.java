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
    private String _music;
    private String directory;

    private int _exitStatus;
    private int _number;
    private String audio=null;
    private Double value=0.0;

    public makeCreationTask(String searchWord, int number, ObservableList<String> audio, String creationName, String music) {
        _searchword=searchWord;
        _number= number;
        _music=music;
        _creationName=creationName;
        _audioChosen=audio;
        directory= "./Files/keywords/"+_creationName;
    }

    @Override
    protected Void call() throws Exception {
        this.flickrSearch();
        this.makeCreation();
        this.mergeAudios();
        this.addMusic();
        this.mergeAV();
        return null;
    }

    private void mergeAudios() {

        String combine = "sox ";
        for (int i=0; i<_audioChosen.size();i++){
            combine = combine + "./Files/temp/"+ _audioChosen.get(i) + " ";
        }
        combine= combine + "./Files/temp/finaloutput2.wav";

        try {
            Process combineAudioProcess = new ProcessBuilder("bash", "-c", combine).start();
            combineAudioProcess.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

    private void addMusic() {
        String chosen= null;

        if (_music!=null){
            if (_music == "Hiphop-beats") {
                chosen = "grapes_-_I_dunno.mp3";
            } else if (_music == "Jazzy Funk") {
                chosen = "Whitewolf225_-_Toronto_Is_My_Beat.mp3";
            }

            try {
                String addMusic = "ffmpeg -y -i ./Music/" + chosen + " -i ./Files/temp/finaloutput2.wav -filter_complex \"[0:0]volume=0.4[a];[1:0]volume=1.5[b];[a][b]amix=inputs=2:duration=shortest\" -c:a libmp3lame ./Files/temp/finaloutput.wav";
                Process process = new ProcessBuilder("bash", "-c", addMusic).start();
                process.waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void mergeAV() {

        try {
            String cmd = "ffmpeg -y -i "+directory+ "/Video/" + _creationName + ".mp4 -i ./Files/temp/finaloutput.wav -r 25 ./Files/creations/" + _creationName + ".mp4; rm ./Files/temp/finaloutput.wav; rm ./Files/temp/finaloutput2.wav ";
            Process process = new ProcessBuilder("bash", "-c", cmd).start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private double getDuration(){

        try {
            for (int i=0;i<_audioChosen.size();i++) {
                String cmd = "soxi -D ./Files/temp/" + _audioChosen.get(i);
                Process process = new ProcessBuilder("bash", "-c", cmd).start();
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                _exitStatus = process.waitFor();
                audio = stdout.readLine();
                value = value + Double.parseDouble(audio);
                stdout.close();
            }

            return value;
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
                writer.println("file ./keywords/"+_creationName+"/Video/image"+i+".jpg");
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
