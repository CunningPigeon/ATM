import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;
import java.util.TreeMap;


class Main {
    public static void main(String[] args) {
        ATM myATM = new ATM(null);
        Map<Integer, Integer> banknotesMap = new HashMap<>();

        BufferedReader br = null;
        UserDetails myUser = new UserDetails(null, null, null, null);
        myUser.readFile();
        String[] arrUsers = myUser.getArrUserdetails();
        Scanner scanner = new Scanner(System.in);
        int input;
        int inputTwo;
        BigDecimal useMoney = BigDecimal.ZERO;
        boolean loopOne = true, loopTwo = true;


        while(loopOne){

            try {
                System.out.print("Введите номер карты/счета: ");
                input = scanner.nextInt();
                System.out.print("Введите PIN карты/счета: ");
                inputTwo = scanner.nextInt();
                for(String arrUser : arrUsers) {
                    String[] user = arrUser.split(",\\s*");
                    if (String.valueOf(input).equals(user[2])) {
                        if(String.valueOf(inputTwo).equals(user[3])){
                            long id = Long.valueOf(user[0]);
                            String fullname = user[1];
                            String cardNumber = user[2];
                            int pin = Integer.valueOf(user[3]);
                            BigDecimal balance = new BigDecimal(user[4]);
                            myUser = new UserDetails(id, fullname, cardNumber, pin, balance);
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
        while (loopTwo){
            System.out.println("Выберите операцию над балансом: \n\t0 Просмотр\n\t1 Пополнение\n\t2 Снятие\n\t3 Выход");

            try {
                myATM.readFile();
                myUser.readFile();
                for (Map.Entry<Integer, Integer> entry : myATM.getBanknotes().entrySet()) {
                    banknotesMap.put(entry.getKey(), 0);
                }
                banknotesMap = new TreeMap<>(banknotesMap);

                BigDecimal moneyATM = myATM.getMoney();
                input = scanner.nextInt();

                switch (input) {
                    case (0):
                        BigDecimal balance = myUser.getBalance();
                        System.out.println("Пользователь " + myUser.getFullName());
                        System.out.println("Ваш баланс: " + balance);
                        break;
                    case (1):
                        useMoney = BigDecimal.ZERO;
                        System.out.println("Купюры для пополнения:");
                        boolean countBanknotesFlag = true;
                        for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                            System.out.print(entry.getKey() + ": ");
                            int countBanknotes = scanner.nextInt();

                            BigDecimal banknotes = BigDecimal.valueOf(entry.getKey());
                            BigDecimal fullValueBanknotes = banknotes.multiply(BigDecimal.valueOf(countBanknotes));
                            useMoney = useMoney.add(fullValueBanknotes);

                            if (countBanknotes < 0) {
                                System.out.println("Ошибка: количество не может быть отрицательной. Пожалуйста, введите положительное значение.");
                                countBanknotesFlag = false;
                                break ;
                            }
                            banknotesMap.put(entry.getKey(), countBanknotes);
                        }
                        if(countBanknotesFlag){
                            myUser.topUp(useMoney);
                            myUser.recordFile();
                            myATM.topUpMoney(useMoney);
                            myATM.topUpMap(banknotesMap);
                            myATM.recordFile();
                            System.out.println("Операция выполнена успешно. Карта пополнена на сумму: " + useMoney);
                        }
                        break;
                    case (2):
                        System.out.print("Сумма для снятия: ");
                        input = scanner.nextInt();
                        useMoney = BigDecimal.ZERO;
                        BigDecimal withdraw = BigDecimal.valueOf(input);
                        boolean withdrawFlag = true;

                        if (withdraw.compareTo(BigDecimal.ZERO) == 0) {
                            System.out.println("Ошибка: Не балуйся.");
                            break;
                        }

                        if (withdraw.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение.");
                            break;
                        }
                        if (withdraw.compareTo(moneyATM) > 0) {
                            System.out.println("Ошибка: суммы нет в наличии.");
                            break;
                        }
                        if (withdraw.compareTo(myUser.getBalance()) > 0) {
                            System.out.println("Ошибка: недостаточно средств.");
                            break;
                        }

                        for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                            System.out.print(entry.getKey() + ": ");
                            int countBanknotes = scanner.nextInt();

                            if (countBanknotes < 0) {
                                System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение.");
                                break;
                            }

                            BigDecimal banknotes = BigDecimal.valueOf(entry.getKey());
                            BigDecimal fullValueBanknotes = banknotes.multiply(BigDecimal.valueOf(countBanknotes));
                            useMoney = useMoney.add(fullValueBanknotes);
                            banknotesMap.put(entry.getKey(), countBanknotes);

                            if (useMoney.compareTo(withdraw) == 0) break;
                        }

                        if (useMoney.compareTo(withdraw) != 0) {
                            withdrawFlag = false;
                            System.out.println("Ошибка: сумма купюр не соответствует сумме снятия.");
                            break;
                        }
                        Map<Integer, Integer> result = myATM.SubtractMap(banknotesMap);

                        if (result == null) {
                            System.out.println("Ошибка: Снятие невозможно, не хватает банкнот.");
                            break;
                        }
                        if(withdrawFlag){
                            System.out.println("Операция выполнена успешно. С карты снято " + useMoney + ".");
                            for (Map.Entry<Integer, Integer> entry : banknotesMap.entrySet()) {
                                if(entry.getValue() > 0){
                                    System.out.println(entry.getKey() + " x " + entry.getValue());
                                }
                            }
                            myUser.withdraw(withdraw);
                            myUser.recordFile();
                            myATM.withdrawMoney(withdraw);
                            myATM.withdrawMap(banknotesMap);
                            myATM.recordFile();
                        }
                        break;
                    default:
                        loopTwo = false;
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Неверный ввод. Пожалуйста, введите корректную сумму." + ex);
                scanner.next();
            }
        }
    }
}

