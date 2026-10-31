package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Transaction> ledger = new ArrayList<>();
    static DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
       loadTransactions();
//        mainMenu();
        // Test Amount
        double test = promptAmount();
        System.out.print(test);
    }

    public static void loadTransactions() {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
            // TODO: HANDLE first line safely
            bufReader.readLine();

            String input;
            while((input = bufReader.readLine()) != null){
                // TODO: HANDLE missing delimiter
                String[] tokens = input.split("\\|");

                // date|time|description|vendor|amount
                String formatDateTime = String.format("%sT%s", tokens[0], tokens[1]);
                LocalDateTime dateTime = LocalDateTime.parse(formatDateTime);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);

                ledger.add(new Transaction(dateTime, description, vendor, amount));
            }
            bufReader.close();

        } catch (IOException e) {
            System.out.println("ERROR: Cannot read file" + e.getMessage());
        }
    }

    public static void printTransactions(ArrayList<Transaction> transactions) {
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);
        String heading = " ".repeat(30);

        System.out.println(heading + "==* L E D G E R *==");
        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t: ledger) {
            System.out.printf( rowFormat,
                    t.getDateTime().format(dateOnly),
                    t.getDateTime().format(timeOnly),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
        System.out.println(separator);
    }

    public static String promptText() {
        // TODO: HANDLE non-string types?
        String text = scanner.nextLine();
        return text.trim().toLowerCase();
    }

    public static double promptAmount() {
        // TODO: PARSE to .2f during input?
        String text = null;
        double amount = 0;

        try {
            text = scanner.nextLine().trim();
            amount = Double.parseDouble(text);

        } catch (NumberFormatException e) {
            System.out.println("ERROR: Input not <double>");
        }

        return amount;
    }

    public static void mainMenu() {
        String menu = """
                D) Add Deposit
                P) Make Payment (Debit)
                L) Ledger
                X) Exit
                
                Enter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);

            String option = promptText();

            switch (option) {
                case "d" -> System.out.println("addDeposit()");
                case "p" -> System.out.println("makePayment()");
                case "l" -> printTransactions(ledger);
                case "x" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
        System.out.println("Thank you for using JDL services!");
    }
}
