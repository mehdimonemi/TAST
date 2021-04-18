package company.Outputs;

import company.Data.Block;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

import static company.Assignment.*;

/**
 * Created by Monemi_M on 01/21/2018.
 */
public class OutputAssignment extends OutPut {


    public OutputAssignment(String output, String formatFile, ArrayList<Block> outputBlocks, ArrayList<Block> blocks) {

        FileInputStream formatFileIn;
        XSSFWorkbook formatWorkBook;

        FileInputStream inFile;
        FileOutputStream outFile;
        XSSFWorkbook mainWorkBook;
        setMassageForWritingFile("Assignment");

        try {
            formatFileIn = new FileInputStream(new File(formatFile));
            formatWorkBook = new XSSFWorkbook(formatFileIn);

            inFile = new FileInputStream(new File(output));
            try {
                mainWorkBook = new XSSFWorkbook(inFile);
            } catch (EmptyFileException e) {
                mainWorkBook = new XSSFWorkbook();
            }

            XSSFSheet sheet1 = formatWorkBook.getSheetAt(4);
            sheet1.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            XSSFSheet sheet2 = mainWorkBook.createSheet("Assignment");
            sheet2.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

            CellStyle style = setStyle(formatWorkBook, "B Zar");

            int counter = 1;
            for (Block outputBlock : outputBlocks) {
                row = sheet1.getRow(counter);
                for (int i = 0; i < blocks.size(); i++) {
                    if (blocks.get(i).getDirection() == 1) {
                        if (outputBlock.getOriginId() == blocks.get(i).getOriginId() && outputBlock.getDestinationId() == blocks.get(i).getDestinationId()) {
                            if (blocks.get(i).getTrack() == 1) {
                                setCell(row, 4, blocks.get(i).getDemandWentPlanTon(), style);
                                setCell(row, 5, blocks.get(i).getDemandBackPlanTon(), style);
                                setCell(row, 6, blocks.get(i).getDemandBackPlanTon() + blocks.get(i).getDemandWentPlanTon(), style);

                                setCell(row, 7, blocks.get(i).getDemandWentPlanWagon(), style);
                                setCell(row, 8, blocks.get(i).getDemandBackPlanWagon(), style);
                                setCell(row, 9, blocks.get(i).getDemandBackPlanWagon() + blocks.get(i).getDemandWentPlanWagon(), style);

                                setCell(row, 10, blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                setCell(row, 11, blocks.get(i).getDemandBackPlanTonKilometer(), style);
                                setCell(row, 12, blocks.get(i).getDemandBackPlanTonKilometer() + blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                division(row, 13, row.getCell(12), row.getCell(6), style);
                                setCell(row, 14, blocks.get(i).getCapacity(), style);
                            }
                            if (blocks.get(i).getTrack() == 2) {
                                setCell(row, 4, blocks.get(i).getDemandWentPlanTon(), style);
                                setCell(row, 5, 0.0, style);
                                setCell(row, 6, blocks.get(i).getDemandWentPlanTon(), style);

                                setCell(row, 7, blocks.get(i).getDemandWentPlanWagon(), style);
                                setCell(row, 8, 0.0, style);
                                setCell(row, 9, blocks.get(i).getDemandWentPlanWagon(), style);

                                setCell(row, 10, blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                setCell(row, 11, 0.0, style);
                                setCell(row, 12, blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                division(row, 13, row.getCell(12), row.getCell(6), style);
                                setCell(row, 14, blocks.get(i).getCapacity(), style);
                            }
                        }
                        if (outputBlock.getOriginId() == blocks.get(i).getDestinationId() && outputBlock.getDestinationId() == blocks.get(i).getOriginId()) {
                            if (blocks.get(i).getTrack() == 1) {
                                setCell(row, 5, blocks.get(i).getDemandWentPlanTon(), style);
                                setCell(row, 4, blocks.get(i).getDemandBackPlanTon(), style);
                                setCell(row, 6, blocks.get(i).getDemandBackPlanTon() + blocks.get(i).getDemandWentPlanTon(), style);

                                setCell(row, 8, blocks.get(i).getDemandWentPlanWagon(), style);
                                setCell(row, 7, blocks.get(i).getDemandBackPlanWagon(), style);
                                setCell(row, 9, blocks.get(i).getDemandBackPlanWagon() + blocks.get(i).getDemandWentPlanWagon(), style);

                                setCell(row, 11, blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                setCell(row, 10, blocks.get(i).getDemandBackPlanTonKilometer(), style);
                                setCell(row, 12, blocks.get(i).getDemandBackPlanTonKilometer() + blocks.get(i).getDemandWentPlanTonKilometer(), style);
                                division(row, 13, row.getCell(12), row.getCell(6), style);
                                setCell(row, 14, blocks.get(i).getCapacity(), style);

                            }
                            if (blocks.get(i).getTrack() == 2) {
                                setCell(row, 5, blocks.get(i).getDemandBackPlanTon(), style);
                                setCell(row, 4, 0.0, style);
                                setCell(row, 6, blocks.get(i).getDemandBackPlanTon(), style);

                                setCell(row, 8, blocks.get(i).getDemandBackPlanWagon(), style);
                                setCell(row, 7, 0.0, style);
                                setCell(row, 9, blocks.get(i).getDemandBackPlanWagon(), style);

                                setCell(row, 11, blocks.get(i).getDemandBackPlanTonKilometer(), style);
                                setCell(row, 10, 0.0, style);
                                setCell(row, 12, blocks.get(i).getDemandBackPlanTonKilometer(), style);
                                division(row, 13, row.getCell(12), row.getCell(6), style);
                                setCell(row, 14, blocks.get(i+1).getCapacity(), style);

                            }
                        }
                    }
                }

                if ((outputBlock.getOrigin().equals("میمند") && outputBlock.getDestination().equals("احمدآباد")) ||
                        (outputBlock.getOrigin().equals("میرجاوه") && outputBlock.getDestination().equals("مرز(ميل72)")) ||
                        (outputBlock.getOrigin().equals("سیوند") && outputBlock.getDestination().equals("شیراز"))
                ) {
                    setCell(row, 4, sheet1.getRow(counter - 1).getCell(4).getNumericCellValue(), style);
                    setCell(row, 5, sheet1.getRow(counter - 1).getCell(5).getNumericCellValue(), style);
                    setCell(row, 6, sheet1.getRow(counter - 1).getCell(6).getNumericCellValue(), style);
                    setCell(row, 7, sheet1.getRow(counter - 1).getCell(7).getNumericCellValue(), style);
                    setCell(row, 8, sheet1.getRow(counter - 1).getCell(8).getNumericCellValue(), style);
                    setCell(row, 9, sheet1.getRow(counter - 1).getCell(9).getNumericCellValue(), style);
                    setCell(row, 10, sheet1.getRow(counter - 1).getCell(10).getNumericCellValue(), style);
                    setCell(row, 11, sheet1.getRow(counter - 1).getCell(11).getNumericCellValue(), style);
                    setCell(row, 12, sheet1.getRow(counter - 1).getCell(12).getNumericCellValue(), style);
                    division(row, 13, row.getCell(12), row.getCell(6), style);
                    setCell(row, 14, sheet1.getRow(counter - 1).getCell(14).getNumericCellValue(), style);

                }
                counter++;
            }
            formatFileIn.close();
            CopySheet newCopy = new CopySheet();
            newCopy.copySheets(sheet2, sheet1, true);
            outFile = new FileOutputStream(new File(output));
            mainWorkBook.write(outFile);

            outFile.flush();
            outFile.close();
            mainWorkBook.close();

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
