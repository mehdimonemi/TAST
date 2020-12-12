package company.Outputs;

import company.Data.Block;
import company.Data.Commodity;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

import static company.Main.*;
import static company.windows.alert;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutputDistricts extends OutPut {

    private int period = 365;

    public OutputDistricts(String filePath, TreeSet<String> districts, ArrayList<Commodity> commodities, String periodTime) {
        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook workbook;

        if (!periodTime.equals("")) {
            this.period = Integer.parseInt(periodTime);
        }

        ZipSecureFile.setMinInflateRatio(0.001);

        setMassageForWritingFile("Districts");
        try {
            inFile = new FileInputStream(new File(filePath));
            try {
                workbook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                workbook = new XSSFWorkbook();
            }


            XSSFSheet sheet = workbook.createSheet("Districts");
            sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(workbook, "B Zar");

            labeling(workbook, sheet);

            int counter = 2;
            for (String district : districts) {
                row = sheet.createRow(counter);
                if (district.contains("یک")) {
                    int index = district.indexOf(" یک");
                    setCell(row, 0, district.substring(0, index), style);
                } else
                    setCell(row, 0, district, style);
                //bargiri har district
                Double bargiriTon = 0.0;
                Double bargiriTonDay = 0.0;
                Double bargiriWagon = 0.0;
                Double bargiriWagonDay = 0.0;
                for (Commodity commodity : commodities) {
                    if (district.equals(commodity.getOriginDistrict())) {
                        bargiriTon += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                        bargiriTonDay += commodity.getHowMuchIsAllowed() * (commodity.getPlanTon() / period);
                        bargiriWagon += commodity.getHowMuchIsAllowed() * commodity.getPlanWagon();
                        bargiriWagonDay += commodity.getHowMuchIsAllowed() * (commodity.getPlanWagon() / period);
                    }
                }

                setCell(row, 1, bargiriTon, style);
                setCell(row, 2, bargiriTonDay, style);
                setCell(row, 3, bargiriWagon, style);
                setCell(row, 4, bargiriWagonDay, style);

                //takhlie har district
                Double takhlieTon = 0.0;
                Double takhlieTonDay = 0.0;
                Double takhlieWagon = 0.0;
                Double takhlieWagonDay = 0.0;
                for (Commodity commodity : commodities) {
                    if (district.equals(commodity.getDestinationDistrict())) {
                        takhlieTon += commodity.getHowMuchIsAllowed() * commodity.getPlanTon();
                        takhlieTonDay += commodity.getHowMuchIsAllowed() * (commodity.getPlanTon() / period);
                        takhlieWagon += commodity.getHowMuchIsAllowed() * commodity.getPlanWagon();
                        takhlieWagonDay += commodity.getHowMuchIsAllowed() * (commodity.getPlanWagon() / period);
                    }
                }

                setCell(row, 5, takhlieTon, style);
                setCell(row, 6, takhlieTonDay, style);
                setCell(row, 7, takhlieWagon, style);
                setCell(row, 8, takhlieWagonDay, style);

                //ton-kilometr marzi navahi
                double tonKilometer = 0.0;
                double tonKilometerDay = 0.0;
                for (Commodity commodity : commodities) {
                    for (Block block : commodity.getBlocks()) {
                        if (block.getDistrict().equals(district)&& !(commodity.getDistance()==150)) {
                            tonKilometer += commodity.getHowMuchIsAllowed() * (block.getLength() * (commodity.getPlanTon()));
                            tonKilometerDay += commodity.getHowMuchIsAllowed() * ((block.getLength() * (commodity.getPlanTon())) / period);
                            commodity.setCheck(commodity.getCheck()+1);
                        }
                    }
                    //add ton kilometer saier
                    if(commodity.getDistance()==150 && commodity.getOriginDistrict().equals(district)){
                        tonKilometer += commodity.getHowMuchIsAllowed() * (commodity.getDistance() * (commodity.getPlanTon()));
                        tonKilometerDay += commodity.getHowMuchIsAllowed() * ((commodity.getDistance() * (commodity.getPlanTon())) / period);
                        commodity.setCheck(0);
                    }
                }


                setCell(row, 9, tonKilometer, style);
                setCell(row, 10, tonKilometerDay, style);

                //ton-kilometer mabda-maghsad
                tonKilometer = 0.0;
                tonKilometerDay = 0.0;
                for (Commodity commodity : commodities) {
                    if (district.equals(commodity.getOriginDistrict())) {
                        tonKilometer += commodity.getHowMuchIsAllowed() * commodity.getTonKilometerPlan();
                        tonKilometerDay += commodity.getHowMuchIsAllowed() * (commodity.getTonKilometerPlan() / period);
                    }
                }

                setCell(row, 11, tonKilometer, style);
                setCell(row, 12, tonKilometerDay, style);
                counter++;

                //wagon oboori navahi
                double wagon = 0.0;
                double wagonDay = 0.0;
                ComodityLoop:
                for (Commodity commodity : commodities) {
                    for (Block block : commodity.getBlocks()) {
                        if (block.getDistrict().equals(district)) {
                            wagon += commodity.getHowMuchIsAllowed() * commodity.getPlanWagon();
                            wagonDay += commodity.getHowMuchIsAllowed() * (commodity.getPlanWagon() / period);
                            continue ComodityLoop;
                        }
                    }
                }

                setCell(row, 13, wagon, style);
                setCell(row, 14, wagonDay, style);


                //wagon kilometer marzi navahi
                double wagonKilometer = 0.0;
                double wagonKilometerDay = 0.0;
                for (Commodity commodity : commodities) {
                    for (Block block : commodity.getBlocks()) {
                        if (block.getDistrict().equals(district)) {
                            wagonKilometer += commodity.getHowMuchIsAllowed() * (block.getLength() * (commodity.getPlanWagon()));
                            wagonKilometerDay += commodity.getHowMuchIsAllowed() * ((block.getLength() * (commodity.getPlanWagon())) / period);
                        }
                    }
                    //add wagon kilometer saier
                    if(commodity.getBlocks().size()==0){
                        tonKilometer += commodity.getHowMuchIsAllowed() * (commodity.getDistance() * (commodity.getPlanWagon()));
                        tonKilometerDay += commodity.getHowMuchIsAllowed() * ((commodity.getDistance() * (commodity.getPlanWagon())) / period);
                    }
                }
                setCell(row, 15, wagonKilometer, style);
                setCell(row, 16, wagonKilometerDay, style);
            }

            //jame har soton
            row = sheet.createRow(counter);
            setCell(row, 0, "مجموع", style);
            for (int i = 1; i <= 16; i++) {
                sumColumn(row, i, 3, districts.size() + 2, "SUM", style);
                counter++;
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

    private void labeling(XSSFWorkbook workbook, XSSFSheet sheet) {
        row = sheet.createRow(0);

        XSSFCellStyle style = setStyle(workbook, "B Zar");
        Color c = new Color(203, 194, 160);
        XSSFColor headingColor = new XSSFColor(c);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(headingColor);

        setCell(row, 0, "نواحی", style);
        setCell(row, 1, "تناژ بارگيري", style);
        setCell(row, 2, "", style);
        setCell(row, 3, "تعداد واگن بارگیری", style);
        setCell(row, 4, "", style);
        setCell(row, 5, "تناژ تخليه", style);
        setCell(row, 6, "", style);
        setCell(row, 7, "تعداد واگن تخلیه", style);
        setCell(row, 8, "", style);
        setCell(row, 9, "تن كيلومتر مرزي", style);
        setCell(row, 10, "", style);
        setCell(row, 11, "تن كيلومتربارگيري  مبدا-مقصد", style);
        setCell(row, 12, "", style);
        setCell(row, 13, "واگن عبوری", style);
        setCell(row, 14, "", style);
        setCell(row, 15, "واگن کیلومتر", style);
        setCell(row, 16, "", style);

        row = sheet.createRow(1);

        if (period <= 31) {
            for (int i = 1; i <= 16; i++) {
                setCell(row, i, "ماهانه", style);
                setCell(row, ++i, "روزانه", style);
            }
        } else if (period > 31 && period <= 93) {
            for (int i = 1; i <= 16; i++) {
                setCell(row, i, "فصلی", style);
                setCell(row, ++i, "روزانه", style);
            }
        } else if (period > 93 && period <= 186) {
            for (int i = 1; i <= 16; i++) {
                setCell(row, i, "نیم سال", style);
                setCell(row, ++i, "روزانه", style);
            }
        } else {
            for (int i = 1; i <= 16; i++) {
                setCell(row, i, "سالانه", style);
                setCell(row, ++i, "روزانه", style);
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 8));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 10));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 12));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 13, 14));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 15, 16));
    }
}

