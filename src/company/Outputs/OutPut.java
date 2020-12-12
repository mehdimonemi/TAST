package company.Outputs;

import company.Data.Station;

import company.windows;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;

import static company.Main.cell;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutPut {
    public String successMassage;
    public String failMassage;


    public static XSSFCellStyle setStyle(XSSFWorkbook workbook, String fontName) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setCharSet(FontCharset.ARABIC);
        font.setFontName(fontName);
        style.setFont(font);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    public static void setCell(XSSFRow row, int columnId, String value, CellStyle style) {
        cell = row.createCell(columnId);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static void setCell(XSSFRow row, int columnId, Double value, CellStyle style) {
        cell = row.createCell(columnId);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static void sumColumn(XSSFRow row, int columnId, int firstRow, int SecondRow, String operator, CellStyle style) {
        cell = row.createCell(columnId);
        cell.setCellFormula(operator + "(" + CellReference.convertNumToColString(cell.getColumnIndex()).toString() + firstRow + ":"
                + CellReference.convertNumToColString(cell.getColumnIndex()) + SecondRow + ")");
        cell.setCellStyle(style);
    }

    public static void division(XSSFRow row, int columnId, XSSFCell cell1, XSSFCell cell2, CellStyle style) {
        cell = row.createCell(columnId);
        cell.setCellFormula(CellReference.convertNumToColString(cell1.getColumnIndex()) + Integer.toString(cell1.getRowIndex() + 1) + "/"
                + CellReference.convertNumToColString(cell2.getColumnIndex()) + Integer.toString(cell2.getRowIndex() + 1));
        cell.setCellStyle(style);

    }
    public static void increasePercentage(XSSFRow row, int columnId, XSSFCell cell1, XSSFCell cell2, CellStyle style) {
        cell = row.createCell(columnId);
        cell.setCellFormula(
                ("("+CellReference.convertNumToColString(cell1.getColumnIndex()) + Integer.toString(cell1.getRowIndex() + 1)+"-"+
                        CellReference.convertNumToColString(cell2.getColumnIndex()) + Integer.toString(cell2.getRowIndex() + 1))+ ")/"
                + CellReference.convertNumToColString(cell2.getColumnIndex()) + Integer.toString(cell2.getRowIndex() + 1));
        cell.setCellStyle(style);
    }

    public static String districtOf(String station, ArrayList<Station> stations) {
        for (Station station1 : stations) {
            if (station1.getName().equals(station)) {
                return station1.getDistrict();
            }
        }
        return "";
    }

    public void setMassageForWritingFile(String outputName){
        successMassage = "making the " + outputName + " file" + "----Done";
        failMassage = "Error in making the " + outputName + " file";
    }

    public void setMassageForReadingFile(String outputName){
        successMassage = "Reading the " + outputName + " file" + "----Done";
        failMassage = "Error in Reading the " + outputName + " file";
    }
    public void setMassageForAnalyseNames(String outputName){
        successMassage = "Analyse the " + outputName + " names" + "----Done";
        failMassage = "Error in Analyse the " + outputName + " names";
    }

    public void successDisplay(){
        windows.alert(successMassage);
    }

    public void failDisplay(Exception e) {
        windows.alert(failMassage, e);
    }
}
