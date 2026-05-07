import { useState, useEffect, useRef } from 'react';
import { FileText, BarChart3, Database, Info } from 'lucide-react';
import styles from './VisorArchivos.module.css';

const VisorArchivos = ({ archivo, accion, resultadoProcesado, referenciaManual, nombreReferencia, huffmanData }) => {
  const [contenidoOriginal, setContenidoOriginal] = useState('');
  const panelOriginalRef = useRef(null);
  const panelProcesadoRef = useRef(null);

  useEffect(() => {
    if (archivo) {
      const nombre = archivo.name.toLowerCase();
      const esBinario = nombre.endsWith('.pdf') || nombre.endsWith('.pptx') || nombre.endsWith('.ppt');
      
      if (esBinario) {
        setContenidoOriginal('Vista previa no disponible para archivos binarios (PDF/PowerPoint).');
      } else {
        const lector = new FileReader();
        lector.onload = (evento) => {
          setContenidoOriginal(evento.target.result);
        };
        lector.readAsText(archivo);
      }
    }
  }, [archivo]);

  if (!archivo) {
    return (
      <div className={styles.contenedorVacio}>
        <p>Seleccioná un archivo para comenzar el análisis de <b>Huffman</b>.</p>
      </div>
    );
  }

  return (
    <div className={styles.contenedorVisor}>
      <div className={styles.cabecera}>
        <div className={styles.tituloWrapper}>
          <FileText className={styles.iconoTitulo} />
          <h2 className={styles.titulo}>Análisis Huffman: {archivo.name}</h2>
        </div>
      </div>

      {huffmanData && (
        <div className={styles.gridEstadisticas}>
          <div className={styles.tarjetaStats}>
            <BarChart3 size={20} className={styles.iconoStat} />
            <div className={styles.statLabel}>Tamaños (Bytes)</div>
            <div className={styles.statValor}>
              {huffmanData.tamanoOriginalBytes || huffmanData.tamanoDescomprimidoBytes || '?'} ➔ {huffmanData.tamanoComprimidoBytes || '?'}
            </div>
          </div>
          <div className={styles.tarjetaStats}>
            <Info size={20} className={styles.iconoStat} />
            <div className={styles.statLabel}>Ahorro / Ratio</div>
            <div className={styles.statValor}>
              {huffmanData.ratioCompresion ? `${((1 - 1/huffmanData.ratioCompresion) * 100).toFixed(1)}%` : 
               huffmanData.tamanoDescomprimidoBytes ? `${((1 - huffmanData.tamanoComprimidoBytes/huffmanData.tamanoDescomprimidoBytes) * 100).toFixed(1)}%` : '-'}
            </div>
          </div>
          <div className={styles.tarjetaStats}>
            <Database size={20} className={styles.iconoStat} />
            <div className={styles.statLabel}>Eficiencia (η)</div>
            <div className={styles.statValor}>{huffmanData.eficiencia ? `${(huffmanData.eficiencia * 100).toFixed(2)}%` : '-'}</div>
          </div>
          <div className={styles.tarjetaStats}>
            <FileText size={20} className={styles.iconoStat} />
            <div className={styles.statLabel}>Entropía (H)</div>
            <div className={styles.statValor}>{huffmanData.entropia ? huffmanData.entropia.toFixed(4) : '-'}</div>
          </div>
        </div>
      )}

      <div className={styles.contenedorDoble}>
        {/* Panel Izquierdo: Frecuencias y Códigos */}
        <div className={styles.panel}>
          <h3 className={styles.subtituloPanel}><Database size={16} /> Frecuencias y Códigos</h3>
          <div className={styles.tablaWrapper}>
            <table className={styles.tablaHuffman}>
              <thead>
                <tr>
                  <th>Símbolo</th>
                  <th>Freq.</th>
                  <th>Código</th>
                </tr>
              </thead>
              <tbody>
                {huffmanData?.frecuencias && Object.entries(huffmanData.frecuencias).sort((a,b) => b[1] - a[1]).map(([char, freq]) => (
                  <tr key={char}>
                    <td>
                      {char === ' ' ? '␣ (Espacio)' : 
                       char === '\n' ? '↵ (Enter)' : 
                       char === '\r' ? '↵ (CR)' :
                       char === '\t' ? '⇥ (Tab)' :
                       (char.charCodeAt(0) < 32 || char.charCodeAt(0) > 126) ? 
                       `0x${char.charCodeAt(0).toString(16).toUpperCase().padStart(2, '0')}` : 
                       char}
                    </td>
                    <td>{freq}</td>
                    <td className={styles.codigoHuffman}>{huffmanData.codigos[char]}</td>
                  </tr>
                ))}
                {(!huffmanData || !huffmanData.frecuencias) && <tr><td colSpan="3" style={{textAlign: 'center'}}>Procesá para ver frecuencias</td></tr>}
              </tbody>
            </table>
          </div>
        </div>

        {/* Panel Derecho: Salida */}
        <div className={styles.panel}>
          <h3 className={styles.subtituloPanel}>
            <FileText size={16} /> 
            {accion === 'DESCOMPRIMIR' ? 'Texto Descomprimido' : 'Salida Binaria (Huffman)'}
          </h3>
          <div className={styles.areaTexto}>
            {resultadoProcesado || 'Aquí aparecerá el resultado...'}
          </div>
          {huffmanData && (
            <div className={styles.metadatosSalida}>
              Bytes: {huffmanData.tamanoComprimidoBytes || 0}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default VisorArchivos;