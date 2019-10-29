package task;

import javafx.concurrent.Task;
import java.io.IOException;

public class previewTask extends Task<Void>{

    private String _previewCmd;

    public previewTask(String command) {
        _previewCmd=command;
    }

    protected Void call() throws Exception {

        try {
            Process process = new ProcessBuilder("/bin/bash", "-c", _previewCmd).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) { }
        return null;
    }
}
