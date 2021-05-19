package company.Outputs;

import company.Data.Block;
import company.Data.Commodity;
import company.Data.PathExceptions;
import company.Data.Station;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

import static company.Assignment.doModel;
import static company.windows.alert;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class ODFreight extends OutPut {

    private ArrayList<Block> givenPathBlocks;
    private int stationA, stationB;
    private String a, b;

    public ODFreight(String output, ArrayList<Block> blocks,
                     ArrayList<Commodity> commodities, PathExceptions pathExceptions,
                     ArrayList<Station> stations, String CargoOrigin, String cargoDestination) {

        //first of all we need our path
        //Now we Start main goal
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workBook;
        setMassageForWritingFile("OD Freight");

        try {
            inFile = new FileInputStream(output);
            try {
                workBook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workBook = new XSSFWorkbook();
            }

            XSSFSheet sheet = workBook.createSheet("ODFreight");
            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workBook, "B Zar");

            //a to b freight
            double tonPlan = 0;
            double tonKilometerPlan = 0;
            double wagonPlan = 0;
            double wagonOperation = 0;
            double tonOperation = 0;
            double tonKilometerOperation = 0;
            givenPathBlocks = new ArrayList<>();

            Commodity temp = new Commodity();
            givenPathBlocks.addAll(doModel(blocks,pathExceptions,stations, temp, stationA, stationB, a, b));
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
            outFile = new FileOutputStream(output);
            workBook.write(outFile);

            outFile.flush();
            outFile.close();
            workBook.close();

            successDisplay();

        } catch (IOException | NullPointerException | IllegalStateException e) {
            failDisplay(e);
        }
    }
}