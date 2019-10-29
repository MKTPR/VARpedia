package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class controls the main menu GUI
 */
public class MainMenuController {

    public MainMenuController MainMenuController() {
        System.out.println("yo");
        return null;
    }

    @FXML private Button _createCreationButton;
    @FXML private Button _manageCreationButton;

    //change scene to creating new creations menu
    public void createCreationOnClick() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/scene/CreateCreation.fxml"));
        Parent layout = loader.load();

        Scene scene = new Scene(layout);
        Stage Stage = (Stage) _createCreationButton.getScene().getWindow();
        Stage.setScene(scene);
    }

    //change scene to managing creations menu
    @FXML public void manageCreationOnClick() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/scene/ManageCreation.fxml"));
        Parent layout = loader.load();

        Scene scene = new Scene(layout);
        Stage Stage = (Stage) _manageCreationButton.getScene().getWindow();
        Stage.setScene(scene);
    }
}
