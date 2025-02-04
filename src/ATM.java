import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ATM {
    private BigDecimal countMoney;
    private Map<Integer, Integer> banknotes;

    public ATM(BigDecimal money){
        this.countMoney = money;
    }


    public ATM(BigDecimal money, Map<Integer, Integer> banknotes) {
        this.countMoney = money;
        this.banknotes = banknotes;
    }

    public ATM() {
    }

    public void setCountMoney(BigDecimal countMoney){
        this.countMoney = countMoney;
    }

    public void setBanknotes(Map<Integer, Integer> banknotes) {
        this.banknotes = banknotes;
    }

    public BigDecimal getCountMoney() {
        return countMoney;
    }

    public Map<Integer, Integer> getBanknotes() {
        return banknotes;
    }

    public BigDecimal topUpMoney(BigDecimal useMoney){
        if (useMoney == null) {
            throw new IllegalArgumentException("Ошибка: сумма для пополнения не может быть null.");
        }
        this.countMoney = this.countMoney.add(useMoney);
        return this.countMoney;
    }

    public BigDecimal withdrawMoney(BigDecimal useMoney) {
        if (useMoney == null) {
            throw new IllegalArgumentException("Ошибка: сумма для снятия не может быть null.");
        }
        if (useMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Ошибка: сумма для снятия не может быть отрицательной.");
        }
        if (useMoney.compareTo(this.countMoney) > 0) {
            throw new IllegalArgumentException("Ошибка: недостаточно средств для снятия.");
        }
        this.countMoney = this.countMoney.subtract(useMoney);
        return this.countMoney;
    }

    public void recordFile(){
        try {
            File file = new File("MoneyATM.txt"); //
            if (!file.exists()) file.createNewFile();
            PrintWriter pw = new PrintWriter(file);
            Map<Integer, Integer> sortedMap = new TreeMap<>(this.banknotes);
            for (Map.Entry<Integer, Integer> entry :sortedMap.entrySet()) {
                pw.println(entry.getKey() + ", " + entry.getValue());
            }
            pw.close();
        } catch (IOException ex){
            System.out.println("Ошибка записи файла. " + ex);
        }
    }

    /*
    public Map<Integer, Integer> readFile(){
        Map<Integer, Integer> banknotes = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("MoneyATM.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",\\s*");

                if (parts.length == 2) {
                    Integer kup = Integer.parseInt(parts[0]);
                    Integer count = Integer.parseInt(parts[1]);

                    banknotes.put(kup, count);
                }
            }
            countingMoney(banknotes);
            banknotes = new TreeMap<>(banknotes);
        }catch(IOException ex){
            System.out.println("Ошибка чтения файла. " + ex);
        }
        return this.banknotes = banknotes;
    }*/

    public Map<Integer, Integer> topUpMap(Map<Integer, Integer> useBanknotes){
        Map<Integer, Integer> result = new HashMap<>(this.banknotes);

        for (Map.Entry<Integer, Integer> entry : useBanknotes.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return this.banknotes = result;
    }

    public Map<Integer, Integer> withdrawBanknotes(Map<Integer, Integer> useBanknotes){
        Map<Integer, Integer> result = new HashMap<>(this.banknotes);

        for (Map.Entry<Integer, Integer> entry : useBanknotes.entrySet()) {
            result.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
        return this.banknotes = result;
    }

    public Map<Integer, Integer> processingAnArrayOfData(String[] arrayOfData){
        Map<Integer, Integer> banknotes = new HashMap<>();
        try{
            for (int i = 0; i < arrayOfData.length; i++) {
                String[] parts = arrayOfData[i].trim().split(",\\s*");

                if (parts.length == 2) {
                    Integer kup = Integer.parseInt(parts[0]);
                    Integer count = Integer.parseInt(parts[1]);

                    banknotes.put(kup, count);
                }
            }
            countingMoney(banknotes);
            banknotes = new TreeMap<>(banknotes);
        }catch(Exception ex){
            System.out.println("Ошибка: " + ex);
        }
        return this.banknotes = banknotes;
    }

    public BigDecimal countingMoney(Map<Integer, Integer> banknotes){
        BigDecimal useMoney = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
            useMoney = useMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return this.countMoney = useMoney;
    }

    public Map<Integer, Integer> subtractBanknotes(Map<Integer, Integer> useBanknotesMap) {
        Map<Integer, Integer> resultMap = new HashMap<>(this.banknotes);
        Integer key, value;

        for (Map.Entry<Integer, Integer> entry : useBanknotesMap.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();

            if (resultMap.containsKey(key)) {
                int sumKeyValue = resultMap.get(key) - value;
                if (sumKeyValue < 0) {
                    return new HashMap<>();
                }
                resultMap.put(key, sumKeyValue);
            }
        }
        return resultMap;
    }


    public static void main(String[] args) {

        Map<Integer, Integer> mapOne = new HashMap<>();
        mapOne.put(50, 5);
        mapOne.put(100, 7);
        mapOne.put(15, 7);

        FileHandler handler = new FileHandler();
        String[] array = handler.readFile(handler.getFILE_NAME_USERDETAILS());

        ATM atm = new ATM();
        atm.processingAnArrayOfData(array);

        System.out.println("mapTwo");
        for (Map.Entry<Integer, Integer> entry : mapOne.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        Map<Integer, Integer> sortedMap = new TreeMap<>(mapOne);
        for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}