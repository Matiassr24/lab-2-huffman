package backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuffmanNode implements Comparable<HuffmanNode> {
    private Character caracter;
    private int frecuencia;
    private HuffmanNode izquierdo;
    private HuffmanNode derecho;

    public HuffmanNode(Character caracter, int frecuencia) {
        this.caracter = caracter;
        this.frecuencia = frecuencia;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }

    @Override
    public int compareTo(HuffmanNode otro) {
        return Integer.compare(this.frecuencia, otro.frecuencia);
    }
}
