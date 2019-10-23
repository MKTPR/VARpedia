package task;

import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WikitSearchTask extends Task<Void> {

        private String _searchWord;
        private int _exitStatus;
        private String _textReturned;

        public WikitSearchTask(String searchWord){
            _searchWord = searchWord;
        }

        @Override
        protected Void call() throws Exception {
            String cmd = "rm -r ./Files/temp; mkdir ./Files/temp; wikit " + _searchWord;
            Process process = new ProcessBuilder("bash", "-c", cmd).start();

            _exitStatus = process.waitFor();

            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(process.getInputStream()));

            _textReturned = stdoutBuffered.readLine();

            if (_textReturned.startsWith("  ")) {
                _textReturned = _textReturned.replaceFirst("  ", "");
            }
            _textReturned = _textReturned.replace(". ", ".\n");

            stdoutBuffered.close();
            return null;
        }

        public int get_exitStatus() {
            return _exitStatus;
        }

        public String get_textReturned(){
            return _textReturned;
        }

        public String get_searchWord() {
            return _searchWord;
        }

}
