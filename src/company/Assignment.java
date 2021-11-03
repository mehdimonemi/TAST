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

import static company.App.mainController;

public class Assignment {
    public static XSSFRow row;

    public ArrayList<Station> stations = null;
    public ArrayList<Block> blocks = null;
    public ArrayList<Block> outputBlocks = null;
    public ArrayList<Commodity> commodities = null;
    public PathExceptions pathExceptions = null;
    public TreeSet<String> districts = null;
    public HashSet<String> wagons = null;
    public HashSet<String> mainCargoTypes = null;
    HashSet<String> cargoTypes = null;
    HashSet<String> transportKinds = null;

    public static XSSFCell cell;
    public static OutPut outPut = new OutPut();


    public void main(boolean fullAssignmentSelected) {

        PrintStream logFile;

        try (PrintStream printStream = logFile = new PrintStream(new FileOutputStream("log.txt"))) {
//            System.setOut(logFile);

            //before start solving models we should sure the commodities are empty from previews result
            resetCommoditiesResult();

            findPaths();
            if (fullAssignmentSelected)
                barGozari();
            else
                multiCommodity();

        } catch (FileNotFoundException | NullPointerException e) {
            mainController.alert(e.getMessage());
        }
    }

    private void resetCommoditiesResult() {
        for (Commodity commodity : commodities) {

            commodity.setHowMuchIsAllowed(1);
            commodity.setDistance(0);
            commodity.setTonKilometer(0);
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
                        blocks.get(i).setDemandWentPlanTon(blocks.get(i).getDemandWentPlanTon() + commodity.getTon());
                        blocks.get(i).setDemandWentPlanWagon(blocks.get(i).getDemandWentPlanWagon() + commodity.getWagon());

                        blocks.get(i).setDemandWentPlanTonKilometer(blocks.get(i).getDemandWentPlanTonKilometer() + commodity.getTonKilometer());
                    } else {
                        blocks.get(i - 1).setDemandBackPlanTon(blocks.get(i - 1).getDemandBackPlanTon() + commodity.getTon());
                        blocks.get(i - 1).setDemandBackPlanWagon(blocks.get(i - 1).getDemandBackPlanWagon() + commodity.getWagon());

                        blocks.get(i - 1).setDemandBackPlanTonKilometer(blocks.get(i - 1).getDemandBackPlanTonKilometer() + commodity.getTonKilometer());
                    }
                }
            }
        }
    }

    public boolean findPaths() {
        try {
            IloCplex model = new IloCplex();
            for (Commodity commodity : commodities) {
                int stationA = commodity.getOriginId();
                int stationB = commodity.getDestinationId();
                String a = commodity.getOrigin();
                String b = commodity.getDestination();
                if (a.equals(b) && !a.contains("سایر")) {
                    mainController.alert("same OD for " + commodity);
                    commodity.setDistance(150);
                    commodity.setTonKilometer(150 * commodity.getTon());
                    continue;
                }
                doModel(blocks, pathExceptions, stations, commodity, stationA, stationB, a, b, model);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        System.gc();
        return true;
    }

    public static ArrayList<Block> doModel(ArrayList<Block> blocks, PathExceptions pathExceptions,
                                           ArrayList<Station> stations, Commodity commodity,
                                           int stationA, int stationB, String a, String b, IloCplex model) {
        try {
            IloNumVar[] X = new IloNumVar[blocks.size()];
            IloNumExpr goalFunction;
            IloNumExpr constraint;
            //start solving model for the commodity
            //masirhai estesna baiad az masir khas beran
            int temp = pathExceptions.isException(OutPut.districtOf(a, stations),
                    OutPut.districtOf(b, stations));
            if (temp == 1 || temp == 2) {
                for (int j = 0; j < blocks.size(); j++) {
                    boolean flag = true;
                    for (int i = (temp - 1); i < pathExceptions.getBlocksMustbe().size(); ) {
                        if (blocks.get(j).equals(pathExceptions.getBlocksMustbe().get(i))) {
                            X[j] = model.numVar(1, 1, IloNumVarType.Int);
                            flag = false;
                        } else if ((a.equals("ری") || a.equals("تهران") || b.equals("تهران") || b.equals("ری")) &&
                                ((blocks.get(j).getOrigin().equals("ری")
                                        && blocks.get(j).getDestination().equals("تهران")) ||
                                        ((blocks.get(j).getOrigin().equals("تهران")
                                                && blocks.get(j).getDestination().equals("ری"))))) {
                            X[j] = model.numVar(0, 1, IloNumVarType.Int);
                            flag = false;
                        } else if (((blocks.get(j).getOrigin().equals("ری")
                                && blocks.get(j).getDestination().equals("تهران")) ||
                                ((blocks.get(j).getOrigin().equals("تهران")
                                        && blocks.get(j).getDestination().equals("ری"))))) {
                            X[j] = model.numVar(0, 0, IloNumVarType.Int);
                            flag = false;
                        }
                        i += 2;
                    }
                    if (flag) {
                        X[j] = model.numVar(0, 1, IloNumVarType.Int);
                    }
                }
            } else if ((a.equals("ری") || a.equals("تهران") || b.equals("تهران") || b.equals("ری"))) {
                for (int j = 0; j < blocks.size(); j++) {
                    X[j] = model.numVar(0, 1, IloNumVarType.Int);
                }
            } else {
                for (int i = 0; i < blocks.size(); i++) {
                    if ((blocks.get(i).getOrigin().equals("ری")
                            && blocks.get(i).getDestination().equals("تهران")) ||
                            ((blocks.get(i).getOrigin().equals("تهران")
                                    && blocks.get(i).getDestination().equals("ری")))) {
                        X[i] = model.numVar(0, 0, IloNumVarType.Int);
                    } else {
                        X[i] = model.numVar(0, 1, IloNumVarType.Int);
                    }
                }
            }

            goalFunction = model.constant(0);
            for (int i = 0; i < blocks.size(); i++) {
                goalFunction = model.sum(goalFunction, model.prod(X[i], blocks.get(i).getLength()));
            }
            model.addMinimize(goalFunction);

            // constraints
            for (Station station : stations) {
                constraint = model.constant(0);
                if (station.getId() == stationA) {
                    for (int j = 0; j < blocks.size(); j++) {
                        if (stationA == blocks.get(j).getOriginId()) {
                            constraint = model.sum(constraint, X[j]);
                        }
                        if (stationA == blocks.get(j).getDestinationId()) {
                            constraint = model.sum(constraint, model.negative(X[j]));
                        }
                    }
                    model.addEq(constraint, 1);
                } else if (station.getId() == (stationB)) {
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
                        if (station.getId() == (blocks.get(j).getOriginId())) {
                            constraint = model.sum(constraint, X[j]);
                        }
                        if (station.getId() == (blocks.get(j).getDestinationId())) {
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
                    commodity.setTonKilometer(model.getObjValue() * commodity.getTon());
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
                    commodity.setTonKilometer(150 * commodity.getTon());
                    mainController.alert("No path for " + commodity);
                    model.clearModel();
                }
                if (commodity.getDistance() < 150) {
                    commodity.setDistance(150);
                    commodity.setTonKilometer(150 * commodity.getTon());
                    model.clearModel();
                }
                return commodity.getBlocks();
            } catch (CpxException e) {
                mainController.alert(e.getMessage());
                return null;
            }
        } catch (IloException e) {
            mainController.alert(e.getMessage());
            return null;
        }
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
                        row.getCell(2).getBooleanCellValue(),
                        row.getCell(3).getStringCellValue());
                if (!row.getCell(3).getStringCellValue().equals("null"))
                    districts.add(row.getCell(3).getStringCellValue());

                for (int j = 5; j < row.getLastCellNum(); j++) {
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
                        if (!row.getCell(i).getStringCellValue().equals("")) {
                            switch (i) {
                                case 0:
                                    pathExceptions.getOriginDistricts().add(row.getCell(i).getStringCellValue());
                                    break;
                                case 1:
                                    pathExceptions.getDestinationDistricts().add(row.getCell(i).getStringCellValue());
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
                    } catch (IllegalStateException | NullPointerException e) {
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

        } catch (IOException | NullPointerException | IllegalStateException e) {
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
        FileOutputStream file = null;
        XSSFWorkbook data = null;
        try (FileInputStream dataFile = new FileInputStream(outPutDirectory + "/Data.xlsx")) {
            data = new XSSFWorkbook(dataFile);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            //read commodities data
            XSSFSheet sheet3 = data.getSheetAt(2);
            for (int i = Integer.parseInt(result[0]); i <= sheet3.getLastRowNum(); i++) {
                XSSFRow row = sheet3.getRow(i);
                if ((!result[1].equals("") || !result[2].equals("")) && i == Integer.parseInt(result[0])) {
                    if (nameIsNotOkay(result[1]) ||
                            nameIsNotOkay(result[2])) {
                        return result;
                    }
                } else if (nameIsNotOkay(row.getCell(0).getStringCellValue().trim()) ||
                        nameIsNotOkay(row.getCell(1).getStringCellValue().trim())) {
                    result[0] = String.valueOf(i);
                    result[1] = row.getCell(0).getStringCellValue().trim();
                    result[2] = row.getCell(1).getStringCellValue().trim();
                    result[3] = row.getCell(0).getStringCellValue().trim();
                    result[4] = row.getCell(1).getStringCellValue().trim();
                    return result;
                }

                if ((!result[1].equals("") || !result[2].equals("")) && i == Integer.parseInt(result[0])) {
                    Commodity commodity = new Commodity(
                            findName(result[1])[0],
                            findName(result[2])[0],
                            row.getCell(2).getNumericCellValue(),
                            row.getCell(3).getNumericCellValue(),
                            row.getCell(4).getStringCellValue(),
                            row.getCell(5).getStringCellValue(),
                            row.getCell(6).getStringCellValue(),
                            row.getCell(7).getStringCellValue(),
                            stations);
                    commodities.add(commodity);

                    //we update alter names if only the station name is not duplicate (special)
                    if (!stations.get(Integer.parseInt(findName(result[1])[1])).isSpecialTag() ||
                            !stations.get(Integer.parseInt(findName(result[2])[1])).isSpecialTag()
                    ) {
                        updateAlterNames(result, data);
                        (new File(outPutDirectory + "/Data.xlsx")).delete();
                        file = new FileOutputStream(outPutDirectory + "/Data.xlsx");
                        data.write(file);
                        file.flush();
                        file.close();
                    }

                } else {
                    Commodity commodity = new Commodity(
                            findName(row.getCell(0).getStringCellValue().trim())[0],
                            findName(row.getCell(1).getStringCellValue().trim())[0],
                            row.getCell(2).getNumericCellValue(),
                            row.getCell(3).getNumericCellValue(),
                            row.getCell(4).getStringCellValue(),
                            row.getCell(5).getStringCellValue(),
                            row.getCell(6).getStringCellValue(),
                            row.getCell(7).getStringCellValue(),
                            stations);
                    commodities.add(commodity);
                }
                wagons.add(row.getCell(4).getStringCellValue().toLowerCase());

                transportKinds.add(row.getCell(5).getStringCellValue().toLowerCase());

                mainCargoTypes.add(row.getCell(6).getStringCellValue().toLowerCase());

                cargoTypes.add(row.getCell(7).getStringCellValue().toLowerCase());

            }

            outPut.successDisplay();
            commodities.trimToSize();
            result[0] = "-1";
            result[1] = "";
            result[2] = "";
        } catch (IOException | NullPointerException | IllegalStateException e) {
            outPut.failDisplay(e);
        } finally {
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

    private void updateAlterNames(String[] result, XSSFWorkbook workBook) {

        String correctOriginName = findName(result[1])[0];
        String correctDestinationName = findName(result[2])[0];
        try {

            XSSFSheet sheet1 = workBook.getSheetAt(0);

            //update excel
            for (int i = 0; i < sheet1.getLastRowNum(); i++) {
                boolean finishedWithOrigin = false;
                boolean finishedWithDestination = false;
                XSSFRow row = sheet1.getRow(i + 1);
                if (row.getCell(1).getStringCellValue().equals(correctOriginName)) {
                    boolean check = true;
                    for (int j = 5; j < row.getLastCellNum(); j++) {
                        if (result[3].equals(row.getCell(j).getStringCellValue()))
                            check = false;
                    }
                    if (check) {
                        row.createCell(row.getLastCellNum()).setCellValue(result[3]);
                    }
                    finishedWithOrigin = true;
                }
                if (row.getCell(1).getStringCellValue().equals(correctDestinationName)) {
                    boolean check = true;
                    for (int j = 5; j < row.getLastCellNum(); j++) {
                        if (result[4].equals(row.getCell(j).getStringCellValue()))
                            check = false;
                    }
                    if (check) {
                        row.createCell(row.getLastCellNum()).setCellValue(result[4]);
                    }
                    finishedWithDestination = true;
                }
                if (finishedWithDestination && finishedWithOrigin)
                    break;
            }

            //station names
            for (Station station : stations) {
                if (station.getName().equals(correctOriginName)) {
                    boolean check = true;
                    for (String name : station.getAlterNames()) {
                        if (result[3].equals(name)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        station.getAlterNames().add(result[3]);
                        break;
                    }
                }
                if (station.getName().equals(correctDestinationName)) {
                    boolean check = true;
                    for (String name : station.getAlterNames()) {
                        if (result[4].equals(name)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        station.getAlterNames().add(result[4]);
                        break;
                    }
                }
            }
        } catch (NullPointerException | IllegalStateException e) {
            outPut.failDisplay(e);
        }
    }

    public String[] findName(String stringCellValue) {
        for (Station station : stations) {
            for (String name : station.getAlterNames()) {
                if (name.equals(stringCellValue)) {
                    return new String[]{station.getName(), String.valueOf(station.getId())};
                }
            }
        }
        return null;
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

    public void multiCommodity() {
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
                goalFunction = model.sum(goalFunction, model.prod(X[x], commodities.get(x).getTonKilometer()));
            }
            model.addMaximize(goalFunction);

            for (int i = 0; i < blocks.size(); i++) {
                constraint = model.constant(0);
                Boolean flag = false;
                if (blocks.get(i).getDirection() == 1 && blocks.get(i).getTrack() == 1) {
                    for (int x = 0; x < commodities.size(); x++) {
                        if (commodities.get(x).hasBlock(blocks.get(i)) || commodities.get(x).hasBlock(blocks.get(i + 1))) {
                            Commodity commodity = commodities.get(x);
                            constraint = model.sum(constraint, model.prod(X[x], commodities.get(x).getTon()));
                            flag = true;
                        }
                    }
                } else if (blocks.get(i).getTrack() == 2) {
                    for (int x = 0; x < commodities.size(); x++) {
                        if (commodities.get(x).hasBlock(blocks.get(i))) {
                            constraint = model.sum(constraint, model.prod(X[x], commodities.get(x).getTon()));
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
                mainController.alert(e.getMessage());
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
                            blocks.get(i).setDemandWentPlanTon(blocks.get(i).getDemandWentPlanTon() + (commodity.getHowMuchIsAllowed() * commodity.getTon()));
                            blocks.get(i).setDemandWentPlanWagon(blocks.get(i).getDemandWentPlanWagon() + (commodity.getHowMuchIsAllowed() * commodity.getWagon()));

                            blocks.get(i).setDemandWentPlanTonKilometer(blocks.get(i).getDemandWentPlanTonKilometer() + (commodity.getHowMuchIsAllowed() * commodity.getTonKilometer()));
                        } else {
                            blocks.get(i - 1).setDemandBackPlanTon(blocks.get(i - 1).getDemandBackPlanTon() + (commodity.getHowMuchIsAllowed() * commodity.getTon()));
                            blocks.get(i - 1).setDemandBackPlanWagon(blocks.get(i - 1).getDemandBackPlanWagon() + (commodity.getHowMuchIsAllowed() * commodity.getWagon()));

                            blocks.get(i - 1).setDemandBackPlanTonKilometer(blocks.get(i - 1).getDemandBackPlanTonKilometer() + (commodity.getHowMuchIsAllowed() * commodity.getTonKilometer()));
                        }
                    }
                }
            }
        }
    }
}


