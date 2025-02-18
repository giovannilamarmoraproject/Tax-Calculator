async function generatePDF() {
  try {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "application/json";

    input.onchange = async (event) => {
      const file = event.target.files[0];
      if (!file) {
        throw new Error("No file selected");
      }

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
      window.open(urlObject, "_blank");

      //const link = document.createElement('a');
      //link.href = urlObject;
      //link.download = 'report.pdf';

      //document.body.appendChild(link);
      //link.click();

      //URL.revokeObjectURL(urlObject);
      //document.body.removeChild(link);

      //console.log('Download completato.');
      console.log("Visualizzazione completata.");
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