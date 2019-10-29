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

/**
 * This class handles the "Creation" menu. It contains the actions for all the buttons on the GUI.
 * It also takes user input for different functionalities.
 */

public class CreateCreationController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    private ExecutorService team2 = Executors.newSingleThreadExecutor();
    private File _directory;
    @FXML private HBox _hbox;
    @FXML private VBox _vbox;
    @FXML private Button _searchButton;
    @FXML private Button _homeButton2;
    @FXML private ComboBox _voiceList;
    @FXML private ComboBox _musicList;
    @FXML private ComboBox _numberList;
    @FXML private Button _previewButton;
    @FXML private Button _saveAudioButton;
    @FXML private Button _deleteAudioButton;
    @FXML private Button _cancelCreationButton;
    @FXML private Button _listenAudioButton;
    @FXML private Button _mergeAudioButton;
    @FXML private Button _createCreationButton;
    @FXML private TextField _searchTerm;
    @FXML private TextField _audioName;
    @FXML private TextField _creationName;
    @FXML private TextArea _content;
    @FXML private ListView _audioList;
    @FXML private ListView _confirmedAudioList;
    private Optional<ButtonType> result;
    private ObservableList<String> _items;
    private ObservableList<String> _confirmedAudio;
    private String _audioChosen=null;
    private String _removeAudioChosen=null;
    private String _searchTermChosen=null;
    private String selected;
    private Process process;
    private Process process2;

    /**
     * Takes the input from the user to search the term given through
     * wikipedia by calling the WikitSearchTask.
     */
    @FXML private void wikiSearch(ActionEvent actionEvent) {

        _searchTermChosen=_searchTerm.getText();
        ProgressIndicator _progressSearch = new ProgressIndicator();
        _hbox.getChildren().add(_progressSearch);
        _progressSearch.setOpacity(100);

        //Create a new task and execute it.
        task.WikitSearchTask task = new task.WikitSearchTask(_searchTermChosen);
        team.submit(task);
        _searchButton.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            //Handle errors and alert the user
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_searchTerm.getText().isEmpty() | task.get_exitStatus() != 0 | task.get_textReturned() == _searchTermChosen+" not found :^(") {
                    _progressSearch.setOpacity(0);
                    makeAlert("Error", "Invalid Term", "Please try a different term");
                    _searchButton.setDisable(false);
                    return;
                }
                //Return the output to the user and set up appropriate controls for further creation
                _content.setText(task.get_textReturned());
                _searchButton.setDisable(false);
                _progressSearch.setOpacity(0);
                setUp();
                reset();
                refreshAudio();
            }
        });
    }

    /**
     * Allows the user to listen to the selected Text through the selected voice without having to save an audio file.
     */
    @FXML private void preview(ActionEvent actionEvent) {
        selected = _content.getSelectedText();
        int wordCount = selected.split("\\s+").length;
        //Testing for errors
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

            _previewButton.setDisable(true);
            //preview the selected part
            task.previewTask task = new task.previewTask(previewCmd);
            team.submit(task);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    _previewButton.setDisable(false);
                }
            });
        }
    }

    /**
     * Used to save the audio file with the specified text and voice.
     */
    @FXML private void saveAudio(ActionEvent actionEvent) {

        //Test for different errors (invalid inputs)
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

                //Save the audio file
                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", previewCmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //The saved audio should show on the audio list
            refreshAudio();
            selected = null;
        }
    }

    /**
     * Returns the appropriate String command for bash, to save or preview the text with the selected voice.
     */
    private String getVoice(String command){
        String previewCmd=null;
        //In case of saving
        if (command == "Save") {

            if (_voiceList.getSelectionModel().getSelectedItem() == "British Male") {
                previewCmd = "echo \"" + selected + "\" | espeak --stdin -v en-us -w ./Files/temp/" + _audioName.getText() + ".wav";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "American Male") {
                previewCmd = "echo \"" + selected + "\" | espeak --stdin -w ./Files/temp/" + _audioName.getText()+".wav";
            }
            return previewCmd;
        }
        //In case of preview
        else {
            if (_voiceList.getSelectionModel().getSelectedItem() == "British Male") {
                previewCmd = "echo \"" + selected + "\" | espeak --stdin -v en-us";
            } else if (_voiceList.getSelectionModel().getSelectedItem() == "American Male") {
                previewCmd = "echo \"" + selected + "\" | espeak --stdin ";
            }
            return previewCmd;
        }
    }

    /**
     * Deletes the selected audio file.
     */
    @FXML private void deleteAudio(ActionEvent actionEvent){
        if (_audioChosen==null){
            makeAlert("Error", "Invalid Selection", "Please Select an AudioFile");
        }
        else {
            String cmd = "rm ./Files/temp/"+_audioChosen;

            //Ask for confirmation from the user
            makeConfirmation("Confirmation","You have chosen "+_audioChosen, "Are you sure you want to delete "+_audioChosen);
            if (result.get() == ButtonType.OK) {
                try {
                    Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                }
                _confirmedAudio.remove(_audioChosen);
                refreshAudio();
                makeInfo("Information Dialogue", "Success", "You have deleted" + _audioChosen);
                _audioChosen = null;
            }
        }
    }

    /**
     * Removes the selected audio file from the confirmed list
     * to be put into the creation. (Not deleting)
     */
    @FXML private void removeAudio(ActionEvent actionEvent){
        if (_removeAudioChosen==null){
            makeAlert("Error","Invalid Selection", "Please Select an AudioFile");
        }
        else {
            _confirmedAudio.remove(_removeAudioChosen);
           refreshAudio();
        }
    }

    /**
     * Moves the selected audio file into the confirmed list
     * to be put into the creation.
     */
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

    /**
     * Allows the user to listen to the saved audio file.
     * Selected from the given audio list.
     */
    @FXML private void listenAudio(){

        //Creates a new audio playing task
        task.audioPlayTask task = new task.audioPlayTask(_audioChosen);
        team.submit(task);
        _listenAudioButton.setDisable(true);
        _deleteAudioButton.setDisable(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_audioChosen==null){
                    makeAlert("Error", "Invalid Selection", "Please select an AudioFile");
                }
                _listenAudioButton.setDisable(false);
                _deleteAudioButton.setDisable(false);
            }
        });
    }

    /**
     * Final creation with the image slideshow, background music and the combined audio files.
     */
    @FXML private void createCreation(){

        //Test for invalid/empty inputs
        if (_searchTerm.getText().isEmpty() | _numberList.getSelectionModel().getSelectedItem()==null | _confirmedAudioList.getItems().isEmpty() | _creationName.getText().isEmpty()){
            makeAlert("Error", "Invalid Selection", "Please fill in all the required parts");
        }
        else if(creationExists(_creationName.getText())){
            makeAlert("Error", "Existing Name", "Please Choose a Different Name \nOr go to the Manage Creations Menu and Delete");
        }
        else if (_musicList.getSelectionModel().getSelectedItem()==null){
            makeAlert("Error", "Nothing Chosen", "Please choose an option for your background music");
        }
        else {
            ProgressBar _progressCreation = new ProgressBar();
            _vbox.getChildren().add(_progressCreation);
            _progressCreation.setOpacity(100);

            //Creates a new audioCreation task, merges the different audiofiles together with the selected music
            task.prepareAudioTask task= new task.prepareAudioTask(_confirmedAudio,_musicList.getSelectionModel().getSelectedItem().toString());
            team.submit(task);
            _createCreationButton.setDisable(true);
            _deleteAudioButton.setDisable(true);

            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    if (task.get_exitStatus() != 0) {
                        makeAlert("Error", "Unsuccessful", "Sorry, Please Cancel and Try again");
                        return;
                    }
                    //If succeeded, call the makeCreationTask to finally make the slideshow of images and combine it with the audio created above.
                    task.makeCreationTask task2 = new task.makeCreationTask(_searchTermChosen, _numberList.getSelectionModel().getSelectedIndex()+1, _creationName.getText(), _musicList.getSelectionModel().getSelectedItem().toString());
                    team2.submit(task2);

                    task2.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            if (task2.get_exitStatus() != 0) {
                                makeAlert("Error", "Unsuccessful", "Sorry, Please Cancel and Try again");
                                return;
                            }
                            _progressCreation.setOpacity(0);
                            makeInfo("Creation", "Success", "Your Video has been created!\nYou can check the video in the\n\"Manage Creations\" menu");
                            _createCreationButton.setDisable(false);
                            _deleteAudioButton.setDisable(true);
                        }
                    });
                }
            });
        }
    }

    /**
     * Method to create the default Java Alert pop up.
     */
    private void makeAlert(String first, String second, String third){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(first);
        alert.setHeaderText(second);
        alert.setContentText(third);
        alert.showAndWait();
    }

    /**
     * Method to create the default Java Information dialogue pop up.
     */
    private void makeInfo(String first, String second, String third){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(first);
        alert.setHeaderText(second);
        alert.setContentText(third);
        alert.showAndWait();
    }

    /**
     * Method to create the default Java Confirmation pop up.
     */
    private void makeConfirmation(String first, String second, String third){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(first);
        alert.setHeaderText(second);
        alert.setContentText(third);
        result = alert.showAndWait();
    }

    /**
     * Sets the selected audio from the user to a field.
     */
    @FXML public void handleAudioSelected(MouseEvent mouseEvent) {
        _audioChosen= (String) _audioList.getSelectionModel().getSelectedItem();
    }

    /**
     * Method to remove the selected audio from the confirmed audiolist.
     */
    @FXML public void handleRemoveAudioSelected(MouseEvent mouseEvent) {
        _removeAudioChosen= (String) _confirmedAudioList.getSelectionModel().getSelectedItem();
    }

    /**
     * Loads the main menu in a new scene.
     */
    @FXML private void goBackMain2(ActionEvent actionEvent) throws IOException {
        makeConfirmation("Confirmation", "Going Back to Main Menu", "All unsaved progress will be lost\nDo you still want to go back?");
        if (result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/scene/MainMenu.fxml"));
            Parent layout = loader.load();

            Scene scene = new Scene(layout);
            Stage Stage = (Stage) _cancelCreationButton.getScene().getWindow();
            Stage.setScene(scene);
        }
    }

    /**
     * Resets the previous saved audio lists in case the user wants to start creating
     * a video regarding a different keyword.
     */
    private void reset() {
        if(_items.isEmpty()!=true) {
            _items.removeAll();
        }
        if(_confirmedAudio.isEmpty()!=true) {
            _confirmedAudio.removeAll();
        }
    }

    /**
     * Set up the arrays for the different comboBoxes in the GUI.
     */
    private void setUp() {
        _confirmedAudio=FXCollections.observableArrayList();
        _items=FXCollections.observableArrayList();

        ObservableList<String> audioOptions =
                FXCollections.observableArrayList(
                        "British Male",
                        "American Male"
                );
        _voiceList.setItems(audioOptions);

        ObservableList<String> voiceOptions =
                FXCollections.observableArrayList(
                        "Hiphop-beats",
                        "Jazzy Funk",
                        "No Music"
                );
        _musicList.setItems(voiceOptions);

        ObservableList<Integer> numberOption= FXCollections.observableArrayList();
        for (int i = 1; i <= 10; i++) {
            numberOption.add(i);
        }
        _numberList.setItems(numberOption);
    }

    /**
     * Refresh the list of saved audio files and the confirmed audioList.
     */
    private void refreshAudio(){
        _directory = new File("./Files/temp");
        _items = FXCollections.observableArrayList(getArrayList(_directory));
        _audioList.setItems(_items);
        _confirmedAudioList.setItems(_confirmedAudio);
    }

    /**
     * Returns the list of audio files in an arrayList format.
     */
    private ArrayList<String> getArrayList(final File directory) {
        ArrayList<String> list = new ArrayList<String>();

        for (final File creations : directory.listFiles()) {
            list.add(creations.getName());
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    /**
     * Checks if a creating with the same name exists already.
     */
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

    /**
     * Checks if an audio files with the same name exists already.
     */
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
