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

public class MainMenuController {

    public MainMenuController MainMenuController() {
        System.out.println("yo");
        return null;
    }

    @FXML private Button _createCreationButton;
    @FXML private Button _manageCreationButton;
    @FXML private Button _playGameButton;

    public void createCreationOnClick() { System.out.println("yo"); }

    @FXML public void manageCreationOnClick() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("../scene/ManageCreation.fxml"));
        Parent layout = loader.load();

        Scene scene = new Scene(layout);
        Stage Stage = new Stage();
        Stage.setScene(scene);
        //We can exclude the code below for multi-purpose/concurrency
        Stage.initModality(Modality.APPLICATION_MODAL);
        Stage.showAndWait();
    }
    public void playGameOnClick() {
        System.out.println("yo");
    }
    public void helpOnClick() {
        System.out.println("yo");
    }

}
