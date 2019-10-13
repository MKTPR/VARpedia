package sample;

import javafx.concurrent.Task;
import java.io.IOException;

public class audioPlay extends Task<Void>{

    private String _chosen;

    public audioPlay(String audioChosen) {
        _chosen=audioChosen;
    }

    protected Void call() throws Exception {
        String cmd = "aplay ./Files/temp/"+ _chosen;
        try {
            Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) { }
        return null;
    }
}
