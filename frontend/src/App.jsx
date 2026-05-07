import { useState } from 'react';
import CargadorArchivos from './components/CargadorArchivos/CargadorArchivos';
import MenuAcciones from './components/MenuAcciones/MenuAcciones';
import VisorArchivos from './components/VisorArchivos/VisorArchivos';

function App() {
  const [archivoReferencia, setArchivoReferencia] = useState(null);
  const [archivoTrabajo, setArchivoTrabajo] = useState(null);
  const [selectedAction, setSelectedAction] = useState(null);
  const [contenidoProcesado, setContenidoProcesado] = useState('');
  const [huffmanData, setHuffmanData] = useState(null);
  const [procesando, setProcesando] = useState(false);
  
  const [refText, setRefText] = useState('');

  const enviarAlBackend = async (archivoSeleccionado, accionSeleccionada) => {
    if (!archivoSeleccionado) {
      alert("Por favor, subí primero un archivo.");
      return;
    }

    console.log("🚀 Iniciando petición Huffman:", { archivo: archivoSeleccionado.name, accion: accionSeleccionada });
    setProcesando(true);

    try {
      const formData = new FormData();
      formData.append('file', archivoSeleccionado);
      
      const endpoint = accionSeleccionada === 'DESCOMPRIMIR' ? '/api/huffman/decompress' : '/api/huffman/compress';
      
      const respuesta = await fetch(endpoint, {
        method: 'POST',
        body: formData,
      });

      if (respuesta.ok) {
        const data = await respuesta.json();
        console.log("✅ Datos Huffman recibidos:", data);
        setHuffmanData(data);
        if (accionSeleccionada === 'COMPRIMIR') {
          setContenidoProcesado(data.cadenaBits || '');
          if (data.datosBinarios) {
            // Convert base64 to blob
            const byteCharacters = atob(data.datosBinarios);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: 'application/octet-stream' });
            
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            const baseName = archivoSeleccionado.name.substring(0, archivoSeleccionado.name.lastIndexOf('.')) || archivoSeleccionado.name;
            link.download = baseName + ".huf";
            link.click();
          }
        } else if (accionSeleccionada === 'DESCOMPRIMIR') {
          setContenidoProcesado(data.textoOriginal || '');
          
          let blob;
          if (data.datosBinarios) {
            // Convert base64 to blob (for binary files like PDF/PPTX)
            const byteCharacters = atob(data.datosBinarios);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            blob = new Blob([byteArray], { type: 'application/octet-stream' });
          } else {
            blob = new Blob([data.textoOriginal], { type: 'text/plain' });
          }

          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          const baseName = archivoSeleccionado.name.substring(0, archivoSeleccionado.name.lastIndexOf('.')) || archivoSeleccionado.name;
          link.download = baseName + ".dhu";
          link.click();
        }
      } else {
        console.error("❌ Error en el servidor:", respuesta.status);
        alert("Error en el servidor al procesar Huffman. ¿El archivo es un .huf válido?");
      }
    } catch (error) {
      console.error("❌ Error de red/conexión:", error);
      alert("No se pudo conectar con el servidor backend.");
    } finally {
      setProcesando(false);
    }
  };

  const handleRefFileDrop = async (file) => {
    setArchivoReferencia(file);
    const nombre = file.name.toLowerCase();
    const esBinario = nombre.endsWith('.pdf') || nombre.endsWith('.pptx') || nombre.endsWith('.ppt');
    
    if (esBinario) {
      setRefText('Referencia binaria cargada (vista previa no disponible).');
    } else {
      try {
        setRefText(await file.text());
      } catch (e) {
        console.error("Error leyendo archivo de referencia:", e);
      }
    }
  };

  const handleWorkFileDrop = (file) => {
    setArchivoTrabajo(file);
    setSelectedAction(null);
    setContenidoProcesado('');
    setHuffmanData(null);
  };

  const handleActionSelect = (action) => {
    setSelectedAction(action);
    enviarAlBackend(archivoTrabajo, action);
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'minmax(20rem, 1fr) 2fr', gap: '1.5rem', padding: '1.5rem', height: '100vh', boxSizing: 'border-box', overflow: 'hidden' }}>
      <aside style={{ display: 'flex', flexDirection: 'column', gap: '1rem', overflowY: 'auto', paddingRight: '0.25rem' }}>
        
        <CargadorArchivos 
          titulo="1. Archivo Original (Ref)"
          subtitulo={archivoReferencia ? `✅ ${archivoReferencia.name}` : "Opcional para comparar"}
          onFileDrop={handleRefFileDrop}
        />

        <CargadorArchivos 
          titulo="2. Archivo a Procesar"
          subtitulo={archivoTrabajo ? `🛠️ ${archivoTrabajo.name}` : "Subí el archivo para Huffman"}
          onFileDrop={handleWorkFileDrop}
        />

        {archivoTrabajo && (
          <div style={{ opacity: procesando ? 0.5 : 1, pointerEvents: procesando ? 'none' : 'auto' }}>
            <MenuAcciones onSelect={handleActionSelect} />
          </div>
        )}

        {procesando && (
            <div style={{ textAlign: 'center', color: '#3b82f6', fontWeight: 'bold', padding: '1rem', background: '#eff6ff', borderRadius: '0.5rem' }}>
                ⌛ Procesando Huffman...
            </div>
        )}
      </aside>

      <main style={{ height: '100%', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
        <VisorArchivos 
          archivo={archivoTrabajo} 
          accion={selectedAction} 
          resultadoProcesado={contenidoProcesado} 
          referenciaManual={refText}
          nombreReferencia={archivoReferencia?.name}
          huffmanData={huffmanData}
        />
      </main>
    </div>
  );
}

export default App;