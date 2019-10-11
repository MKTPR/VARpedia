package sample;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class CreateCreationController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    @FXML private Button _searchButton;
    @FXML private Button _previewButton;
    @FXML private Button _saveAudioButton;
    @FXML private TextField _searchTerm;
    @FXML private TextField _audioName;
    @FXML private TextArea _content;
    private String selected;
    private Process process;


    @FXML public void deleteCreationOnClick(ActionEvent actionEvent) {
    }
    @FXML public void playCreationOnClick(ActionEvent actionEvent) {
    }
    @FXML public void playMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void pauseMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void fastPlayMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void slowPlayMediaOnClick(ActionEvent actionEvent) {
    }

    @FXML private void wikiSearch(ActionEvent actionEvent) {
        sample.wikipedSearch task = new sample.wikipedSearch(_searchTerm.getText());
        team.submit(task);
        _searchButton.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_searchTerm.getText().isEmpty() | task.get_exitStatus() != 0 | task.get_textReturned() == _searchTerm.getText()+" not found :^(") {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Term");
                    alert.setContentText("Please try a different term");
                    alert.showAndWait();
                    _searchButton.setDisable(false);
                    return;
                }

                _content.setText(task.get_textReturned());
                _searchButton.setDisable(false);
            }
        });
    }

    @FXML
    private void preview(ActionEvent actionEvent) {
        selected = _content.getSelectedText();
        int wordCount = selected.split("\\s+").length;
        if (_content.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please highlight a part of the given text that you would like to listen to.");
            alert.showAndWait();
        }

        else if (wordCount > 20) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please highlight less than 20 words");
            alert.showAndWait();
        }
        else {
            //preview the selected part
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "echo "+selected+ " | festival --tts");
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selected=null;
    }

    @FXML
    private void saveAudio(ActionEvent actionEvent) {

        if (_audioName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please choose a name for the Audio");
            alert.showAndWait();
        }
        else if (audioexists(_audioName.getText(),_searchTerm.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Existing Name");
            alert.setContentText("Please select a different name");
            alert.showAndWait();
        }
        else {
            selected = _content.getSelectedText();
            int wordCount = selected.split("\\s+").length;
            if (wordCount == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Selection");
                alert.setContentText("Please highlight a part of the given text that you would like to save.");
                alert.showAndWait();
            } else if (wordCount > 20) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Selection");
                alert.setContentText("Please highlight less than 20 words");
                alert.showAndWait();
            } else {
                String cmd = "echo " + selected + " | text2wave -o ./Files/temp/"+ _audioName.getText() + ".wav ";
                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "echo " + selected + " | festival --tts");
                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            selected = null;
        }
    }

    private Boolean audioexists(String audioName, String searchWord) {
        String cmd = "test -f ./Files/temp/"+_audioName.getText() + ".wav";
        try {
            process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) { }
        if (process.exitValue()==0) {
            return true;
        }
        return false;
    }

}
