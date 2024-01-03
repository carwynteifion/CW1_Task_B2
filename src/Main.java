import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // Creates new scanner object to read user input
    Scanner scanner = new Scanner(System.in);
    /* Creates a Map object named config expecting key-value pairs of
    a String (the .csv file to read) and a List of the ConfigData custom class
    */
    Map<String, List<ConfigData>> config;

    public Main(){
        // Reads CSV config file for tax/NI/pension/parking amounts
        config = readCSVFile();
    }

    public static void main(String[] args) {
        // Runs the program
        new Main().run(args);
    }
    public void run(String[] args) {
        /*
        This is the core program. First, displayWelcome() is run. Then, the user inputs
        for name and employee number are collected using getName() and getEmployeeNumber()
        and stored in the values fullName and employeeNumber respectively.
        The getYearlyGrossSalary() method calculates the salary and assigns this to the
        double yearlyGrossSalary. This is then passed through the method
        calculateMonthlySalary to produce the final output.
         */

        displayWelcome();

        String fullName = getName();
        int employeeNumber = getEmployeeNumber();
        double yearlyGrossSalary = getYearlyGrossSalary();

        calculateMonthlySalary(yearlyGrossSalary);
    }

    private static void displayWelcome() {
        // Displays welcome message when the program starts
        System.out.println("\nWelcome to USW Employee Salary Calculator\n");
    }

    private void calculateMonthlySalary(double yearlyGrossSalary) {
        // Gets answers for optional deductions
        String pensionOption = String.valueOf(getPensionOption());
        String parkingFeeOption = String.valueOf(getParkingFeeOption());

        // Confirms to user that pay is being calculated
        System.out.println("\nCalculating Monthly Net Pay....\n");

        // Calculates and displays gross monthly salary
        double grossMonthlySalary = yearlyGrossSalary / 12;
        System.out.printf("Gross salary: %.2f\n", grossMonthlySalary);

        // Calculates and displays monthly teachers' pension if selected by user
        double yearlyTeachersPension = teachersPension(yearlyGrossSalary, config);
        double monthlyTeachersPension = yearlyTeachersPension / 12;
        double taxableAmount = yearlyGrossSalary;
        if (pensionOption.equals("true")) {
            System.out.printf("Pension deductions: %.2f\n", monthlyTeachersPension);
            taxableAmount -= yearlyTeachersPension;
        } else {
            monthlyTeachersPension = 0;
        }

        // Calculates and displays monthly taxable amount and ensures this figure is not negative
        double personalAllowance = 12570;
        double monthlyTaxableAmount = (taxableAmount - personalAllowance) / 12;
        if (monthlyTaxableAmount < 0)
            monthlyTaxableAmount = 0;
        System.out.printf("Taxable amount: %.2f\n", monthlyTaxableAmount);

        // Calculates and displays monthly income tax
        double monthlyIncomeTax = yearlyIncomeTax(taxableAmount, config) / 12;
        System.out.printf("Tax paid: %.2f\n", monthlyIncomeTax);

        // Calculates and displays monthly NI
        double monthlyNI = yearlyNI(taxableAmount, config) / 12;
        System.out.printf("NI paid: %.2f\n", monthlyNI);

        // Displays parking fee if selected by user
        double parkingFee = parkingFee(config);
        if (parkingFeeOption.equals("true")) {
            System.out.printf("Monthly parking fee: %.2f\n\n", parkingFee);
        } else {
            parkingFee = 0;
        }

        // Adds up deductions and displays total (pension and parking equal 0 if not selected)
        double totalDeductions = monthlyNI + monthlyIncomeTax + monthlyTeachersPension + parkingFee;
        System.out.printf("Total deductions: %.2f\n", totalDeductions);

        // Calculates and displays monthly net pay, and ensures answer is not negative
        double monthlyNetPay = grossMonthlySalary - totalDeductions;
        if (monthlyNetPay < 0)
            monthlyNetPay = 0;
        System.out.printf("Monthly Net Pay: %.2f\n", monthlyNetPay);
    }

    private double getYearlyGrossSalary() {
        // Input gross salary; also checks value entered is an int
        double yearlyGrossSalary;
        System.out.print("What is the yearly gross salary (in £) of the employee? ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter yearly gross salary using numeric characters only (0-9).");
            scanner.next();
        }
        yearlyGrossSalary = scanner.nextDouble();
        return yearlyGrossSalary;
    }

    private int getEmployeeNumber() {
        // Input employee number; also checks value entered is an int
        int employeeNumber;
        System.out.print("Please enter your employee number: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter employee number using numeric characters only (0-9).");
            scanner.next();
        }
        employeeNumber = scanner.nextInt();
        return employeeNumber;
    }

    private String getName() {
        // Prompts user to input full name. The input is assigned to a new string, fullName
        System.out.print("Employee name: ");
        String fullName;

        /*
        Ensures fullName user input only consists of letters and spaces, is not an empty string,
        and contains a space which is not at the start or end of the string, before proceeding. The user receives
        the appropriate error message if their input matches any of these conditions.
        */

        while (true) {
            fullName = scanner.nextLine();
            if (!fullName.matches("^[-'a-zA-ZÀ-ÿ\\s]*$")) {
                System.out.println("Names can only contain letters and spaces. Please try again.");
            } else if (fullName.isEmpty()) {
                System.out.println("Nothing entered. Please enter your full name to continue:");
            } else if (!fullName.contains(" ") || fullName.startsWith(" ") || fullName.endsWith(" ")) {
                System.out.println(
                    "Names should be separated with a space and not begin or end with a space. Please try again."
                );
            } else {
                break;
            }
        }
        return fullName;
    }
    // Selector for option to add pension to calculation
    public boolean getPensionOption() {
        while (true) {
            String pensionOption = scanner.nextLine().toLowerCase();
            switch (pensionOption) {
                case "y": {
                    return true;
                }
                case "n": {
                    return false;
                }
                default: {
                    System.out.print("Include teacher's pension in calculation? (Y/N)");
                }
            }
        }
    }

    // Selector for option to add parking fee to calculation
    public boolean getParkingFeeOption() {
        while (true) {
            String parkingFeeOption = scanner.nextLine().toLowerCase();
            switch (parkingFeeOption) {
                case "y": {
                    return true;
                }
                case "n": {
                    return false;
                }
                default: {
                    System.out.print("Include parking fee in calculation? (Y/N)");
                }
            }
        }
    }

    /*
    Uses tax bands from figures.csv to calculate annual tax figure.
    The for loop allows for any number of tax bands in the csv to be iterated through
    to obtain the correct tax band depending on the user-provided income figure.
    */
    static double yearlyIncomeTax(double income, Map<String, List<ConfigData>> config) {
        double taxAmount = 0;
        List<ConfigData> taxConfig = config.get("incometax");
        for (int i = 0; i < taxConfig.size(); i++) {
            ConfigData currentBand = taxConfig.get(i);
            if (taxConfig.size() <= i+1 || taxConfig.get(i+1).number > income) {
                taxAmount += (income - currentBand.number) * currentBand.percentage;
                break;
            } else {
                taxAmount += (taxConfig.get(i+1).number - currentBand.number) * currentBand.percentage;
            }
        }
        return taxAmount;
    }

    static double yearlyNI(double income, Map<String, List<ConfigData>> config) {
        double niAmount = 0;
        List<ConfigData> niConfig = config.get("nationalinsurance");
        for (int i = 0; i < niConfig.size(); i++) {
            ConfigData currentBand = niConfig.get(i);
            if (niConfig.size() <= i+1 || niConfig.get(i+1).number > income) {
                niAmount += (income - currentBand.number) * currentBand.percentage;
                break;
            } else {
                niAmount += (niConfig.get(i+1).number - currentBand.number) * currentBand.percentage;
            }
        }
        return niAmount;
    }

    static double parkingFee(Map<String, List<ConfigData>> config) {
        double parkingFeeAmount = 0;
        List<ConfigData> parkingConfig = config.get("parking");
        parkingFeeAmount = parkingConfig.get(0).number;
        return parkingFeeAmount;
    }

    static double teachersPension(double income, Map<String, List<ConfigData>> config) {
        List<ConfigData> pensionConfig = config.get("pension");
        for (int i = 0; i < pensionConfig.size(); i++) {
            ConfigData currentBand = pensionConfig.get(i);
            if (pensionConfig.size() <= i+1 || pensionConfig.get(i+1).number > income) {
                return currentBand.percentage * income;
            }
        }
        // Unreachable
        return -1;
    }

    static Map<String, List<ConfigData>> readCSVFile() {
        String figures = "figures.csv";
        String line;
        Map<String,List<ConfigData>> configMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(figures));
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String valueTypes = values[0];
                String numbers = values[1];
                String percentages = values[2];
                if (!configMap.containsKey(valueTypes)) {
                    configMap.put(valueTypes, new ArrayList<>());
                }
                configMap.get(valueTypes).add(new ConfigData(numbers, percentages));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configMap;
    }

    static class ConfigData {
        public double number;
        public double percentage;
        public ConfigData(double number, double percentage) {
            this.number = number;
            this.percentage = percentage;
        }

        public ConfigData(String numbers, String percentages) {
            this(Double.parseDouble(numbers), Double.parseDouble(percentages));
        }
    }
}