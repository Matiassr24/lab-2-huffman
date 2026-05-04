package backend.controller;

import backend.dto.HuffmanResponse;
import backend.model.HuffmanNode;
import backend.service.HuffmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/huffman")
@CrossOrigin(origins = "*")
public class HuffmanController {

    @Autowired
    private HuffmanService huffmanService;

    @PostMapping("/compress")
    public ResponseEntity<HuffmanResponse> compress(@RequestParam("file") MultipartFile file) throws Exception {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<Character, Integer> frequencies = huffmanService.calculateFrequencies(text);
        HuffmanNode root = huffmanService.buildTree(frequencies);
        Map<Character, String> codes = huffmanService.generateCodes(root);

        byte[] compressedBinary = huffmanService.compress(text, codes, frequencies);

        StringBuilder bitString = new StringBuilder();
        for (char c : text.toCharArray()) {
            bitString.append(codes.get(c));
        }

        int totalChars = text.length();
        double entropy = huffmanService.calculateEntropy(frequencies, totalChars);
        double avgLength = huffmanService.calculateAverageLength(frequencies, codes, totalChars);
        double efficiency = entropy / avgLength;

        long originalSizeBytes = file.getSize();
        long compressedSizeBytes = compressedBinary.length;
        double compressionRatio = (double) originalSizeBytes / compressedSizeBytes;

        HuffmanResponse response = HuffmanResponse.builder()
                .originalText(text)
                .frequencies(frequencies)
                .codes(codes)
                .entropy(entropy)
                .averageLength(avgLength)
                .efficiency(efficiency)
                .originalSizeBytes(originalSizeBytes)
                .compressedSizeBytes(compressedSizeBytes)
                .compressionRatio(compressionRatio)
                .bitString(bitString.toString())
                .binaryData(compressedBinary)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/decompress")
    public ResponseEntity<HuffmanResponse> decompress(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] compressedData = file.getBytes();
        String decompressedText = huffmanService.decompress(compressedData);

        long compressedSizeBytes = file.getSize();
        long decompressedSizeBytes = decompressedText.getBytes(StandardCharsets.UTF_8).length;

        HuffmanResponse response = HuffmanResponse.builder()
                .originalText(decompressedText)
                .compressedSizeBytes(compressedSizeBytes)
                .decompressedSizeBytes(decompressedSizeBytes)
                .build();

        return ResponseEntity.ok(response);
    }
}
