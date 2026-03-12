const API_ORIGIN =
  window.location.origin && window.location.origin.startsWith("http")
    ? window.location.origin
    : "http://localhost:8080";
const API_URL = `${API_ORIGIN}/api`;

const originalFetch = window.fetch.bind(window);

window.fetch = async (input, init = {}) => {
  const requestUrl = typeof input === "string" ? input : input.url;
  const absoluteUrl = requestUrl.startsWith("http")
    ? requestUrl
    : new URL(requestUrl, window.location.origin).toString();

  const isApiCall = absoluteUrl.startsWith(API_URL);
  const isAuthRoute = absoluteUrl.includes("/api/usuarios/login") || absoluteUrl.includes("/api/usuarios/register");

  const nextInit = { ...init, headers: { ...(init.headers || {}) } };
  if (isApiCall && !isAuthRoute) {
    const token = localStorage.getItem("token");
    if (token) {
      nextInit.headers.Authorization = `Bearer ${token}`;
    }
  }

  const response = await originalFetch(input, nextInit);
  if (isApiCall && response.status === 401) {
    localStorage.removeItem("usuario");
    localStorage.removeItem("token");
    if (!window.location.pathname.endsWith("login.html")) {
      window.location.href = "login.html";
    }
  }
  return response;
};

function redirectIfNotLogged() {
  if (!localStorage.getItem("usuario") || !localStorage.getItem("token")) {
    window.location.href = "login.html";
  }
}

document.addEventListener("click", (event) => {
  const target = event.target instanceof Element ? event.target.closest("[data-logout]") : null;
  if (!target) return;

  event.preventDefault();
  localStorage.removeItem("usuario");
  localStorage.removeItem("token");
  window.location.replace("login.html");
});
