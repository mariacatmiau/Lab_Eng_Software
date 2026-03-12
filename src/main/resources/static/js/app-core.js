window.AppCore = (() => {
  const apiOrigin =
    window.location.origin && window.location.origin.startsWith("http")
      ? window.location.origin
      : "http://localhost:8080";
  const apiBase = `${apiOrigin}/api`;

  function readStoredUser() {
    try {
      return JSON.parse(localStorage.getItem("usuario") || "null");
    } catch {
      return null;
    }
  }

  function writeStoredUser(user) {
    localStorage.setItem("usuario", JSON.stringify(user));
  }

  function clearSession() {
    localStorage.removeItem("usuario");
    localStorage.removeItem("token");
  }

  function escapeHtml(value) {
    return String(value)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
  }

  function formatCurrency(value) {
    const number = Number(value);
    if (!Number.isFinite(number) || number <= 0) {
      return "a combinar";
    }
    return number.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
  }

  function resolveDashboardByRole(role) {
    const normalized = String(role || "").trim().toUpperCase();
    if (normalized === "ONG") return "dashboard-ong.html";
    if (normalized === "CLIENTE") return "dashboard-cliente.html";
    if (normalized === "FUNCIONARIO") return "dashboard-funcionario.html";
    return "login.html";
  }

  return {
    apiOrigin,
    apiBase,
    readStoredUser,
    writeStoredUser,
    clearSession,
    escapeHtml,
    formatCurrency,
    resolveDashboardByRole,
  };
})();