package task;

import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is responsible for returning the wikisearch output
 * to the managecreation controller.
 */
public class WikitSearchTask extends Task<Void> {

    private String _searchWord;
    private int _exitStatus;
    private String _textReturned;

    public WikitSearchTask(String searchWord){
        _searchWord = searchWord;
    }

    /**
     * Searches wiki for the given search term, and splits sentences
     * for better readability.
     */
    @Override
    protected Void call() throws Exception {

        //Search on Wiki
        try {
            String cmd = "rm -r ./Files/temp; mkdir ./Files/temp; touch ./Files/shortened.txt; wikit " + _searchWord;
            Process process = new ProcessBuilder("bash", "-c", cmd).start();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(process.getInputStream()));
            _textReturned = stdoutBuffered.readLine();

            if (_textReturned.startsWith("  ")) {
                _textReturned = _textReturned.replaceFirst("  ", "");
            }
            _textReturned = _textReturned.replace(". ", ".\n");
            _exitStatus = process.waitFor();
            stdoutBuffered.close();
        } catch (IOException | InterruptedException e){}
        return null;
    }

    //Returns the exit status of the executed task
    public int get_exitStatus() {
        return _exitStatus;
    }

    //Returns the text output from the wiki search
    public String get_textReturned(){
        return _textReturned;
    }
}
