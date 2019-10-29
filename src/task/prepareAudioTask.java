package task;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;

public class prepareAudioTask extends Task<Void> {

    private ObservableList<String> _audioChosen;
    private String _music;
    private int _exitStatus;


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
                _exitStatus=process.waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mergeAudios() {
        String combine = "sox ";
        for (int i=0; i<_audioChosen.size();i++){
            combine = combine + "./Files/temp/"+ _audioChosen.get(i) + " ";
        }

        if (_music==null){
            combine = combine + "./Files/temp/finaloutput.wav";
        }
        else {
            combine= combine + "./Files/temp/finaloutput2.wav";
        }

        try {
            Process combineAudioProcess = new ProcessBuilder("bash", "-c", combine).start();
            _exitStatus=combineAudioProcess.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public int get_exitStatus() {
        return _exitStatus;
    }
}
