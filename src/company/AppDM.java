package company;

import company.UI.CorrectNameController;
import company.UI.DM.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AppDM extends Application {

    public static MainController mainController;
    public static CorrectNameController dialogController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = null;
        FXMLLoader loader = null;
        Parent root1 = null;
        Parent root2 = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/company/UI/CorrectNameFxml.fxml"));
            mainLoader = new FXMLLoader(getClass().getResource("/company/UI/DM/MainWindowFxml.fxml"));
            root1 = mainLoader.load();
            root2 = loader.load();
        } catch (IOException e) {
            System.out.println("Fxml File Not Found");
        }
        mainController = (MainController) mainLoader.getController();
        dialogController = (CorrectNameController) loader.getController();

        Image icon = new Image(getClass().getResourceAsStream("/company/Logo1.png"));

        primaryStage.setTitle("TAST");
        assert root1 != null;

        primaryStage.setScene(new Scene(root1));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
