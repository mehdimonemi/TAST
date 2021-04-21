package company;

import company.UI.CorrectNameController;
import company.UI.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static MainController mainController;
    public static CorrectNameController nameController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = null;
        FXMLLoader mainLoader = null;
        Parent root = null;
        Parent root2 = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/company/UI/CorrectNameFxml.fxml"));
            mainLoader = new FXMLLoader(getClass().getResource("/company/UI/MainWindowFxml.fxml"));
            root = mainLoader.load();
            root2 = loader.load();
        } catch (IOException e) {
            System.out.println("Fxml File Not Found");
        }
        mainController = (MainController) mainLoader.getController();
        nameController = (CorrectNameController) loader.getController();

        Image icon = new Image(getClass().getResourceAsStream("/company/Logo1.png"));

        primaryStage.setTitle("TAST");
        assert root != null;

        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }
}
