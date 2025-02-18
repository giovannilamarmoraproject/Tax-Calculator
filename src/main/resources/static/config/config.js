const localConfig = {
  baseUrl: "http://localhost:8081",
  client_id: "client_id=TAX-CALCULATOR-TEST-01",
  redirect_uri: "redirect_uri=http://localhost:8085",
  authorize: "/v1/oAuth/2.0/authorize",
  token: "/v1/oAuth/2.0/token",
  logout: "/v1/oAuth/2.0/logout",
  param: "?",
  divider: "&",
  access_type: "access_type=online",
  scope: "scope=openid",
  login_type_bearer: "type=bearer",
  login_type_google: "type=google",
  response_type: "response_type=code",
  grant_type: "grant_type=authorization_code",
};

const fallbackConfig = {
  baseUrl: "https://access.sphere.service.giovannilamarmora.com",
  client_id: "client_id=TAX-CALCULATOR-01",
  redirect_uri: "redirect_uri=https://tax-calculator.giovannilamarmora.com",
  authorize: "/v1/oAuth/2.0/authorize",
  token: "/v1/oAuth/2.0/token",
  logout: "/v1/oAuth/2.0/logout",
  param: "?",
  divider: "&",
  access_type: "access_type=online",
  scope: "scope=openid",
  login_type_bearer: "type=bearer",
  login_type_google: "type=google",
  response_type: "response_type=code",
  grant_type: "grant_type=authorization_code",
};

async function loadConfig() {
  const isLocal =
    window.location.hostname === "localhost" ||
    window.location.hostname === "127.0.0.1";

  if (isLocal) {
    console.log("Using local configuration");
    return localConfig;
  }

  try {
    const response = await fetch(
      "https://raw.githubusercontent.com/giovannilamarmoraproject/Tax-Calculator/refs/heads/master/src/main/resources/static/config/config.jsonc"
    );

    if (!response.ok) {
      throw new Error(`Failed to fetch remote config: ${response.statusText}`);
    }

    const jsoncText = await response.text();

    // Rimuovi i commenti dal JSONC
    const jsonText = jsoncText.replace(
      /("(?:\\.|[^"\\])*")|\/\/.*|\/\*[\s\S]*?\*\//g,
      (match, group1) => group1 || ""
    );

    const remoteConfig = JSON.parse(jsonText);

    console.log("Using remote configuration from GitHub");
    return remoteConfig;
  } catch (error) {
    console.error(
      "Failed to load remote configuration. Using fallback config.",
      error
    );
    return localConfig;
  }
}
