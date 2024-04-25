package ch.zhaw.buergli1.project2;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;

public class WaterQualityPredictor {
    private MultiLayerNetwork model;

    public WaterQualityPredictor() {
        loadModel();
    }

    private void loadModel() {
        try {
            model = MultiLayerNetwork.load(new File("trained_model.zip"), true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load the trained model.");
        }
    }

    public double predictWaterQuality(WaterQualityData data) {
        INDArray input = Nd4j.create(new double[] {
            data.getSalinity(),
            data.getDissolvedOxygen(),
            data.getpH(),
            data.getSecchiDepth(),
            data.getWaterDepth(),
            data.getWaterTemperature(),
            data.getAirTemperature()
        });

        INDArray output = model.output(input);
        return output.getDouble(0);
    }
}

