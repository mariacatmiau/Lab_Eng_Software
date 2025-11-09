const API_BASE = "http://localhost:8080/api";

// --- LOGIN ---
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const email = document.getElementById("email").value.trim();
      const senha = document.getElementById("senha").value.trim();

      console.log("Tentando login com:", { email, senha });

      try {
        const res = await fetch(`${API_BASE}/usuarios/login`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, senha }),
        });

        console.log("Status da resposta:", res.status);

        if (!res.ok) {
          const msg = await res.text();
          console.warn("Erro de login:", msg);
          alert("E-mail ou senha incorretos!");
          return;
        }

        const user = await res.json();
        console.log("Usuário retornado pelo backend:", user);

        if (!user || !user.tipo) {
          alert("Erro: o servidor não retornou o tipo de usuário!");
          return;
        }

        localStorage.setItem("usuario", JSON.stringify(user));
        const tipo = (user.tipo || "").toString().toUpperCase();
        console.log("Tipo de usuário detectado:", tipo);

        if (tipo === "FUNCIONARIO") {
          window.location.href = "dashboard-funcionario.html";
        } else if (tipo === "ONG") {
          window.location.href = "dashboard-ong.html";
        } else {
          alert("Tipo de usuário desconhecido: " + tipo);
        }
      } catch (err) {
        console.error("Erro no login:", err);
        alert("Erro de conexão com o servidor. Verifique se o backend está rodando.");
      }
    });
  }

  // --- REGISTRO DE FUNCIONÁRIO ---
  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const nome = document.getElementById("nome").value.trim();
      const email = document.getElementById("email").value.trim();
      const senha = document.getElementById("senha").value.trim();

      try {
        const res = await fetch(`${API_BASE}/usuarios/register`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ nome, email, senha, tipo: "FUNCIONARIO" }),
        });

        if (!res.ok) throw new Error(await res.text());

        alert("Cadastro realizado com sucesso!");
        window.location.href = "login.html";
      } catch (err) {
        console.error("Erro no cadastro:", err);
        alert("Erro ao cadastrar funcionário. Verifique o backend.");
      }
    });
  }

  // --- REGISTRO DE ONG ---
  const registerOngForm = document.getElementById("registerOngForm");
  if (registerOngForm) {
    registerOngForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const nome = document.getElementById("nome").value.trim();
      const email = document.getElementById("email").value.trim();
      const senha = document.getElementById("senha").value.trim();
      const endereco = document.getElementById("endereco").value.trim();
      const telefone = document.getElementById("telefone").value.trim();

      try {
        const res = await fetch(`${API_BASE}/usuarios/register`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            nome,
            email,
            senha,
            tipo: "ONG",
            endereco,
            telefone,
          }),
        });

        if (!res.ok) throw new Error(await res.text());

        alert("Cadastro da ONG realizado com sucesso!");
        window.location.href = "login.html";
      } catch (err) {
        console.error("Erro ao cadastrar ONG:", err);
        alert("Erro ao cadastrar ONG. Verifique o backend.");
      }
    });
  }
});
