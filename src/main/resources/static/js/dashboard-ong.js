// dashboard-ong.js
const API_BASE = "http://localhost:8080";

const userType = localStorage.getItem("userType");
if (!userType) {
  window.location.href = "login.html"; // segurança básica
}

// Exemplo: botão "voltar para dashboard"
function voltarDashboard() {
  if (userType === "ONG") {
    window.location.href = "dashboard-ong.html";
  } else {
    window.location.href = "dashboard-funcionario.html";
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  await carregarResumo();
  await carregarDoacoesRecentes();
});

async function carregarResumo() {
  try {
    const response = await fetch(`${API_BASE}/api/doacoes`);
    const doacoes = await response.json();

    const pendentes = doacoes.filter(d => d.status === "PENDENTE").length;
    const aceitas = doacoes.filter(d => d.status === "ACEITA").length;
    const concluidas = doacoes.filter(d => d.status === "CONCLUIDA").length;

    document.getElementById("doacoes-pendentes").textContent = pendentes;
    document.getElementById("doacoes-aceitas").textContent = aceitas;
    document.getElementById("retiradas-concluidas").textContent = concluidas;

    const produtosRes = await fetch(`${API_BASE}/api/produtos`);
    const produtos = await produtosRes.json();
    document.getElementById("produtos-disponiveis").textContent = produtos.length;
  } catch (err) {
    console.error("Erro ao carregar resumo:", err);
  }
}

async function carregarDoacoesRecentes() {
  try {
    const response = await fetch(`${API_BASE}/api/doacoes`);
    const doacoes = await response.json();

    const tabela = document.getElementById("tabela-doacoes");
    tabela.innerHTML = "";

    doacoes.slice(-5).reverse().forEach(d => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${d.produto?.nome || "-"}</td>
        <td>${d.supermercado?.nome || "-"}</td>
        <td>${d.status || "-"}</td>
      `;
      tabela.appendChild(tr);
    });
  } catch (err) {
    console.error("Erro ao carregar doações:", err);
  }
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}
