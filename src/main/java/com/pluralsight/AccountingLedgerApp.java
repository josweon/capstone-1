package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {
    static Scanner scanner = new Scanner(System.in);
    // TODO: Dates need to be in chronological order (current earliest -> lastest)*
    static ArrayList<Transaction> ledger = new ArrayList<>();
    static DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
       loadTransactions();
        showHomeScreen();
    }

    //region Displays
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
    //endregion

    //region Loaders
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

    public static ArrayList<Transaction> loadDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();
        for (Transaction t: ledger) {
            if (t.isDeposit()) {
                deposits.add(t);
            }
        }
        return deposits;
    }

    public static ArrayList<Transaction> loadPayments() {
        ArrayList<Transaction> payments = new ArrayList<>();
        for (Transaction t: ledger) {
            if (t.isPayment()) {
                payments.add(t);
            }
        }
        return payments;
    }
    //endregion

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

            // TODO: NOW3 recordTransactions, saveTransactions
            switch (option) {
                case "d" -> addDeposit();
                // TODO: NOW2 makePayment
                case "p" -> System.out.println("makePayment()");
                case "l" -> showLedgerScreen();
                case "x" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
        System.out.println("Thank you for using JDL services!");
    }

    // TODO: NOW1 addDeposit
    public static void addDeposit () {
        //*prompt the user for the deposit information and save it to the CSV file
        System.out.println("\t==* A D D * D E P O S I T *==");
        System.out.print("Enter amount: $");
        double amount = promptAmount();
        System.out.print("Enter description: ");
        // TODO: Do while for description to valid
        String description = scanner.nextLine().trim();
        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        LocalDateTime now = LocalDateTime.now();

        Transaction newTrans = new Transaction(now, description, vendor, amount);

        // test newTrans
        System.out.println(newTrans.toString());
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
                // TODO: Change ledger to loadTransactions()?
                case "a" -> displayTransactions(ledger, "a");
                case "d" -> displayTransactions(loadDeposits(), "d");
                case "p" -> displayTransactions(loadPayments(), "p");
                case "r" -> showReportsScreen();
                case "h" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while(!exit);
    }

    // TODO: Report menu screen
    public static void showReportsScreen() {
        String menu = """
                \t\t==* R E P O R T S *==
                \t\t1) Month To Date
                \t\t2) Previous Month
                \t\t3) Year to Date
                \t\t4) Previous Year
                \t\t5) Search by Vendor
                \t\t0) <- Go back
                
                \t\tEnter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                case "1" -> System.out.println("\t\tmonthToDate");
                case "2" -> System.out.println("\t\tpreviousMonth");
                case "3" -> System.out.println("\t\tyearToDate()");
                case "4" -> System.out.println("\t\tdisplayReports()");
                case "5" -> System.out.println("\t\tsearchByVendor()");
                case "0" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while(!exit);
    }
    //endregion
}
