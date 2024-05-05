package ch.zhaw.buergli1.project2;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CSVDataProcessor {

    public static void main(String[] args) throws CsvException {
        CSVDataProcessor processor = new CSVDataProcessor();
        processor.processCSV("data.csv");
    }

    public void processCSV(String filename) throws CsvException {
        try {
            // Read CSV file
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> rows = csvReader.readAll();
            csvReader.close();

            // Process rows
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    if (row[i].isEmpty()) {
                        // Fill out empty values with appropriate data
                        switch (i) {
                            case 0:
                                row[i] = "A"; // Example default value for Site_Id
                                break;
                            case 1:
                                row[i] = "DefaultUnitId"; // Example default value for Unit_Id
                                break;
                            case 2: // If it's the Read_Date column
                                row[i] = "01/01/1970"; // Set default date
                                break;
                            // Add cases for other columns if needed
                            default:
                                row[i] = "0"; // Example default value for numeric columns
                        }
                    } else if (i == 2) { // If it's the Read_Date column
                        // Parse and format the date
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                            row[i] = outputFormat.format(inputFormat.parse(row[i]));
                        } catch (ParseException e) {
                            // Set date to 01/01/1970 if parsing fails
                            row[i] = "01/01/1970";
                        }
                    } else if (i == 11) { // If it's the Time (24:00) column
                        // Format the time
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
                            row[i] = outputFormat.format(inputFormat.parse(row[i]));
                        } catch (ParseException e) {
                            // Set time to 00:00 if parsing fails
                            row[i] = "00:00";
                        }
                    }
                }
            }

            // Write back to CSV
            OutputStream os = new FileOutputStream("output.csv");
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeAll(rows);
            csvWriter.close();

            System.out.println("Processing complete. Output written to output.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
