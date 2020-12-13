package company.Outputs;

import company.Data.Commodity;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

import static company.Main.row;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutputPaths extends OutPut {

    public OutputPaths(String filePath, ArrayList<Commodity> commodities) {
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook pathsWorkbook;
        setMassageForWritingFile("Paths");
        try {
            inFile = new FileInputStream(new File(filePath));
            try {
                pathsWorkbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                pathsWorkbook = new XSSFWorkbook();
            }
            XSSFSheet sheet = pathsWorkbook.createSheet("Paths");
            sheet.setRightToLeft(true);

            CellStyle style = setStyle(pathsWorkbook, "B Nazanin");

            row = sheet.createRow(0);
            setCell(row, 0, "مبدا", style);
            setCell(row, 1, "مقصد", style);
            setCell(row, 2, "کیلومتر", style);
            setCell(row, 3, "تن برنامه", style);
            setCell(row, 4, "تن کیلومتر برنامه", style);
            setCell(row, 5, "واگن برنامه", style);
            setCell(row, 6, "مسیر", style);

            for (Commodity commodity : commodities) {
                row = sheet.createRow(commodity.getId());

                setCell(row, 0, commodity.getOrigin(), style);
                setCell(row, 1, commodity.getDestination(), style);
                setCell(row, 2, commodity.getDistance(), style);
                setCell(row, 3, commodity.getPlanTon()*commodity.getHowMuchIsAllowed(), style);
                setCell(row, 4, commodity.getTonKilometerPlan()*commodity.getHowMuchIsAllowed(), style);
                setCell(row, 5, commodity.getPlanWagon()*commodity.getHowMuchIsAllowed(), style);

                for (int i = 0; i < commodity.getBlocks().size(); i++) {
                    setCell(row, i + 6, commodity.getBlocks().get(i).getOrigin(), style);
                    if ((commodity.getBlocks().size() - 1) == i) {
                        setCell(row, i + 7, commodity.getBlocks().get(i).getDestination(), style);
                    }
                }

            }
            inFile.close();
            outFile = new FileOutputStream(new File(filePath));
            pathsWorkbook.write(outFile);
            outFile.flush();
            outFile.close();
//            sheet = null;
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