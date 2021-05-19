package company.UI;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.jfoenix.controls.*;
import company.Assignment;
import company.NameException;
import company.Outputs.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import static company.App.dialogController;
import static company.App.mainController;

public class MainController {
    @FXML
    VBox primaryStage;
    @FXML
    private JFXCheckBox pathCheckBox, assignmentCheckBox, summeryCheckBox,
            cargoTypeCheckBox, cargoSummeryCheckBox, districtsCheckBox, loadUnloadODCheckBox;
    @FXML
    private JFXRadioButton fullAssignment, restrictedAssignment;
    @FXML
    private JFXTextField periodField, cargoOrigin, cargoDestination;
    @FXML
    private JFXButton selectDirectory, analyse, startProcess, exit, openOutput, suggestions, dialogStop;
    @FXML
    private Tab assignmentTab, cargoForODTab;
    @FXML
    JFXTextArea textArea;

    Dialog<Boolean> correctNameDialog;

    public Service commoditiesCheck;
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
    ObservableBooleanValue cargoTabSelected = new SimpleBooleanProperty(false);

    private final int maxNumSelected = 3;
    public FileOutputStream fileOut = null;

    @FXML
    private void initialize() {
        //we should configure each check box to know if its check or not
        configureCheckBox(pathCheckBox);
        configureCheckBox(assignmentCheckBox);
        configureCheckBox(summeryCheckBox);
        configureCheckBox(cargoTypeCheckBox);
        configureCheckBox(cargoSummeryCheckBox);
        configureCheckBox(districtsCheckBox);
        configureCheckBox(loadUnloadODCheckBox);

        manageListeners();
        checkCommodities();
        configureButtons();
        processAssignment();
    }

    private void processAssignment() {
        Service process = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {

                        boolean finalCheck = true;
                        if (cargoForODTab.isSelected()) {
                            String[] finalCargoOrigin =
                                    assignmentClass.findName(cargoOrigin.getText());
                            String[] finalCargoDestination =
                                    assignmentClass.findName(cargoDestination.getText());
                            if (cargoOrigin.getText().equals("null") || cargoDestination.getText().equals("null")) {
                                processState = false;
                                finalCheck = false;
                                alert("Origin/Destination names are NOT CORRECT!");
                            }
                        }

                        if (finalCheck) {
                            ULocale locale = new ULocale("en_US@calendar=persian");

                            Calendar calendar = Calendar.getInstance(locale);
                            SimpleDateFormat df = new SimpleDateFormat("MM.dd - EEE, MMM - HH.mm.ss", locale);

                            outPutFileName = "./Output " + df.format(calendar) + ".xlsx";

                            if (isFileClose(outPutDirectory + outPutFileName)) {

                                assignmentClass.main(fullAssignment.isSelected());

                                fileOut = new FileOutputStream(outPutDirectory + outPutFileName);
                                fileOut.flush();
                                fileOut.close();

                                if (pathCheckBox.isSelected()) {
                                    new OutputPaths(outPutDirectory + outPutFileName, assignmentClass.commodities);
                                }
                                if (assignmentCheckBox.isSelected()) {
                                    new OutputAssignment(outPutDirectory + outPutFileName,
                                            outPutDirectory + "/Data.xlsx",
                                            assignmentClass.outputBlocks, assignmentClass.blocks);
                                }
                                if (summeryCheckBox.isSelected()) {
                                    new OutputSummery(outPutDirectory + outPutFileName,
                                            assignmentClass.commodities);
                                }
                                if (cargoTypeCheckBox.isSelected()) {
                                    new OutputCargoType(outPutDirectory + outPutFileName,
                                            assignmentClass.commodities, assignmentClass.mainCargoTypes,
                                            assignmentClass.wagons);
                                }
                                if (cargoSummeryCheckBox.isSelected()) {
                                    new OutputCargoSummery(outPutDirectory + outPutFileName,
                                            assignmentClass.commodities, assignmentClass.mainCargoTypes,
                                            assignmentClass.wagons);
                                }
                                if (districtsCheckBox.isSelected()) {
                                    new OutputDistricts(outPutDirectory + outPutFileName,
                                            assignmentClass.districts, assignmentClass.commodities,
                                            periodField.getText());
                                }
                                if (loadUnloadODCheckBox.isSelected()) {
                                    new OutputLoadUnloadOD(
                                            outPutDirectory + outPutFileName,
                                            assignmentClass.districts, assignmentClass.commodities);
                                }
                                if (cargoForODTab.isSelected()) {

                                    new ODFreight(outPutDirectory + outPutFileName, assignmentClass.blocks,
                                            assignmentClass.commodities, assignmentClass.pathExceptions,
                                            assignmentClass.stations, cargoOrigin.getText(), cargoDestination.getText());
                                }

                            } else {
                                processState = false;
                                alert("Excel File is open. Please close it first.");
                            }
                        }
                        System.gc();
                        return null;
                    }
                };
            }
        };

        startProcess.setOnAction(event -> {
            if (!process.isRunning()) {
                process.reset();
                process.start();
            }
        });

        process.setOnFailed(e -> {
            alert("Process Failed\n----------------------------------------\n");
            openOutput.setDisable(true);
        });

        process.setOnSucceeded(e -> {
            if (!processState) {
                alert("Process Failed\n----------------------------------------\n");
                openOutput.setDisable(true);
            } else {
                alert("Process complete\n----------------------------------------\n");
                openOutput.setDisable(false);
            }
        });

        process.setOnRunning(eve -> {
            alert("Wait please ---- I'm working...");
            openOutput.setDisable(true);
        });
    }

    private void configureButtons() {
        selectDirectory.setOnAction(arg0 -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            //read path from last chosen directory in histoy
            String path = "";
            try {
                Scanner scanner = new Scanner(new File("lib/bin"));
                for (String line; scanner.hasNextLine() && (line = scanner.nextLine()) != null; ) {
                    path = line;
                }

                File file = new File(path);
                if (!file.exists() && !file.isDirectory())
                    path = System.getProperty("user.home");

                directoryChooser.setInitialDirectory(new File(path));
            } catch (FileNotFoundException e) {
                alert("problem with path file history");
            }
            try {
                File dir = directoryChooser.showDialog(primaryStage.getScene().getWindow());
                outPutDirectory = dir.getAbsolutePath();
                BufferedWriter bufwriter = new BufferedWriter(new FileWriter("lib/bin"));
                bufwriter.write(outPutDirectory);
                bufwriter.close();
            } catch (NullPointerException e) {
                alert("No directory  Selected");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (outPutDirectory != null) {
                ((SimpleBooleanProperty) directorySelected).set(true);
            }
        });

        exit.setOnAction(event -> onExit());

        openOutput.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(new File(outPutDirectory + outPutFileName));
            } catch (IOException e) {
                alert("There is a problem with output file");
            }
        });

        suggestions.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://mail.google.com/mail/?view=" +
                        "cm&fs=1&to=mehdimonemi70@gmail.com&su=trafficAssignment&body="));
            } catch (URISyntaxException | IOException e) {
                alert("Can't send email");
            }
        });

        ToggleGroup radioGroup = new ToggleGroup();
        restrictedAssignment.setToggleGroup(radioGroup);
        fullAssignment.setToggleGroup(radioGroup);
    }

    private void checkCommodities() {
        //blow is our commoditiesCheck result:
        commodityCheckResult[0][0] = "0";//0 means its first run and we should read data. also means app had read all names
        commodityCheckResult[0][1] = "";//correct origin name that we get it from correct name dialog
        commodityCheckResult[0][2] = "";//correct destination name that we get it from correct name dialog
        commodityCheckResult[0][3] = "";//original origin name that might be incorrect
        commodityCheckResult[0][4] = "";//original destination name that might be incorrect
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
        commoditiesCheck.setOnFailed(event -> loadDialog(commodityCheckResult[0]));
        commoditiesCheck.setOnSucceeded(event -> ((SimpleBooleanProperty) analyseInputs).set(true));

        analyse.setOnAction(event -> {
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });
    }

    private void manageListeners() {
        //A listener for periodField which only numbers characters are allowed
        periodField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                periodField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        /*
        StartProcess button is conditional and for not being disable 3 conditions should be meet (startProcessConditions):
        1-checkboxes selected or cargoForODTab selected
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

        //a listener to add or remove the status of checkBox to the start process conditions
        checkBoxSelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(checkBoxSelected);
            } else {
                startProcessConditions.remove(checkBoxSelected);
            }
        });

        //a listener to add or remove the status of directory to the start process conditions
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


        //a listener to add or remove the status of input analyse to the start process conditions
        analyseInputs.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(analyseInputs);
            } else {
                startProcessConditions.remove(analyseInputs);
            }
        });

        //Check weather CargoForODTab selected or not
        cargoForODTab.selectedProperty().addListener((obs, isFalse, isTrue) ->
                ((SimpleBooleanProperty) checkBoxSelected).set(isTrue));

        //a listener to add or remove the status of cargoTab to the start process conditions
        cargoTabSelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(cargoTabSelected);
            } else startProcessConditions.remove(cargoTabSelected);
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
        textArea.appendText("Something Wrong!" + "\n");
        textArea.appendText(massage + "\n");
        textArea.appendText(exception.getMessage() + "\n");
    }

    public void alert(String s) {
        textArea.appendText(s + "\n");
    }

    public void loadDialog(String[] oldResult) {
        correctNameDialog = new Dialog<Boolean>();
        correctNameDialog.initOwner(primaryStage.getScene().getWindow());
        correctNameDialog.setTitle("wrong names for commodity " + oldResult[0] + ": ");

        //dummy button for working X(close)
        correctNameDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = correctNameDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        correctNameDialog.initModality(Modality.APPLICATION_MODAL);
        dialogController.dialogOrigin.setText(oldResult[1]);
        dialogController.dialogDestination.setText(oldResult[2]);
        correctNameDialog.setOnCloseRequest(Event -> {
            dialogController.onStop();
            if (!mainController.commoditiesCheck.isRunning()) {
                mainController.alert("Correcting name process: stopped");
            }
        });
        correctNameDialog.getDialogPane().setContent(dialogController.dialogPan);
        correctNameDialog.show();
    }

    private void onExit() {
        File folder1 = new File(".\\temp\\");
        File[] listOfFiles1 = folder1.listFiles();
        if (listOfFiles1 != null) {
            for (File value : listOfFiles1) {
                File file = new File(".\\temp\\" + value.getName());
                file.delete();
            }
        }
        System.exit(0);
    }

}
