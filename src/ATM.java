import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ATM {
    private BigDecimal money;
    private Map<Integer, Integer> banknotes;

    public ATM(BigDecimal money){
        this.money = money;
    }


    public ATM(BigDecimal money, Map<Integer, Integer> banknotes) {
        this.money = money;
        this.banknotes = banknotes;
    }

    public void setMoney(BigDecimal money){
        this.money = money;
    }

    public void setBanknotes(Map<Integer, Integer> banknotes) {
        this.banknotes = banknotes;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public Map<Integer, Integer> getBanknotes() {
        return banknotes;
    }

    public BigDecimal topUpMoney(BigDecimal useMoney){
        this.money = this.money.add(useMoney);
        return this.money;
    }

    public BigDecimal withdrawMoney(BigDecimal useMoney) {
        this.money = this.money.subtract(useMoney);
        return this.money;
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
    }

    public Map<Integer, Integer> topUpMap(Map<Integer, Integer> useBanknotes){
        Map<Integer, Integer> result = new HashMap<>(this.banknotes);

        for (Map.Entry<Integer, Integer> entry : useBanknotes.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        return this.banknotes = result;
    }

    public Map<Integer, Integer> withdrawMap(Map<Integer, Integer> useBanknotes){
        Map<Integer, Integer> result = new HashMap<>(this.banknotes);

        for (Map.Entry<Integer, Integer> entry : useBanknotes.entrySet()) {
            result.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
        return this.banknotes = result;
    }

    public BigDecimal countingMoney(Map<Integer, Integer> banknotes){
        BigDecimal useMoney = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
            useMoney = useMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return this.money = useMoney;
    }

    public Map<Integer, Integer> SubtractMap(Map<Integer, Integer> useBanknotesMap) {
        Map<Integer, Integer> resultMap = new HashMap<>(this.banknotes);
        Integer key, value;

        for (Map.Entry<Integer, Integer> entry : useBanknotesMap.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();

            if (resultMap.containsKey(key)) {
                int sumKeyValue = resultMap.get(key) - value;
                if (sumKeyValue < 0) {
                    return null;
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