package task;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;

/**
 * This class is responsible for preparing the final audio file, which will
 * be used in the final creation.
 */
public class prepareAudioTask extends Task<Void> {

    private ObservableList<String> _audioChosen;
    private String _music;
    private int _exitStatus;

    //Takes the input of a list of chosen audio files, and the background music.
    public prepareAudioTask(ObservableList<String> audio, String music) {
        _music=music;
        _audioChosen=audio;
    }

    @Override
    protected Void call() throws Exception {
        this.mergeAudios();
        this.addMusic();
        return null;
    }

    /**
     * This method merges multiple audio files and outputs one combined wav file.
     */
    private void mergeAudios() {

        //create command to merge audio files.
        String combine = "sox ";
        for (int i=0; i<_audioChosen.size();i++){
            combine = combine + "./Files/temp/"+ _audioChosen.get(i) + " ";
        }

            combine= combine + "./Files/temp/finaloutput2.wav";

        try {
            Process combineAudioProcess = new ProcessBuilder("bash", "-c", combine).start();
            _exitStatus=combineAudioProcess.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds selected background music into the merged audio file.
     */
    private void addMusic() {
        String chosen= null;
        //See which music has been selected
        if (_music!= "No Music"){
            if (_music == "Hiphop-beats") {
                chosen = "grapes_-_I_dunno.mp3";
            } else if (_music == "Jazzy Funk") {
                chosen = "Whitewolf225_-_Toronto_Is_My_Beat.mp3";
            }
            //Add music to the background with adjusted volume, and create final audio wav file.
            try {
                String addMusic = "ffmpeg -y -i ./Music/" + chosen + " -i ./Files/temp/finaloutput2.wav -filter_complex \"[0:0]volume=0.4[a];[1:0]volume=1.5[b];[a][b]amix=inputs=2:duration=shortest\" -c:a libmp3lame ./Files/temp/finaloutput.wav";
                Process process = new ProcessBuilder("bash", "-c", addMusic).start();
                _exitStatus=process.waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Returns the exit status of each task.
    public int get_exitStatus() {
        return _exitStatus;
    }
}
