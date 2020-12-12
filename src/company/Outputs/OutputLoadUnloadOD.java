package company.Outputs;

import company.Data.Block;
import company.Data.Commodity;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by Monemi_M on 01/27/2018.
 */
public class OutputLoadUnloadOD extends OutPut {
    public OutputLoadUnloadOD(String filePath, TreeSet<String> districts, ArrayList<Commodity> commodities) {
        setMassageForWritingFile("LoadUnload O-D matrix");

        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workbook;
        try {
            inFile = new FileInputStream(new File(filePath));
            try {
                workbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook();
            }

            XSSFSheet sheet1 = workbook.createSheet("O-D matrix");
            sheet1.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);
            XSSFSheet sheet2 = workbook.createSheet("ton-km O-D matrix");
            sheet2.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);


            CellStyle style = setStyle(workbook, "B Zar");

            XSSFRow row1 = sheet1.createRow(0);
            XSSFRow row2 = sheet2.createRow(0);

            //labeling first row
            int z = 1;
            for (Iterator<String> it1 = districts.iterator(); it1.hasNext(); ) {
                String a = it1.next();
                setCell(row1, z, a, style);
                setCell(row2, z, a, style);
                z++;
            }
            setCell(row1, z, "جمع بارگیری هر ناحیه", style);
            setCell(row2, z, "تن کیلومتر بارگیری", style);


            //load and unload
            int i = 1;
            for (Iterator<String> it1 = districts.iterator(); it1.hasNext(); ) {
                double temp = 0;//jame har radif
                String a = it1.next();
                row1 = sheet1.createRow(i);
                setCell(row1, 0, a, style);
                int j = 1;
                for (Iterator<String> it2 = districts.iterator(); it2.hasNext(); ) {
                    String b = it2.next();
                    double ton = 0;
                    for (Commodity commodity : commodities) {
                        if (commodity.getOriginDistrict().equals(a) && commodity.getDestinationDistrict().equals(b)) {
                            ton += commodity.getHowMuchIsAllowed()*commodity.getPlanTon();
                        }
                    }

                    setCell(row1, j, ton, style);
                    temp += ton;
                    j++;
                }
                setCell(row1, j, temp, style);
                i++;
            }
            //jame har soton
            row1 = sheet1.createRow(i);
            setCell(row1, 0, "جمع هر ناحیه", style);
            i = 1;
            for (Iterator<String> it1 = districts.iterator(); it1.hasNext(); ) {
                it1.next();
                sumColumn(row1, i, 2, districts.size() + 1, "SUM", style);
                i++;
            }
            sumColumn(row1, i, 2, districts.size() + 1, "SUM", style);

            //ton kilometer
            i = 1;
            for (Iterator<String> it1 = districts.iterator(); it1.hasNext(); ) {
                double temp = 0;//jame har radif
                String a = it1.next();
                row2 = sheet2.createRow(i);
                setCell(row2, 0, a, style);
                int j = 1;
                for (Iterator<String> it2 = districts.iterator(); it2.hasNext(); ) {
                    String b = it2.next();
                    double tonKilometer = 0;
                    for (Commodity commodity : commodities) {
                        if (commodity.getOriginDistrict().equals(a)) {
                            for (Block block : commodity.getBlocks()) {
                                if (block.getDistrict().equals(b)) {
                                    tonKilometer += commodity.getHowMuchIsAllowed()*(commodity.getPlanTon() * block.getLength());
                                }
                            }
                            //add ton kilometer saier
                            if(commodity.getBlocks().size()==0){
                                tonKilometer += commodity.getHowMuchIsAllowed()*(commodity.getPlanTon() * commodity.getDistance());

                            }
                        }
                    }
                    setCell(row2, j, tonKilometer, style);
                    temp += tonKilometer;
                    j++;
                }
                setCell(row2, j, temp, style);
                i++;
            }

//            jame har soton
            row2 = sheet2.createRow(i);
            setCell(row2, 0, "تن کیلومتر مرزی نواحی", style);
            i = 1;
            for (Iterator<String> it1 = districts.iterator(); it1.hasNext(); ) {
                it1.next();
                sumColumn(row2, i, 2, districts.size() + 1, "SUM", style);
                i++;
            }
            sumColumn(row2, i, 2, districts.size() + 1, "SUM", style);

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
