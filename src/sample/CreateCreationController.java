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
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
    @FXML private HBox _hbox;
    @FXML private VBox _vbox;
    @FXML private Button _searchButton;
    @FXML private ComboBox _voiceList;
    @FXML private Button _previewButton;
    @FXML private Button _saveAudioButton;
    @FXML private Button _deleteAudioButton;
    @FXML private Button _cancelCreationButton;
    @FXML private Button _listenAudioButton;
    @FXML private Button _mergeAudioButton;
    @FXML private Button _createCreationButton;
    @FXML private TextField _searchTerm;
    @FXML private TextField _audioName;
    @FXML private TextField _imageNumber;
    @FXML private TextField _creationName;
    @FXML private TextArea _content;
    @FXML private ListView _audioList;
    @FXML private ListView _confirmedAudioList;
    private Optional<ButtonType> result;
    private ObservableList<String> _items;
    private ObservableList<String> _confirmedAudio;
    private String _audioChosen=null;
    private String _removeAudioChosen=null;
    private String selected;
    private Process process;
    private Process process2;


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
        ProgressIndicator _progressSearch = new ProgressIndicator();
        _hbox.getChildren().add(_progressSearch);
        _progressSearch.setOpacity(100);
        task.WikitSearchTask task = new task.WikitSearchTask(_searchTerm.getText());
        team.submit(task);
        _searchButton.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_searchTerm.getText().isEmpty() | task.get_exitStatus() != 0 | task.get_textReturned() == _searchTerm.getText()+" not found :^(") {
                    makeAlert("Error", "Invalid Term", "Please try a different term");
                    _searchButton.setDisable(false);
                    return;
                }


                _content.setText(task.get_textReturned());
                _searchButton.setDisable(false);
                _progressSearch.setOpacity(0);
            }
        });
        setUpVoice();
        _confirmedAudio=FXCollections.observableArrayList();
    }

    private void setUpVoice() {
        ObservableList<String> audioOptions =
                FXCollections.observableArrayList(
                        "British Male",
                        "American Male",
                        "Robot Voice"
                );
        _voiceList.setItems(audioOptions);
    }

    private void refreshAudio(){
            _directory = new File("./Files/temp");
            _items = FXCollections.observableArrayList(getArrayList(_directory));
            _audioList.setItems(_items);
            setUpConfirmedAudioList();
    }

    private ArrayList<String> getArrayList(final File directory) {
        ArrayList<String> list = new ArrayList<String>();

        for (final File creations : directory.listFiles()) {
            list.add(creations.getName());
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private void setUpConfirmedAudioList() {
        _confirmedAudioList.setItems(_confirmedAudio);
    }


    @FXML private void preview(ActionEvent actionEvent) {
        selected = _content.getSelectedText();
        int wordCount = selected.split("\\s+").length;
        if (_content.getSelectedText().isEmpty()) {
            makeAlert("Error", "Invalid Selection", "Please highlight a part of the given text that you would like to listen to");
        }

        else if (wordCount > 20) {
            makeAlert("Error", "Invalid Selection", "Please highlight less than 20 words");
        }
        else if (_voiceList.getSelectionModel()==null){
            makeAlert("Error", "Invalid Selection", "Please select a voice");
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
            makeAlert("Error", "Invalid Selection", "Please Choose a name for the Audio");
        } else if (audioexists(_audioName.getText(), _searchTerm.getText())) {
            makeAlert("Error", "Existing Name", "Please select a different name");
        } else if (_voiceList.getSelectionModel().getSelectedItem()==null){
            makeAlert("Error", "Invalid Selection", "Please Select a voice");
        } else {
            selected = _content.getSelectedText();
            int wordCount = selected.split("\\s+").length;
            if (wordCount == 0) {
                makeAlert("Error", "Invalid Selection", "Please highlight a part of the given text that you would like to save");
            } else if (wordCount > 20) {
                makeAlert("Error", "Invalid Selection", "Please Highlight less than 20 words");
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
                previewCmd = "echo " + selected + " | espeak --stdin -w ./Files/temp/" + _audioName.getText()+".wav";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "Robot Voice") {
                previewCmd = "echo " + selected + " | text2wave -o ./Files/temp/" + _audioName.getText() + ".wav ";
            }
            return previewCmd;
        }
        else {
            if (_voiceList.getSelectionModel().getSelectedItem() == "British Male") {
                previewCmd = "echo " + selected + " | espeak --stdin -v en-us";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "American Male") {
                previewCmd = "echo " + selected + " | espeak --stdin ";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "Robot Voice") {
                previewCmd = "echo " + selected + " | festival --tts";
            }
            return previewCmd;
        }
    }

    @FXML private void deleteAudio(ActionEvent actionEvent){
        if (_audioChosen==null){
            makeAlert("Error", "Invalid Selection", "Please Select an AudioFile");
        }
        else {
            String cmd = "rm ./Files/temp/"+_audioChosen;
            makeConfirmation("Confirmation","You have chosen "+_audioChosen, "Are you sure you want to delete "+_audioChosen);
            if (result.get() == ButtonType.OK) {
                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                }
                _confirmedAudio.remove(_audioChosen);
                refreshAudio();
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Information Dialog");
                alert2.setHeaderText("Success!");
                alert2.setContentText("You have deleted " + _audioChosen);
                alert2.showAndWait();

                _audioChosen = null;
            }
        }
    }

    @FXML private void removeAudio(ActionEvent actionEvent){
        if (_removeAudioChosen==null){
            makeAlert("Error","Invalid Selection", "Please Select an AudioFile");
        }
        else {
            _confirmedAudio.remove(_removeAudioChosen);
           refreshAudio();
        }
    }

    @FXML private void confirmAudio(ActionEvent actionEvent){
        if (_audioChosen==null){
            makeAlert("Error", "Invalid Selection", "Please select an AudioFile");
        } else if (_confirmedAudio.contains(_audioChosen)){
            makeAlert("Error", "Invalid Selection", "Selected audio has already been added");
        }
        else {
            _confirmedAudio.add(_audioChosen);
            refreshAudio();
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
                    makeAlert("Error", "Invalid Selection", "Please select an AudioFile");
                }
                _audioChosen=null;
                _listenAudioButton.setDisable(false);
            }
        });
    }

    @FXML private void createCreation(){
        if (_searchTerm.getText().isEmpty() | _imageNumber.getText().isEmpty() | _confirmedAudioList.getItems().isEmpty() | _creationName.getText().isEmpty()){
            makeAlert("Error", "Invalid Selection", "Please fill in all the required parts");
        }
        else if(Integer.parseInt(_imageNumber.getText())>10 | Integer.parseInt(_imageNumber.getText())<1){
            makeAlert("Error", "Invalid Selection", "1~10");
        }
        else if(creationExists(_creationName.getText())){
            makeAlert("Error", "Invalid Selection", "different Name");
        }
        else {
            ProgressBar _progressCreation = new ProgressBar();
            _vbox.getChildren().add(_progressCreation);
            _progressCreation.setOpacity(100);
            task.makeCreationTask task = new task.makeCreationTask(_searchTerm.getText(), _imageNumber.getText(), _confirmedAudio, _creationName.getText());
            team.submit(task);
            _createCreationButton.setDisable(true);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    if (task.get_exitStatus() != 0) {
                        makeAlert("Error", "Unsuccessful", "Sorry");
                        return;
                    }
                    _createCreationButton.setDisable(false);
                    _progressCreation.setOpacity(0);
                }
            });
        }
    }

    private void makeAlert(String first, String second, String third){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(first);
        alert.setHeaderText(second);
        alert.setContentText(third);
        alert.showAndWait();
    }

    private void makeConfirmation(String first, String second, String third){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(first);
        alert.setHeaderText(second);
        alert.setContentText(third);
        result = alert.showAndWait();
    }

    @FXML public void handleAudioSelected(MouseEvent mouseEvent) {
        _audioChosen= (String) _audioList.getSelectionModel().getSelectedItem();
    }

    @FXML public void handleRemoveAudioSelected(MouseEvent mouseEvent) {
        _removeAudioChosen= (String) _confirmedAudioList.getSelectionModel().getSelectedItem();
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

    private boolean creationExists(String name) {
        String cmd = "test -f ./Files/creations/" + name +".mp4";
        try {
            process2 = new ProcessBuilder("/bin/bash", "-c", cmd).start();
            process2.waitFor();
        } catch (IOException | InterruptedException e) { }
        if (process2.exitValue()==0) {
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
