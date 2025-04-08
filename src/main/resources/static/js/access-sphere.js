(async function initialize() {
  // Assicurati che loadConfig sia già definito globalmente
  const urlConfig = await configuration;
  if (!urlConfig) urlConfig = await loadConfig();
  console.log("✅ Configuration loaded:", urlConfig);

  /**
   * ------------------------------
   * Access Sphere Authorize
   * ------------------------------
   */
  document.addEventListener("ACCESS_SPHERE_AUTH", (event) => {
    const { success, data, error } = event.detail;
    if (success && !error) {
      const landing_page = document.getElementById("landing-page");
      if (landing_page) landing_page.style.display = "block";
    } else {
      window.location.href = window.location.origin + "/error.html";
    }
  });

  /**
   * ------------------------------
   * Access Sphere Token
   * ------------------------------
   */
  document.addEventListener("ACCESS_SPHERE_TOKEN", (event) => {
    const { success, data, error } = event.detail;
    if (success && !error) {
      const landing_page = document.getElementById("landing-page");
      if (landing_page) landing_page.style.display = "block";
    } else {
      window.location.href = window.location.origin + "/error.html";
    }
  });
})();

/**
 * ------------------------------
 * Logout Process
 * ------------------------------
 */
function logout() {
  console.log("🔴 Logout started...");
  window.AccessSphere.logout();
}
