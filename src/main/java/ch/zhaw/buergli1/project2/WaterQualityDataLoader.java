package ch.zhaw.buergli1.project2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class WaterQualityDataLoader implements ApplicationRunner {

    @Value("classpath:output.csv")
    private InputStream csvFile;

    private final List<WaterQualityData> waterQualityData = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) {
        readCsvFile();
    }

    private void readCsvFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile))) {

            // Skip the header row
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // Split line by commas (",")
                String[] values = line.split(",");
    
                // Remove surrounding quotes from each value
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("^\"|\"$", "");
                }
    
                WaterQualityData data = createWaterQualityData(values);
                waterQualityData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printWaterQualityData() {
        System.out.println("Water Quality Data:");
        for (WaterQualityData data : waterQualityData) {
            System.out.println("Site ID: " + data.getSiteId());
            System.out.println("Unit ID: " + data.getUnitId());
            System.out.println("Read Date: " + data.getReadDate());
            System.out.println("Salinity: " + data.getSalinity() + " ppt");
            System.out.println("Dissolved Oxygen: " + data.getDissolvedOxygen() + " mg/L");
            System.out.println("pH: " + data.getpH() + " standard units");
            System.out.println("Secchi Depth: " + data.getSecchiDepth() + " m");
            System.out.println("Water Depth: " + data.getWaterDepth() + " m");
            System.out.println("Water Temperature: " + data.getWaterTemperature() + " °C");
            System.out.println("Air Temperature: " + data.getAirTemperature() + " °C");
            System.out.println();
        }
    }

    private WaterQualityData createWaterQualityData(String[] values) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        String siteId = values[0]; // Bay
        String unitId = values[1]; // DefaultUnitId
        LocalDate readDate = LocalDate.parse(values[2], dateFormatter); // 01/03/1994
        double salinity = Double.parseDouble(values[3]); // 1.3
        double dissolvedOxygen = Double.parseDouble(values[4]); // 11.7
        double pH = Double.parseDouble(values[5]); // 7.3
        double secchiDepth = Double.parseDouble(values[6]); // 0.4
        double waterDepth = Double.parseDouble(values[7]); // 0.4
        double waterTemperature = Double.parseDouble(values[8]); // 5.9
        double airTemperature = Double.parseDouble(values[9]); // 8
        // 46.4 
        // LocalTime time = LocalTime.parse(values[11], timeFormatter); // 11:00
        // String fieldTech = values[11]; // 0
        // LocalDate dateVerified = LocalDate.parse(values[12], dateFormatter); // 0
        // String whoVerified = values[13]; // 0
        // double airTempCelsius = Double.parseDouble(values[14]); // 0
        // int year = Integer.parseInt(values[15]); // 1994

        return new WaterQualityData(
            siteId, unitId, readDate, salinity, dissolvedOxygen, pH, secchiDepth, waterDepth,
            waterTemperature, airTemperature
        );
    }

    public List<WaterQualityData> getWaterQualityData() {
        return waterQualityData;
    }
}
