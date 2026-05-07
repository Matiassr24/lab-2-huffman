package backend.service;

import backend.model.HuffmanNode;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class HuffmanService {

    public Map<Character, Integer> calculateFrequencies(byte[] data) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (byte b : data) {
            char c = (char) (b & 0xFF); // Convert byte to unsigned char
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }
        return frequencies;
    }

    public HuffmanNode buildTree(Map<Character, Integer> frequencies) {
        if (frequencies.isEmpty()) return null;
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(null, left.getFrequency() + right.getFrequency(), left, right);
            pq.add(parent);
        }

        return pq.poll();
    }

    public Map<Character, String> generateCodes(HuffmanNode root) {
        Map<Character, String> codes = new HashMap<>();
        if (root != null) {
            if (root.isLeaf()) {
                codes.put(root.getCharacter(), "0");
            } else {
                generateCodesRecursive(root, "", codes);
            }
        }
        return codes;
    }

    private void generateCodesRecursive(HuffmanNode node, String code, Map<Character, String> codes) {
        if (node == null) return;
        if (node.isLeaf()) {
            codes.put(node.getCharacter(), code);
            return;
        }
        generateCodesRecursive(node.getLeft(), code + "0", codes);
        generateCodesRecursive(node.getRight(), code + "1", codes);
    }

    public byte[] compress(byte[] data, Map<Character, String> codes, Map<Character, Integer> frequencies) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // 1. Write frequency table size
        dos.writeInt(frequencies.size());

        // 2. Write frequency table
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            dos.writeChar(entry.getKey());
            dos.writeInt(entry.getValue());
        }

        // Calculate total bits first to write the header
        long totalBits = 0;
        for (byte b : data) {
            totalBits += codes.get((char) (b & 0xFF)).length();
        }

        // 3. Write total bits count
        dos.writeLong(totalBits);

        // 4. Pack bits into bytes and write
        int byteVal = 0;
        int bitCount = 0;
        for (byte b : data) {
            String code = codes.get((char) (b & 0xFF));
            for (int i = 0; i < code.length(); i++) {
                byteVal = (byteVal << 1) | (code.charAt(i) == '1' ? 1 : 0);
                bitCount++;
                if (bitCount == 8) {
                    dos.writeByte(byteVal);
                    byteVal = 0;
                    bitCount = 0;
                }
            }
        }
        
        if (bitCount > 0) {
            byteVal <<= (8 - bitCount);
            dos.writeByte(byteVal);
        }

        return baos.toByteArray();
    }

    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        DataInputStream dis = new DataInputStream(bais);

        // 1. Read frequency table size
        int tableSize = dis.readInt();

        // 2. Read frequency table
        Map<Character, Integer> frequencies = new HashMap<>();
        for (int i = 0; i < tableSize; i++) {
            char c = dis.readChar();
            int freq = dis.readInt();
            frequencies.put(c, freq);
        }

        // 3. Rebuild tree
        HuffmanNode root = buildTree(frequencies);

        // 4. Read total bits count
        long totalBits = dis.readLong();

        // 5. Read bits and traverse tree
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        HuffmanNode current = root;
        long bitsRead = 0;

        while (bitsRead < totalBits) {
            int b = dis.readUnsignedByte();
            for (int i = 7; i >= 0 && bitsRead < totalBits; i--) {
                int bit = (b >> i) & 1;
                if (bit == 0) {
                    current = current.getLeft();
                } else {
                    current = current.getRight();
                }

                if (current.isLeaf()) {
                    decompressed.write((byte) (current.getCharacter() & 0xFF));
                    current = root;
                }
                bitsRead++;
            }
        }

        return decompressed.toByteArray();
    }

    public double calculateEntropy(Map<Character, Integer> frequencies, int totalChars) {
        if (totalChars == 0) return 0;
        double entropy = 0;
        for (int freq : frequencies.values()) {
            double p = (double) freq / totalChars;
            entropy += p * (Math.log(1 / p) / Math.log(2));
        }
        return entropy;
    }

    public double calculateAverageLength(Map<Character, Integer> frequencies, Map<Character, String> codes, int totalChars) {
        if (totalChars == 0) return 0;
        double avgLength = 0;
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            double p = (double) entry.getValue() / totalChars;
            String code = codes.get(entry.getKey());
            if (code != null) {
                avgLength += p * code.length();
            }
        }
        return avgLength;
    }
}
