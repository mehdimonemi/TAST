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
public class OutputCargoSummery extends OutPut {

    public OutputCargoSummery(String filePath, ArrayList<Commodity> commodities,
                              HashSet<String> mainCargoTypes, HashSet<String> wagons) {
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workbook;

        setMassageForWritingFile("Cargo Summery");
        try {
            inFile = new FileInputStream(filePath);
            try {
                workbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook();
            }

            XSSFSheet sheet = workbook.createSheet("Cargo Summery");

            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workbook, "B Zar");

            row = sheet.createRow(0);
            setCell(row, 0, "برنامه تناژ بارگيري و تن كيلومتر حمل و نقل ناوگان (واگني) مورد نياز در سال", style);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            row = sheet.createRow(1);
            setCell(row, 0, "نوع کالا", style);
            setCell(row, 1, "نوع واگن", style);
            setCell(row, 2, "تناژ بارگیری", style);
            setCell(row, 3, "تن کیلومتر", style);


            double totalTonPlan = 0;
            double totalTonKilometerPlan = 0;
            for (Commodity commodity : commodities) {
                totalTonPlan += commodity.getHowMuchIsAllowed()*commodity.getTon();
                totalTonKilometerPlan += commodity.getHowMuchIsAllowed()*commodity.getTonKilometer();
            }

            int rowCounter = 2;
            boolean flag;
            for (String cargoType : mainCargoTypes) {
                StringBuffer wagonsNames = new StringBuffer();
                double tonMainCargoType = 0;
                double tonKilometerMainCargoType = 0;
                for (String wagon : wagons) {
                    flag = true;
                    for (Commodity commodity : commodities) {
                        if (commodity.getMainCargoType().equalsIgnoreCase(cargoType) && commodity.getWagonType().equalsIgnoreCase(wagon)) {
                            if (flag) {
                                if (wagonsNames.length() == 0) {
                                    wagonsNames.append(wagon);
                                } else {
                                    wagonsNames.append(" و ").append(wagon);
                                }
                                flag = false;
                            }
                            tonKilometerMainCargoType += commodity.getHowMuchIsAllowed() * commodity.getTonKilometer();
                            tonMainCargoType += commodity.getHowMuchIsAllowed() * commodity.getTon();
                        }
                    }
                }
                row = sheet.createRow(rowCounter);
                setCell(row, 0, cargoType, style);
                setCell(row, 1, String.valueOf(wagonsNames), style);
                setCell(row, 2, tonMainCargoType, style);
                setCell(row, 3, tonKilometerMainCargoType, style);
                rowCounter++;
            }

            row = sheet.createRow(rowCounter);
            setCell(row, 0, "مجموع", style);
            setCell(row, 1, "", style);
            sheet.addMergedRegion(new CellRangeAddress(rowCounter, rowCounter, 0, 1));
            for (int i = 2; i <= 3; i++) {
                sumColumn(row, i, 3, mainCargoTypes.size() + 2, "SUM", style);
                rowCounter++;
            }

            inFile.close();
            outFile = new FileOutputStream(filePath);
            workbook.write(outFile);

            outFile.flush();
            outFile.close();

            successDisplay();

        } catch (IOException | NullPointerException | IllegalStateException e) {
            failDisplay(e);
        }
    }
}
