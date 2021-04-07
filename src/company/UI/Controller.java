package company.UI;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.FileOutputStream;

public class Controller {
    @FXML
    private Label welcomeLabel, periodLabel;
    @FXML
    private JFXCheckBox Path, assignment, summery, cargoType, cargoSummery, districts, loadUnloadOD;
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
    public void initialize() {
        //we should configure each check box to know if its check or not
        configureCheckBox(Path);
        configureCheckBox(assignment);
        configureCheckBox(summery);
        configureCheckBox(cargoType);
        configureCheckBox(cargoSummery);
        configureCheckBox(districts);
        configureCheckBox(loadUnloadOD);

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

    private void configureCheckBox(JFXCheckBox checkBox) {
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
}
