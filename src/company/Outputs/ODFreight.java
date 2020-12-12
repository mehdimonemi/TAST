package company.Outputs;

import company.Data.Block;
import company.Data.Commodity;
import company.Data.PathExceptions;
import company.Data.Station;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.CpxException;
import ilog.cplex.IloCplex;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.print.attribute.AttributeSetUtilities;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import static company.Main.row;
import static company.windows.alert;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class ODFreight extends OutPut {

    private ArrayList<Block> givenPathBlocks;

    public ODFreight(String output, ArrayList<Block> blocks, ArrayList<Commodity> commodities, PathExceptions pathExceptions
            , ArrayList<Station> stations) {

        int stationA = 429;
        String a = "بافق";
        int stationB = 126;
        String b = "میرجاوه";
        //first of all we need our path
        //Now we Start main goal
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workBook;
        setMassageForWritingFile("OD Freight");

        try {
            inFile = new FileInputStream(new File(output));
            try {
                workBook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workBook = new XSSFWorkbook();
            }

            XSSFSheet sheet = workBook.createSheet("ODFreight");
            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workBook, "B Zar");

            //a to b freight
            solve(stationA, stationB, a, b, blocks, commodities, pathExceptions, stations);
            double tonPlan = 0;
            double tonKilometerPlan = 0;
            double wagonPlan = 0;
            double wagonOperation = 0;
            double tonOperation = 0;
            double tonKilometerOperation = 0;
            for (Commodity commodity : commodities) {
                boolean tonAdded = false;
                for (Block block : commodity.getBlocks()) {
                    for (Block block1 : givenPathBlocks) {
                        if (block.equals(block1)) {
                            if (!tonAdded) {
                                tonPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                                tonOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationTon();
                                wagonPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanWagon();
                                wagonOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationWagon();
                                tonAdded = true;
                            }
                            tonKilometerPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanTon() * block.getLength();
                            tonKilometerOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationTon() * block.getLength();
                        }
                    }
                }
            }

            setCell(sheet.createRow(0), 0, "ابتدای مسیر", style);
            setCell(sheet.getRow(0), 1, a, style);
            setCell(sheet.createRow(1), 0, "انتهای مسیر", style);
            setCell(sheet.getRow(1), 1, b, style);
            setCell(sheet.createRow(2), 0, "تن عبوری برنامه", style);
            setCell(sheet.getRow(2), 1, tonPlan, style);
            setCell(sheet.createRow(3), 0, "تن عبوری عملکرد", style);
            setCell(sheet.getRow(3), 1, tonOperation, style);
            setCell(sheet.createRow(4), 0, "واگن عبوری برنامه", style);
            setCell(sheet.getRow(4), 1, wagonPlan, style);
            setCell(sheet.createRow(5), 0, "واگن عبوری عملکرد", style);
            setCell(sheet.getRow(5), 1, wagonOperation, style);
            setCell(sheet.createRow(6), 0, "تن کیلومتر برنامه", style);
            setCell(sheet.getRow(6), 1, tonKilometerPlan, style);
            setCell(sheet.createRow(7), 0, "تن کیلومتر عملکرد", style);
            setCell(sheet.getRow(7), 1, tonKilometerOperation, style);
            setCell(sheet.createRow(8), 0, "توضیح: تن کیلومتر تنها مربوط بخش همین  مسیر است", style);
            setCell(sheet.getRow(8), 1, "", style);
            setCell(sheet.getRow(8), 2, "", style);
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 2));

            //b to a freight
            solve(stationB, stationA, b, a, blocks, commodities, pathExceptions, stations);
            tonPlan = 0;
            tonKilometerPlan = 0;
            wagonPlan = 0;
            wagonOperation = 0;
            tonOperation = 0;
            tonKilometerOperation = 0;
            for (Commodity commodity : commodities) {
                boolean tonAdded = false;
                for (Block block : commodity.getBlocks()) {
                    for (Block block1 : givenPathBlocks) {
                        if (block.equals(block1)) {
                            if (!tonAdded) {
                                tonPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                                tonOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationTon();
                                wagonPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanWagon();
                                wagonOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationWagon();
                                tonAdded = true;
                            }
                            tonKilometerPlan += commodity.getHowMuchIsAllowed() * commodity.getPlanTon() * block.getLength();
                            tonKilometerOperation += commodity.getHowMuchIsAllowed() * commodity.getOperationTon() * block.getLength();
                        }
                    }
                }
            }

            setCell(sheet.getRow(0), 2, b, style);
            setCell(sheet.getRow(1), 2, a, style);
            setCell(sheet.getRow(2), 2, tonPlan, style);
            setCell(sheet.getRow(3), 2, tonOperation, style);
            setCell(sheet.getRow(4), 2, wagonPlan, style);
            setCell(sheet.getRow(5), 2, wagonOperation, style);
            setCell(sheet.getRow(6), 2, tonKilometerPlan, style);
            setCell(sheet.getRow(7), 2, tonKilometerOperation, style);
            outFile = new FileOutputStream(new File(output));
            workBook.write(outFile);

            outFile.flush();
            outFile.close();
            workBook.close();

            successDisplay();

        } catch (FileNotFoundException e) {
            failDisplay(e);
        } catch (IOException e) {
            failDisplay(e);
        } catch (NullPointerException e) {
            failDisplay(e);
        } catch (IllegalStateException e) {
            failDisplay(e);
        }
    }

    public void solve(int stationA, int stationB, String a, String b, ArrayList<Block> blocks, ArrayList<Commodity> commodities, PathExceptions pathExceptions, ArrayList<Station> stations) {
        try {
            IloCplex model = new IloCplex();
            IloNumVar[] X = new IloNumVar[blocks.size()];
            IloNumExpr goalFunction;
            IloNumExpr constraint;


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
                givenPathBlocks = new ArrayList<>();
                if (model.solve()) {
                    for (int i = 0; i < blocks.size(); i++) {
                        if (model.getValue(X[i]) > 0.5) {
                            givenPathBlocks.add(blocks.get(i));
                        }
                    }

                    //sort blocks
                    String tempOrigin = a;
                    ArrayList<Block> tempBlocks = new ArrayList<>();

                    while (!givenPathBlocks.isEmpty()) {
                        for (Block block : givenPathBlocks) {
                            if (block.getOrigin().equals(tempOrigin)) {
                                tempBlocks.add(block);
                                tempOrigin = block.getDestination();
                                givenPathBlocks.remove(block);
                                break;
                            }
                        }
                    }
                    givenPathBlocks.addAll(tempBlocks);

                    model.clearModel();
                    for (int i = 0; i < blocks.size(); i++) {
                        if (X[i] != null) {
                            X[i] = null;
                        }
                    }
                    goalFunction = null;
                    constraint = null;
                } else {
                    alert("No path for " + a + " to " + b);
                    model.clearModel();
                }
            } catch (CpxException e) {
                alert(e.getMessage());
            }

        } catch (IloException e) {
            e.printStackTrace();
        }

    }
}
