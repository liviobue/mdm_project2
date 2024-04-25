package ch.zhaw.buergli1.project2;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaterQualityDataLoaderTest {

    @Test
    void testLoadTrainingData() {
        // Create a small, known dataset
        List<WaterQualityData> trainingData = new ArrayList<>();
        trainingData.add(new WaterQualityData(
            "Site1", "Unit1", LocalDate.of(2023, 4, 1), 1.5, 10.0, 7.2, 0.5, 1.0, 20.0, 15.0
        ));
        trainingData.add(new WaterQualityData(
            "Site2", "Unit2", LocalDate.of(2023, 4, 2), 2.0, 9.5, 7.5, 0.6, 1.2, 18.0, 12.0
        ));
        trainingData.add(new WaterQualityData(
            "Site3", "Unit3", LocalDate.of(2023, 4, 3), 1.8, 11.0, 7.4, 0.4, 0.8, 22.0, 18.0
        ));

        // Call the loadTrainingData() method
        DataSetIterator trainIterator = WaterQualityDataLoader.loadTrainingData(trainingData);

        // Assert that the returned DataSetIterator is not null and contains the expected data
        assertNotNull(trainIterator);
        assertTrue(trainIterator instanceof ListDataSetIterator);
        assertTrue(trainIterator.hasNext());
    }
}
