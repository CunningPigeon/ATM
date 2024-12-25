import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ATM {
    private BigDecimal money;
    private Map<Integer, Integer> banknotes;

    public ATM(BigDecimal money){
        this.money = money;
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

    public BigDecimal topUp(BigDecimal useMoney){
        this.money = this.money.add(useMoney);
        return this.money;
    }

    public BigDecimal withdraw(BigDecimal useMoney) {
        this.money = this.money.subtract(useMoney);
        return this.money;
    }

    public void record(){
        try {
            File file = new File("MoneyATM.txt"); //
            if (!file.exists()) file.createNewFile();
            PrintWriter pw = new PrintWriter(file);
            for (Map.Entry<Integer, Integer> entry : this.banknotes.entrySet()) {
                pw.println(entry.getKey() + ", " + entry.getValue());
            }
            pw.close();
        } catch (IOException ex){
            System.out.println("Сфеср"); // TODO: message
        }
    }

    public Map<Integer, Integer> read(){
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
        }catch(IOException ex){
            System.out.println("Сфеср"); // TODO: message
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
//            System.out.println(entry.getKey() + ", " + entry.getValue());
            useMoney = useMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())));
//            System.out.println(useMoney);
        }
        return this.money = useMoney;
    }

    // Сравнение и вычитание двух словарей
    public Map<Integer, Integer> compareAndSubtract(Map<Integer, Integer> dict2) {
        Map<Integer, Integer> result = new HashMap<>(this.banknotes);
        Integer key;
        Integer valueToSubtract;

        // Проверяем, возможно ли вычитание
        for (Map.Entry<Integer, Integer> entry : dict2.entrySet()) {
            key = entry.getKey();
            valueToSubtract = entry.getValue();

            // Если ключ существует в первом словаре, проверяем, не станет ли значение отрицательным
            if (result.containsKey(key)) {
                int newValue = result.get(key) - valueToSubtract;
                if (newValue < 0) {
                    return null;
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : dict2.entrySet()) {
            key = entry.getKey();
            valueToSubtract = entry.getValue();

            result.merge(key, -valueToSubtract, (oldValue, subtractValue) -> Math.max(oldValue + subtractValue, 0));
        }

        return result;
    }


    public static void main(String[] args) {
        ATM myATM = new ATM(null);
        BigDecimal money = BigDecimal.ZERO;
        // Перебор элементов
        myATM.read();
        System.out.println(myATM.getMoney());
        myATM.record();

        for (Map.Entry<Integer, Integer> entry : myATM.getBanknotes().entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
    }
}