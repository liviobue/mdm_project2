package ch.zhaw.buergli1.project2;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class WaterQualityDataLoader implements ApplicationRunner {
    private static final double VALIDATION_SPLIT = 0.2; // 20% of the data for validation
    private static final int BATCH_SIZE = 32;

    @Value("classpath:output.csv")
    private InputStream csvFile;

    private final List<WaterQualityData> waterQualityData = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws IOException {
        readCsvFile();
        trainModel();
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

    public void trainModel() throws IOException {
        // Load the full dataset
        List<WaterQualityData> waterQualityData = getWaterQualityData();

        // Split the dataset into training and validation sets
        int validationSize = (int) (waterQualityData.size() * VALIDATION_SPLIT);
        List<WaterQualityData> trainingData = new ArrayList<>(waterQualityData.subList(0, waterQualityData.size() - validationSize));
        List<WaterQualityData> validationData = new ArrayList<>(waterQualityData.subList(waterQualityData.size() - validationSize, waterQualityData.size()));

        // Create the iterators
        DataSetIterator trainIterator = loadTrainingData(trainingData);
        DataSetIterator validationIterator = loadTestingData(validationData);

        // Create and train the model
        MultiLayerNetwork model = NeuralNetworkModule.createModel();
        trainModel(model, trainIterator, validationIterator);
    }

    public static void trainModel(MultiLayerNetwork model, DataSetIterator trainIterator, DataSetIterator testIterator) throws IOException {
        int epochs = 100;

        for (int i = 0; i < epochs; i++) {
            model.fit(trainIterator);
            evaluateModel(model, testIterator);
        }

        // Save the trained model
        model.save(new File("trained_model.zip"));
    }

    private static void evaluateModel(MultiLayerNetwork model, DataSetIterator testIterator) {
        Evaluation evaluation = model.evaluate(testIterator);
        double f1Score = evaluation.f1();
        double precision = evaluation.precision();
        double recall = evaluation.recall();
        double accuracy = evaluation.accuracy();
    
        System.out.println("Test F1 score: " + f1Score);
        System.out.println("Test Precision: " + precision);
        System.out.println("Test Recall: " + recall);
        System.out.println("Test Accuracy: " + accuracy);
    }    

    private static DataSetIterator loadTrainingData(List<WaterQualityData> data) {
        List<org.nd4j.linalg.dataset.DataSet> dataSets = new ArrayList<>();
        for (WaterQualityData waterQualityData : data) {
            double[] features = new double[] {
                waterQualityData.getSalinity(),
                waterQualityData.getDissolvedOxygen(),
                //waterQualityData.getpH(),
                waterQualityData.getSecchiDepth(),
                waterQualityData.getWaterDepth(),
                waterQualityData.getWaterTemperature(),
                waterQualityData.getAirTemperature()
            };
            INDArray input = Nd4j.create(features);
            INDArray label = Nd4j.create(new double[] { waterQualityData.getpH() }, new int[] { 1 });
            dataSets.add(new DataSet(input, label));
        }

        return new ListDataSetIterator<>(dataSets, BATCH_SIZE);
    }

    private static DataSetIterator loadTestingData(List<WaterQualityData> data) {
        List<org.nd4j.linalg.dataset.DataSet> dataSets = new ArrayList<>();
        for (WaterQualityData waterQualityData : data) {
            double[] features = new double[] {
                waterQualityData.getSalinity(),
                waterQualityData.getDissolvedOxygen(),
                waterQualityData.getpH(),
                waterQualityData.getSecchiDepth(),
                waterQualityData.getWaterDepth(),
                waterQualityData.getWaterTemperature(),
                waterQualityData.getAirTemperature()
            };
            INDArray input = Nd4j.create(features);
            INDArray label = Nd4j.create(new double[] { waterQualityData.getpH() }, new int[] { 1 });
            dataSets.add(new DataSet(input, label));
        }
    
        return new ListDataSetIterator<>(dataSets, BATCH_SIZE);
    }
}
