const localConfig = {
  baseUrl: "http://localhost:8081",
  access_sphere_script: "/app/authentication/auth.js",
  client_id: "TAX-CALCULATOR-TEST-01",
  redirect_uri: "http://localhost:8085",
};

const fallbackConfig = {
  baseUrl: "https://access.sphere.service.giovannilamarmora.com",
  access_sphere_script: "/app/authentication/auth.js",
  client_id: "TAX-CALCULATOR-01",
  redirect_uri: "https://tax-calculator.giovannilamarmora.com",
};

let configuration;

async function init() {
  configuration = await loadConfig(); // ✅ Aspetta il valore prima di continuare
  window.accessSphereConfig = {
    client_id: configuration.client_id,
    redirect_uri: configuration.redirect_uri,
  };
  initScriptAccessSphere(configuration);
}

init();

async function loadConfig() {
  const isLocal =
    window.location.hostname === "localhost" ||
    window.location.hostname === "127.0.0.1";

  if (isLocal) {
    console.log("🔍 Using local configuration");
    return localConfig;
  }

  try {
    const response = await fetch(
      "https://raw.githubusercontent.com/giovannilamarmoraproject/Tax-Calculator/refs/heads/master/src/main/resources/static/config/config.jsonc"
    );

    if (!response.ok) {
      throw new Error(
        `❌ Failed to fetch remote config: ${response.statusText}`
      );
    }

    const jsoncText = await response.text();

    // Rimuovi i commenti dal JSONC
    const jsonText = jsoncText.replace(
      /("(?:\\.|[^"\\])*")|\/\/.*|\/\*[\s\S]*?\*\//g,
      (match, group1) => group1 || ""
    );

    const remoteConfig = JSON.parse(jsonText);

    console.log("🔍 Using remote configuration from GitHub");
    return remoteConfig;
  } catch (error) {
    console.error(
      "❌ Failed to load remote configuration. Using fallback config.",
      error
    );
    return localConfig;
  }
}

/**
 * ---------------------------------
 * Caricamento dello Script Access Sphere
 * ---------------------------------
 */
function initScriptAccessSphere(config) {
  console.log("Caricamento Script");
  window.accessSphereConfig = {
    client_id: config.client_id,
    redirect_uri: config.redirect_uri,
  };
  loadScript(config.baseUrl + config.access_sphere_script, function () {
    console.log("✅ Access Sphere Script loaded successfully!");
  });
}
function loadScript(url, callback) {
  const script = document.createElement("script");
  script.src = url;
  script.onload = callback;
  document.head.appendChild(script);
}
/**
 * ---------------------------------
 * END Caricamento dello Script Access Sphere
 * ---------------------------------
 */
