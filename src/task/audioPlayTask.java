package task;

import javafx.concurrent.Task;
import java.io.IOException;

public class audioPlayTask extends Task<Void>{

    private String _chosen;

    //Assign the selected audio to a field.
    public audioPlayTask(String audioChosen) {
        _chosen=audioChosen;
    }

    protected Void call() throws Exception {
        //Plays the file chosen
        String cmd = "aplay ./Files/temp/"+ _chosen;
        try {
            Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) { }
        return null;
    }
}
