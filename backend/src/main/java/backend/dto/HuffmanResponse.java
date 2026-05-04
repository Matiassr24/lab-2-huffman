package backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HuffmanResponse {
    private String originalText;
    private String compressedText;
    private Map<Character, Integer> frequencies;
    private Map<Character, String> codes;
    private double entropy;
    private double averageLength;
    private double efficiency;
    private int originalSizeBits;
    private int compressedSizeBits;
    private long originalSizeBytes;
    private long compressedSizeBytes;
    private long decompressedSizeBytes;
    private double compressionRatio;
    private String bitString;
    private byte[] binaryData;
}
