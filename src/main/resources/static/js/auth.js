const API_ORIGIN =
  window.location.origin && window.location.origin.startsWith("http")
    ? window.location.origin
    : "http://localhost:8081";
const API_BASE = window.AppCore.apiBase;

function montarEnderecoEstruturado() {
  const rua = document.getElementById("rua")?.value.trim() || "";
  const numero = document.getElementById("numero")?.value.trim() || "";
  const bairro = document.getElementById("bairro")?.value.trim() || "";
  const cidade = document.getElementById("cidade")?.value.trim() || "";
  const estado = (document.getElementById("estado")?.value || "").trim().toUpperCase();

  if (!rua || !numero || !bairro || !cidade || !estado) {
    return { valido: false, endereco: "" };
  }

  return {
    valido: true,
    endereco: `${rua}, ${numero}, ${bairro}, ${cidade} - ${estado}`,
  };
}

function showAuthMessage(message, type = "error") {
  const box = document.getElementById("authMessage");
  if (!box) {
    alert(message);
    return;
  }

  box.classList.remove("hidden", "bg-red-50", "text-red-700", "border", "border-red-200", "bg-green-50", "text-green-700", "border-green-200");
  if (type === "success") {
    box.classList.add("bg-green-50", "text-green-700", "border", "border-green-200");
  } else {
    box.classList.add("bg-red-50", "text-red-700", "border", "border-red-200");
  }
  box.textContent = message;
}

function setSubmitState(form, isSubmitting, idleLabel, busyLabel) {
  const submitButton = form?.querySelector("button[type='submit'], button");
  if (!submitButton) {
    return;
  }

  submitButton.disabled = isSubmitting;
  submitButton.textContent = isSubmitting ? busyLabel : idleLabel;
}

function buildStructuredAddressOrShowError() {
  const enderecoMontado = window.AddressForm.buildFromFields();
  if (!enderecoMontado.valid) {
    showAuthMessage("Preencha rua, número, bairro, cidade e estado.");
    return null;
  }
  return enderecoMontado.address;
}

async function submitRegister(payload) {
  const res = await fetch(`${API_BASE}/usuarios/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return res.json();
}

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
          showAuthMessage("E-mail ou senha incorretos.");
          return;
        }

        const loginData = await res.json();
        const user = loginData?.usuario || loginData;
        console.log("Usuário retornado pelo backend:", user);

        if (!user || !user.tipo) {
          showAuthMessage("Não foi possível identificar o perfil de acesso.");
          return;
        }

        if (loginData?.token) {
          localStorage.setItem("token", loginData.token);
        }
        window.AppCore.writeStoredUser(user);
        const tipo = (user.tipo || "").toString().toUpperCase();
        console.log("Tipo de usuário detectado:", tipo);

        const nextPage = window.AppCore.resolveDashboardByRole(tipo);
        if (nextPage === "login.html") {
          showAuthMessage("Tipo de usuário desconhecido: " + tipo);
          return;
        }

        window.location.href = nextPage;
      } catch (err) {
        console.error("Erro no login:", err);
        showAuthMessage("Erro de conexão com o servidor. Verifique se o backend está rodando.");
      }
    });
  }

  // --- REGISTRO DE FUNCIONÁRIO ---
  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const tipo = document.getElementById("tipo")?.value || "";
      const nome = document.getElementById("nome").value.trim();
      const email = document.getElementById("email").value.trim();
      const senha = document.getElementById("senha").value.trim();
      const telefone = (document.getElementById("telefone")?.value || "").trim();
      const endereco = buildStructuredAddressOrShowError();

      if (!tipo) {
        showAuthMessage("Selecione o tipo de usuário.");
        return;
      }

      if (!telefone) {
        showAuthMessage("Informe um telefone para contato.");
        return;
      }

      if (!endereco) {
        return;
      }

      try {
        setSubmitState(registerForm, true, "Cadastrar", "Cadastrando...");
        await submitRegister({ nome, email, senha, telefone, endereco, tipo });
        showAuthMessage("Cadastro realizado com sucesso! Redirecionando para login...", "success");
        window.location.href = "login.html";
      } catch (err) {
        console.error("Erro no cadastro:", err);
        showAuthMessage(err?.message || "Erro ao cadastrar usuário. Verifique o backend.");
      } finally {
        setSubmitState(registerForm, false, "Cadastrar", "Cadastrando...");
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
      const telefone = document.getElementById("telefone").value.trim();
      const endereco = buildStructuredAddressOrShowError();

      if (!endereco) {
        return;
      }

      try {
        setSubmitState(registerOngForm, true, "Cadastrar ONG", "Cadastrando ONG...");
        await submitRegister({
          nome,
          email,
          senha,
          tipo: "ONG",
          endereco,
          telefone,
        });
        showAuthMessage("Cadastro da ONG realizado com sucesso! Redirecionando para login...", "success");
        window.location.href = "login.html";
      } catch (err) {
        console.error("Erro ao cadastrar ONG:", err);
        showAuthMessage(err?.message || "Erro ao cadastrar ONG. Verifique o backend.");
      } finally {
        setSubmitState(registerOngForm, false, "Cadastrar ONG", "Cadastrando ONG...");
      }
    });
  }
});
