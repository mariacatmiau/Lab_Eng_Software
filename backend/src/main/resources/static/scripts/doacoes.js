const API_DOACOES = "http://localhost:8080/api/doacoes";

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
