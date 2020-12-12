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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
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

    Main main = new Main();
    public static Map<String, String> appVersion = new HashMap<>();

    Service commoditiesCheck;
    final String[][] result = {new String[5]};//these string is for names check result. we will pass it throw many classes

    static TextArea textArea;

    String outPutDirectory = null;
    String outPutFileName = null;
    //check box for each output
    CheckBox checkBox1 = new CheckBox("Paths");
    CheckBox checkBox2 = new CheckBox("Assignment");
    CheckBox checkBox3 = new CheckBox("Summery");
    CheckBox checkBox4 = new CheckBox("CargoType");
    CheckBox checkBox5 = new CheckBox("CargoSummery");
    CheckBox checkBox6 = new CheckBox("Districts");
    CheckBox checkBox7 = new CheckBox("LoadUnloadOD");

    public Button button1 = new Button("Select Output Directory");
    Button button2 = new Button("Start Process");
    Button button3 = new Button("Exit");
    Button button4 = new Button("Open Output File");
    Button button5 = new Button("Send me Suggestions");
    Button button6 = new Button("analyse & read Commodities");
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

    public static void main(String[] args) {
        launch(args);
    }

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

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(400, 200);
        gridPane.setVgap(20);
        gridPane.setHgap(30);
        gridPane.setAlignment(Pos.CENTER);

        Text text1 = new Text("Hi there\nWelcome to Railway Network Assignment App\nclick on button to start the Process");
        text1.setTextAlignment(TextAlignment.CENTER);

        Text text2 = new Text("Planing Period in day");
        TextField textField1 = new TextField();
        textField1.setPromptText("365");
        textField1.setAlignment(Pos.CENTER);
        textField1.setMaxWidth(50);

        textField1.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField1.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
//        textField1.getParent().requestFocus();

        textArea = new TextArea();
        textArea.autosize();
        textArea.setEditable(false);


        //we use a vbox for check boxes and description text for them
        //to be in less gap between them
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        Text text3 = new Text("Which output do u want?");
        text3.setTextAlignment(TextAlignment.CENTER);

        //two horizontal row for check boxes
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        hBox1.getChildren().addAll(checkBox1, checkBox2, checkBox3);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(5);
        hBox2.getChildren().addAll(checkBox4, checkBox5, checkBox6, checkBox7);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(5);

        vBox.getChildren().addAll(text3, hBox1, hBox2);
        vBox.setAlignment(Pos.CENTER);

        //we should configure each check box to know if its check or not
        configureCheckBox(checkBox1);
        configureCheckBox(checkBox2);
        configureCheckBox(checkBox3);
        configureCheckBox(checkBox4);
        configureCheckBox(checkBox5);
        configureCheckBox(checkBox6);
        configureCheckBox(checkBox7);


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
                button6.setDisable(false);
            } else {
                startProcessConditions.remove(directorySelected);
                button6.setDisable(true);
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
            button2.setDisable(newSelectedCount.intValue() != 3);
            System.gc();
        });

        //button default state is disable
        button2.setDisable(true);
        button6.setDisable(true);

        //blow is our commoditiesCheck result
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
                                main.readData(outPutDirectory);
                                result[0][0] = "1";//change to first row of commodities excel file
                            }

                            result[0] = main.manageNames(outPutDirectory, result[0]);
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

        button6.setOnAction(event -> {
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

                            main.main(outPutDirectory, fullAssignment.isSelected());

                            fileOut = new FileOutputStream(outPutDirectory + outPutFileName);
                            fileOut.flush();
                            fileOut.close();

                            if (checkBox1.isSelected()) {
                                new ODFreight(outPutDirectory + outPutFileName, main.blocks, main.commodities, main.pathExceptions, main.stations);
                            }
                            if (checkBox2.isSelected()) {
                                new OutputAssignment(outPutDirectory + outPutFileName,
                                        outPutDirectory + "/Data.xlsx", main.outputBlocks, main.blocks);
                            }
                            if (checkBox3.isSelected()) {
                                new OutputSummery(outPutDirectory + outPutFileName, main.commodities);
                            }
                            if (checkBox4.isSelected()) {
                                new OutputCargoType(outPutDirectory + outPutFileName,
                                        main.commodities, main.mainCargoTypes, main.wagons);
                            }
                            if (checkBox5.isSelected()) {
                                new OutputCargoSummery(outPutDirectory + outPutFileName,
                                        main.commodities, main.mainCargoTypes, main.wagons);
                            }
                            if (checkBox6.isSelected()) {
                                new OutputDistricts(outPutDirectory + outPutFileName,
                                        main.districts, main.commodities, textField1.getText());
                            }
                            if (checkBox7.isSelected()) {
                                new OutputLoadUnloadOD(
                                        outPutDirectory + outPutFileName, main.districts, main.commodities);
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
            button4.setDisable(true);
        });

        process.setOnSucceeded(e -> {
            if (!processState) {
                alert("Process Failed\n----------------------------------------\n");
                button4.setDisable(true);
            } else {
                alert("Process complete\n----------------------------------------\n");
                button4.setDisable(false);
            }
        });

        process.setOnRunning(eve -> {
            alert("Wait please ---- I'm working...");
            button4.setDisable(true);
        });

        button1.setOnAction(arg0 -> {
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

        button2.setOnAction(event -> {
            if (!process.isRunning()) {
                process.reset();
                process.start();
            }
        });

        button3.setOnAction(event -> onExit());

        button4.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(new File(outPutDirectory + outPutFileName));
            } catch (IOException e) {
                alert("There is a problem with output file");
            }
        });

        button5.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://mail.google.com/mail/?view=" +
                        "cm&fs=1&to=mehdimonemi70@gmail.com&su=trafficAssignment&body="));
            } catch (URISyntaxException | IOException e) {
                alert("Can't send email");
            }
        });


        {
            GridPane.setHalignment(text1, HPos.CENTER);
            gridPane.add(text1, 0, 0);

            gridPane.add(vBox, 0, 1);

            fullAssignment.setToggleGroup(assignmentChose);
            restrictedAssignment.setToggleGroup(assignmentChose);
            HBox hBox5 = new HBox(fullAssignment, restrictedAssignment);
            hBox5.setAlignment(Pos.CENTER);
            hBox5.setSpacing(20);
            GridPane.setHalignment(hBox5, HPos.CENTER);
            gridPane.add(hBox5, 0, 2);

            HBox hBox4 = new HBox(text2, textField1);
            hBox4.setAlignment(Pos.CENTER);
            hBox4.setSpacing(20);
            GridPane.setHalignment(hBox4, HPos.CENTER);
            gridPane.add(hBox4, 0, 3);

            HBox hBox3 = new HBox(button6, button1, button2);
            hBox3.setAlignment(Pos.CENTER);
            hBox3.setSpacing(20);
            GridPane.setHalignment(hBox3, HPos.CENTER);
            gridPane.add(hBox3, 0, 4);


            GridPane.setHalignment(textArea, HPos.CENTER);
            gridPane.add(textArea, 0, 5);

            HBox lastRow = new HBox();
            lastRow.setAlignment(Pos.CENTER);
            lastRow.setSpacing(20);

            GridPane.setHalignment(lastRow, HPos.CENTER);
            gridPane.add(lastRow, 0, 6);
            button4.setDisable(true);
            lastRow.getChildren().addAll(button3, button4, button5);

            BorderPane bp = new BorderPane();
            bp.setCenter(gridPane);

            Scene scene = new Scene(bp, 500, 550);

            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/company/Logo1.png")));
            primaryStage.setTitle("TAST");
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> onExit());

            loginDialog(primaryStage);
            //check and do update
        }
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

    public static boolean needUpdate(String username, String password) {

        boolean result = false;
        try {
            String url = "smb://abbfile01/Department of Abbasabad/Seir & Harekat/Monemi_M/TAST/";
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "Railways\\" + username, password);
            SmbFile dir = new SmbFile(url+"lib/", auth);
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
}

