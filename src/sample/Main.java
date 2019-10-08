package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    // Create global application constants
    public static String applicationName = "VARpedia";
    public static int[] applicationDimensions = {500,500};

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../scene/MainWindow.fxml"));
        primaryStage.setTitle(applicationName);
        Scene mainScene = new Scene(root, applicationDimensions[0], applicationDimensions[1]);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
