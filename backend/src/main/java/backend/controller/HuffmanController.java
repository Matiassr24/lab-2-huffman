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
    public ResponseEntity<HuffmanResponse> comprimir(@RequestParam("file") MultipartFile archivo) throws Exception {
        byte[] bytesArchivo = archivo.getBytes();
        if (bytesArchivo.length == 0) {
            return ResponseEntity.badRequest().build();
        }

        // Extraemos texto para previsualización (válido para .txt)
        String vistaPreviaTexto = new String(bytesArchivo, StandardCharsets.UTF_8);

        Map<Character, Integer> frecuencias = huffmanService.calcularFrecuencias(bytesArchivo);
        HuffmanNode raiz = huffmanService.construirArbol(frecuencias);
        Map<Character, String> codigos = huffmanService.generarCodigos(raiz);

        byte[] binarioComprimido = huffmanService.comprimir(bytesArchivo, codigos, frecuencias);

        // Limitar cadena de bits para visualización para evitar problemas de memoria
        StringBuilder vistaPreviaBits = new StringBuilder();
        int maxBitsPrevia = 10000;
        int bitsAnadidos = 0;
        for (byte b : bytesArchivo) {
            String codigo = codigos.get((char) (b & 0xFF));
            if (bitsAnadidos + codigo.length() <= maxBitsPrevia) {
                vistaPreviaBits.append(codigo);
                bitsAnadidos += codigo.length();
            } else {
                vistaPreviaBits.append("...");
                break;
            }
        }

        int totalSimbolos = bytesArchivo.length;
        double entropia = huffmanService.calcularEntropia(frecuencias, totalSimbolos);
        double longitudMedia = huffmanService.calcularLongitudMedia(frecuencias, codigos, totalSimbolos);
        double eficiencia = longitudMedia > 0 ? entropia / longitudMedia : 0;

        long tamanoOriginalBytes = archivo.getSize();
        long tamanoComprimidoBytes = binarioComprimido.length;
        double ratioCompresion = tamanoComprimidoBytes > 0 ? (double) tamanoOriginalBytes / tamanoComprimidoBytes : 0;

        HuffmanResponse respuesta = HuffmanResponse.builder()
                .textoOriginal(bytesArchivo.length < 50000 ? vistaPreviaTexto : "Archivo demasiado grande para vista previa")
                .frecuencias(frecuencias)
                .codigos(codigos)
                .entropia(entropia)
                .longitudMedia(longitudMedia)
                .eficiencia(eficiencia)
                .tamanoOriginalBytes(tamanoOriginalBytes)
                .tamanoComprimidoBytes(tamanoComprimidoBytes)
                .ratioCompresion(ratioCompresion)
                .cadenaBits(vistaPreviaBits.toString())
                .datosBinarios(binarioComprimido)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/decompress")
    public ResponseEntity<HuffmanResponse> descomprimir(@RequestParam("file") MultipartFile archivo) throws Exception {
        byte[] datosComprimidos = archivo.getBytes();
        byte[] bytesDescomprimidos = huffmanService.descomprimir(datosComprimidos);

        long tamanoComprimidoBytes = archivo.getSize();
        long tamanoDescomprimidoBytes = bytesDescomprimidos.length;
        
        String textoDescomprimido = new String(bytesDescomprimidos, StandardCharsets.UTF_8);

        HuffmanResponse respuesta = HuffmanResponse.builder()
                .textoOriginal(textoDescomprimido)
                .tamanoComprimidoBytes(tamanoComprimidoBytes)
                .tamanoDescomprimidoBytes(tamanoDescomprimidoBytes)
                .datosBinarios(bytesDescomprimidos)
                .build();

        return ResponseEntity.ok(respuesta);
    }
}
