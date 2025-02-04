import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FileHandler {
    private final String FILE_NAME_USERDETAILS = "UserDetails.txt";
    private final String FILE_NAME_ATM = "MoneyATM.txt";

    public String getFILE_NAME_USERDETAILS() {
        return FILE_NAME_USERDETAILS;
    }

    public String getFILE_NAME_ATM() {
        return FILE_NAME_ATM;
    }

    public String[] readFile(String nameFile){
        String[] arrayOfData = new String[0];

        try (var br = new BufferedReader(new FileReader(nameFile))) {
            var lines = br.lines();

            arrayOfData = lines
                    .toArray(String[]::new);

        } catch (IOException ex){
            System.out.println("Ошибка чтения файла. " + ex);
        }

        return arrayOfData;
    }

    public static void main(String[] args) {
        FileHandler handler = new FileHandler();

        String[] list = handler.readFile(handler.FILE_NAME_ATM);

        for(String x : list){
            System.out.println(x);
        }
    }
}
