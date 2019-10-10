package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    // Global application constants
    public static String applicationName = "VARpedia";
    public static int[] applicationDimensions = {500,500};

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Setup temporary and creations directory
        FileSystem fs = FileSystem.getFileSystem();
        fs.createTempDirectory();
        fs.createCreationsDirectory();
        fs.createKeywordsDirectory();


        // Load GUI
        Parent root = FXMLLoader.load(getClass().getResource("../scene/MainMenu.fxml"));
        primaryStage.setTitle(applicationName);

        //File logo = new File("../../resources/logo.svg");
        //primaryStage.getIcons().add(new Image(logo.toURI().toURL().toString()));

        Scene mainScene = new Scene(root, applicationDimensions[0], applicationDimensions[1]);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
