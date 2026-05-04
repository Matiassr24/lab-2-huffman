import { useRef } from 'react';
import { CloudUpload } from 'lucide-react';
import styles from './CargadorArchivos.module.css';

const CargadorArchivos = ({ onFileDrop, titulo, subtitulo, colorClass, icono: Icono = CloudUpload }) => {
  const inputRef = useRef(null);

  const extensionesPermitidas = [
    '.txt', 
    '.HA1', '.HA2', '.HA3', 
    '.HE1', '.HE2', '.HE3', 
    '.DE1', '.DE2', '.DE3', 
    '.DC1', '.DC2', '.DC3'
  ];

  const manejarCambioArchivo = (evento) => {
    const archivoSeleccionado = evento.target.files[0];
    
    if (archivoSeleccionado) {
      const nombre = archivoSeleccionado.name;
      const esValido = extensionesPermitidas.some(ext => nombre.toLowerCase().endsWith(ext.toLowerCase()));

      if (esValido) {
        onFileDrop(archivoSeleccionado);
      } else {
        alert("Archivo no permitido. Usá .txt o variables de Hamming (.HAx, .HEx, .DEx, .DCx)");
      }
    }
  };

  return (
    <div className={`${styles.contenedorCargador} ${colorClass ? styles[colorClass] : ''}`}>
      <h2 className={styles.titulo}>{titulo || 'Archivo de Trabajo'}</h2>

      <div
        className={styles.areaSubida}
        onClick={() => inputRef.current.click()}
      >
        <Icono className={styles.iconoNube} />
        <p className={styles.textoSubida}>
          {subtitulo || 'Seleccionar archivo'}
        </p>
        <input
          type="file" 
          accept=".txt,.HA1,.HA2,.HA3,.HE1,.HE2,.HE3,.DE1,.DE2,.DE3,.DC1,.DC2,.DC3"
          ref={inputRef}
          onChange={manejarCambioArchivo}
          className={styles.inputOculto}
        />
      </div>
    </div>
  );
};

export default CargadorArchivos;