package ch.zhaw.buergli1.project2;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import java.io.File;

public class WaterQualityPredictor {
    public static void main(String[] args) throws Exception {
        // Load the water quality data
        WaterQualityDataLoader dataLoader = new WaterQualityDataLoader();
        Instances data = dataLoader.createWekaInstances();

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
}

