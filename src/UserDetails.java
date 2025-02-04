import java.io.*;
import java.math.BigDecimal;

public class UserDetails {
    private Long id;
    private String fullName;
    private String cardNumber;
    private int pin;
    private BigDecimal balance;
    private String[] arrUserdetails;

    public UserDetails(Long id, String fullName, String cardNumber, int pin, BigDecimal balance, String[] arrUserdetails) {
        this.id = id;
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
        this.arrUserdetails = arrUserdetails;
    }

    public UserDetails(Long id, String fullName, String cardNumber, int pin, BigDecimal balance) {
        this.id = id;
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;

    }

    public UserDetails() {}

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getPin() {
        return pin;
    }

    public String[] getArrUserdetails() {
        return arrUserdetails;
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

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setArrUserdetails(String[] arrUserdetails) {
        this.arrUserdetails = arrUserdetails;
    }

    public BigDecimal topUp(BigDecimal useBalance) {
        this.balance = this.balance.add(useBalance);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal useBalance) {
        this.balance = this.balance.subtract(useBalance);
        return this.balance;
    }

    public void recordFile(){
        try {
            File file = new File("UserDetails.txt"); //
            if (!file.exists()) file.createNewFile();
            PrintWriter pw = new PrintWriter(file);

            for (int i = 0; i < this.arrUserdetails.length; i++) {
                String arrUser = this.arrUserdetails[i];
                String[] user = arrUser.split(",\\s*");

                if (String.valueOf(this.id).equals(user[0])) {
                    user[4] = String.valueOf(this.balance);
                    this.arrUserdetails[i] = String.join(", ", user);
                }

                pw.println(this.arrUserdetails[i]);
            }

            pw.close();
        } catch (IOException ex){
            System.out.println("Ошибка записи файла." + ex);
        }
    }
    /*
    public String[] readFile(){
        String[] parts = new String[0];

        try (var br = new BufferedReader(new FileReader("UserDetails.txt"))) {
            var lines = br.lines();

            parts = lines
                    .toArray(String[]::new);

        } catch (IOException ex){
            System.out.println("Ошибка чтения файла. " + ex);
        }

        return this.arrUserdetails = parts;
    }*/

    public boolean isValidCardNumber (String cardNumber) {
        int charSum = 0;
        for (int i = cardNumber.length() - 1; i >= 0; i--){
            int charSym = Character.getNumericValue(cardNumber.charAt(i));
            if ((cardNumber.length() - 1 - i) % 2 == 1) {
                charSym = charSym * 2;
                charSym = (charSym > 9) ? charSym - 9 : charSym;
            }
            charSum += charSym;
        }
        if(charSum % 10 == 0) {
            return true;
        }
        else{
            return false;
        }
    }

    public static void main(String[] args) {
        UserDetails myUser = new UserDetails();
        System.out.println(myUser.isValidCardNumber("5062821734567892"));
        System.out.println(myUser.isValidCardNumber("5062821234567892"));

    }
}

