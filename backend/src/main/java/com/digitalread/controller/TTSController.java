package com.digitalread.controller;

import com.digitalread.service.TTSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tts")
@CrossOrigin(origins = "*")
public class TTSController {

    @Autowired
    private TTSService ttsService;

    @GetMapping("/voices")
    public ResponseEntity<?> getVoices() {
        try {
            List<Map<String, Object>> voices = ttsService.getAvailableVoices();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", voices);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesize(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            if (text == null || text.isEmpty()) {
                throw new RuntimeException("请提供要合成的文本");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> options = (Map<String, Object>) request.getOrDefault("options", new HashMap<>());

            Map<String, Object> synthesis = ttsService.synthesize(text, options);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", synthesis);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/audio")
    public ResponseEntity<?> synthesizeToAudio(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            if (text == null || text.isEmpty()) {
                throw new RuntimeException("请提供要合成的文本");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> options = (Map<String, Object>) request.getOrDefault("options", new HashMap<>());

            byte[] audioData = ttsService.synthesizeToAudio(text, options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("audio", "wav"));
            headers.setContentLength(audioData.length);
            headers.setContentDispositionFormData("attachment", "speech.wav");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audioData);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/markers")
    public ResponseEntity<?> getSpeechMarkers(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            if (text == null || text.isEmpty()) {
                throw new RuntimeException("请提供文本");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> options = (Map<String, Object>) request.getOrDefault("options", new HashMap<>());

            Map<String, Object> markers = ttsService.getSpeechMarkers(text, options);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", markers);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/chunk")
    public ResponseEntity<?> chunkText(@RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            if (text == null || text.isEmpty()) {
                throw new RuntimeException("请提供文本");
            }

            Integer maxChars = (Integer) request.getOrDefault("maxChars", 500);

            List<String> chunks = ttsService.chunkText(text, maxChars);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", chunks);
            result.put("count", chunks.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("sampleRate", 22050);
            config.put("sampleSize", 16);
            config.put("channels", 1);
            config.put("supportedVoices", List.of("male", "female", "child", "deep"));
            config.put("minRate", 0.5);
            config.put("maxRate", 2.0);
            config.put("defaultRate", 1.0);
            config.put("minPitch", 0.5);
            config.put("maxPitch", 2.0);
            config.put("defaultPitch", 1.0);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
