package company.Outputs;

import company.Data.Commodity;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

import static company.Assignment.*;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutputCargoType extends OutPut {

    public OutputCargoType(String filePath, ArrayList<Commodity> commodities,
                           HashSet<String> mainCargoTypes, HashSet<String> wagons) {
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workbook;

        setMassageForWritingFile("Cargo Type");
        try {
            inFile = new FileInputStream(new File(filePath));
            try {
                workbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook();
            }

            XSSFSheet sheet = workbook.createSheet("Cargo Type");
            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workbook, "B Zar");

            row = sheet.createRow(0);
            setCell(row, 0, "برنامه حمل و نقل سال به تفکيک نوع و طبقه بندي کالا", style);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

            row = sheet.createRow(1);
            setCell(row, 0, "ردیف", style);
            setCell(row, 1, "طبقه بندي کالا", style);
            setCell(row, 2, "مبدا", style);
            setCell(row, 3, "مقصد", style);
            setCell(row, 4, "نوع بار", style);
            setCell(row, 5, "تناژ بارگيري سالانه", style);
            setCell(row, 6, "تن كيلومتر بارگيری سالانه", style);
            setCell(row, 7, "نوع واگن", style);
            setCell(row, 8, "تناژ بارگيري هر نوع بار", style);
            setCell(row, 9, "تن کيلومتر بارگيري هرنوع بار", style);
            setCell(row, 10, "سهم تناژهر نوع بار", style);
            setCell(row, 11, "سهم تن کيلومترهر نوع بار", style);


            double totalTonPlan = 0;
            double totalTonKilometerPlan = 0;
            for (Commodity commodity : commodities) {
                totalTonPlan += commodity.getHowMuchIsAllowed()*commodity.getPlanTon();
                totalTonKilometerPlan += commodity.getHowMuchIsAllowed()*commodity.getTonKilometerPlan();
            }

            int rowCounter = 2;
            int mainCargoRowCounter;
            int wagonRowCounter;
            int firstRowCargoType;
            for (String mainCargoType : mainCargoTypes) {
                firstRowCargoType = rowCounter;
                mainCargoRowCounter = rowCounter;
                double tonMainCargoType = 0;
                double tonKilometerMainCargoType = 0;
                for (String wagonType : wagons) {
                    wagonRowCounter = rowCounter;
                    for (Commodity commodity : commodities) {
                        if (commodity.getMainCargoType().equalsIgnoreCase(mainCargoType) && commodity.getWagonType().equalsIgnoreCase(wagonType)) {

                            row = sheet.createRow(rowCounter);
                            setCell(row, 0, (double) (rowCounter - 1), style);
                            setCell(row, 1, mainCargoType, style);
                            setCell(row, 2, commodity.getOrigin(), style);
                            setCell(row, 3, commodity.getDestination(), style);
                            setCell(row, 4, commodity.getCargoType(), style);
                            setCell(row, 5, commodity.getHowMuchIsAllowed()*commodity.getPlanTon(), style);
                            setCell(row, 6, commodity.getHowMuchIsAllowed()*commodity.getTonKilometerPlan(), style);
                            setCell(row, 7, commodity.getWagonType(), style);
                            setCell(row, 8, "", style);
                            setCell(row, 9, "", style);
                            setCell(row, 10, "", style);
                            setCell(row, 11, "", style);

                            tonKilometerMainCargoType += commodity.getHowMuchIsAllowed()*commodity.getTonKilometerPlan();
                            tonMainCargoType += commodity.getHowMuchIsAllowed()*commodity.getPlanTon();

                            rowCounter++;
                        }
                    }
                }
                row = sheet.getRow(firstRowCargoType);
                setCell(row, 8, tonMainCargoType, style);
                setCell(row, 9, tonKilometerMainCargoType, style);
                setCell(row, 10, tonMainCargoType / totalTonPlan, style);
                setCell(row, 11, tonKilometerMainCargoType / totalTonKilometerPlan, style);

                if (rowCounter > mainCargoRowCounter && (rowCounter - 1) != mainCargoRowCounter) {
                    sheet.addMergedRegion(new CellRangeAddress(mainCargoRowCounter, rowCounter - 1, 1, 1));
                    sheet.addMergedRegion(new CellRangeAddress(mainCargoRowCounter, rowCounter - 1, 8, 8));
                    sheet.addMergedRegion(new CellRangeAddress(mainCargoRowCounter, rowCounter - 1, 9, 9));
                    sheet.addMergedRegion(new CellRangeAddress(mainCargoRowCounter, rowCounter - 1, 10, 10));
                    sheet.addMergedRegion(new CellRangeAddress(mainCargoRowCounter, rowCounter - 1, 11, 11));
                }
            }

            inFile.close();
            outFile = new FileOutputStream(new File(filePath));
            workbook.write(outFile);

            outFile.flush();
            outFile.close();

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
}
