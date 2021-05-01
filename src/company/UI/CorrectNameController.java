package company.UI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import static company.App.mainController;

public class CorrectNameController {
    @FXML
    public GridPane dialogPan;
    @FXML
    public JFXTextField dialogOrigin, dialogDestination;
    @FXML
    public JFXButton dialogOk, dialogStop;

    @FXML
    public void initialize() {
        this.dialogOk.setOnAction(event -> {
            mainController.commodityCheckResult[0][1] = this.dialogOrigin.getText();
            mainController.commodityCheckResult[0][2] = this.dialogDestination.getText();
            this.dialogOk.getScene().getWindow().hide();
            if (!mainController.commoditiesCheck.isRunning()) {
                mainController.commoditiesCheck.reset();
                mainController.commoditiesCheck.start();
            }
        });
        this.dialogStop.setOnAction(event -> {
            this.onStop();
        });

        this.dialogPan.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                this.dialogOk.fire();
                ev.consume();
            }
        });
    }

    public void onStop() {
        mainController.correctNameDialog.setResult(true);
        mainController.correctNameDialog.close();
        if (!mainController.commoditiesCheck.isRunning()) {
            mainController.commoditiesCheck.reset();
        }
    }

}

