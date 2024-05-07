package ch.zhaw.buergli1.project2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
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

import ai.djl.MalformedModelException;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import reactor.core.publisher.Mono;

import org.springframework.ui.Model;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.springframework.core.io.Resource;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class WaterQualityController {

    private final WaterQualityDataLoader waterQualityDataLoader;

    @Autowired
    private Training trainingService;

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

    @PostMapping("/nuralnetwork") //[1.3, 11.7, 7.3, 0.4, 0.4]
    public ResponseEntity<String> predictWaterQuality(@RequestBody float[] input) {
        try {
            NDManager manager = NDManager.newBaseManager(null, "PyTorch");
            NDArray inputArray = manager.create(input);
            int output = (int) trainingService.predict(inputArray);
            return ResponseEntity.ok(String.valueOf(output));
        } catch (MalformedModelException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("-1: " + e.getMessage()); // Or any default value you want to return in case of error
        } catch (TranslateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("-2: " + e.getMessage()); // Or any default value you want to return in case of error
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("-4: " + e.getMessage()); // Or any default value you want to return in case of error
        }
    }
    
    @PostMapping(path = "/analyze")
    public ResponseEntity<String> predict(@RequestBody Float[] input) {
        try {
            var uri = "http://localhost:8080/predictions/dataclassifier-0002";
            if (this.isDockerized()) {
                uri = "http://model-service:8080/predictions/dataclassifier-0002";
            }
            var webClient = WebClient.create();
            var result = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(input), Float[].class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Behandlung von Ausnahmen
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler bei der Verarbeitung des Requests: " + e.getMessage());
        }
    }

    private boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }

}
