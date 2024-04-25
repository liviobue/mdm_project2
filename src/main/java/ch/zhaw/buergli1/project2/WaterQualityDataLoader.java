package ch.zhaw.buergli1.project2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;

@Component
public class WaterQualityDataLoader implements ApplicationRunner {

    @Value("classpath:output.csv")
    private InputStream csvFile;

    private LinearRegression model;

    private final List<WaterQualityData> waterQualityData = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        readCsvFile();
        trainRegression();
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

    public Instances createWekaInstances() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Salinity"));
        attributes.add(new Attribute("DissolvedOxygen"));
        attributes.add(new Attribute("SecchiDepth"));
        attributes.add(new Attribute("WaterDepth"));
        attributes.add(new Attribute("WaterTemperature"));
        attributes.add(new Attribute("AirTemperature"));
        attributes.add(new Attribute("pH"));

        Instances data = new Instances("WaterQualityData", attributes, 0);
        data.setClassIndex(data.numAttributes() - 1);

        for (WaterQualityData waterData : waterQualityData) {
            DenseInstance instance = new DenseInstance(data.numAttributes());
            instance.setValue(0, waterData.getSalinity());
            instance.setValue(1, waterData.getDissolvedOxygen());
            instance.setValue(2, waterData.getSecchiDepth());
            instance.setValue(3, waterData.getWaterDepth());
            instance.setValue(4, waterData.getWaterTemperature());
            instance.setValue(5, waterData.getAirTemperature());
            instance.setValue(6, waterData.getpH());
            data.add(instance);
        }

        return data;
    }

    public void trainRegression() throws Exception {
        Instances data = createWekaInstances();

        model = new LinearRegression();
        model.buildClassifier(data);
    
        // Create a linear regression model
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data);
    
        // Evaluate the model
        weka.classifiers.evaluation.Evaluation eval = new weka.classifiers.evaluation.Evaluation(data);
        eval.crossValidateModel(model, data, 10, data.getRandomNumberGenerator(1));
        System.out.println(eval.toSummaryString());
    
        // Save the model to a file
        File modelFile = new File("waterQualityModel.model");
        weka.core.SerializationHelper.write(modelFile.getAbsolutePath(), model);
    }

    public double predictpH(WaterQualityData waterQualityData) throws Exception {
        Instances data = createWekaInstances();
        DenseInstance instance = new DenseInstance(data.numAttributes());
        instance.setValue(0, waterQualityData.getSalinity());
        instance.setValue(1, waterQualityData.getDissolvedOxygen());
        instance.setValue(2, waterQualityData.getSecchiDepth());
        instance.setValue(3, waterQualityData.getWaterDepth());
        instance.setValue(4, waterQualityData.getWaterTemperature());
        instance.setValue(5, waterQualityData.getAirTemperature());
        data.add(instance);
        return model.classifyInstance(instance);
    }
}
