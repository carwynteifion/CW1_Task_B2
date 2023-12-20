// Java's built-in scanner utility needs to be imported so that user input can be read
package com.salarycalculator;
import java.util.Scanner;
import com.salarycalculator.CSVReader;

public class Main {
    public static void main(String[] args) {
        // Creates a new scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Displays welcome message when the program starts
        System.out.println("\nWelcome to USW Employee Salary Calculator\n");

        // Prompts user to input full name. The input is assigned to a new string, fullName
        System.out.print("Employee name: ");
        String fullName = scanner.nextLine();

        /* Ensures fullName user input only consists of letters and spaces, is not an empty string,
        and contains a space which is not at the start or end of the string, before proceeding. The user receives
        the appropriate error message if their input matches any of these conditions. */
        while (
            !fullName.matches("^[-'a-zA-ZÀ-ÿ\\s]*$") ||
            !fullName.contains(" ") ||
            fullName.startsWith(" ") ||
            fullName.endsWith(" ")
        ) {
            if (!fullName.matches("^[-'a-zA-ZÀ-ÿ\\s]*$")) {
                System.out.println("Names can only contain letters and spaces. Please try again.");
            } else if (fullName.isEmpty()) {
                System.out.println("Nothing entered. Please enter your full name to continue:");
            } else if (!fullName.contains(" ") || fullName.startsWith(" ") || fullName.endsWith(" ")) {
                System.out.println(
                    "Names should be separated with a space and not begin or end with a space. Please try again."
                );
            }
            fullName = scanner.nextLine();
        }

        // Input employee number; also checks value entered is an int
        int employeeNumber;
        System.out.print("Please enter your employee number: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter employee number using numeric characters only (0-9).");
            scanner.next();
        }
        employeeNumber = scanner.nextInt();

        // Input gross salary; also checks value entered is an int
        double yearlyGrossSalary;
        System.out.print("What is the yearly gross salary (in £) of the employee? ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter yearly gross salary using numeric characters only (0-9).");
            scanner.next();
        }
        yearlyGrossSalary = scanner.nextDouble();

        System.out.println("\nCalculating Monthly Net Pay....\n");

        double grossMonthlySalary = yearlyGrossSalary / 12;
        System.out.printf("Gross salary: %.2f\n", grossMonthlySalary);

        double monthlyTeachersPension = teachersPension(yearlyGrossSalary) / 12;
        System.out.printf("Pension deductions: %.2f\n", monthlyTeachersPension);

        double taxableAmount = yearlyGrossSalary - teachersPension(yearlyGrossSalary);
        double personalAllowance = 12570;
        double monthlyTaxableAmount = (taxableAmount - personalAllowance) / 12;
        if (monthlyTaxableAmount < 0)
            monthlyTaxableAmount = 0;
        System.out.printf("Taxable amount: %.2f\n", monthlyTaxableAmount);

        double monthlyIncomeTax = yearlyIncomeTax(taxableAmount) / 12;
        System.out.printf("Tax paid: %.2f\n", monthlyIncomeTax);

        double monthlyNI = yearlyNI(taxableAmount) / 12;
        System.out.printf("NI paid: %.2f\n", monthlyNI);

        double parkingFee = 10;
        System.out.printf("Monthly parking fee: %.2f\n\n", parkingFee);

        double totalDeductions = monthlyNI + monthlyIncomeTax + monthlyTeachersPension + parkingFee;
        System.out.printf("Total deductions: %.2f\n", totalDeductions);

        double monthlyNetPay = grossMonthlySalary - totalDeductions;
        System.out.printf("Monthly Net Pay: %.2f\n", monthlyNetPay);
    }
    static double yearlyIncomeTax(double i) {
        double taxAmount = 0;
        double personalAllowance = 12570;
        double basicRate = 50270;
        double higherRate = 125140;
        double basicTaxAmount = Math.min(basicRate - personalAllowance, i - personalAllowance) * 0.2;
        double higherTaxAmount = Math.min(higherRate - basicRate, i - basicRate) * 0.4;
        double additionalAmount = (i-higherRate) * 0.45;
        if(i > personalAllowance) {
            taxAmount += basicTaxAmount;
        }
        if(i > 50270) {
            taxAmount += higherTaxAmount;
        }
        if (i > 125140) {
            taxAmount += additionalAmount;
        }
        return taxAmount;
    }

    static double yearlyNI(double i) {
        double niAmount = 0;
        double lowNI = 12570;
        double highNI = 50270;
        if(i > 12570) {
            niAmount += Math.min(highNI - lowNI, i - lowNI) * 0.12;
        }
        if(i > 50270) {
            niAmount += (i - highNI) * 0.02;
        }
        return niAmount;
    }

    static double teachersPension(double i) {
        double pensionAmount;
        if (i <= 32135.99)
            pensionAmount = 0.074*i;
        else if(i <= 43259.99)
            pensionAmount = 0.086*i;
        else if(i <= 51292.99)
            pensionAmount = 0.096*i;
        else if(i <= 67979.99)
            pensionAmount = 0.102*i;
        else if(i <= 92697.99)
            pensionAmount = 0.113*i;
        else
            pensionAmount = 0.117*i;
        return pensionAmount;
    }
}