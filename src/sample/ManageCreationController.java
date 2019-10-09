package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class ManageCreationController {

    @FXML private MediaView _mediaView;
    private ObservableList<String> _items;
    private File _directory;
    @FXML private Button _deleteCreationButton;
    @FXML private Button _playCreationButton;
    @FXML private Button _playButton;
    @FXML private Button _pauseButton;
    @FXML private Button _fastPlayButton;
    @FXML private Button _slowPlayButton;
    @FXML private ListView _creationList;
    private String _creationChosen=null;


    private void refresh(){
        _directory = new File("./Files/creations");
        _items = FXCollections.observableArrayList(getArrayList(_directory));
        _creationList.setItems(_items);
    }

    private ArrayList<String> getArrayList(final File directory) {
        ArrayList<String> list = new ArrayList<String>();

        for (final File creations : directory.listFiles()) {
            list.add(creations.getName());
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    @FXML public void deleteCreationOnClick(ActionEvent actionEvent) {

        if (_creationChosen == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("No creation has been chosen");
            alert.showAndWait();
        } else {
            String cmd = "rm -rf ./Files/creations/" + _creationChosen + " ./Files/Keywords/" + _creationChosen;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("You have chosen" + _creationChosen);
            alert.setContentText("Are you sure you want to delete" + _creationChosen);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                {
                    try {
                        Process process = new ProcessBuilder("/bin/bash", "-c", cmd).start();
                        process.waitFor();
                    } catch (IOException | InterruptedException e) {
                    }
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Information Dialog");
                    alert2.setHeaderText("Success!");
                    alert2.setContentText("You have deleted"+_creationChosen);
                    alert2.showAndWait();

                    _creationChosen = null;
                }
            }
        }
    }

    @FXML public void playCreationOnClick(ActionEvent actionEvent) {
        if (_creationChosen==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("No creation has been chosen");
            alert.showAndWait();
        }
        else {
            File file = new File("./Files/creations/" + _creationChosen);
            Media vid = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(vid);

            player.setAutoPlay(true);
            _mediaView.setMediaPlayer(player);
            player.setOnReady(() -> {
            });
            player.setOnEndOfMedia(() -> {
            });
        }
    }
    @FXML public void playMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void pauseMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void fastPlayMediaOnClick(ActionEvent actionEvent) {
    }
    @FXML public void slowPlayMediaOnClick(ActionEvent actionEvent) {
    }

    @FXML public void handleCreationSelected(MouseEvent mouseEvent) {
        _creationChosen= (String) _creationList.getSelectionModel().getSelectedItem();
    }
}
