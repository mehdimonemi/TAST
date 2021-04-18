package company.UI;

import com.jfoenix.controls.*;
import com.sun.javafx.application.PlatformImpl;
import company.Assignment;
import company.NameException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;

public class Controller {
    @FXML
    private Label welcomeLabel, periodLabel;
    @FXML
    private JFXCheckBox path, assignment, summery, cargoType, cargoSummery, districts, loadUnloadOD;
    @FXML
    private JFXRadioButton fullAssignment, restrictedAssignment;
    @FXML
    private JFXTextField periodField;
    @FXML
    private JFXButton selectDirectory, analyse, startProcess, exit, openOutput, suggestions;
    @FXML
    private Tab assignmentTab, cargoForODTab;
    @FXML
    private JFXTextArea textArea;

    Service commoditiesCheck;
    final String[][] commodityCheckResult = {new String[5]};//these string is for names check result. we will pass it throw many classes

    String outPutDirectory = null;
    String outPutFileName = null;
    Boolean processState = true;

    Assignment assignmentClass = new Assignment();

    private final ObservableSet<ObservableBooleanValue> startProcessConditions = FXCollections.observableSet();
    private final IntegerBinding startProcessStatus = Bindings.size(startProcessConditions);
    private final ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private final ObservableSet<CheckBox> unselectedCheckBoxes = FXCollections.observableSet();
    private final IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);
    ObservableBooleanValue checkBoxSelected = new SimpleBooleanProperty(false);
    ObservableBooleanValue directorySelected = new SimpleBooleanProperty(false);
    ObservableBooleanValue analyseInputs = new SimpleBooleanProperty(false);

    private final int maxNumSelected = 3;
    public FileOutputStream fileOut = null;

    @FXML
    private void initialize() {
        //we should configure each check box to know if its check or not
        configureCheckBox(path);
        configureCheckBox(assignment);
        configureCheckBox(summery);
        configureCheckBox(cargoType);
        configureCheckBox(cargoSummery);
        configureCheckBox(districts);
        configureCheckBox(loadUnloadOD);

        manageListeners();
        checkCommodities();
    }

    private void checkCommodities() {
        //blow is our commoditiesCheck result:
        //excel row id
        //origin
        //destination

        commodityCheckResult[0][0] = "0";//0 means its first run and we should read data. also means app had read all names
        commodityCheckResult[0][1] = "";
        commodityCheckResult[0][2] = "";
        commodityCheckResult[0][3] = "";
        commodityCheckResult[0][4] = "";
        commoditiesCheck = new Service() {
            @Override
            public Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {

                        if (isFileClose(outPutDirectory + "/Data.xlsx")) {
                            if (commodityCheckResult[0][0].equals("0")) {
                                assignmentClass.readData(outPutDirectory);
                                commodityCheckResult[0][0] = "1";//change to first row of commodities excel file
                            }

                            commodityCheckResult[0] = assignmentClass.manageNames(outPutDirectory, commodityCheckResult[0]);
                            if (Integer.parseInt(commodityCheckResult[0][0]) > 0)
                                throw new NameException("Wrong Names");
                        } else {
                            processState = false;
                            alert("Excel File is open. Please close it first.");
                        }
                        System.gc();
                        return null;
                    }
                };
            }
        };
        commoditiesCheck.setOnFailed(event -> correctNamesDialog(commodityCheckResult[0]));
        commoditiesCheck.setOnSucceeded(event -> ((SimpleBooleanProperty) analyseInputs).set(true));

        analyse.setOnAction(event -> {
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });
    }

    private void manageListeners() {
        //A listener for periodField which only numbers characters are acceptable
        periodField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                periodField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        /*
        StartProcess button is conditional and for not being disable 3 conditions should be meet (startProcessConditions):
        1-checkboxes selected
        2-directory selected
        3-input file be analysed with analyse button
        */
        startProcessStatus.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            startProcess.setDisable(newSelectedCount.intValue() != 3);
            System.gc();
        });

        /*
        A listener for # of checkbox selected. the number will affect on cheBoxSelected listener. the results:
        if # between 0 or maxNumSelected, other checkBoxes will not be disable and checkBoxSelected will be true
        if # equal 0, other checkBoxes will will not be disable and checkBoxSelected will be false
        if else, other checkBoxes will be disable and checkBoxSelected will be false
        */
        numCheckBoxesSelected.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (newSelectedCount.intValue() <= maxNumSelected && newSelectedCount.intValue() > 0) {
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(false));
                ((SimpleBooleanProperty) checkBoxSelected).set(true);
            } else if (newSelectedCount.intValue() == 0) {
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(false));
                ((SimpleBooleanProperty) checkBoxSelected).set(false);
            } else {
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(true));
                ((SimpleBooleanProperty) checkBoxSelected).set(false);
            }
            System.gc();
        });

        //A listener for knowing whether directory is selected or not.
        checkBoxSelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(checkBoxSelected);
            } else {
                startProcessConditions.remove(checkBoxSelected);
            }
        });

        //A listener for knowing whether directory is selected or not.
        // of course the result will affect on analyse button.
        directorySelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(directorySelected);
                analyse.setDisable(false);
            } else {
                startProcessConditions.remove(directorySelected);
                analyse.setDisable(true);
            }
        });

        //A listener for knowing whether input analyse has been successful or not.
        analyseInputs.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(analyseInputs);
            } else {
                startProcessConditions.remove(analyseInputs);
            }
        });
    }

    public void configureCheckBox(JFXCheckBox checkBox) {
        if (checkBox.isSelected()) {
            selectedCheckBoxes.add(checkBox);
        } else {
            unselectedCheckBoxes.add(checkBox);
        }

        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                unselectedCheckBoxes.remove(checkBox);
                selectedCheckBoxes.add(checkBox);
            } else {
                selectedCheckBoxes.remove(checkBox);
                unselectedCheckBoxes.add(checkBox);
            }
        });
        System.gc();
    }

    public boolean isFileClose(String fileName) {
        //  TO CHECK WHETHER A FILE IS OPENED
        //  OR NOT (not for .txt files)

        File file = new File(fileName);
        if (file == null) {
            return true;
        }

        // try to rename the file with the same name
        File sameFileName = new File(fileName);

        // if the file didnt accept the renaming operation
        if (file.renameTo(sameFileName)) {
            // if the file is renamed
            return true;
        } else return !file.exists() && !file.isDirectory();
    }

    public void alert(String massage, Exception exception) {
        PlatformImpl.runAndWait(() -> {
            textArea.appendText("Something Wrong!" + "\n");
            textArea.appendText(massage + "\n");
            textArea.appendText(exception.getMessage() + "\n");
        });
    }

    public void alert(String s) {
        PlatformImpl.runAndWait(() -> textArea.appendText(s + "\n"));
    }

    public void correctNamesDialog(String[] oldResult) {
        Dialog myDialog = new Dialog();
        myDialog.setTitle("wrong names for commodity " + oldResult[0] + ": ");
        GridPane gridpane = new GridPane();
        gridpane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        Label label1 = new Label("Origin");
        Label label2 = new Label("Destination");
        gridpane.add(label1, 1, 1);
        gridpane.add(label2, 1, 2);

        TextField textField1 = new TextField(oldResult[1]);
        TextField textField2 = new TextField(oldResult[2]);
        gridpane.add(textField1, 2, 1);
        gridpane.add(textField2, 2, 2);

        Button login = new Button("Ok");
        gridpane.add(login, 2, 3);
        GridPane.setColumnSpan(login, 2);
        login.setAlignment(Pos.CENTER_RIGHT);
        login.setOnAction(event -> {

            commodityCheckResult[0][1] = textField1.getText();
            commodityCheckResult[0][2] = textField2.getText();
            login.getScene().getWindow().hide();
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });

        myDialog.getDialogPane().setContent(gridpane);
        myDialog.show();
    }

}
