package ch.zhaw.buergli1.project2;

import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.tensorflow.engine.TfEngine;
import ai.djl.training.dataset.RandomAccessDataset;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Models {

    // the number of features
    public static final int NUM_OF_FEATURES = 6;

    // the number of output classes
    public static final int NUM_OF_OUTPUT = 4;

    // the name of the model
    public static final String MODEL_NAME = "dataclassifier";

    private Models() {
    }

    public static Model getModel() {

        // create new instance of an empty model
        Model model = Model.newInstance(MODEL_NAME);

        // create the neural network block
        Block mlpBlock = createMlpBlock();

        // set the neural network to the model
        model.setBlock(mlpBlock);
        return model;
    }

    private static Block createMlpBlock() {
        SequentialBlock block = new SequentialBlock();
        block.add(Blocks.batchFlattenBlock(NUM_OF_FEATURES));
        block.add(Linear.builder().setUnits(128).build());
        block.add(ai.djl.nn.Activation::relu);
        block.add(Linear.builder().setUnits(64).build());
        block.add(ai.djl.nn.Activation::relu);
        block.add(Linear.builder().setUnits(5).build());
        return block;
    }
}
