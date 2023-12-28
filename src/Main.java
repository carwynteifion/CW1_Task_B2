import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    Scanner scanner = new Scanner(System.in);
    Map<String, List<ConfigData>> config;

    public Main(){
        // Read CSV config file for tax/NI/pension/parking amounts
        config = readCSVFile();
    }

    public static void main(String[] args) {
        new Main().run(args);
    }
    public void run(String[] args) {

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
        System.out.println("\nCalculating Monthly Net Pay....\n");

        double grossMonthlySalary = yearlyGrossSalary / 12;
        System.out.printf("Gross salary: %.2f\n", grossMonthlySalary);

        double yearlyTeachersPension = teachersPension(yearlyGrossSalary, config);
        double monthlyTeachersPension = yearlyTeachersPension / 12;
        System.out.printf("Pension deductions: %.2f\n", monthlyTeachersPension);

        double taxableAmount = yearlyGrossSalary - yearlyTeachersPension;
        double personalAllowance = 12570;
        double monthlyTaxableAmount = (taxableAmount - personalAllowance) / 12;
        if (monthlyTaxableAmount < 0)
            monthlyTaxableAmount = 0;
        System.out.printf("Taxable amount: %.2f\n", monthlyTaxableAmount);

        double monthlyIncomeTax = yearlyIncomeTax(taxableAmount, config) / 12;
        System.out.printf("Tax paid: %.2f\n", monthlyIncomeTax);

        double monthlyNI = yearlyNI(taxableAmount, config) / 12;
        System.out.printf("NI paid: %.2f\n", monthlyNI);

        double parkingFee = 10;
        System.out.printf("Monthly parking fee: %.2f\n\n", parkingFee);

        double totalDeductions = monthlyNI + monthlyIncomeTax + monthlyTeachersPension + parkingFee;
        System.out.printf("Total deductions: %.2f\n", totalDeductions);

        double monthlyNetPay = grossMonthlySalary - totalDeductions;
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

        /* Ensures fullName user input only consists of letters and spaces, is not an empty string,
        and contains a space which is not at the start or end of the string, before proceeding. The user receives
        the appropriate error message if their input matches any of these conditions. */
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
        String line = "";
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