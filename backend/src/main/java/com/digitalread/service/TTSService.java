package com.digitalread.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class TTSService {

    private static final int SAMPLE_RATE = 22050;
    private static final int SAMPLE_SIZE = 16;
    private static final int CHANNELS = 1;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final Map<String, VoiceProfile> voiceProfiles = new HashMap<>();
    
    public TTSService() {
        initVoiceProfiles();
    }
    
    private void initVoiceProfiles() {
        voiceProfiles.put("male", new VoiceProfile("male", "男声", 1.0, 1.0, 85, 180));
        voiceProfiles.put("female", new VoiceProfile("female", "女声", 1.1, 1.2, 165, 255));
        voiceProfiles.put("child", new VoiceProfile("child", "童声", 1.3, 1.4, 200, 300));
        voiceProfiles.put("deep", new VoiceProfile("deep", "深沉", 0.85, 0.8, 65, 130));
    }
    
    public List<Map<String, Object>> getAvailableVoices() {
        List<Map<String, Object>> voices = new ArrayList<>();
        for (VoiceProfile profile : voiceProfiles.values()) {
            Map<String, Object> voice = new HashMap<>();
            voice.put("id", profile.getId());
            voice.put("name", profile.getName());
            voice.put("lang", "zh-CN");
            voice.put("default", "male".equals(profile.getId()));
            voices.add(voice);
        }
        return voices;
    }
    
    public Map<String, Object> synthesize(String text, Map<String, Object> options) {
        String voiceId = (String) options.getOrDefault("voice", "male");
        double rate = ((Number) options.getOrDefault("rate", 1.0)).doubleValue();
        double pitch = ((Number) options.getOrDefault("pitch", 1.0)).doubleValue();
        double volume = ((Number) options.getOrDefault("volume", 1.0)).doubleValue();
        
        VoiceProfile profile = voiceProfiles.getOrDefault(voiceId, voiceProfiles.get("male"));
        
        List<WordSegment> segments = segmentText(text);
        List<Map<String, Object>> phonemes = new ArrayList<>();
        
        for (WordSegment segment : segments) {
            Map<String, Object> phoneme = new HashMap<>();
            phoneme.put("text", segment.text);
            phoneme.put("type", segment.type);
            phoneme.put("duration", calculateDuration(segment, rate, profile));
            phoneme.put("frequency", calculateFrequency(segment, pitch, profile));
            phoneme.put("startTime", 0);
            phoneme.put("endTime", 0);
            phonemes.add(phoneme);
        }
        
        double currentTime = 0;
        for (Map<String, Object> phoneme : phonemes) {
            double duration = (double) phoneme.get("duration");
            phoneme.put("startTime", currentTime);
            currentTime += duration;
            phoneme.put("endTime", currentTime);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("voice", voiceId);
        result.put("rate", rate);
        result.put("pitch", pitch);
        result.put("volume", volume);
        result.put("phonemes", phonemes);
        result.put("totalDuration", currentTime);
        result.put("wordCount", segments.size());
        
        return result;
    }
    
    public byte[] synthesizeToAudio(String text, Map<String, Object> options) throws IOException {
        String voiceId = (String) options.getOrDefault("voice", "male");
        double rate = ((Number) options.getOrDefault("rate", 1.0)).doubleValue();
        double pitch = ((Number) options.getOrDefault("pitch", 1.0)).doubleValue();
        double volume = ((Number) options.getOrDefault("volume", 1.0)).doubleValue();
        
        VoiceProfile profile = voiceProfiles.getOrDefault(voiceId, voiceProfiles.get("male"));
        
        List<WordSegment> segments = segmentText(text);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, true, false);
        
        for (WordSegment segment : segments) {
            double baseFreq = profile.getBaseFrequency(segment.type);
            double freq = baseFreq * pitch;
            double duration = calculateDuration(segment, rate, profile);
            
            byte[] audioData = generateTone(freq, duration, volume, segment.type);
            baos.write(audioData);
            
            byte[] silence = generateSilence(0.05);
            baos.write(silence);
        }
        
        return baos.toByteArray();
    }
    
    private byte[] generateTone(double frequency, double duration, double volume, String type) {
        int sampleCount = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[sampleCount * 2];
        
        double amplitude = Short.MAX_VALUE * Math.min(Math.max(volume, 0), 1);
        
        for (int i = 0; i < sampleCount; i++) {
            double t = (double) i / SAMPLE_RATE;
            double sample = 0;
            
            switch (type) {
                case "chinese":
                    sample = generateChineseSyllable(frequency, t, amplitude);
                    break;
                case "number":
                case "letter":
                    sample = amplitude * Math.sin(2 * Math.PI * frequency * t) * 
                             Math.exp(-3 * t / duration);
                    break;
                case "punctuation":
                    sample = 0;
                    break;
                default:
                    sample = amplitude * Math.sin(2 * Math.PI * frequency * t) * 
                             (0.5 + 0.5 * Math.sin(2 * Math.PI * 5 * t)) *
                             Math.exp(-2 * t / duration);
            }
            
            short value = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample));
            data[i * 2] = (byte) (value & 0xFF);
            data[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }
        
        return data;
    }
    
    private double generateChineseSyllable(double frequency, double t, double amplitude) {
        double base = amplitude * Math.sin(2 * Math.PI * frequency * t);
        double harmonic1 = 0.3 * amplitude * Math.sin(2 * Math.PI * 2 * frequency * t);
        double harmonic2 = 0.15 * amplitude * Math.sin(2 * Math.PI * 3 * frequency * t);
        double harmonic3 = 0.07 * amplitude * Math.sin(2 * Math.PI * 4 * frequency * t);
        
        double envelope = Math.sin(Math.PI * t * 2) * (1 - 0.3 * Math.sin(2 * Math.PI * frequency * 0.1 * t));
        
        return (base + harmonic1 + harmonic2 + harmonic3) * envelope;
    }
    
    private byte[] generateSilence(double duration) {
        int sampleCount = (int) (SAMPLE_RATE * duration);
        return new byte[sampleCount * 2];
    }
    
    private List<WordSegment> segmentText(String text) {
        List<WordSegment> segments = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return segments;
        }
        
        StringBuilder current = new StringBuilder();
        String currentType = null;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String type = getCharType(c);
            
            if (currentType == null) {
                currentType = type;
                current.append(c);
            } else if (type.equals(currentType)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    segments.add(new WordSegment(current.toString(), currentType));
                }
                currentType = type;
                current = new StringBuilder();
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            segments.add(new WordSegment(current.toString(), currentType));
        }
        
        return segments;
    }
    
    private String getCharType(char c) {
        if (Character.isIdeographic(c)) {
            return "chinese";
        } else if (Character.isDigit(c)) {
            return "number";
        } else if (Character.isLetter(c)) {
            return "letter";
        } else if (Character.isWhitespace(c)) {
            return "whitespace";
        } else {
            return "punctuation";
        }
    }
    
    private double calculateDuration(WordSegment segment, double rate, VoiceProfile profile) {
        double baseDuration;
        
        switch (segment.type) {
            case "chinese":
                baseDuration = 0.25 * segment.text.length();
                break;
            case "number":
                baseDuration = 0.15 * segment.text.length();
                break;
            case "letter":
                baseDuration = 0.12 * segment.text.length();
                break;
            case "punctuation":
                if (segment.text.contains("。") || segment.text.contains("！") || segment.text.contains("？")) {
                    baseDuration = 0.4;
                } else if (segment.text.contains("，") || segment.text.contains("；") || segment.text.contains("：")) {
                    baseDuration = 0.2;
                } else {
                    baseDuration = 0.1;
                }
                break;
            default:
                baseDuration = 0.1 * segment.text.length();
        }
        
        return baseDuration / Math.max(0.5, Math.min(2.0, rate));
    }
    
    private double calculateFrequency(WordSegment segment, double pitch, VoiceProfile profile) {
        double baseFreq = profile.getBaseFrequency(segment.type);
        return baseFreq * Math.max(0.5, Math.min(2.0, pitch));
    }
    
    public Map<String, Object> getSpeechMarkers(String text, Map<String, Object> options) {
        Map<String, Object> synthesis = synthesize(text, options);
        List<Map<String, Object>> markers = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> phonemes = (List<Map<String, Object>>) synthesis.get("phonemes");
        
        int charIndex = 0;
        for (Map<String, Object> phoneme : phonemes) {
            String phonemeText = (String) phoneme.get("text");
            double startTime = (double) phoneme.get("startTime");
            double endTime = (double) phoneme.get("endTime");
            
            for (int i = 0; i < phonemeText.length(); i++) {
                double charStartTime = startTime + (endTime - startTime) * i / phonemeText.length();
                double charEndTime = startTime + (endTime - startTime) * (i + 1) / phonemeText.length();
                
                Map<String, Object> marker = new HashMap<>();
                marker.put("char", String.valueOf(phonemeText.charAt(i)));
                marker.put("index", charIndex++);
                marker.put("startTime", charStartTime);
                marker.put("endTime", charEndTime);
                markers.add(marker);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("markers", markers);
        result.put("totalDuration", synthesis.get("totalDuration"));
        return result;
    }
    
    public List<String> chunkText(String text, int maxCharsPerChunk) {
        List<String> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        String[] sentences = text.split("(?<=[。！？.!?])");
        
        StringBuilder currentChunk = new StringBuilder();
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > maxCharsPerChunk && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence);
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    private static class VoiceProfile {
        private final String id;
        private final String name;
        private final double rateMultiplier;
        private final double pitchMultiplier;
        private final int minFreq;
        private final int maxFreq;
        
        public VoiceProfile(String id, String name, double rateMultiplier, double pitchMultiplier, int minFreq, int maxFreq) {
            this.id = id;
            this.name = name;
            this.rateMultiplier = rateMultiplier;
            this.pitchMultiplier = pitchMultiplier;
            this.minFreq = minFreq;
            this.maxFreq = maxFreq;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        
        public double getBaseFrequency(String type) {
            switch (type) {
                case "chinese":
                    return (minFreq + maxFreq) / 2.0;
                case "number":
                    return (minFreq + maxFreq) / 2.0 * 1.1;
                case "letter":
                    return (minFreq + maxFreq) / 2.0 * 1.15;
                default:
                    return (minFreq + maxFreq) / 2.0;
            }
        }
    }
    
    private static class WordSegment {
        String text;
        String type;
        
        public WordSegment(String text, String type) {
            this.text = text;
            this.type = type;
        }
    }
}
