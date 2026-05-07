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
    private String textoOriginal;
    private String textoComprimido;
    private Map<Character, Integer> frecuencias;
    private Map<Character, String> codigos;
    private double entropia;
    private double longitudMedia;
    private double eficiencia;
    private int tamanoOriginalBits;
    private int tamanoComprimidoBits;
    private long tamanoOriginalBytes;
    private long tamanoComprimidoBytes;
    private long tamanoDescomprimidoBytes;
    private double ratioCompresion;
    private String cadenaBits;
    private byte[] datosBinarios;
}
