package ch.zhaw.buergli1.project2;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

/*     @PostMapping(path = "/analyze")
    public String predict(@RequestParam("image") MultipartFile image) throws Exception {
        InputStream is = new ByteArrayInputStream(image.getBytes());
        var uri = "http://localhost:8080/predictions/resnet18_v1";
        if (this.isDockerized()) {
            uri = "http://model-service:8080/predictions/resnet18_v1";
        }
        var webClient = WebClient.create();
        Resource resource = new InputStreamResource(is);
        var result = webClient.post()
                .uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromResource(resource))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return result;
    }

    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    } */

}
