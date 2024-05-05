package ch.zhaw.buergli1.project2;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WaterQualityPredictor {
    private static final String MODEL_NAME = "dataclassifier";
    private static final Path MODEL_DIR = Paths.get("models");

    private Predictor<NDArray, NDArray> predictor;

    public WaterQualityPredictor() throws IOException, TranslateException, MalformedModelException {
        Model model = Models.getModel();
        model.load(MODEL_DIR, MODEL_NAME);

        // Create a custom Translator implementation
        Translator<NDArray, NDArray> translator = new WaterQualityTranslator();

        // Create a new Predictor using the custom Translator
        this.predictor = model.newPredictor(translator);
    }

    public int predict(NDArray input) throws TranslateException {
        NDArray output = predictor.predict(input);
        return (int) output.argMax().getLong(); // Cast to int
    }    

    private static class WaterQualityTranslator implements Translator<NDArray, NDArray> {
        @Override
        public NDList processInput(TranslatorContext ctx, NDArray input) throws Exception {
            // Implement the logic to translate the input data into a format that the model can understand
            // You might want to preprocess the input here
            return new NDList(input);
        }
    
        @Override
        public NDArray processOutput(TranslatorContext ctx, NDList list) throws Exception {
            // Implement the logic to translate the model's output back into a format that your application can use
            // You might want to post-process the output here
            return (NDArray) list.singletonOrThrow();
        }
    
        @Override
        public Batchifier getBatchifier() {
            // Implement the logic to handle batching of input data
            return null;
        }
    }
}
