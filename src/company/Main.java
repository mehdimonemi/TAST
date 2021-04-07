package company;

import company.Data.Block;
import company.Data.Commodity;
import company.Data.PathExceptions;
import company.Data.Station;
import company.Outputs.OutPut;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.CpxException;
import ilog.cplex.IloCplex;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import static company.windows.alert;

public class Main {
    static int stationA;
    static int stationB;
    public static XSSFRow row;

    ArrayList<Station> stations = null;
    ArrayList<Block> blocks = null;
    ArrayList<Block> outputBlocks = null;
    ArrayList<Commodity> commodities = null;
    PathExceptions pathExceptions = null;
    TreeSet<String> districts = null;
    HashSet<String> wagons = null;
    HashSet<String> mainCargoTypes = null;
    HashSet<String> cargoTypes = null;
    HashSet<String> transportKinds = null;

    public static XSSFCell cell;
    public static OutPut outPut = new OutPut();


    public void main(String outPutDirectory, boolean fullAssignmentSelected) {

        PrintStream logFile;

        try (PrintStream printStream = logFile = new PrintStream(new FileOutputStream("log.txt"))) {
            System.setOut(logFile);

            //before start silving models we should sure the commodities are emptry from previews result
            resetCommoditiesResult();

            solveModel();
            if (fullAssignmentSelected)
                barGozari();
            else
                multiCommodity();

        } catch (FileNotFoundException e) {
            alert(e.getMessage());
        } catch (NullPointerException e) {
            alert(e.getMessage());
        }
    }

    private void resetCommoditiesResult() {
        for (Commodity commodity : commodities) {

            commodity.setHowMuchIsAllowed(1);
            commodity.setDistance(0);
            commodity.setTonKilometerOperation(0);
            commodity.setTonKilometerPlan(0);
            commodity.setBlocks(new ArrayList<>());
        }

        for (Block block : blocks) {

            block.setDemandWentPlanTon(0);
            block.setDemandWentOperationTon(0);
            block.setDemandWentPlanWagon(0);
            block.setDemandWentOperatoinWagon(0);
            block.setDemandBackPlanTon(0);
            block.setDemandBackOperationTon(0);
            block.setDemandBackPlanWagon(0);
            block.setDemandBackOperationWagon(0);

            block.setDemandWentPlanTonKilometer(0);
            block.setDemandBackPlanTonKilometer(0);
            block.setAverageMovingDistance(0);
        }
    }

    public void barGozari() {

        for (int i = 0; i < blocks.size(); i++) {
            for (Commodity commodity : commodities) {
                if (commodity.hasBlock(blocks.get(i))) {
                    if (blocks.get(i).getDirection() == 1) {
                        blocks.get(i).setDemandWentPlanTon(blocks.get(i).getDemandWentPlanTon() + commodity.getPlanTon());
                        blocks.get(i).setDemandWentOperationTon(blocks.get(i).getDemandWentOperationTon() + commodity.getOperationTon());
                        blocks.get(i).setDemandWentPlanWagon(blocks.get(i).getDemandWentPlanWagon() + commodity.getPlanWagon());
                        blocks.get(i).setDemandWentOperatoinWagon(blocks.get(i).getDemandWentOperatoinWagon() + commodity.getOperationWagon());

                        blocks.get(i).setDemandWentPlanTonKilometer(blocks.get(i).getDemandWentPlanTonKilometer() + commodity.getTonKilometerPlan());
                    } else {
                        blocks.get(i - 1).setDemandBackPlanTon(blocks.get(i - 1).getDemandBackPlanTon() + commodity.getPlanTon());
                        blocks.get(i - 1).setDemandBackOperationTon(blocks.get(i - 1).getDemandBackOperationTon() + commodity.getOperationTon());
                        blocks.get(i - 1).setDemandBackPlanWagon(blocks.get(i - 1).getDemandBackPlanWagon() + commodity.getPlanWagon());
                        blocks.get(i - 1).setDemandBackOperationWagon(blocks.get(i - 1).getDemandBackOperationWagon() + commodity.getOperationWagon());

                        blocks.get(i - 1).setDemandBackPlanTonKilometer(blocks.get(i - 1).getDemandBackPlanTonKilometer() + commodity.getTonKilometerPlan());
                    }
                }
            }
        }
    }

    public boolean solveModel() {
        try {
            IloCplex model = new IloCplex();
            IloNumVar[] X = new IloNumVar[blocks.size()];
            IloNumExpr goalFunction;
            IloNumExpr constraint;

            for (Commodity value : commodities) {
                stationA = value.getOriginId();
                stationB = value.getDestinationId();
                Commodity commodity = value;
                String a = commodity.getOrigin();
                String b = commodity.getDestination();
                if (a.equals(b) && !a.contains("سایر")) {
                    alert("same OD for " + commodity);
                    continue;
                }

                //start solving model for the commodity

                //masirhai estesna baiad az masir khas beran
                int temp = pathExceptions.isException(a, b);
                if (temp == 1 || temp == 2) {
                    for (int j = 0; j < blocks.size(); j++) {
                        boolean flag = true;
                        for (int i = (temp - 1); i < pathExceptions.getBlocksMustbe().size(); ) {
                            if (blocks.get(j).equals(pathExceptions.getBlocksMustbe().get(i))) {
                                X[j] = model.numVar(1, 1, IloNumVarType.Int);
                                flag = false;
                            } else if ((a.equals("ری") && !b.equals("تهران")) && (blocks.get(j).getOrigin().equals("ری") && blocks.get(j).getDestination().equals("بهرام"))) {
                                X[j] = model.numVar(1, 1, IloNumVarType.Int);
                                flag = false;
                            }
                            i += 2;
                        }
                        if (flag) {
                            X[j] = model.numVar(0, 1, IloNumVarType.Int);
                        }
                    }
                } else if (a.equals("ری") && !b.equals("تهران")) {
                    for (int j = 0; j < blocks.size(); j++) {
                        if (blocks.get(j).getOrigin().equals("ری") && blocks.get(j).getDestination().equals("بهرام")) {
                            X[j] = model.numVar(1, 1, IloNumVarType.Int);
                        } else {
                            X[j] = model.numVar(0, 1, IloNumVarType.Int);
                        }
                    }
                } else if (b.equals("ری") && !a.equals("تهران")) {
                    for (int j = 0; j < blocks.size(); j++) {
                        if (blocks.get(j).getOrigin().equals("بهرام") && blocks.get(j).getDestination().equals("ری")) {
                            X[j] = model.numVar(1, 1, IloNumVarType.Int);
                        } else {
                            X[j] = model.numVar(0, 1, IloNumVarType.Int);
                        }
                    }
                } else {
                    for (int i = 0; i < blocks.size(); i++) {
                        X[i] = model.numVar(0, 1, IloNumVarType.Int);
                    }
                }

                goalFunction = model.constant(0);
                for (int i = 0; i < blocks.size(); i++) {
                    goalFunction = model.sum(goalFunction, model.prod(X[i], blocks.get(i).getLength()));
                }
                model.addMinimize(goalFunction);

                // constraints
                for (int i = 0; i < stations.size(); i++) {
                    constraint = model.constant(0);
                    if (stations.get(i).getId() == stationA) {
                        for (int j = 0; j < blocks.size(); j++) {
                            if (stationA == blocks.get(j).getOriginId()) {
                                constraint = model.sum(constraint, X[j]);
                            }
                            if (stationA == blocks.get(j).getDestinationId()) {
                                constraint = model.sum(constraint, model.negative(X[j]));
                            }
                        }
                        model.addEq(constraint, 1);
                    } else if (stations.get(i).getId() == (stationB)) {
                        for (int j = 0; j < blocks.size(); j++) {
                            if (stationB == blocks.get(j).getOriginId()) {
                                constraint = model.sum(constraint, X[j]);
                            }
                            if (stationB == blocks.get(j).getDestinationId()) {
                                constraint = model.sum(constraint, model.negative(X[j]));
                            }
                        }
                        model.addEq(constraint, -1);
                    } else {
                        for (int j = 0; j < blocks.size(); j++) {
                            if (stations.get(i).getId() == (blocks.get(j).getOriginId())) {
                                constraint = model.sum(constraint, X[j]);
                            }
                            if (stations.get(i).getId() == (blocks.get(j).getDestinationId())) {
                                constraint = model.sum(constraint, model.negative(X[j]));
                            }
                        }
                        model.addEq(constraint, 0);
                    }
                } // end of constraints

                model.setOut(null);
                try {
                    if (model.solve()) {
                        commodity.setDistance(model.getObjValue());
                        commodity.setTonKilometerOperation(model.getObjValue() * commodity.getOperationTon());
                        commodity.setTonKilometerPlan(model.getObjValue() * commodity.getPlanTon());
                        for (int i = 0; i < blocks.size(); i++) {
                            if (model.getValue(X[i]) > 0.5) {
                                commodity.getBlocks().add(blocks.get(i));
                            }
                        }

                        //sort blocks
                        String tempOrigin = a;
                        ArrayList<Block> tempBlocks = new ArrayList<>();

                        while (!commodity.getBlocks().isEmpty()) {
                            for (Block block : commodity.getBlocks()) {
                                if (block.getOrigin().equals(tempOrigin)) {
                                    tempBlocks.add(block);
                                    tempOrigin = block.getDestination();
                                    commodity.getBlocks().remove(block);
                                    break;
                                }
                            }
                        }
                        commodity.setBlocks(tempBlocks);

                        model.clearModel();
                        for (int i = 0; i < blocks.size(); i++) {
                            if (X[i] != null) {
                                X[i] = null;
                            }
                        }

                        goalFunction = null;
                        constraint = null;
                    } else {
                        commodity.setDistance(150);
                        commodity.setTonKilometerOperation(150 * commodity.getOperationTon());
                        commodity.setTonKilometerPlan(150 * commodity.getPlanTon());
                        alert("No path for " + commodity);
                        model.clearModel();
                    }
                    if (commodity.getDistance() < 150) {
                        commodity.setDistance(150);
                        commodity.setTonKilometerOperation(150 * commodity.getOperationTon());
                        commodity.setTonKilometerPlan(150 * commodity.getPlanTon());
                        model.clearModel();
                    }
                } catch (CpxException e) {
                    alert(e.getMessage());
                }
            }
        } catch (
                IloException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void readData(String outPutDirectory) {

        stations = new ArrayList<>();
        blocks = new ArrayList<>();
        outputBlocks = new ArrayList<>();
        pathExceptions = new PathExceptions();
        districts = new TreeSet();
        wagons = new HashSet();
        mainCargoTypes = new HashSet();
        cargoTypes = new HashSet();
        transportKinds = new HashSet();
        commodities = new ArrayList<>();

        outPut.setMassageForReadingFile("Data");
        FileInputStream dataFile = null;
        XSSFWorkbook data = null;
        try {
            dataFile = new FileInputStream(outPutDirectory + "/Data.xlsx");
            data = new XSSFWorkbook(dataFile);
            // read stations data
            XSSFSheet sheet1 = data.getSheetAt(0);
            for (int i = 0; i < sheet1.getLastRowNum(); i++) {
                XSSFRow row = sheet1.getRow(i + 1);

                Station station = new Station((int) row.getCell(0).getNumericCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue());
                if (!row.getCell(2).getStringCellValue().equals("null"))
                    districts.add(row.getCell(2).getStringCellValue());

                for (int j = 4; j < row.getLastCellNum(); j++) {
                    station.getAlterNames().add(row.getCell(j).getStringCellValue());
                }
                stations.add(station);

            }

            // read blocks data
            XSSFSheet sheet2 = data.getSheetAt(1);
            for (int i = 0; i < sheet2.getLastRowNum(); i++) {
                XSSFRow row = sheet2.getRow(i + 1);
                //raft
                Block block1 = new Block((int) row.getCell(0).getNumericCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(),
                        (int) row.getCell(4).getNumericCellValue(),
                        (int) row.getCell(5).getNumericCellValue(),
                        1,
                        row.getCell(8).getNumericCellValue(),
                        stations);
                blocks.add(block1);

                //bargasht
                Block block2 = new Block((int) row.getCell(0).getNumericCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(3).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        (int) row.getCell(4).getNumericCellValue(),
                        (int) row.getCell(5).getNumericCellValue(),
                        2,
                        ((row.getCell(5).getNumericCellValue() == 2) ? row.getCell(10).getNumericCellValue() : 0),
                        stations);
                blocks.add(block2);
            }

            //read exceptions
            XSSFSheet sheet4 = data.getSheetAt(3);
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j < sheet4.getLastRowNum(); j++) {
                    XSSFRow row = sheet4.getRow(j + 1);
                    try {
                        if (row.getCell(i).getStringCellValue() != "") {
                            switch (i) {
                                case 0:
                                    pathExceptions.getOrigins().add(row.getCell(i).getStringCellValue());
                                    break;
                                case 1:
                                    pathExceptions.getDestinations().add(row.getCell(i).getStringCellValue());
                                    break;
                                case 2:
                                    for (Block block : blocks) {
                                        if ((block.getOrigin().equals(row.getCell(i).getStringCellValue()) &&
                                                block.getDestination().equals(row.getCell(i + 1).getStringCellValue()))
                                                || (block.getOrigin().equals(row.getCell(i + 1).getStringCellValue()) &&
                                                block.getDestination().equals(row.getCell(i).getStringCellValue()))) {

                                            pathExceptions.getBlocksMustbe().add(block);

                                        }
                                    }
                                    break;
                            }
                        }
                    } catch (IllegalStateException e) {
                        break;
                    } catch (NullPointerException e) {
                        break;
                    }

                }
            }

            XSSFSheet sheet5 = data.getSheetAt(4);
            for (int i = 0; i < sheet5.getLastRowNum(); i++) {
                XSSFRow row = sheet5.getRow(i + 1);
                Block outputBlock = new Block(row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(),
                        stations);
                outputBlocks.add(outputBlock);
            }

            outPut.successDisplay();

            stations.trimToSize();
            blocks.trimToSize();
            commodities.trimToSize();

        } catch (FileNotFoundException e) {
            outPut.failDisplay(e);
        } catch (IOException e) {
            outPut.failDisplay(e);
        } catch (NullPointerException e) {
            outPut.failDisplay(e);
        } catch (IllegalStateException e) {
            outPut.failDisplay(e);
        } finally {
            if (dataFile != null) {
                try {
                    dataFile.close();
                } catch (IOException e) {
                    outPut.failDisplay(e);
                }
            }
            if (data != null) {
                try {
                    data.close();
                } catch (IOException e) {
                    outPut.failDisplay(e);
                }
            }
        }
    }

    public String[] manageNames(String outPutDirectory, String[] result) {

        if (result[0].equals("-1")) {//if result[0] is 0 here,that means we are running this process on second or more time
            commodities = new ArrayList<>();
            result[0] = "1";
        }

        outPut.setMassageForAnalyseNames("origin and destination");
        FileInputStream dataFile = null;
        XSSFWorkbook data = null;
        try {
            dataFile = new FileInputStream(outPutDirectory + "/Data.xlsx");
            data = new XSSFWorkbook(dataFile);

            //read commodities data
            XSSFSheet sheet3 = data.getSheetAt(2);
            for (int i = Integer.valueOf(result[0]); i <= sheet3.getLastRowNum(); i++) {
                XSSFRow row = sheet3.getRow(i);
                if ((!result[1].equals("") || !result[2].equals("")) && i == Integer.valueOf(result[0])) {
                    if (nameIsNotOkay(result[1]) ||
                            nameIsNotOkay(result[2])) {
                        return result;
                    }
                } else if (nameIsNotOkay(row.getCell(1).getStringCellValue().trim()) ||
                        nameIsNotOkay(row.getCell(2).getStringCellValue().trim())) {
                    result[0] = String.valueOf(i);
                    result[1] = row.getCell(1).getStringCellValue().trim();
                    result[2] = row.getCell(2).getStringCellValue().trim();
                    result[3] = row.getCell(1).getStringCellValue().trim();
                    result[4] = row.getCell(2).getStringCellValue().trim();
                    return result;
                }

                if ((!result[1].equals("") || !result[2].equals("")) && i == Integer.valueOf(result[0])) {
                    Commodity commodity = new Commodity((int) row.getCell(0).getNumericCellValue(),
                            findName(result[1]),
                            findName(result[2]),
                            row.getCell(3).getNumericCellValue(),
                            row.getCell(4).getNumericCellValue(),
                            row.getCell(5).getNumericCellValue(),
                            row.getCell(6).getNumericCellValue(),
                            row.getCell(7).getStringCellValue(),
                            row.getCell(8).getStringCellValue(),
                            row.getCell(9).getStringCellValue(),
                            row.getCell(10).getStringCellValue(),
                            stations);
                    commodities.add(commodity);
                    updateAlterNames(result, data, outPutDirectory + "/Data.xlsx");
                } else {
                    Commodity commodity = new Commodity((int) row.getCell(0).getNumericCellValue(),
                            findName(row.getCell(1).getStringCellValue().trim()),
                            findName(row.getCell(2).getStringCellValue().trim()),
                            row.getCell(3).getNumericCellValue(),
                            row.getCell(4).getNumericCellValue(),
                            row.getCell(5).getNumericCellValue(),
                            row.getCell(6).getNumericCellValue(),
                            row.getCell(7).getStringCellValue(),
                            row.getCell(8).getStringCellValue(),
                            row.getCell(9).getStringCellValue(),
                            row.getCell(10).getStringCellValue(),
                            stations);
                    commodities.add(commodity);
                }
                wagons.add(row.getCell(7).getStringCellValue().toLowerCase());

                transportKinds.add(row.getCell(8).getStringCellValue().toLowerCase());

                mainCargoTypes.add(row.getCell(9).getStringCellValue().toLowerCase());

                cargoTypes.add(row.getCell(10).getStringCellValue().toLowerCase());

            }

            outPut.successDisplay();
            commodities.trimToSize();
            result[0] = "-1";
            result[1] = "";
            result[2] = "";
        } catch (FileNotFoundException e) {
            outPut.failDisplay(e);
        } catch (IOException e) {
            outPut.failDisplay(e);
        } catch (NullPointerException e) {
            outPut.failDisplay(e);
        } catch (IllegalStateException e) {
            outPut.failDisplay(e);
        } finally {
            if (dataFile != null) {
                try {
                    dataFile.close();
                } catch (IOException e) {
                    outPut.failDisplay(e);
                }
            }
            if (data != null) {
                try {
                    data.close();
                } catch (IOException e) {
                    outPut.failDisplay(e);
                }
            }
        }
        return result;
    }

    private void updateAlterNames(String[] result, XSSFWorkbook workBook, String filePath) {

        FileOutputStream outFile;

        String correctOriginName = findName(result[1]);
        String correctDestinationName = findName(result[2]);
        try {
            XSSFSheet sheet1 = workBook.getSheetAt(0);

            //update excel
            for (int i = 0; i < sheet1.getLastRowNum(); i++) {
                XSSFRow row = sheet1.getRow(i + 1);
                if (row.getCell(1).getStringCellValue().equals(correctOriginName)) {
                    boolean check = true;
                    for (int j = 4; j < row.getLastCellNum(); j++) {
                        if (result[3].equals(row.getCell(j).getStringCellValue()))
                            check = false;
                    }
                    if (check) {
                        row.createCell(row.getLastCellNum()).setCellValue(result[3]);
                        break;
                    }
                }
                if (row.getCell(1).getStringCellValue().equals(correctDestinationName)) {
                    boolean check = true;
                    for (int j = 4; j < row.getLastCellNum(); j++) {
                        if (result[4].equals(row.getCell(j).getStringCellValue()))
                            check = false;
                    }
                    if (check) {
                        row.createCell(row.getLastCellNum()).setCellValue(result[4]);
                        break;
                    }
                }
            }

            //station names
            for (Station station : stations) {
                if (station.getName().equals(correctOriginName)) {
                    boolean check = true;
                    for (String name : station.getAlterNames()) {
                        if (result[3].equals(name))
                            check = false;
                    }
                    if (check) {
                        station.getAlterNames().add(result[3]);
                        break;
                    }
                }
                if (station.getName().equals(correctDestinationName)) {
                    boolean check = true;
                    for (String name : station.getAlterNames()) {
                        if (result[4].equals(name))
                            check = false;
                    }
                    if (check) {
                        station.getAlterNames().add(result[4]);
                        break;
                    }
                }
            }
            outFile = new FileOutputStream(new File(filePath));
            workBook.write(outFile);
            outFile.flush();
            outFile.close();
            workBook.close();
        } catch (FileNotFoundException e) {
            outPut.failDisplay(e);
        } catch (IOException e) {
            outPut.failDisplay(e);
        } catch (NullPointerException e) {
            outPut.failDisplay(e);
        } catch (IllegalStateException e) {
            outPut.failDisplay(e);
        }
    }

    public String findName(String stringCellValue) {
        for (Station station : stations) {
            for (String name : station.getAlterNames()) {
                if (name.equals(stringCellValue)) {
                    return station.getName();
                }
            }
        }
        return "null";
    }

    private boolean nameIsNotOkay(String stringCellValue) {
        for (Station station : stations) {
            for (String name : station.getAlterNames()) {
                if (name.equals(stringCellValue))
                    return false;
            }
        }
        return true;
    }

    public boolean multiCommodity() {
        try {
            IloCplex model = new IloCplex();
            IloNumVar[] X = new IloNumVar[commodities.size()];
            IloNumExpr goalFunction;
            goalFunction = model.constant(0);
            IloNumExpr constraint;

            for (int x = 0; x < commodities.size(); x++) {
                X[x] = model.numVar(0, 1, IloNumVarType.Float);
            }


            for (int x = 0; x < commodities.size(); x++) {
                goalFunction = model.sum(goalFunction, model.prod(X[x], commodities.get(x).getTonKilometerPlan()));
            }
            model.addMaximize(goalFunction);

            for (int i = 0; i < blocks.size(); i++) {
                constraint = model.constant(0);
                Boolean flag = false;
                if (blocks.get(i).getDirection() == 1 && blocks.get(i).getTrack() == 1) {
                    for (int x = 0; x < commodities.size(); x++) {
                        if (commodities.get(x).hasBlock(blocks.get(i)) || commodities.get(x).hasBlock(blocks.get(i + 1))) {
                            Commodity commodity = commodities.get(x);
                            constraint = model.sum(constraint, model.prod(X[x], commodities.get(x).getPlanTon()));
                            flag = true;
                        }
                    }
                } else if (blocks.get(i).getTrack() == 2) {
                    for (int x = 0; x < commodities.size(); x++) {
                        if (commodities.get(x).hasBlock(blocks.get(i))) {
                            constraint = model.sum(constraint, model.prod(X[x], commodities.get(x).getPlanTon()));
                            flag = true;
                        }
                    }
                }
                if (flag)
                    model.addLe(constraint, blocks.get(i).getCapacity());
            }
            // end of constraints

//            model.setOut(null);
            try {
                if (model.solve()) {
                    for (int i = 0; i < commodities.size(); i++) {
                        if (model.getValue(X[i]) > 0) {
                            commodities.get(i).setHowMuchIsAllowed(model.getValue(X[i]));
                        } else {
                            commodities.get(i).setHowMuchIsAllowed(0);
                        }
                    }
                }
            } catch (CpxException e) {
                alert(e.getMessage());
            }
        } catch (IloException e) {
            e.printStackTrace();
        }

        //bar gozarie mahdod shode
        for (int i = 0; i < blocks.size(); i++) {
            for (Commodity commodity : commodities) {
                if (commodity.getHowMuchIsAllowed() > 0) {
                    if (commodity.hasBlock(blocks.get(i))) {
                        if (blocks.get(i).getDirection() == 1) {
                            blocks.get(i).setDemandWentPlanTon(blocks.get(i).getDemandWentPlanTon() + (commodity.getHowMuchIsAllowed() * commodity.getPlanTon()));
                            blocks.get(i).setDemandWentOperationTon(blocks.get(i).getDemandWentOperationTon() + (commodity.getHowMuchIsAllowed() * commodity.getOperationTon()));
                            blocks.get(i).setDemandWentPlanWagon(blocks.get(i).getDemandWentPlanWagon() + (commodity.getHowMuchIsAllowed() * commodity.getPlanWagon()));
                            blocks.get(i).setDemandWentOperatoinWagon(blocks.get(i).getDemandWentOperatoinWagon() + commodity.getHowMuchIsAllowed() * commodity.getOperationWagon());

                            blocks.get(i).setDemandWentPlanTonKilometer(blocks.get(i).getDemandWentPlanTonKilometer() + (commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan()));
                        } else {
                            blocks.get(i - 1).setDemandBackPlanTon(blocks.get(i - 1).getDemandBackPlanTon() + (commodity.getHowMuchIsAllowed() * commodity.getPlanTon()));
                            blocks.get(i - 1).setDemandBackOperationTon(blocks.get(i - 1).getDemandBackOperationTon() + (commodity.getHowMuchIsAllowed() * commodity.getOperationTon()));
                            blocks.get(i - 1).setDemandBackPlanWagon(blocks.get(i - 1).getDemandBackPlanWagon() + (commodity.getHowMuchIsAllowed() * commodity.getPlanWagon()));
                            blocks.get(i - 1).setDemandBackOperationWagon(blocks.get(i - 1).getDemandBackOperationWagon() + (commodity.getHowMuchIsAllowed() * commodity.getOperationWagon()));

                            blocks.get(i - 1).setDemandBackPlanTonKilometer(blocks.get(i - 1).getDemandBackPlanTonKilometer() + (commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan()));
                        }
                    }
                }
            }
        }
        return true;
    }
}


