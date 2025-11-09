const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
  console.log("📊 Iniciando dashboard ONG...");

  // Verifica sessão
  const usuario = JSON.parse(localStorage.getItem("usuario"));
  if (!usuario || !usuario.tipo) {
    alert("Sessão expirada. Faça login novamente.");
    window.location.replace("login.html");
    return;
  }

  // Exibe nome no topo (se quiser mostrar)
  const nomeSpan = document.getElementById("nomeUsuario");
  if (nomeSpan && usuario.nome) nomeSpan.textContent = usuario.nome;

  try {
    await carregarResumo();
    await carregarDoacoesRecentes();
  } catch (err) {
    console.error("Erro ao carregar dashboard:", err);
  }
});

// === FUNÇÕES PRINCIPAIS ===
async function carregarResumo() {
  try {
    const [doacoesRes, produtosRes] = await Promise.all([
      fetch(`${API_BASE}/api/doacoes`),
      fetch(`${API_BASE}/api/produtos`)
    ]);

    const doacoes = await doacoesRes.json();
    const produtos = await produtosRes.json();

    const pendentes = doacoes.filter(d => d.status === "PENDENTE").length;
    const aceitas = doacoes.filter(d => d.status === "ACEITA").length;
    const concluidas = doacoes.filter(d => d.status === "RETIRADA").length;

    document.getElementById("doacoes-pendentes").textContent = pendentes;
    document.getElementById("doacoes-aceitas").textContent = aceitas;
    document.getElementById("retiradas-concluidas").textContent = concluidas;
    document.getElementById("produtos-disponiveis").textContent = produtos.length;

    console.log("Resumo atualizado:", { pendentes, aceitas, concluidas });
  } catch (error) {
    console.error("Erro ao carregar resumo:", error);
  }
}

async function carregarDoacoesRecentes() {
  try {
    const response = await fetch(`${API_BASE}/api/doacoes`);
    const doacoes = await response.json();

    const tabela = document.getElementById("tabela-doacoes");
    tabela.innerHTML = "";

    if (doacoes.length === 0) {
      tabela.innerHTML =
        '<tr><td colspan="3" class="text-center py-4 text-gray-500">Nenhuma doação encontrada.</td></tr>';
      return;
    }

    doacoes.slice(-5).reverse().forEach(d => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${d.produto?.nome || "-"}</td>
        <td>${d.criadoPor?.nome || d.funcionario?.nome || "Supermercado"}</td>
        <td>${d.status || "-"}</td>
      `;
      tabela.appendChild(tr);
    });
  } catch (err) {
    console.error("Erro ao carregar doações:", err);
  }
}

// === LOGOUT ===
document.addEventListener("click", (e) => {
  if (e.target.id === "logout") {
    e.preventDefault();
    localStorage.removeItem("usuario");
    window.location.replace("login.html");
  }
});
