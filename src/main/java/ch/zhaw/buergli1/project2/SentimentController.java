package ch.zhaw.buergli1.project2;

import org.springframework.web.bind.annotation.RestController;

import ai.djl.translate.TranslateException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SentimentController {

    private SentimentAnalysis analysis = new SentimentAnalysis();
    
    @GetMapping("/ping")
    public String ping() {
        return("Hello World!");
    }

    @GetMapping("/sentiment")
    public String predict(@RequestParam(name="text", required = true) String text) throws TranslateException {
        var result = analysis.predict(text);
        return result.toJson();
    }
}
