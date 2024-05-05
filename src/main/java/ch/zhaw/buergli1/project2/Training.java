package ch.zhaw.buergli1.project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import ai.djl.metric.Metrics;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.ArrayDataset;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import java.nio.file.Path;

@Component
public class Training implements ApplicationRunner {

    // the number of passes over the complete dataset
    private final int EPOCHS = 2;

    @Value("classpath:output.csv")
    private InputStream csvFile;

    private final List<WaterQualityData> waterQualityData = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createNetwork();
    }

    private void createNetwork() throws IOException, TranslateException {
        // the location to save the model
        Path modelDir = Paths.get("models");

        readCsvFile();

        // create CsvDataset from the CSV file
        RandomAccessDataset[] datasets = createTest(waterQualityData);
        System.out.println(datasets);
        // set loss function, which seeks to minimize errors
        Loss loss = Loss.softmaxCrossEntropyLoss();

        // setting training parameters (ie hyperparameters)
        TrainingConfig config = setupTrainingConfig(loss);

        Model model = Models.getModel(); // empty model instance to hold patterns
        Trainer trainer = model.newTrainer(config);
        // metrics collect and report key performance indicators, like accuracy
        trainer.setMetrics(new Metrics());

        // initialize trainer with proper input shape
        // Assuming your CSV data has 3 columns, set the input shape accordingly
        Shape inputShape = new Shape(1, 6);
        trainer.initialize(inputShape);

        // find the patterns in data
        EasyTrain.fit(trainer, EPOCHS, datasets[0], datasets[1]);

        // set model properties
        TrainingResult result = trainer.getTrainingResult();
        model.setProperty("Epoch", String.valueOf(EPOCHS));
        model.setProperty(
                "Accuracy", String.format("%.5f", result.getValidateEvaluation("Accuracy")));
        model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));

        // save the model after done training for inference later
        // model saved as dataclassifier-0000.params
        model.save(modelDir, Models.MODEL_NAME);
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
                waterTemperature, airTemperature);
    }

    private RandomAccessDataset[] createTest(List<WaterQualityData> waterQualityData) {
        // Prepare the data
        NDManager manager = NDManager.newBaseManager(null, "PyTorch");
        int numSamples = waterQualityData.size();
        NDArray features = manager.create(new Shape(numSamples, 6));
        NDArray labels = manager.create(new Shape(numSamples));
    
        for (int i = 0; i < numSamples; i++) {
            WaterQualityData data = waterQualityData.get(i);
            features.set(new NDIndex(i, 0), manager.create((float) data.getSalinity()));
            features.set(new NDIndex(i, 1), manager.create((float) data.getDissolvedOxygen()));
            features.set(new NDIndex(i, 2), manager.create((float) data.getpH()));
            features.set(new NDIndex(i, 3), manager.create((float) data.getSecchiDepth()));
            features.set(new NDIndex(i, 4), manager.create((float) data.getWaterDepth()));
            features.set(new NDIndex(i, 5), manager.create((float) data.getAirTemperature()));
            labels.set(new NDIndex(i), manager.create(getSiteIdIndex(data.getSiteId())));
        }
    
        // Split the data into training and validation sets
        RandomAccessDataset trainDataset = new ArrayDataset.Builder()
                .setData(features)
                .optLabels(labels)
                .setSampling(64, true)
                .build();
        RandomAccessDataset validationDataset = new ArrayDataset.Builder()
                .setData(features)
                .optLabels(labels)
                .setSampling(64, false)
                .build();
    
        return new RandomAccessDataset[] { trainDataset, validationDataset };
    }    

    private int getSiteIdIndex(String siteId) {
        switch (siteId) {
            case "Bay":
                return 0;
            case "A":
                return 1;
            case "B":
                return 2;
            case "C":
                return 3;
            case "D":
                return 4;
            default:
                throw new IllegalArgumentException("Invalid site ID: " + siteId);
        }
    }

    private TrainingConfig setupTrainingConfig(Loss loss) {
        return new DefaultTrainingConfig(loss)
                .addEvaluator(new Accuracy())
                .addTrainingListeners(TrainingListener.Defaults.logging());
    }

    public int predict(NDArray input) throws TranslateException, MalformedModelException, IOException {
        WaterQualityPredictor predictor = new WaterQualityPredictor();
        return predictor.predict(input);
    }

}
