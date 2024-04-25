package ch.zhaw.buergli1.project2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ui.Model;

@RestController
public class WaterQualityController {

    private final WaterQualityDataLoader waterQualityDataLoader;

    public WaterQualityController(WaterQualityDataLoader waterQualityDataLoader) {
        this.waterQualityDataLoader = waterQualityDataLoader;
    }

    @GetMapping("/print")
    public ResponseEntity<String> printWaterQualityData() {
        waterQualityDataLoader.printWaterQualityData();
        return new ResponseEntity<>("Water quality data printed to console.", HttpStatus.OK);
    }

    @GetMapping("/predict")
    public String showPredictionForm(Model model) {
        model.addAttribute("waterQualityData", new WaterQualityData());
        return "prediction";
    }

    @PostMapping("/predict")
    @ResponseBody
        public ResponseEntity<String> predictpH(@RequestBody WaterQualityData waterQualityData) throws Exception {
        double predictedpH = waterQualityDataLoader.predictpH(waterQualityData);
        String response = "{\"predictedpH\": " + predictedpH + "}";
        return ResponseEntity.ok(response);
    }
    
}
