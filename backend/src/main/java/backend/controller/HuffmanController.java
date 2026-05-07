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
        byte[] fileBytes = file.getBytes();
        if (fileBytes.length == 0) {
            return ResponseEntity.badRequest().build();
        }

        // We still extract text for preview purposes (only valid for .txt files)
        String previewText = new String(fileBytes, StandardCharsets.UTF_8);

        Map<Character, Integer> frequencies = huffmanService.calculateFrequencies(fileBytes);
        HuffmanNode root = huffmanService.buildTree(frequencies);
        Map<Character, String> codes = huffmanService.generateCodes(root);

        byte[] compressedBinary = huffmanService.compress(fileBytes, codes, frequencies);

        StringBuilder bitString = new StringBuilder();
        for (byte b : fileBytes) {
            bitString.append(codes.get((char) (b & 0xFF)));
        }

        int totalSymbols = fileBytes.length;
        double entropy = huffmanService.calculateEntropy(frequencies, totalSymbols);
        double avgLength = huffmanService.calculateAverageLength(frequencies, codes, totalSymbols);
        double efficiency = avgLength > 0 ? entropy / avgLength : 0;

        long originalSizeBytes = file.getSize();
        long compressedSizeBytes = compressedBinary.length;
        double compressionRatio = compressedSizeBytes > 0 ? (double) originalSizeBytes / compressedSizeBytes : 0;

        HuffmanResponse response = HuffmanResponse.builder()
                .originalText(previewText)
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
        byte[] decompressedBytes = huffmanService.decompress(compressedData);

        long compressedSizeBytes = file.getSize();
        long decompressedSizeBytes = decompressedBytes.length;
        
        String decompressedText = new String(decompressedBytes, StandardCharsets.UTF_8);

        HuffmanResponse response = HuffmanResponse.builder()
                .originalText(decompressedText)
                .compressedSizeBytes(compressedSizeBytes)
                .decompressedSizeBytes(decompressedSizeBytes)
                .binaryData(decompressedBytes)
                .build();

        return ResponseEntity.ok(response);
    }
}
