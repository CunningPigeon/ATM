import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

interface Command {
    void execute();
}

class ViewBalanceCommand implements Command {
    private UserDetails user;

    public ViewBalanceCommand(UserDetails user) {
        this.user = user;
    }

    @Override
    public void execute() {
        System.out.println("Пользователь " + user.getFullName());
        System.out.println("Ваш баланс: " + user.getBalance());
    }
}

class TopUpCommand implements Command {
    private ATM atm;
    private UserDetails user;
    private Map<Integer, Integer> banknotesMap;

    public TopUpCommand(ATM atm, UserDetails user, Map<Integer, Integer> banknotesMap) {
        this.atm = atm;
        this.user = user;
        this.banknotesMap = banknotesMap;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            BigDecimal useAmount = BigDecimal.ZERO;
            System.out.println("Купюры для пополнения:");
            boolean countBanknotesFlag = true;

            for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                System.out.print(entry.getKey() + ": ");
                int countBanknotes = scanner.nextInt();

                BigDecimal banknotes = BigDecimal.valueOf(entry.getKey());
                BigDecimal fullValueBanknotes = banknotes.multiply(BigDecimal.valueOf(countBanknotes));
                useAmount = useAmount.add(fullValueBanknotes);

                if (countBanknotes < 0) {
                    System.out.println("Ошибка: количество не может быть отрицательной. Пожалуйста, введите положительное значение.");
                    countBanknotesFlag = false;
                    break;
                }
                banknotesMap.put(entry.getKey(), countBanknotes);
            }
            if(countBanknotesFlag) {
                user.topUp(useAmount);
                user.recordFile();
                atm.topUpMoney(useAmount);
                atm.topUpMap(banknotesMap);
                atm.recordFile();
                System.out.println("Операция выполнена успешно. Карта пополнена на сумму: " + useAmount);
            }
        }catch (Exception ex) {
            System.out.println("Неверный ввод. Пожалуйста, введите корректную сумму." + ex);
            scanner.next();
        }


    }
}

class WithdrawCommand implements Command {
    private ATM atm;
    private UserDetails user;
    private Map<Integer, Integer> banknotesMap;

    public WithdrawCommand(ATM atm, UserDetails user, Map<Integer, Integer> banknotesMap) {
        this.atm = atm;
        this.user = user;
        this.banknotesMap = banknotesMap;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Сумма для снятия: ");
            int input = scanner.nextInt();
            BigDecimal useAmount = BigDecimal.ZERO;
            BigDecimal moneyATM = atm.getCountMoney();
            BigDecimal withdraw = BigDecimal.valueOf(input);
            boolean withdrawFlag = true;

            if (withdraw.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Ошибка: Не балуйся.");
            }

            if (withdraw.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение.");
            }
            if (withdraw.compareTo(moneyATM) > 0) {
                throw new IllegalArgumentException("Ошибка: суммы нет в наличии.");
            }
            if (withdraw.compareTo(user.getBalance()) > 0) {
                throw new IllegalArgumentException("Ошибка: недостаточно средств.");
            }

            for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                System.out.print(entry.getKey() + ": ");
                int countBanknotes = scanner.nextInt();

                if (countBanknotes < 0) {
                    throw new IllegalArgumentException("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение.");
                }

                BigDecimal banknotes = BigDecimal.valueOf(entry.getKey());
                BigDecimal fullValueBanknotes = banknotes.multiply(BigDecimal.valueOf(countBanknotes));
                useAmount = useAmount.add(fullValueBanknotes);
                banknotesMap.put(entry.getKey(), countBanknotes);

                if (useAmount.compareTo(withdraw) == 0) break;
            }

            if (useAmount.compareTo(withdraw) != 0) {
                withdrawFlag = false;
                throw new IllegalArgumentException("Ошибка: сумма купюр не соответствует сумме снятия.");
            }
            Map<Integer, Integer> result;
            result = atm.subtractBanknotes(banknotesMap);

            if (result.isEmpty()) {
                throw new IllegalArgumentException("Ошибка: Снятие невозможно, не хватает банкнот.");
            }
            if (withdrawFlag) {
                System.out.println("Операция выполнена успешно. С карты снято " + useAmount + ".\nБанкноты для выдачи:");
                for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                    if (entry.getValue() > 0) {
                        System.out.println(entry.getKey() + " x " + entry.getValue());
                    }
                }
                user.withdraw(withdraw);
                user.recordFile();
                atm.withdrawMoney(withdraw);
                atm.withdrawBanknotes(banknotesMap);
                atm.recordFile();
            }
        } catch(IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch(Exception ex){
            System.out.println("Неверный ввод. Пожалуйста, введите корректную сумму." + ex);
            scanner.next();
        }
    }
}

class Menu {
    private final Map<Integer, Command> commands = new HashMap<>();

    public void addCommand(int option, Command command) {
        commands.put(option, command);
    }

    public void executeCommand(int option) {
        Command command = commands.get(option);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
        }
    }
}


public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();

        ATM myATM = new ATM();
        UserDetails myUser = new UserDetails();
        FileHandler handler = new FileHandler();
        String[] arrayOfData;
        Map<Integer, Integer> banknotesMap = new HashMap<>();

        arrayOfData = handler.readFile(handler.getFILE_NAME_ATM());
        myATM.processingAnArrayOfData(arrayOfData);
        arrayOfData = handler.readFile(handler.getFILE_NAME_USERDETAILS());
        myUser.setArrUserdetails(arrayOfData);
        String[] arrUsers = myUser.getArrUserdetails();
        Scanner scanner = new Scanner(System.in);

        for (Map.Entry<Integer, Integer> entry : myATM.getBanknotes().entrySet()) {
            banknotesMap.put(entry.getKey(), 0);
        }
        banknotesMap = new TreeMap<>(banknotesMap);

        BigInteger inputBigInteger;
        int inputTwo;
        boolean loopOne = true;


        while(loopOne){
            try {
                System.out.print("Введите номер карты/счета: ");
                inputBigInteger = scanner.nextBigInteger();
                if(!myUser.isValidCardNumber(String.valueOf(inputBigInteger))) throw new Exception();
                System.out.print("Введите PIN карты/счета: ");
                inputTwo = scanner.nextInt();
                for(String arrUser : arrUsers) {
                    String[] user = arrUser.split(",\\s*");
                    if (String.valueOf(inputBigInteger).equals(user[2])) {
                        if(String.valueOf(inputTwo).equals(user[3])){
                            long id = Long.valueOf(user[0]);
                            String fullname = user[1];
                            String cardNumber = user[2];
                            int pin = Integer.valueOf(user[3]);
                            BigDecimal balance = new BigDecimal(user[4]);
                            myUser = new UserDetails(id, fullname, cardNumber, pin, balance, arrUsers);


                            menu.addCommand(1, new ViewBalanceCommand(myUser));
                            menu.addCommand(2, new TopUpCommand(myATM, myUser, banknotesMap));
                            menu.addCommand(3, new WithdrawCommand(myATM, myUser, banknotesMap));
                            break;
                        }
                    }
                }

                if(myUser.getId() == null){
                    System.out.println("Неверный номер карты или PIN. Повторите попытку...");
                } else
                {
                    System.out.println("Пользователь " + myUser.getFullName());
                    loopOne = false;
                }
            } catch (Exception ex){
                System.out.println("Неверный ввод. Пожалуйста, введите корректно номер карты/счета." + ex);
                scanner.next();
            }
        }

        int choice;

        do {
            System.out.println("Выберите операцию над балансом: \n\t1 Просмотр\n\t2 Пополнение\n\t3 Снятие\n\t0 Выход");

            choice = scanner.nextInt();

            if (choice != 0) {
                menu.executeCommand(choice);
            }

        } while (choice != 0);

        System.out.println("Выход из программы.");
        scanner.close();
    }
}



