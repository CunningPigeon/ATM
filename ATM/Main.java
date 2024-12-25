import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;
/*
Вопрос:
В файле должен быть 1 пользователь?
Если более одно, то как мне проверять с каким пользователем работать?
 */

class Main {

    public static void main(String[] args) {
        ATM myATM = new ATM(null);
        Map<Integer, Integer> banknotes = new HashMap<>();;
        banknotes.put(5, 0);
        banknotes.put(10, 0);
        banknotes.put(20, 0);
        banknotes.put(50, 0);
        banknotes.put(100, 0);
        banknotes.put(200, 0);
        banknotes.put(500, 0);
        BufferedReader br = null;
        UserDetails myUser = new UserDetails(0L, "Null", null);
        BigDecimal useMoney = BigDecimal.ZERO;

        boolean bool = true;
        while (bool){
            System.out.println("Выберите операцию над балансом: \n\t0 Просмотр\n\t1 Пополнение\n\t2 Снятие\n\t3 Создание\n\t4 Выход");

            try {
                myATM.read();
                myUser.read();
                BigDecimal moneyATM = myATM.getMoney();
                Scanner scanner = new Scanner(System.in);
                int input = scanner.nextInt();

                switch (input) {
                    case (0):
                        BigDecimal balance = myUser.getBalance();
                        moneyATM = myATM.getMoney();
                        System.out.println("Ваш баланс: " + balance);
                        System.out.println("Ваш баланс на ATM: " + moneyATM);
                        // System.out.println("id: "+ myUser.getId() + "\nfullName: "+ myUser.getFullName() + "\nbalance: " + myUser.getBalance());
                        break;
                    case (1):
                        System.out.println("Сумма для пополнения: ");
                        input = scanner.nextInt();
                        BigDecimal sum = BigDecimal.valueOf(input);

                        if (sum.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение."); // TODO: message
                            break;
                        }

                        System.out.println("Купюры для пополнения:");
                        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
                            System.out.print(entry.getKey() + ": ");
                            input = scanner.nextInt();

                            if (input < 0) {
                                System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение."); // TODO: message
                                break;
                            }
                            useMoney = useMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(input)));


                            System.out.println("Доступные деньги: " + useMoney);
                            banknotes.put(entry.getKey(), input);
                            if (useMoney.compareTo(sum) == 0) break;
                        }

                        if (useMoney.compareTo(sum) > 0){
                            System.out.println("Ошибка: сумма купюр больше суммы пополнения.");
                            break;
                        }


                        myUser.topUp(sum);
                        myUser.record();
                        myATM.topUp(sum);
                        myATM.topUpMap(banknotes);
                        myATM.record();
                        break;
                    case (2):
                        System.out.print("Сумма для снятия: ");
                        input = scanner.nextInt();
                        BigDecimal withdraw = BigDecimal.valueOf(input);


                        if (withdraw.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение."); // TODO: message
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

                        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
                            System.out.print(entry.getKey() + ": ");
                            input = scanner.nextInt();

                            if (input < 0) {
                                System.out.println("Ошибка: сумма не может быть отрицательной. Пожалуйста, введите положительное значение."); // TODO: message
                                break;
                            }
                            useMoney = useMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(input)));

                            banknotes.put(entry.getKey(), input);
                            if (useMoney.compareTo(withdraw) == 0) break;
                        }

                        if (useMoney.compareTo(withdraw) > 0){
                            System.out.println("Ошибка: сумма купюр больше суммы снятия.");
                            break;
                        }
                        System.out.println("Вывод словаря");
                        for (Map.Entry<Integer, Integer> entry : banknotes.entrySet()) {
                            System.out.println(entry.getKey() + ", " + entry.getValue());
                        }
                        Map<Integer, Integer> resultDict = myATM.compareAndSubtract(banknotes);

                        if (resultDict == null) {
                            System.out.println("Ошибка: Снятие невозможно, не хватает банкнот.");
                            break;
                        }

                        myUser.withdraw(withdraw);
                        myUser.record();
                        myATM.withdraw(withdraw);
                        myATM.withdrawMap(banknotes);
                        myATM.record();
                        break;
                    default:
                        bool = false;
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Неверный ввод. Пожалуйста, введите корректную сумму." + ex);
            }
        }
    }
}

