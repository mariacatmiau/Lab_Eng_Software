// dashboard-funcionario.js
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
  await carregarProdutosRecentes();
});

async function carregarResumo() {
  try {
    const [produtosRes, vencendoRes, ongsRes, doacoesRes] = await Promise.all([
      fetch(`${API_BASE}/api/produtos`),
      fetch(`${API_BASE}/api/produtos/vencimento`),
      fetch(`${API_BASE}/api/ongs`),
      fetch(`${API_BASE}/api/doacoes`)
    ]);

    const produtos = await produtosRes.json();
    const vencendo = await vencendoRes.json();
    const ongs = await ongsRes.json();
    const doacoes = await doacoesRes.json();

    document.getElementById("total-produtos").textContent = produtos.length;
    document.getElementById("produtos-vencendo").textContent = vencendo.length;
    document.getElementById("total-ongs").textContent = ongs.length;
    document.getElementById("total-doacoes").textContent = doacoes.length;
  } catch (err) {
    console.error("Erro ao carregar resumo:", err);
  }
}

async function carregarProdutosRecentes() {
  try {
    const response = await fetch(`${API_BASE}/api/produtos`);
    const produtos = await response.json();

    const tabela = document.getElementById("tabela-produtos");
    tabela.innerHTML = "";

    produtos.slice(-5).reverse().forEach(produto => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${produto.nome}</td>
        <td>${produto.categoria || "-"}</td>
        <td>${produto.dataValidade || "-"}</td>
      `;
      tabela.appendChild(tr);
    });
  } catch (err) {
    console.error("Erro ao carregar produtos:", err);
  }
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}
