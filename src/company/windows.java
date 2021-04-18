package company;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.sun.javafx.application.PlatformImpl;
import company.Outputs.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;


public class windows extends Application {

    company.Assignment assignment = new Assignment();
    public static Map<String, String> appVersion = new HashMap<>();

    Service commoditiesCheck;
    final String[][] result = {new String[5]};//these string is for names check result. we will pass it throw many classes

    static TextArea textArea;

    String outPutDirectory = null;
    String outPutFileName = null;
    //check box for each output
    CheckBox Paths = new CheckBox("Paths");
    CheckBox Assignment = new CheckBox("Assignment");
    CheckBox Summery = new CheckBox("Summery");
    CheckBox CargoType = new CheckBox("CargoType");
    CheckBox CargoSummery = new CheckBox("CargoSummery");
    CheckBox Districts = new CheckBox("Districts");
    CheckBox LoadUnloadOD = new CheckBox("LoadUnloadOD");

    public Button selectDirectory = new Button("Select Output Directory");
    Button startProcess = new Button("Start Process");
    Button exit = new Button("Exit");
    Button openOutput = new Button("Open Output File");
    Button suggestions = new Button("Send me Suggestions");
    Button analyse = new Button("analyse & read Commodities");
    RadioButton fullAssignment = new RadioButton("Full Assignment");
    RadioButton restrictedAssignment = new RadioButton("Restricted Assignment");
    ToggleGroup assignmentChose = new ToggleGroup();

    private final ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private final ObservableSet<CheckBox> unselectedCheckBoxes = FXCollections.observableSet();
    private final IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);

    private final ObservableSet<ObservableBooleanValue> startProcessConditions = FXCollections.observableSet();
    private final IntegerBinding startProcessStatus = Bindings.size(startProcessConditions);
    ObservableBooleanValue checkBoxSelected = new SimpleBooleanProperty(false);
    ObservableBooleanValue directorySelected = new SimpleBooleanProperty(false);
    ObservableBooleanValue analyseInputs = new SimpleBooleanProperty(false);

    private final int maxNumSelected = 3;
    public FileOutputStream fileOut = null;

    Boolean processState = true;

    public static void alert(String massage, Exception exception) {
        PlatformImpl.runAndWait(() -> {
            textArea.appendText("Something Wrong!" + "\n");
            textArea.appendText(massage + "\n");
            textArea.appendText(exception.getMessage() + "\n");
        });
    }

    public static void alert(String s) {
        PlatformImpl.runAndWait(() -> {
            textArea.appendText(s + "\n");
        });
    }

    private void configureCheckBox(CheckBox checkBox) {

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

    public void loginDialog(Stage owner) {
        Dialog myDialog = new Dialog();
        myDialog.initOwner(owner);
        myDialog.setTitle("Log in");


        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(5);
        gridpane.setVgap(5);
        gridpane.setMinSize(100, 100);

        Label usernameLbl = new Label("User Name: ");
        gridpane.add(usernameLbl, 0, 1);

        Label passwordLbl = new Label("Password: ");
        gridpane.add(passwordLbl, 0, 2);
        final TextField username = new TextField("Admin");
        gridpane.add(username, 1, 1);

        final PasswordField password = new PasswordField();
        password.setText("password");
        gridpane.add(password, 1, 2);
        Label respawn = new Label("");
        gridpane.add(respawn, 0, 3);
        GridPane.setColumnSpan(respawn, 2);

        Button login = new Button("Log in");
        Button close = new Button("Exit");
        HBox hbox = new HBox(login, close);
        hbox.setSpacing(10);
        gridpane.add(hbox, 0, 4);
        GridPane.setColumnSpan(hbox, 2);
        hbox.setAlignment(Pos.CENTER_RIGHT);

        myDialog.getDialogPane().setContent(gridpane);

        close.setOnAction(event -> {
            login.getScene().getWindow().hide();
            System.exit(0);
        });

        myDialog.show();
        login.setOnAction(event -> loginAction(username, password, respawn, owner, login));


        gridpane.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                login.fire();
                ev.consume();
            }
        });
    }

    public static boolean needUpdate(String username, String password) {

        boolean result = false;
        try {
            String url = "smb://abbfile01/Department of Abbasabad/Seir & Harekat/Monemi_M/TAST/";
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "Railways\\" + username, password);
            SmbFile dir = new SmbFile(url + "lib/", auth);
            jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords", "false");
            for (SmbFile f : dir.listFiles()) {

                if (f.getName().equals("version")) {

                    if (!(new File(".\\temp\\lib")).exists()) {
                        (new File(".\\temp\\lib")).mkdir();
                        Thread.sleep(2000);
                    }
                    copyFileToLocal(f, ".\\temp\\");
                    Scanner newVersion = new Scanner(new File(".\\temp\\lib\\" + f.getName()));

                    for (String line; newVersion.hasNextLine() && (line = newVersion.nextLine()) != null; ) {
                        String a = line.substring(0, line.indexOf("=") - 1);
                        String b = line.substring(line.indexOf("=") + 2);
                        if (appVersion.containsKey(a)) {
                            if (Double.parseDouble(appVersion.get(a)) < Double.parseDouble(b)) {
                                if (a.contains("/")) {
                                    copyFileToLocal(new SmbFile(url + "/lib/" + a.substring(1), auth), ".\\temp\\");
                                } else if (a.equals("updater.jar")) {
                                    copyFileToLocal(new SmbFile(url + a, auth), ".\\");
                                } else
                                    copyFileToLocal(new SmbFile(url + a, auth), ".\\temp\\");
                                result = true;
                            }
                        } else {
                            copyFileToLocal(new SmbFile(url + a, auth), ".\\temp\\");
                            result = true;
                        }
                    }
                }
            }

            return result;
        } catch (MalformedURLException | FileNotFoundException | InterruptedException | SmbException e) {
            alert(e.getMessage());
            return result;
        }
    }

    public void loginAction(TextField username, PasswordField password, Label respawn, Stage owner, Button login) {
        jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords", "false");
        String url = "smb://abbfile01/Department of Abbasabad/Seir & Harekat/Monemi_M/TAST/";
        NtlmPasswordAuthentication auth = new
                NtlmPasswordAuthentication(null, "Railways\\" + username.getText(), password.getText());
        try {
            SmbFile dir = new SmbFile(url, auth);
            SmbFile f = dir.listFiles()[0];
            login.getScene().getWindow().hide();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("You have been logged in");
            alert.setContentText("Welcome " + username.getText());
            alert.initOwner(owner);
            alert.showAndWait();
            //now we check for new updates
            readVersion();
            if (needUpdate(username.getText(), password.getText())) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Look, a new version available");
                alert.setContentText("Want to update?");
                alert.initOwner(owner);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM TAST.exe");
                        Runtime.getRuntime().exec("java -jar updater.jar");
                    } catch (IOException e) {
                        alert(e.getMessage());
                    }
                }
            }

            respawn.setText("Invalid user name and password");
        } catch (MalformedURLException | SmbException e) {
            respawn.setText("wrong username or password");
        }
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

    public void correctNamesDialog(Stage owner, String[] oldResult) {
        Dialog myDialog = new Dialog();
        myDialog.initOwner(owner);
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

            result[0][1] = textField1.getText();
            result[0][2] = textField2.getText();
            login.getScene().getWindow().hide();
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });

        myDialog.getDialogPane().setContent(gridpane);
        myDialog.show();
    }

    private void readVersion() {
        try {
            Scanner versionFile = new Scanner(new File(".\\lib\\version"));
            for (String line; versionFile.hasNextLine() && (line = versionFile.nextLine()) != null; ) {
                String a = line.substring(0, line.indexOf("=") - 1);
                String b = line.substring(line.indexOf("=") + 2);
                appVersion.put(a, b);
            }
        } catch (FileNotFoundException e) {
            alert(e.getMessage());
        }
    }

    public static void copyFileToLocal(SmbFile file, String destinationFolder) {
        InputStream in = null;
        OutputStream out = null;
        File child = null;
        try {
            if (file.getPath().contains("/lib/"))
                child = new File(destinationFolder + "lib\\" + file.getName());
            else
                child = new File(destinationFolder + file.getName());

            in = new BufferedInputStream(new SmbFileInputStream(file));
            out = new BufferedOutputStream(new FileOutputStream(child));

            byte[] buffer = new byte[600072000];
            int len; //Read length
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush(); //The refresh buffer output stream
            out.close(); //The refresh buffer output stream
        } catch (Exception e) {
            String msg = "The error occurred: " + e.getLocalizedMessage();
            alert(msg);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        VBox tab1Contents = new VBox();
        tab1Contents.setMinSize(400, 200);
        tab1Contents.setSpacing(20);
        tab1Contents.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Hi there\nWelcome to Railway Network Assignment App\nclick on button to start the Process");
        welcomeLabel.setTextAlignment(TextAlignment.CENTER);

        Label periodLabel = new Label("Planing Period in day");
        TextField periodField = new TextField();
        periodField.setPromptText("365");
        periodField.setAlignment(Pos.CENTER);
        periodField.setMaxWidth(50);

        periodField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    periodField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        textArea = new TextArea();
        textArea.autosize();
        textArea.setEditable(false);


        //we use a vbox for check boxes and description text for them
        //to be in less gap between them

        VBox tab1VBox = new VBox();
        tab1VBox.setSpacing(10);
        Text text3 = new Text("Which output do u want?");
        text3.setTextAlignment(TextAlignment.CENTER);

        //two horizontal row for check boxes
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        hBox1.getChildren().addAll(Paths, Assignment, Summery);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(5);
        hBox2.getChildren().addAll(CargoType, CargoSummery, Districts, LoadUnloadOD);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(5);

        tab1VBox.getChildren().addAll(text3, hBox1, hBox2);
        tab1VBox.setAlignment(Pos.CENTER);

        //we should configure each check box to know if its check or not
        configureCheckBox(Paths);
        configureCheckBox(Assignment);
        configureCheckBox(Summery);
        configureCheckBox(CargoType);
        configureCheckBox(CargoSummery);
        configureCheckBox(Districts);
        configureCheckBox(LoadUnloadOD);


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

        checkBoxSelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(checkBoxSelected);
            } else {
                startProcessConditions.remove(checkBoxSelected);
            }
        });

        directorySelected.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(directorySelected);
                analyse.setDisable(false);
            } else {
                startProcessConditions.remove(directorySelected);
                analyse.setDisable(true);
            }
        });

        analyseInputs.addListener((obs, isFalse, isTrue) -> {
            if (isTrue) {
                startProcessConditions.add(analyseInputs);
            } else {
                startProcessConditions.remove(analyseInputs);
            }
        });

        startProcessStatus.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            startProcess.setDisable(newSelectedCount.intValue() != 3);
            System.gc();
        });

        //button default state is disable
        startProcess.setDisable(true);
        analyse.setDisable(true);

        //blow is our commoditiesCheck result:
        //excel row id
        //origin
        //destination

        result[0][0] = "0";//0 means its first run and we should read data. also means app had read all names
        result[0][1] = "";
        result[0][2] = "";
        result[0][3] = "";
        result[0][4] = "";
        commoditiesCheck = new Service() {
            @Override
            public Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {

                        if (isFileClose(outPutDirectory + "/Data.xlsx")) {
                            if (result[0][0].equals("0")) {
                                assignment.readData(outPutDirectory);
                                result[0][0] = "1";//change to first row of commodities excel file
                            }

                            result[0] = assignment.manageNames(outPutDirectory, result[0]);
                            if (Integer.parseInt(result[0][0]) > 0)
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
        commoditiesCheck.setOnFailed(event -> correctNamesDialog(primaryStage, result[0]));
        commoditiesCheck.setOnSucceeded(event -> ((SimpleBooleanProperty) analyseInputs).set(true));

        analyse.setOnAction(event -> {
            if (!commoditiesCheck.isRunning()) {
                commoditiesCheck.reset();
                commoditiesCheck.start();
            }
        });

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

                            assignment.main(outPutDirectory, fullAssignment.isSelected());

                            fileOut = new FileOutputStream(outPutDirectory + outPutFileName);
                            fileOut.flush();
                            fileOut.close();

                            if (Paths.isSelected()) {
                                new OutputPaths(outPutDirectory + outPutFileName, assignment.commodities);
                            }
                            if (Assignment.isSelected()) {
                                new OutputAssignment(outPutDirectory + outPutFileName,
                                        outPutDirectory + "/Data.xlsx", assignment.outputBlocks, assignment.blocks);
                            }
                            if (Summery.isSelected()) {
                                new OutputSummery(outPutDirectory + outPutFileName, assignment.commodities);
                            }
                            if (CargoType.isSelected()) {
                                new OutputCargoType(outPutDirectory + outPutFileName,
                                        assignment.commodities, assignment.mainCargoTypes, assignment.wagons);
                            }
                            if (CargoSummery.isSelected()) {
                                new OutputCargoSummery(outPutDirectory + outPutFileName,
                                        assignment.commodities, assignment.mainCargoTypes, assignment.wagons);
                            }
                            if (Districts.isSelected()) {
                                new OutputDistricts(outPutDirectory + outPutFileName,
                                        assignment.districts, assignment.commodities, periodField.getText());
                            }
                            if (LoadUnloadOD.isSelected()) {
                                new OutputLoadUnloadOD(
                                        outPutDirectory + outPutFileName, assignment.districts, assignment.commodities);
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
                File dir = directoryChooser.showDialog(primaryStage);
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

        startProcess.setOnAction(event -> {
            if (!process.isRunning()) {
                process.reset();
                process.start();
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


        {
            GridPane.setHalignment(welcomeLabel, HPos.CENTER);
            tab1Contents.getChildren().add(welcomeLabel);

            tab1Contents.getChildren().add(tab1VBox);

            fullAssignment.setToggleGroup(assignmentChose);
            restrictedAssignment.setToggleGroup(assignmentChose);
            HBox hBox5 = new HBox(fullAssignment, restrictedAssignment);
            hBox5.setAlignment(Pos.CENTER);
            hBox5.setSpacing(20);
            tab1Contents.getChildren().add(hBox5);

            HBox hBox4 = new HBox(periodLabel, periodField);
            hBox4.setAlignment(Pos.CENTER);
            hBox4.setSpacing(20);
            tab1Contents.getChildren().add(hBox4);

            HBox hBox3 = new HBox(analyse, selectDirectory, startProcess);
            hBox3.setAlignment(Pos.CENTER);
            hBox3.setSpacing(20);
            tab1Contents.getChildren().add(hBox3);


            tab1Contents.getChildren().add(textArea);

            HBox lastRow = new HBox();
            lastRow.setAlignment(Pos.CENTER);
            lastRow.setSpacing(20);

            tab1Contents.getChildren().add(lastRow);
            openOutput.setDisable(true);
            lastRow.getChildren().addAll(exit, openOutput, suggestions);

            BorderPane bp = new BorderPane();
            bp.setCenter(tab1Contents);

            Scene scene = new Scene(bp, 500, 550);

            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/company/Logo1.png")));
            primaryStage.setTitle("TAST");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> onExit());

//            loginDialog(primaryStage);
            //check and do update
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

