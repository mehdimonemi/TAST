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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import static company.App.nameController;

public class MainController {
    @FXML
    VBox primaryStage, dialogPan;
    @FXML
    private Label welcomeLabel, periodLabel;
    @FXML
    private JFXCheckBox pathCheckBox, assignmentCheckBox, summeryCheckBox,
            cargoTypeCheckBox, cargoSummeryCheckBox, districtsCheckBox, loadUnloadODCheckBox;
    @FXML
    private JFXRadioButton fullAssignment, restrictedAssignment;

    @FXML
    private JFXTextField periodField;
    @FXML
    private JFXButton selectDirectory, analyse, startProcess, exit, openOutput, suggestions;
    @FXML
    private Tab assignmentTab, cargoForODTab;
    @FXML
    JFXTextArea textArea;

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

                        ULocale locale = new ULocale("en_US@calendar=persian");

                        Calendar calendar = Calendar.getInstance(locale);
                        SimpleDateFormat df = new SimpleDateFormat("MM.dd - EEE, MMM - HH.mm.ss", locale);

                        outPutFileName = "./Output " + df.format(calendar) + ".xlsx";

                        processState = true;
                        if (isFileClose(outPutDirectory + outPutFileName)) {

                            assignmentClass.main(outPutDirectory, fullAssignment.isSelected());

                            fileOut = new FileOutputStream(outPutDirectory + outPutFileName);
                            fileOut.flush();
                            fileOut.close();

                            if (pathCheckBox.isSelected()) {
                                new OutputPaths(outPutDirectory + outPutFileName, assignmentClass.commodities);
                            }
                            if (assignmentCheckBox.isSelected()) {
                                new OutputAssignment(outPutDirectory + outPutFileName,
                                        outPutDirectory + "/Data.xlsx", assignmentClass.outputBlocks, assignmentClass.blocks);
                            }
                            if (summeryCheckBox.isSelected()) {
                                new OutputSummery(outPutDirectory + outPutFileName, assignmentClass.commodities);
                            }
                            if (cargoTypeCheckBox.isSelected()) {
                                new OutputCargoType(outPutDirectory + outPutFileName,
                                        assignmentClass.commodities, assignmentClass.mainCargoTypes, assignmentClass.wagons);
                            }
                            if (cargoSummeryCheckBox.isSelected()) {
                                new OutputCargoSummery(outPutDirectory + outPutFileName,
                                        assignmentClass.commodities, assignmentClass.mainCargoTypes, assignmentClass.wagons);
                            }
                            if (districtsCheckBox.isSelected()) {
                                new OutputDistricts(outPutDirectory + outPutFileName,
                                        assignmentClass.districts, assignmentClass.commodities, periodField.getText());
                            }
                            if (loadUnloadODCheckBox.isSelected()) {
                                new OutputLoadUnloadOD(
                                        outPutDirectory + outPutFileName, assignmentClass.districts, assignmentClass.commodities);
                            }
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
        textArea.appendText("Something Wrong!" + "\n");
        textArea.appendText(massage + "\n");
        textArea.appendText(exception.getMessage() + "\n");
    }

    public void alert(String s) {
        textArea.appendText(s + "\n");
    }

    public void correctNamesDialog(String[] oldResult) {

        Dialog myDialog = new Dialog();
        myDialog.initOwner(primaryStage.getScene().getWindow());
        myDialog.setTitle("wrong names for commodity " + oldResult[0] + ": ");

        nameController.dialogOrigin.setText(oldResult[1]);
        nameController.dialogDestination.setText(oldResult[2]);

        myDialog.getDialogPane().setContent(dialogPan);
        nameController.dialogOk.setOnAction(event -> {
            commodityCheckResult[0][1] = nameController.dialogOrigin.getText();
            commodityCheckResult[0][2] = nameController.dialogDestination.getText();
            nameController.dialogOk.getScene().getWindow().hide();
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });

        myDialog.show();
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
