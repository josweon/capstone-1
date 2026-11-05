package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class AccountingLedgerApp {
    //region Initializers
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Transaction> ledger = new ArrayList<>();

    static DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH:mm:ss");

    //    static String PROD = "src/main/resources/transactions.csv";
    static String TEST = "src/main/resources/test-transactions.csv";
    //endregion

    public static void main(String[] args) {
        loadTransactions();
        showHomeScreen();
    }

    //region Displays
    public static void displayTransactions(ArrayList<Transaction> transactions, String transType) {
        if (transactions.isEmpty()) {
            System.out.println("no transactions found...");

        } else {
            String rowFormatHeading = "| %-10s | %-8s | %-30s | %-20s | %-10s | %n";
            String rowFormat = "| %-10s | %-8s | %-30s | %-20s | $%-9.2f | %n";

            String separator = "-".repeat(94);
            String heading = " ".repeat(35);
            String reportsHeading = "\nreport." + " ".repeat(19);
            String searchHeading = "\nfilter applied." + " ".repeat(20);

            String transHeader = "==* T R A N S A C T I O N S *==";
            String depositHeader = "==* D E P O S I T S *==";
            String paymentsHeader = "==* P A Y M E N T S *==";
            String reportMonthToDate = "==* M O N T H * T O * D A T E *==";
            String reportPreviousMonth = "==* P R E V * M O N T H *==";
            String reportYearToDate = "==* Y E A R * T O * D A T E *==";
            String reportPreviousYear = "==* P R E V * Y E A R *==";
            String customHeader = "==* S E A R C H *==";

            switch (transType) {
                case "a" -> System.out.println(heading + transHeader);
                case "d" -> System.out.println(heading + depositHeader);
                case "p" -> System.out.println(heading + paymentsHeader);
                case "mtd" -> System.out.println(reportsHeading + reportMonthToDate);
                case "pm" -> System.out.println(reportsHeading + reportPreviousMonth);
                case "ytd" -> System.out.println(reportsHeading + reportYearToDate);
                case "py" -> System.out.println(reportsHeading + reportPreviousYear);
                case "vs" -> System.out.println("\nvendor search.");
                case "cs" -> System.out.println(searchHeading + customHeader);

                default -> System.out.println("Invalid operation");
            }

            System.out.println(separator);
            System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
            System.out.println(separator);

            for (Transaction t : transactions) {
                System.out.printf(rowFormat,
                        t.getDateTime().format(dateOnly),
                        t.getDateTime().format(timeOnly),
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());
            }
            System.out.println(separator);
        }
    }

    public static void displayReceipt(ArrayList<Transaction> receipt) {
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);

        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t : receipt) {
            System.out.printf(rowFormat,
                    t.getDateTime().format(dateOnly),
                    t.getDateTime().format(timeOnly),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
    }
    //endregion

    //region Loaders
    public static void loadTransactions() {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(TEST));
            bufReader.readLine();

            String input;
            while ((input = bufReader.readLine()) != null) {
                String[] tokens = input.split("\\|");
                if (tokens.length < 5) {
                    System.out.println("Skipping malformed lines: " + input);
                    continue;
                }
                try {
                    String formatDateTime = String.format("%sT%s", tokens[0], tokens[1]);
                    LocalDateTime dateTime = LocalDateTime.parse(formatDateTime);
                    String description = tokens[2];
                    String vendor = tokens[3];
                    double amount = Double.parseDouble(tokens[4]);

                    ledger.add(new Transaction(dateTime, description, vendor, amount));

                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid amount: " + input);
                } catch (DateTimeParseException e) {
                    System.out.println("Skipping line with invalid datetime: " + input);
                }
            }
            bufReader.close();

        } catch (IOException e) {
            System.out.println("ERROR: Cannot read file" + e.getMessage());
        }
    }

    public static ArrayList<Transaction> loadDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();

        for (Transaction t : ledger) {
            if (t.isDeposit()) {
                deposits.add(t);
            }
        }
        return deposits;
    }

    public static ArrayList<Transaction> loadPayments() {
        ArrayList<Transaction> payments = new ArrayList<>();

        for (Transaction t : ledger) {
            if (t.isPayment()) {
                payments.add(t);
            }
        }
        return payments;
    }
    //endregion

    //region Prompters
    public static String promptText() {
        String text = scanner.nextLine();
        return text.trim();
    }

    public static double promptAmount() {
        String text;
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
        Collections.sort(ledger);
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
                case "d" -> addDeposit();
                case "p" -> makePayment();
                case "l" -> showLedgerScreen();
                case "x" -> exit = true;

                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
        System.out.println("Thank you for using our services.");
    }

    public static void showLedgerScreen() {
        Collections.sort(ledger);
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
                case "d" -> displayTransactions(loadDeposits(), "d");
                case "p" -> displayTransactions(loadPayments(), "p");
                case "r" -> showReportsScreen();
                case "h" -> exit = true;

                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
    }

    public static void showReportsScreen() {
        String menu = """
                \t\t==* R E P O R T S *==
                \t\t1) Month To Date
                \t\t2) Previous Month
                \t\t3) Year to Date
                \t\t4) Previous Year
                \t\t5) Search by Vendor
                \t\t6) *BONUS* Custom Search
                \t\t0) <- Go back
                
                \t\tEnter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                case "1" -> displayTransactions(monthToDate(), "mtd");
                case "2" -> displayTransactions(previousMonth(), "pm");
                case "3" -> displayTransactions(yearToDate(), "ytd");
                case "4" -> displayTransactions(previousYear(), "py");
                case "5" -> searchByVendor();
                case "6" -> customSearch();
                case "0" -> exit = true;

                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
    }
    //endregion

    public static void customSearch() {
        System.out.println("\t\t\t==* C U S T O M * S E A R C H *==");
        System.out.println("\t\t\tpress enter to skip field.\n");

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String description;
        String vendor;
        Double amount = null;

        System.out.print("\t\t\tEnter Start Date(yyyy-MM-dd): ");
        String startDateInput = promptText();
        if (!startDateInput.isEmpty()) {
            try {
                startDate = LocalDate.parse(startDateInput, dateOnly).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("\t\t\tERROR: Input is not a DateTime");
            }
        }

        System.out.print("\t\t\tEnter End Date(yyyy-MM-dd): ");
        String endDateInput = promptText();
        if (!endDateInput.isEmpty()) {
            try {
                endDate = LocalDate.parse(endDateInput, dateOnly).atTime(LocalTime.MAX);
            } catch (DateTimeParseException e) {
                System.out.println("\t\t\tERROR: Input is not a DateTime");
            }
        }

        System.out.print("\t\t\tEnter Description: ");
        description = promptText();

        System.out.print("\t\t\tEnter Vendor: ");
        vendor = promptText();

        System.out.print("\t\t\tEnter Amount: $");
        String amountInput = promptText();
        if (!amountInput.isEmpty())
            try {
                amount = Double.parseDouble(amountInput);
            } catch (NumberFormatException e) {
                System.out.println("\t\t\tERROR: Input is not Double");
            }

        filterSearch(startDate, endDate, description, vendor, amount);
    }

    public static void filterSearch(LocalDateTime startDate,
                                    LocalDateTime endDate,
                                    String description,
                                    String vendor,
                                    Double amount) {

        ArrayList<Transaction> filterTrans = new ArrayList<>();

        for (Transaction t : ledger) {
            boolean isMatch = true;

            if (startDate != null) {
                if (t.getDateTime().isBefore(startDate)) {
                    isMatch = false;
                }
            }

            if (isMatch && endDate != null) {
                if (t.getDateTime().isAfter(endDate)) {
                    isMatch = false;
                }
            }

            if (isMatch && description != null && !description.isEmpty()) {
                String transDescription = t.getDescription().toLowerCase();
                if (!transDescription.contains(description.toLowerCase())) {
                    isMatch = false;
                }
            }

            if (isMatch && vendor != null && !vendor.isEmpty()) {
                String transVendor = t.getVendor().toLowerCase();
                if (!transVendor.contains(vendor.toLowerCase())) {
                    isMatch = false;
                }
            }

            if (isMatch && amount != null) {
                if (t.getAmount() != amount) {
                    isMatch = false;
                }
            }

            if (isMatch) {
                filterTrans.add(t);
            }
        }
        if (filterTrans.isEmpty()) {
            System.out.println("\n\t\t\tno matches.");
        } else {
            displayTransactions(filterTrans, "cs");
        }
    }

    //region Reports
    public static ArrayList<Transaction> previousYear() {
        ArrayList<Transaction> report = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now();
        int prevYear = currentDate.getYear() - 1;

        for (Transaction t : ledger) {
            if (t.getDateTime().getYear() == prevYear) {
                report.add(t);
            }
        }
        Collections.sort(report);
        return report;
    }

    public static ArrayList<Transaction> previousMonth() {
        ArrayList<Transaction> report = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime previousMonth = currentDate.minusMonths(1);

        for (Transaction t : ledger) {
            boolean monthCheck = t.getDateTime().getMonth().equals(previousMonth.getMonth());
            boolean yearCheck = t.getDateTime().getYear() == currentDate.getYear();
            if (monthCheck && yearCheck) {
                report.add(t);
            }
        }
        Collections.sort(report);
        return report;
    }

    public static ArrayList<Transaction> yearToDate() {
        ArrayList<Transaction> report = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Transaction t : ledger) {
            if (t.getDateTime().getYear() == currentDate.getYear()) {
                report.add(t);
            }
        }
        Collections.sort(report);
        return report;
    }

    public static ArrayList<Transaction> monthToDate() {
        ArrayList<Transaction> report = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Transaction t : ledger) {
            boolean yearCheck = t.getDateTime().getYear() == currentDate.getYear();
            boolean monthCheck = t.getDateTime().getMonthValue() == currentDate.getMonthValue();
            if (yearCheck && monthCheck) {
                report.add(t);
            }
        }
        Collections.sort(report);
        return report;
    }

    public static void searchByVendor() {
        System.out.println("\t\t==* V E N D O R * S E A R C H *==");
        ArrayList<Transaction> report = new ArrayList<>();

        System.out.print("\t\tSearch for vendor: ");
        String input = promptText();

        for (Transaction t : ledger) {
            if (t.getVendor().toLowerCase().contains(input)) {
                report.add(t);
            }
        }
        displayTransactions(report, "vs");
    }
    //endregion

    //region Helpers
    public static void addDeposit() {
        System.out.println("\t==* A D D * D E P O S I T *==");
        ArrayList<Transaction> receipt = new ArrayList<>();

        String done;
        do {
            double amount = 0;
            String description = null;
            String vendor = null;

            try {

                while (amount == 0) {
                    System.out.print("\tEnter amount: $");
                    amount = promptAmount();
                }

                System.out.print("\tEnter description: ");
                description = scanner.nextLine().trim();

                System.out.print("\tEnter vendor: ");
                vendor = scanner.nextLine().trim();
            } catch (NullPointerException e) {
                System.out.println("\tfields cannot be empty.");
            }
            LocalDateTime now = LocalDateTime.now();

            Transaction newTrans = new Transaction(now, description, vendor, amount);
            if (newTrans.getAmount() == 0) {
                return;
            } else {
                recordTransaction(newTrans, receipt);
            }

            System.out.print("Would you like to add another? (y/n): ");
            done = promptText();

        } while (!done.equalsIgnoreCase("n"));
        System.out.print("\n");

        if (!receipt.isEmpty()) saveTransaction(receipt);
    }

    public static void makePayment() {
        System.out.println("\t==* M A K E * P A Y M E N T *==");
        ArrayList<Transaction> receipt = new ArrayList<>();

        String done;
        do {
            double amount = 0;
            String description = null;
            String vendor = null;

            try {

                System.out.print("\tEnter description: ");
                description = scanner.nextLine().trim();

                System.out.print("\tEnter vendor: ");
                vendor = scanner.nextLine().trim();

                while (amount == 0) {
                    System.out.print("\tEnter amount: $");
                    amount = promptAmount();
                }
            } catch (NullPointerException e) {
                System.out.println("\tfields cannot be empty.");
            }
            LocalDateTime now = LocalDateTime.now();

            Transaction newTrans = new Transaction(now, description, vendor, amount * -1);
            if (newTrans.getAmount() == 0) {
                return;
            } else {
                recordTransaction(newTrans, receipt);
            }

            System.out.print("Would you like to add another? (y/n): ");
            done = promptText();

        } while (!done.equalsIgnoreCase("n"));
        System.out.print("\n");

        if (!receipt.isEmpty()) saveTransaction(receipt);
    }

    public static void saveTransaction(ArrayList<Transaction> receipt) {
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(TEST, true));
            for (Transaction t : receipt) {
                String date = (t.getDateTime()).format(dateOnly);
                String time = (t.getDateTime()).format(timeOnly);
                String record = String.format("%n%s|%s|%s|%s|%.2f",
                        date,
                        time,
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());

                bufWriter.write(record);
            }
            bufWriter.close();
        } catch (IOException e) {
            System.out.println("ERROR: Cannot write to file " + e.getMessage());
        }
    }

    public static void recordTransaction(Transaction t, ArrayList<Transaction> receipt) {
        ledger.add(t);
        receipt.add(t);
        System.out.println("\nsuccess!");
        displayReceipt(receipt);
    }
    //endregion
}
