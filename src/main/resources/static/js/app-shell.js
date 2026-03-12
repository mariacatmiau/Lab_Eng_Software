document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.querySelector(".sidebar");
  const overlay = document.getElementById("menuOverlay");
  const toggle = document.getElementById("menuToggle");

  if (toggle && sidebar) {
    toggle.addEventListener("click", () => {
      sidebar.classList.toggle("active");
      if (overlay) overlay.classList.toggle("active");
    });
  }

  if (overlay && sidebar) {
    overlay.addEventListener("click", () => {
      sidebar.classList.remove("active");
      overlay.classList.remove("active");
    });
  }

  document.querySelectorAll("[data-logout]").forEach((node) => {
    node.addEventListener("click", (e) => {
      e.preventDefault();
      localStorage.removeItem("usuario");
      localStorage.removeItem("token");
      window.location.replace("login.html");
    });
  });

  const user = JSON.parse(localStorage.getItem("usuario") || "null");
  if (user?.nome) {
    document.querySelectorAll("[data-user-name]").forEach((node) => {
      node.textContent = user.nome;
    });
  }

  if (window.feather) {
    window.feather.replace();
  }
});
