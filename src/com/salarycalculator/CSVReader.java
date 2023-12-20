package com.salarycalculator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CSVReader {
    public static void main(String[] args) {
        String figures = "src/figures.csv";
        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(figures));
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                System.out.println(Arrays.toString(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

