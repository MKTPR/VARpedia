package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class ManageCreationController {

    @FXML private MediaView _mediaView;
    @FXML private Button _deleteCreationButton;
    @FXML private Button _playCreationButton;
    @FXML private Button _playButton;
    @FXML private Button _pauseButton;
    @FXML private Button _fastPlayButton;
    @FXML private Button _slowPlayButton;
    @FXML private ListView _creationList;
    private String _creationChosen=null;


    @FXML public void deleteCreationOnClick(ActionEvent actionEvent) {
        if (_creationChosen==null){

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

    @FXML public void handleCreationSelected(MouseEvent mouseEvent) {
        _creationChosen= (String) _creationList.getSelectionModel().getSelectedItem();
    }
}
