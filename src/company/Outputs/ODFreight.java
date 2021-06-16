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
import java.util.Objects;

import static company.Assignment.doModel;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class ODFreight extends OutPut {

    public ODFreight(String output, ArrayList<Block> blocks,
                     ArrayList<Commodity> commodities, PathExceptions pathExceptions,
                     ArrayList<Station> stations, String origin, String destination, int originId, int destinationId) {

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

            XSSFSheet sheet1 = workBook.createSheet(origin + "--" + destination);
            sheet1.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            XSSFSheet sheet2 = workBook.createSheet(destination + "--" + origin);
            sheet2.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workBook, "B Zar");

            //a to b freight
            double ton = 0;
            double pathTonKilometer = 0;
            double tonKilometer = 0;
            double wagon = 0;

            Commodity temp = new Commodity();
            ArrayList<Block> givenPathBlocks = new ArrayList<>
                    (Objects.requireNonNull(doModel(blocks, pathExceptions, stations, temp, originId, destinationId, origin, destination)));

            int rowCounter = 1;
            for (Commodity commodity : commodities) {
                boolean tonAdded = false;
                double commodityPathTonKilometer = 0;
                for (Block block : commodity.getBlocks()) {
                    for (Block block1 : givenPathBlocks) {
                        if (block.equals(block1)) {
                            if (!tonAdded) {
                                setCell(sheet1.createRow(rowCounter), 0, commodity.getOrigin(), style);
                                setCell(sheet1.getRow(rowCounter), 1, commodity.getDestination(), style);
                                setCell(sheet1.getRow(rowCounter), 2,
                                        commodity.getHowMuchIsAllowed() * commodity.getTon(), style);
                                setCell(sheet1.getRow(rowCounter), 3,
                                        commodity.getHowMuchIsAllowed() * commodity.getTonKilometer(), style);
                                setCell(sheet1.getRow(rowCounter), 4, commodity.getWagonType(), style);
                                setCell(sheet1.getRow(rowCounter), 5, commodity.getCargoType(), style);

                                ton += commodity.getHowMuchIsAllowed() * commodity.getTon();
                                wagon += commodity.getHowMuchIsAllowed() * commodity.getWagon();
                                tonKilometer += commodity.getHowMuchIsAllowed() * commodity.getTonKilometer();
                                tonAdded = true;
                            }
                            pathTonKilometer += commodity.getHowMuchIsAllowed() *
                                    commodity.getTon() * block.getLength();
                            commodityPathTonKilometer += commodity.getHowMuchIsAllowed() *
                                    commodity.getTon() * block.getLength();
                        }
                    }
                }
                if (tonAdded) {
                    setCell(sheet1.getRow(rowCounter), 6, commodityPathTonKilometer, style);
                    rowCounter++;
                }
            }

            setCell(sheet.createRow(0), 0, "ابتدای مسیر", style);
            setCell(sheet.createRow(1), 0, "انتهای مسیر", style);
            setCell(sheet.createRow(2), 0, "تن عبوری", style);
            setCell(sheet.createRow(3), 0, "واگن عبوری", style);
            setCell(sheet.createRow(4), 0, "تن کیلومتر مبدا مقصد", style);
            setCell(sheet.createRow(5), 0, "تن کیلومتر مختص مسیر", style);

            setCell(sheet1.createRow(0), 0, "مبدا", style);
            setCell(sheet1.getRow(0), 0, "مقصد", style);
            setCell(sheet1.getRow(0), 0, "واگن", style);
            setCell(sheet1.getRow(0), 0, "تناژ", style);
            setCell(sheet1.getRow(0), 0, "نوع واگن", style);
            setCell(sheet1.getRow(0), 0, "نوع بار", style);
            setCell(sheet1.getRow(0), 0, "تن کیلومتر مختص مسیر", style);

            setCell(sheet2.createRow(0), 0, "مبدا", style);
            setCell(sheet2.getRow(0), 0, "مقصد", style);
            setCell(sheet2.getRow(0), 0, "واگن", style);
            setCell(sheet2.getRow(0), 0, "تناژ", style);
            setCell(sheet2.getRow(0), 0, "نوع واگن", style);
            setCell(sheet2.getRow(0), 0, "نوع بار", style);
            setCell(sheet2.getRow(0), 0, "تن کیلومتر مختص مسیر", style);

            setCell(sheet.getRow(0), 1, origin, style);
            setCell(sheet.getRow(1), 1, destination, style);
            setCell(sheet.getRow(2), 1, ton, style);
            setCell(sheet.getRow(3), 1, wagon, style);
            setCell(sheet.getRow(4), 1, tonKilometer, style);
            setCell(sheet.getRow(5), 1, pathTonKilometer, style);

            //b to a freight
            ton = 0;
            pathTonKilometer = 0;
            tonKilometer = 0;
            wagon = 0;

            rowCounter = 1;
            for (Commodity commodity : commodities) {
                boolean tonAdded = false;
                double commodityPathTonKilometer = 0;
                for (Block block : commodity.getBlocks()) {
                    for (Block block1 : givenPathBlocks) {
                        if (block.equals(block1)) {
                            if (!tonAdded) {
                                setCell(sheet2.createRow(rowCounter), 0, commodity.getOrigin(), style);
                                setCell(sheet2.getRow(rowCounter), 1, commodity.getDestination(), style);
                                setCell(sheet2.getRow(rowCounter), 2,
                                        commodity.getHowMuchIsAllowed() * commodity.getTon(), style);
                                setCell(sheet2.getRow(rowCounter), 3,
                                        commodity.getHowMuchIsAllowed() * commodity.getTonKilometer(), style);
                                setCell(sheet2.getRow(rowCounter), 4, commodity.getWagonType(), style);
                                setCell(sheet2.getRow(rowCounter), 5, commodity.getCargoType(), style);

                                ton += commodity.getHowMuchIsAllowed() * commodity.getTon();
                                wagon += commodity.getHowMuchIsAllowed() * commodity.getWagon();
                                tonKilometer += commodity.getHowMuchIsAllowed() * commodity.getTonKilometer();
                                tonAdded = true;
                            }
                            pathTonKilometer += commodity.getHowMuchIsAllowed() *
                                    commodity.getTon() * block.getLength();
                            commodityPathTonKilometer += commodity.getHowMuchIsAllowed() *
                                    commodity.getTon() * block.getLength();
                        }
                    }
                }
                if (tonAdded) {
                    setCell(sheet2.getRow(rowCounter), 6, commodityPathTonKilometer, style);
                    rowCounter++;
                }
            }

            setCell(sheet.getRow(0), 2, destination, style);
            setCell(sheet.getRow(1), 2, origin, style);
            setCell(sheet.getRow(2), 2, ton, style);
            setCell(sheet.getRow(3), 2, wagon, style);
            setCell(sheet.getRow(4), 2, tonKilometer, style);
            setCell(sheet.getRow(5), 2, pathTonKilometer, style);
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