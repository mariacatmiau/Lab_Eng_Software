const API = "http://localhost:8080/api/auth";

if (document.getElementById("loginForm")) {
  document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("email").value;
    const senha = document.getElementById("senha").value;

    const res = await fetch(`${API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha })
    });

    if (res.ok) {
      const user = await res.json();
      localStorage.setItem("user", JSON.stringify(user));
      localStorage.setItem("userType", user.tipo);
      window.location.href = user.tipo === "ONG" ? "dashboard-ong.html" : "dashboard-funcionario.html";
    } else {
      alert("Email ou senha inválidos!");
    }
  });
}

if (document.getElementById("registerForm")) {
  document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      nome: nome.value,
      email: email.value,
      senha: senha.value,
      tipo: "FUNCIONARIO"
    };
    const res = await fetch(`${API}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    res.ok ? window.location.href = "login.html" : alert("Erro ao cadastrar");
  });
}

if (document.getElementById("registerOngForm")) {
  document.getElementById("registerOngForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      nome: nome.value,
      email: email.value,
      senha: senha.value,
      tipo: "ONG",
      endereco: endereco.value,
      telefone: telefone.value
    };
    const res = await fetch(`${API}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    res.ok ? window.location.href = "login.html" : alert("Erro ao cadastrar ONG");
  });
}
