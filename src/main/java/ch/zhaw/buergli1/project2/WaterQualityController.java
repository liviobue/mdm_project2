package ch.zhaw.buergli1.project2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/water-quality")
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

    
}
