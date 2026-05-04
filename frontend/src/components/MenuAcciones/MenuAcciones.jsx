import { FileArchive, FileText, BarChart3, Database } from 'lucide-react';
import styles from './MenuAcciones.module.css';

const MenuAcciones = ({ onSelect }) => {
  return (
    <div className={styles.contenedorMenu}>
      <h2 className={styles.titulo}>Acciones Huffman</h2>

      {/* SECCIÓN 1: COMPRESIÓN */}
      <div className={styles.seccion}>
        <h3 className={styles.subtitulo}>1. Compresión</h3>
        <div className={styles.grupoBotones}>
          <button className={styles.boton} onClick={() => onSelect('COMPRIMIR')}>
            <FileArchive size={16} />
            <span>Comprimir Archivo (.huf)</span>
          </button>
        </div>
      </div>

      <hr className={styles.divisor} />

      {/* SECCIÓN 2: DESCOMPRESIÓN */}
      <div className={styles.seccion}>
        <h3 className={styles.subtitulo}>2. Descompresión</h3>
        <div className={styles.grupoBotones}>
          <button className={styles.boton} onClick={() => onSelect('DESCOMPRIMIR')}>
            <FileText size={16} />
            <span>Descomprimir (.txt)</span>
          </button>
        </div>
      </div>

      <hr className={styles.divisor} />

      {/* SECCIÓN 3: ANÁLISIS */}
      <div className={styles.seccion}>
        <h3 className={styles.subtitulo}>3. Análisis y Datos</h3>
        <div className={styles.grupoBotones}>
          <button className={styles.boton} onClick={() => onSelect('VER_TABLA')}>
            <Database size={16} />
            <span>Tabla de Frecuencias</span>
          </button>
          
          <button className={styles.boton} onClick={() => onSelect('VER_ESTADISTICAS')}>
            <BarChart3 size={16} />
            <span>Ver Estadísticas</span>
          </button>
        </div>
      </div>

    </div>
  );
};

export default MenuAcciones;