const API_DOACOES = "http://44.198.34.216:8081/api/doacoes";

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

async function carregarDoacoes() {
  const res = await fetch(`${API_DOACOES}/pendentes`);
  const doacoes = await res.json();
  const tbody = document.querySelector("tbody");
  tbody.innerHTML = doacoes.map(d => `
    <tr>
      <td>${d.produto.nome}</td>
      <td>${d.ong.usuario.nome}</td>
      <td>${d.status}</td>
      <td>
        <button onclick="aceitar(${d.id})" class="text-green-600">Aceitar</button>
        <button onclick="recusar(${d.id})" class="text-red-600 ml-2">Recusar</button>
      </td>
    </tr>`).join("");
}

async function aceitar(id) {
  await fetch(`${API_DOACOES}/${id}/aceitar`, { method: "PUT" });
  carregarDoacoes();
}

async function recusar(id) {
  await fetch(`${API_DOACOES}/${id}/recusar?motivo=Indisponível`, { method: "PUT" });
  carregarDoacoes();
}

document.addEventListener("DOMContentLoaded", carregarDoacoes);
