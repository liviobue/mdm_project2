package ch.zhaw.buergli1.project2;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ModelPredictor {
    public static double predictWaterQuality(MultiLayerNetwork model, WaterQualityData data) {
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
