package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

public class ManageCreationController {

    @FXML private Slider _mediaSlider;
    @FXML private Slider _volumeSlider;
    @FXML private MediaView _mediaView;
    private ObservableList<String> _items;
    private File _directory;
    @FXML private Button _deleteCreationButton;
    @FXML private Button _homeButton1;
    @FXML private Button _playCreationButton;
    @FXML private Button _playButton;
    @FXML private Button _pauseButton;
    @FXML private Button _fastPlayButton;
    @FXML private Button _slowPlayButton;
    @FXML private ListView _creationList;
    private String _creationChosen=null;
    private MediaPlayer player;

    /**
     * Plays the selected creation from the list. It makes a new MediaPlayer
     * object to set to the existing mediaView.
     */
    @FXML public void playCreationOnClick(ActionEvent actionEvent) {
        refresh();
        if (validSelection()){
            File file = new File("./Files/creations/" + _creationChosen+".mp4");
            Media vid = new Media(file.toURI().toString());

            if(player!=null){
                player.stop();
                player.dispose();
            }
            player = new MediaPlayer(vid);
            _mediaView.setMediaPlayer(player);

            player.setAutoPlay(true);
            player.setOnReady(() -> {
                sliderSetUp();
                _volumeSlider.setDisable(false);
                _mediaSlider.setDisable(false);
            });
            player.setOnEndOfMedia(() -> {
                player.dispose();
                _mediaView.setMediaPlayer(null);
                player=null;
                _volumeSlider.setDisable(true);
                _mediaSlider.setDisable(true);
            });
        }
    }

    /**
     * Deletes the selected creation from the list. The creation itself, and the
     * audio and video files related to it is deleted for storage purposes.
     */
    @FXML public void deleteCreationOnClick(ActionEvent actionEvent) {

        if (validSelection()) {

            if (player != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Middle of Process!");
                alert.setContentText("Please Wait for the Video to Finish");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("You have chosen " + _creationChosen);
                alert.setContentText("Are you sure you want to delete " + _creationChosen);
                Optional<ButtonType> result = alert.showAndWait();
                String cmd = "rm -rf ./Files/creations/" + _creationChosen + ".mp4 ./Files/keywords/" + _creationChosen;
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
                        alert2.setContentText("You have deleted " + _creationChosen);
                        alert2.showAndWait();

                        _creationChosen = null;
                        refresh();

                    }
                }
            }
        }
    }

    /**
     * Sets up the slider which displays the point where the media is at.
     * It syncs the slider with the video and also handles mouse clicks to
     * move the point which the media is at.
     */
    private void sliderSetUp() {

        //Set up the volume slider.
        _volumeSlider.setValue(player.getVolume() * 100);
        _volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                player.setVolume(_volumeSlider.getValue()/100);
            }
        });

        //Set up the Media Slider
        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                _mediaSlider.setValue(newValue.toMillis());

            }
        });
        //Allows user to jump between various points of the video.
        _mediaSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                player.seek(Duration.millis(_mediaSlider.getValue()));
            }
        });

        _mediaSlider.setMax(player.getTotalDuration().toMillis());
        _mediaSlider.setValue(0);
    }

    @FXML public void playMediaOnClick(ActionEvent actionEvent) {
        if(validSelection() && mediaExist()) {
            player.play();
        }
    }
    @FXML public void pauseMediaOnClick(ActionEvent actionEvent) {
        if (validSelection() && mediaExist()) {
            player.pause();
        }
    }
    @FXML public void fastPlayMediaOnClick(ActionEvent actionEvent) {
        if (validSelection() && mediaExist()) {
            player.setRate(1.2);
        }
    }
    @FXML public void slowPlayMediaOnClick(ActionEvent actionEvent) {
        if (validSelection() && mediaExist()) {
            player.setRate(0.8);
        }
    }

    //handles the creation selection from the user
    @FXML public void handleCreationSelected(MouseEvent mouseEvent) {
        _creationChosen= (String) _creationList.getSelectionModel().getSelectedItem();
    }

    //Loads the main menu and stops all media processes.
    @FXML public void goBackMain1(ActionEvent actionEvent) throws IOException {
        if(player!=null){
            player.stop();
            player.dispose();
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/scene/MainMenu.fxml"));
        Parent layout = loader.load();

        Scene scene = new Scene(layout);
        Stage Stage = (Stage) _homeButton1.getScene().getWindow();
        Stage.setScene(scene);
    }

    //Refreshes the creation listview.
    private void refresh(){
        _directory = new File("./Files/creations");
        _items = FXCollections.observableArrayList(getArrayList(_directory));
        _creationList.setItems(_items);
    }

    //Returns the existing creation names in an arrayList.
    private ArrayList<String> getArrayList(final File directory) {
        ArrayList<String> list = new ArrayList<String>();
        String cmd = "ls " + directory + " | grep mp4 | sort | cut -d'.' -f1";
        try {
            Process process = new ProcessBuilder("bash", "-c", cmd).start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Checks if any selection has been made.
    private boolean validSelection(){
        if (_creationChosen==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("No creation has been chosen");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    //Checks if media currently has a video loaded onto it.
    private boolean mediaExist() {
        if (player != null) {
            return true;
        }
        return false;
    }
}
