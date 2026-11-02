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
        showHomeScreen();
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

    public static void displayTransactions(ArrayList<Transaction> transactions, String transType) {
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);
        String heading = " ".repeat(25);

        String transHeader = "==* T R A N S A C T I O N S *==";
        String depositHeader = "==* D E P O S I T S *==";
        String paymentsHeader = "==* P A Y M E N T S *==";

        switch (transType) {
            case "a" -> System.out.println(heading + transHeader);
            case "d" -> System.out.println(heading + depositHeader);
            case "p" -> System.out.println(heading + paymentsHeader);
            default -> System.out.println("Invalid operation");
        }

        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t: transactions) {
            System.out.printf( rowFormat,
                    t.getDateTime().format(dateOnly),
                    t.getDateTime().format(timeOnly),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
        System.out.println(separator);
    }

    // TODO: displayDeposits()
    public static void displayDeposits() {
        // create displayTransaction that takes in a sorted array
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);
        String heading = " ".repeat(30);

        System.out.println(heading + "==* D E P O S I T S *==");
        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t: ledger) {
            if (t.isDeposit()) {
                System.out.printf(rowFormat,
                        t.getDateTime().format(dateOnly),
                        t.getDateTime().format(timeOnly),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
        }
        System.out.println(separator);
    }

    public static ArrayList<Transaction> loadDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();
        for (Transaction t: ledger) {
            if (t.isDeposit()) {
                deposits.add(t);
            }
        }
        return deposits;
    }

    // TODO: displayPayments()
    // TODO: displayReports()

    //region Prompters
    public static String promptText() {
        // TODO: HANDLE non-string types?
        String text = scanner.nextLine();
        return text.trim().toLowerCase();
    }

    public static double promptAmount() {
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
    //endregion

    //region Menus
    public static void showHomeScreen() {
        String menu = """
                ==* H O M E S C R E E N *=
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
                case "l" -> showLedgerScreen();
                case "x" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
        System.out.println("Thank you for using JDL services!");
    }

    public static void showLedgerScreen() {
        String menu = """
                \t==* L E D G E R *==
                \tA) All
                \tD) Deposits
                \tP) Payments
                \tR) Reports
                \tH) <-Homescreen
                
                \tEnter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                case "a" -> displayTransactions(ledger, "a");
                case "d" -> displayTransactions(loadDeposits(), "deposit");
                case "p" -> System.out.println("\tdisplayPayments()");
                case "r" -> System.out.println("\tdisplayReports()");
                case "h" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while(!exit);
    }
    //endregion
}
