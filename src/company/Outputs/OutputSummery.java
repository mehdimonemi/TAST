package company.Outputs;

import company.Data.Commodity;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static company.Assignment.*;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutputSummery extends OutPut {

    public OutputSummery(String filePath, ArrayList<Commodity> commodities) {
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workbook;

        setMassageForWritingFile("Summery");
        try {
            inFile = new FileInputStream(filePath);
            try {
                workbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook();
            }

            XSSFSheet sheet = workbook.createSheet("Summery");
            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workbook, "B Zar");
            CellStyle percentageStyle = setStyle(workbook, "B Zar");
            percentageStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));


            labeling(workbook, sheet);

            XSSFRow row1 = sheet.getRow(2);
            XSSFRow row2 = sheet.getRow(3);
            double temp1 = 0.0;
            double temp2 = 0.0;
            double temp3 = 0.0;
            double temp4 = 0.0;
            for (Commodity commodity : commodities) {
                temp1 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                temp2 += commodity.getOperationTon();
                temp3 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                temp4 += commodity.getTonKilometerOperation();
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);

            row1 = sheet.getRow(4);
            row2 = sheet.getRow(5);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("داخلی")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp2 += commodity.getOperationTon();
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp4 += commodity.getTonKilometerOperation();
                }
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


            row1 = sheet.getRow(6);
            row2 = sheet.getRow(7);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("صادرات (واگن های داخلی)")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp2 += commodity.getOperationTon();
                }
                if (commodity.getTransportKind().equals("صادرات (با واگن های خارجی)")) {
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp4 += commodity.getOperationTon();
                }
            }

            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


            row1 = sheet.getRow(8);
            row2 = sheet.getRow(9);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("صادرات (واگن های داخلی)")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp2 += commodity.getTonKilometerOperation();
                }
                if (commodity.getTransportKind().equals("صادرات (با واگن های خارجی)")) {
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp4 += commodity.getTonKilometerOperation();
                }
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


            row1 = sheet.getRow(10);
            row2 = sheet.getRow(11);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("واردات (واگن های داخلی)")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp2 += commodity.getOperationTon();
                }
                if (commodity.getTransportKind().equals("واردات (با واگن های خارجی)")) {
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp4 += commodity.getOperationTon();
                }
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


            row1 = sheet.getRow(12);
            row2 = sheet.getRow(13);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("واردات (واگن های داخلی)")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp2 += commodity.getTonKilometerOperation();
                }
                if (commodity.getTransportKind().equals("واردات (با واگن های خارجی)")) {
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp4 += commodity.getTonKilometerOperation();
                }
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


            row1 = sheet.getRow(14);
            row2 = sheet.getRow(15);
            temp1 = 0.0;
            temp2 = 0.0;
            temp3 = 0.0;
            temp4 = 0.0;
            for (Commodity commodity : commodities) {
                if (commodity.getTransportKind().equals("ترانزیت")) {
                    temp1 += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                    temp2 += commodity.getOperationTon();
                    temp3 += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                    temp4 += commodity.getTonKilometerOperation();
                }
            }
            setCell(row1, 3, temp1, style);
            setCell(row1, 4, temp2, style);
            increasePercentage(row1, 5, row1.getCell(3), row1.getCell(4), percentageStyle);
            setCell(row2, 3, temp3, style);
            setCell(row2, 4, temp4, style);
            increasePercentage(row2, 5, row1.getCell(3), row1.getCell(4), percentageStyle);


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

    private void labeling(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFCellStyle style = setStyle(workbook, "B Zar");
        Color c = new Color(234, 225, 188);
        XSSFColor headingColor = new XSSFColor(c);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(headingColor);

        row = sheet.createRow(0);
        setCell(row, 0, " اهداف كمي برنامه حمل بار", style);

        row = sheet.createRow(1);
        setCell(row, 0, "اهداف كمي برنامه", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "واحد شاخص", style);
        setCell(row, 3, "برنامه", style);
        setCell(row, 4, "عملکرد", style);
        setCell(row, 5, "درصد رشد برنامه  نسبت به عملکرد", style);

        row = sheet.createRow(2);
        setCell(row, 0, "كل حمل بار", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن", style);

        row = sheet.createRow(3);
        setCell(row, 0, "", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن کیلومتر", style);

        row = sheet.createRow(4);
        setCell(row, 0, "حمل بار داخلي", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن", style);

        row = sheet.createRow(5);
        setCell(row, 0, "", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن کیلومتر", style);

        row = sheet.createRow(6);
        setCell(row, 0, "حمل بار صادره", style);
        setCell(row, 1, "با واگن های داخلی", style);
        setCell(row, 2, "تن", style);

        row = sheet.createRow(7);
        setCell(row, 0, "", style);
        setCell(row, 1, " با واگن های خارجی", style);
        setCell(row, 2, "", style);

        row = sheet.createRow(8);
        setCell(row, 0, "", style);
        setCell(row, 1, "با واگن های داخلی", style);
        setCell(row, 2, "تن کیلومتر", style);

        row = sheet.createRow(9);
        setCell(row, 0, "", style);
        setCell(row, 1, "با واگن های خارجی", style);
        setCell(row, 2, "", style);

        row = sheet.createRow(10);
        setCell(row, 0, "حمل بار وارده", style);
        setCell(row, 1, "با واگن های داخلی", style);
        setCell(row, 2, "تن", style);

        row = sheet.createRow(11);
        setCell(row, 0, "", style);
        setCell(row, 1, " با واگن های خارجی", style);
        setCell(row, 2, "", style);

        row = sheet.createRow(12);
        setCell(row, 0, "", style);
        setCell(row, 1, "با واگن های داخلی", style);
        setCell(row, 2, "تن کیلومتر", style);

        row = sheet.createRow(13);
        setCell(row, 0, "", style);
        setCell(row, 1, "با واگن های خارجی", style);
        setCell(row, 2, "", style);

        row = sheet.createRow(14);
        setCell(row, 0, "حمل ترانزيت", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن", style);

        row = sheet.createRow(15);
        setCell(row, 0, "", style);
        setCell(row, 1, "", style);
        setCell(row, 2, "تن کیلومتر", style);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(4, 5, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(6, 9, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(6, 7, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(8, 9, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(10, 13, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(12, 13, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(14, 15, 0, 1));
    }

}
