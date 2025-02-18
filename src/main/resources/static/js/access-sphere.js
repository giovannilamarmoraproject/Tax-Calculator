(async function initialize() {
  // Assicurati che loadConfig sia già definito globalmente
  const urlConfig = await loadConfig();
  console.log("Configuration loaded:", urlConfig);

  localStorage.setItem(
    "Client-ID",
    urlConfig.client_id.replace("client_id=", "")
  );

  $(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    const access_token = urlParams.get("access-token");
    const session_id = urlParams.get("session-id");
    const client_id = localStorage.getItem("Client-ID");

    if (code) {
      // Chiama l'endpoint /token per scambiare il codice con un token
      exchangeCodeForToken(code);
    } else {
      if (access_token) {
        if (session_id) localStorage.setItem("Session-ID", session_id);
        localStorage.setItem(client_id + "_access-token", access_token);
        const cleanUrl = window.location.origin + window.location.pathname;
        window.history.replaceState(null, "", cleanUrl);
      }
      authorizeToken(); // Se il codice non è presente, richiama authorizeToken
    }
    $("#logout-btn").click(function () {
      logout(); // Chiamata alla funzione logout
    });
  });

  function exchangeCodeForToken(code) {
    const urlParams = new URLSearchParams(window.location.search);
    const scope = urlParams.get("scope");

    // Costruisce l'URL per ottenere il token
    const tokenUrl = new URL(urlConfig.baseUrl + urlConfig.token);
    const params = new URLSearchParams({
      client_id: urlConfig.client_id.replace("client_id=", ""),
      code: code,
      redirect_uri: urlConfig.redirect_uri.replace("redirect_uri=", ""),
      grant_type: urlConfig.grant_type.replace("grant_type=", ""),
      scope: scope,
    });
    tokenUrl.search = params.toString();

    const cleanUrl = window.location.origin + window.location.pathname;
    window.history.replaceState(null, "", cleanUrl);

    const client_id = localStorage.getItem("Client-ID");

    fetch(tokenUrl.toString(), {
      method: "POST",
      //mode: "no-cors", // Disabilita il controllo CORS (ma la risposta sarà "opaque")
      headers: {
        "Content-Type": "application/json",
        ...getSavedHeaders(),
      },
      credentials: "same-origin",
    })
      .then((response) => {
        fetchHeader(response.headers);
        return response.json();
      })
      .then((data) => {
        if (data.data) {
          const token = data.data.token;
          const strapiToken = data.data.strapiToken;
          // Salva il token nel cookie o nel local storage
          if (token && strapiToken) {
            if (token.access_token)
              localStorage.setItem(
                client_id + "_access-token",
                token.access_token
              );
          } else {
            window.location.href = window.location.origin + "/error.html";
          }
          const landing_page = document.getElementById("landing-page");
          if (landing_page) landing_page.style.display = "block";
        } else {
          localStorage.clear();
          console.error("Token exchange failed", data);
          window.location.href = window.location.origin + "/error.html";
        }
      })
      .catch((error) => {
        //localStorage.clear();
        window.location.href = window.location.origin + "/error.html";
      });
  }

  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
  }

  function authorizeToken() {
    const client_id = localStorage.getItem("Client-ID");
    const token =
      getCookie("access-token") ||
      localStorage.getItem(client_id + "_access-token");

    const url =
      urlConfig.baseUrl +
      urlConfig.authorize +
      urlConfig.param +
      urlConfig.client_id +
      urlConfig.divider +
      urlConfig.access_type +
      urlConfig.divider +
      urlConfig.redirect_uri +
      urlConfig.divider +
      urlConfig.scope +
      urlConfig.divider +
      urlConfig.response_type;

    // Configura gli header
    const headers = token
      ? { Authorization: `Bearer ${token}`, ...getSavedHeaders() }
      : {
          Authorization: null,
        };

    fetch(url, {
      method: "GET",
      headers: headers,
      redirect: "follow",
      mode: "cors", // no-cors, *cors, same-origin
      //credentials: "include",
    })
      .then((response) => {
        fetchHeader(response.headers);
        console.log(response);
        if (response.ok) {
          const locationHeader = response.headers.get("Location");
          const redirectUrl =
            locationHeader ?? (response.url !== url ? response.url : null);

          if (redirectUrl) {
            window.location.href = redirectUrl;
          }
        } else if (!response.ok) {
          localStorage.clear();
          console.error("Authorization check failed.");
          location.reload(true);
        }
        return response.json();
      })
      .then((response) => {
        const landing_page = document.getElementById("landing-page");
        if (landing_page) landing_page.style.display = "block";
      })
      .catch((error) => {
        window.location.href = window.location.origin + "/error.html";
      });
  }

  function logout() {
    const client_id = localStorage.getItem("Client-ID");
    const token =
      getCookie("access-token") ||
      localStorage.getItem(client_id + "_access-token");

    const logoutUrl =
      urlConfig.baseUrl +
      urlConfig.logout +
      urlConfig.param +
      urlConfig.client_id;

    fetch(logoutUrl, {
      method: "POST",
      //mode: "no-cors", // Disabilita il controllo CORS (ma la risposta sarà "opaque")
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        ...getSavedHeaders(),
      },
      credentials: "same-origin",
    })
      .then((response) => {
        fetchHeader(response.headers);
        return response.json();
      })
      .finally((res) => {
        localStorage.clear();
        location.reload();
      })
      .catch((error) => {
        //localStorage.clear();
        window.location.href = window.location.origin + "/error.html";
      });
  }

  function fetchHeader(headers) {
    // Leggi gli header specifici che ti interessano
    const parentId = headers.get("Parent-ID");
    const redirectUri = headers.get("redirect-uri");
    const sessionId = headers.get("Session-ID");
    const spanId = headers.get("Span-ID");
    const traceId = headers.get("Trace-ID");

    // Salva gli header in localStorage o sessionStorage
    if (parentId) localStorage.setItem("Parent-ID", parentId);
    if (redirectUri) localStorage.setItem("redirect-uri", redirectUri);
    if (sessionId) localStorage.setItem("Session-ID", sessionId);
    if (spanId) localStorage.setItem("Span-ID", spanId);
    if (traceId) localStorage.setItem("Trace-ID", traceId);
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
})();
