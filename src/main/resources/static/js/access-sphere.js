(async function initialize() {
  let urlConfig = await configuration;
  if (!urlConfig) urlConfig = await loadConfig();
  console.log("✅ Configuration loaded:", urlConfig);

  let authGranted = false;
  let contentInjected = false;

  function renderContent() {
    if (contentInjected) return;
    
    // Check if DOM is ready
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', inject);
    } else {
      inject();
    }
  }

  function inject() {
    if (contentInjected) return;
    const template = document.getElementById("protected-template");
    const appRoot = document.getElementById("app-root");
    
    if (template && appRoot) {
      appRoot.appendChild(template.content.cloneNode(true));
      template.remove();
      contentInjected = true;
    } else {
      const landing_page = document.getElementById("landing-page");
      if (landing_page) {
        landing_page.style.display = "block";
        contentInjected = true;
      }
    }
  }

  document.addEventListener("ACCESS_SPHERE_AUTH", (event) => {
    const { success, error } = event.detail;
    if (success && !error) {
      authGranted = true;
      renderContent();
    } else {
      window.location.href = window.location.origin + "/error.html";
    }
  });

  document.addEventListener("ACCESS_SPHERE_TOKEN", (event) => {
    const { success, error } = event.detail;
    if (success && !error) {
      authGranted = true;
      renderContent();
    } else {
      window.location.href = window.location.origin + "/error.html";
    }
  });
})();

function logout() {
  console.log("🔴 Logout started...");
  window.AccessSphere.logout();
}