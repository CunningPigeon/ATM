import java.io.*;
import java.math.BigDecimal;

public class UserDetails {
    private Long id;
    private String fullName;
    private BigDecimal balance; // TODO: BigDecimal

    public UserDetails(Long id, String fullName, BigDecimal balance) {
        this.id = id;
        this.fullName = fullName;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal topUp(BigDecimal useBalance) {
        this.balance = this.balance.add(useBalance);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal useBalance) {
        this.balance = this.balance.subtract(useBalance);
        return this.balance;
    }

    public void record(){
        try {
            File file = new File("UserDetails.txt"); //
            if (!file.exists()) file.createNewFile();
            PrintWriter pw = new PrintWriter(file);

            pw.println(this.id.toString() + ", " + this.fullName + ", " +  this.balance.toString());
            pw.close();
        } catch (IOException ex){
            System.out.println("Сфеср"); // TODO: message
        }
    }

    public void read(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("UserDetails.txt"));
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",\\s*");
                this.setId(Long.valueOf(parts[0]));
                this.setFullName(parts[1]);
                this.setBalance(new BigDecimal(parts[2]));
            }
        } catch (IOException ex){
            System.out.println("Сфеср"); // TODO: message
        }
    }
}
