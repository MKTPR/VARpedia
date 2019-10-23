package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class CreateCreationController {


    private ExecutorService team = Executors.newSingleThreadExecutor();
    private File _directory;
    @FXML private Button _searchButton;
    @FXML private ComboBox _voiceList;
    @FXML private Button _previewButton;
    @FXML private Button _saveAudioButton;
    @FXML private Button _deleteAudioButton;
    @FXML private Button _cancelCreationButton;
    @FXML private Button _listenAudioButton;
    @FXML private Button _mergeAudioButton;
    @FXML private TextField _searchTerm;
    @FXML private TextField _audioName;
    @FXML private TextArea _content;
    @FXML private ListView _audioList;
    private ObservableList<String> _items;
    private String _audioChosen=null;
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
        task.WikitSearchTask task = new task.WikitSearchTask(_searchTerm.getText());
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
        setUpVoice();
    }

    private void setUpVoice() {
        ObservableList<String> voiceOptions =
                FXCollections.observableArrayList(
                        "British Male",
                        "American Male",
                        "Robot Voice"
                );
        _voiceList.setItems(voiceOptions);
    }

    private void refreshAudio(){
            _directory = new File("./Files/temp");
            _items = FXCollections.observableArrayList(getArrayList(_directory));
            _audioList.setItems(_items);

    }
    private ArrayList<String> getArrayList(final File directory) {
        ArrayList<String> list = new ArrayList<String>();

        for (final File creations : directory.listFiles()) {
            list.add(creations.getName());
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }


    @FXML private void preview(ActionEvent actionEvent) {
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
        else if (_voiceList.getSelectionModel()==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please select a Voice");
            alert.showAndWait();
        }
        else {
            String previewCmd = getVoice("Preview");

            //preview the selected part
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", previewCmd);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selected=null;
    }

    @FXML private void saveAudio(ActionEvent actionEvent) {

        if (_audioName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please choose a name for the Audio");
            alert.showAndWait();
        } else if (audioexists(_audioName.getText(), _searchTerm.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Existing Name");
            alert.setContentText("Please select a different name");
            alert.showAndWait();
        } else if (_voiceList.getSelectionModel().getSelectedItem()==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please select a Voice");
            alert.showAndWait();
        } else {
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
                String previewCmd= getVoice("Save");

                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", previewCmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            refreshAudio();
            selected = null;
        }
    }

    private String getVoice(String command){
        String previewCmd=null;
        if (command == "Save") {

            if (_voiceList.getSelectionModel().getSelectedItem() == "British Male") {
                previewCmd = "echo " + selected + " | espeak --stdin -v en-us -w ./Files/temp/" + _audioName.getText() + ".wav";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "American Male") {
                previewCmd = "echo " + selected + " | espeak --stdin -v default -w ./Files/temp/" + _audioName.getText()+".wav";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "Robot Voice") {
                previewCmd = "echo " + selected + " | text2wave -o ./Files/temp/" + _audioName.getText() + ".wav ";
            }
            return previewCmd;
        }
        else {
            if (_voiceList.getSelectionModel().getSelectedItem() == "British Male") {
                previewCmd = "echo " + selected + " | espeak --stdin -v en-us";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "American Male") {
                previewCmd = "echo " + selected + " | espeak --stdin -v default";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "Robot Voice") {
                previewCmd = "echo " + selected + " | festival --tts";
            }
            return previewCmd;
        }
    }

    @FXML private void deleteAudio(){
        if (_audioChosen==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please select an AudioFile");
            alert.showAndWait();
        }
        else {
            String cmd = "rm ./Files/temp/"+_audioChosen;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("You have chosen " + _audioChosen);
            alert.setContentText("Are you sure you want to delete " + _audioChosen);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                }
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Information Dialog");
                alert2.setHeaderText("Success!");
                alert2.setContentText("You have deleted " + _audioChosen);
                alert2.showAndWait();

                _audioChosen = null;
                refreshAudio();
            }
        }
    }

    @FXML private void listenAudio(){

        task.audioPlayTask task = new task.audioPlayTask(_audioChosen);
        team.submit(task);
        _listenAudioButton.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_audioChosen==null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Selection");
                    alert.setContentText("Please select an AudioFile");
                    alert.showAndWait();
                }
                _audioChosen=null;
                _listenAudioButton.setDisable(false);
            }
        });
    }


    @FXML private void mergeAudio(){
    }

    @FXML public void handleAudioSelected(MouseEvent mouseEvent) {
        _audioChosen= (String) _audioList.getSelectionModel().getSelectedItem();
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

    @FXML private void goBackMain1(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/scene/MainMenu.fxml"));
        Parent layout = loader.load();

        Scene scene = new Scene(layout);
        Stage Stage = (Stage) _cancelCreationButton.getScene().getWindow();
        Stage.setScene(scene);
    }
}
