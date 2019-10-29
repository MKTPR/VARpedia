package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {

    //TEMP
    private ExecutorService team = Executors.newSingleThreadExecutor();

    // Global application constants
    public static String applicationName = "VARpedia";
    public static int[] applicationDimensions = {1034,566};

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Setup temporary and creations directory
        FileSystem fs = FileSystem.getFileSystem();
        fs.createTempDirectory();
        fs.createCreationsDirectory();
        fs.createKeywordsDirectory();

        // Load GUI
        Parent root = FXMLLoader.load(getClass().getResource("/scene/MainMenu.fxml"));
        primaryStage.setTitle(applicationName);
        Scene mainScene = new Scene(root, applicationDimensions[0], applicationDimensions[1]);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //Make sure all threads are closed when the program is closed.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
