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
import java.util.Iterator;

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
            inFile = new FileInputStream(new File(filePath));
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
                totalTonPlan += commodity.getHowMuchIsAllowed()*commodity.getPlanTon();
                totalTonKilometerPlan += commodity.getHowMuchIsAllowed()*commodity.getTonKilometerPlan();
            }

            int rowCounter = 2;
            boolean flag;
            for (Iterator<String> it1 = mainCargoTypes.iterator(); it1.hasNext(); ) {
                StringBuffer wagonsNames = new StringBuffer();
                String mainCargoType = it1.next();
                double tonMainCargoType = 0;
                double tonKilometerMainCargoType = 0;
                for (Iterator<String> it2 = wagons.iterator(); it2.hasNext(); ) {
                    flag = true;
                    String wagonType = it2.next();
                    for (Commodity commodity : commodities) {
                        if (commodity.getMainCargoType().equalsIgnoreCase(mainCargoType) && commodity.getWagonType().equalsIgnoreCase(wagonType)) {
                            if (flag) {
                                if (wagonsNames.length() == 0) {
                                    wagonsNames.append(wagonType);
                                    flag = false;
                                } else {
                                    wagonsNames.append(" و " + wagonType);
                                    flag = false;
                                }
                            }
                            tonKilometerMainCargoType += commodity.getHowMuchIsAllowed()*commodity.getTonKilometerPlan();
                            tonMainCargoType += commodity.getHowMuchIsAllowed()*commodity.getPlanTon();
                        }
                    }
                }
                row = sheet.createRow(rowCounter);
                setCell(row, 0, mainCargoType, style);
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
