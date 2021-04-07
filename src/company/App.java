package company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/company/UI/FxmlFile.fxml"));
        } catch (IOException e) {
            System.out.println("Fxml File Not Found");
        }

        Image icon = new Image(getClass().getResourceAsStream("/company/Logo1.png"));

        primaryStage.setTitle("TAST");
        assert root != null;

        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(icon);
        primaryStage.show();

    }
}
