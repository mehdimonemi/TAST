import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class updater {
    public static void main(String[] args) {
        try {
            Thread.sleep(2000);
            File folder = new File(".\\temp\\");
            File[] listOfFiles1 = folder.listFiles();
            for (int i = 0; i < listOfFiles1.length; i++) {
                File fileOnShare = new File(".\\temp\\" + listOfFiles1[i].getName());
                if (fileOnShare.isDirectory()) {
                    File[] listOfFiles2 = fileOnShare.listFiles();
                    for (int j = 0; j < listOfFiles2.length; j++) {
                        File fileOnShare2 = new File(".\\temp\\" + listOfFiles1[i].getName()+"\\" + listOfFiles2[j].getName());
                        File fileOnPc = new File(".\\" + listOfFiles1[i].getName()+"\\" + listOfFiles2[j].getName());
                        Files.move(fileOnShare2.toPath(), fileOnPc.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    File fileOnPc = new File(".\\" + listOfFiles1[i].getName());
                    Files.move(fileOnShare.toPath(), fileOnPc.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}
