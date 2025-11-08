const API = "http://localhost:8080/api/auth";

// --- LOGIN ---
if (document.getElementById("loginForm")) {
  document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value.trim();
    const errorMsg = document.getElementById("loginError");

    try {
      const res = await fetch(`${API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha })
      });

      if (!res.ok) {
        console.error("Falha no login:", res.status);
        errorMsg.classList.remove("hidden");
        return;
      }

      const data = await res.json();
      console.log("Login retornou:", data);

      // Garante que há usuário retornado
      if (!data.usuario) {
        alert("Resposta inesperada do servidor. Tente novamente.");
        return;
      }

      // Salva no localStorage para uso posterior
      localStorage.setItem("usuario", JSON.stringify(data.usuario));

      if (data.funcionarioId) {
        localStorage.setItem("funcionarioId", data.funcionarioId);
      }
      if (data.ongId) {
        localStorage.setItem("ongId", data.ongId);
      }

      // Redireciona conforme o tipo de usuário
      if (data.usuario.tipo === "FUNCIONARIO") {
        window.location.href = "dashboard-funcionario.html";
      } else if (data.usuario.tipo === "ONG") {
        window.location.href = "dashboard-ong.html";
      } else {
        alert("Tipo de usuário desconhecido.");
      }
    } catch (err) {
      console.error("Erro ao conectar com o servidor:", err);
      alert("Erro ao conectar com o servidor. Verifique se o backend está rodando.");
    }
  });
}

// --- REGISTRO DE FUNCIONÁRIO ---
if (document.getElementById("registerForm")) {
  document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefau
