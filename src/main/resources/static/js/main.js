async function generatePDF(btnElement) {
  try {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "application/json";
    input.style.display = "none";
    document.body.appendChild(input);

    // Gestione della rimozione se l'utente annulla (non sempre affidabile, ma una sicurezza in più)
    window.addEventListener('focus', () => {
      setTimeout(() => {
        if (!input.value) {
          if (input.parentNode) input.parentNode.removeChild(input);
        }
      }, 1000);
    }, { once: true });

    input.onchange = async (event) => {
      const file = event.target.files[0];
      if (!file) {
        if (input.parentNode) input.parentNode.removeChild(input);
        return;
      }

      // Salva testo originale e imposta caricamento
      let originalText = "";
      if (!btnElement || !btnElement.innerHTML) {
          btnElement = document.getElementById("generate-pdf-btn");
      }
      if (btnElement) {
        originalText = btnElement.innerHTML;
        if (!document.getElementById("spinner-style")) {
            const style = document.createElement("style");
            style.id = "spinner-style";
            style.innerHTML = "@keyframes spin { to { transform: rotate(360deg); } }";
            document.head.appendChild(style);
        }
        btnElement.innerHTML = `<span style="display:inline-block; margin-right:8px; border:2px solid #fff; border-top-color:transparent; border-radius:50%; width:16px; height:16px; animation:spin 1s linear infinite;"></span> Processing...`;
        btnElement.disabled = true;
        btnElement.style.opacity = "0.7";
        btnElement.style.cursor = "wait";
      }

      try {
        const formData = new FormData();
        formData.append("file", file);

        const client_id = localStorage.getItem("Client-ID");
        const token = localStorage.getItem(client_id + "_access-token");

        const headers = {
          Authorization: `Bearer ${token}`,
          ...getSavedHeaders(),
        };

        const requestOptions = {
          method: "POST",
          body: formData,
          redirect: "follow",
          headers: headers,
        };

        const response = await fetch(
          `${window.location.origin}/api/generate-pdf`,
          requestOptions
        );

        if (!response.ok) {
          window.location.href = "/error.html";
          throw new Error(`Error durante la richiesta: ${response.statusText}`);
        }

        const blob = await response.blob();
        const urlObject = URL.createObjectURL(blob);

        let filename = "Resoconto_Fiscale.pdf";
        const disposition = response.headers.get("Content-Disposition");
        if (disposition && disposition.indexOf("filename=") !== -1) {
            const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(disposition);
            if (matches != null && matches[1]) {
                filename = matches[1].replace(/['"]/g, '');
            }
        }

        const actionToggle = document.getElementById("pdfActionToggle");
        const action = (actionToggle && actionToggle.checked) ? "view" : "download";

        if (action === "download") {
            // Crea link per scaricare il file col nome corretto
            const a = document.createElement("a");
            a.href = urlObject;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            console.log("Download completato: " + filename);
        } else {
            // Apre il file in una nuova tab (perderà il nome originale)
            window.open(urlObject, "_blank");
            console.log("Visualizzazione completata: " + filename);
        }

      } catch (innerError) {
        console.error("Errore durante la generazione:", innerError);
        //alert("Si è verificato un errore durante la generazione del PDF. Riprova.");
      } finally {
        if (input.parentNode) input.parentNode.removeChild(input);
        
        // Ripristina il bottone
        if (btnElement) {
          btnElement.innerHTML = originalText;
          btnElement.disabled = false;
          btnElement.style.opacity = "1";
          btnElement.style.cursor = "pointer";
        }
      }
    };

    input.click();
  } catch (error) {
    console.error("Si è verificato un errore:", error);
  }
}

function getSavedHeaders() {
    const headers = {};

    const parentId = localStorage.getItem("Parent-ID");
    const redirectUri = localStorage.getItem("redirect-uri");
    const sessionId = localStorage.getItem("Session-ID");
    const spanId = localStorage.getItem("Span-ID");
    const traceId = localStorage.getItem("Trace-ID");

    if (parentId) headers["Parent-ID"] = parentId;
    if (redirectUri) headers["redirect-uri"] = redirectUri;
    if (sessionId) headers["Session-ID"] = sessionId;
    if (spanId) headers["Span-ID"] = spanId;
    if (traceId) headers["Trace-ID"] = traceId;

    return headers;
  }