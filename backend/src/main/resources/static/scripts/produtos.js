const API_PRODUTOS = "http://localhost:8080/api/produtos";

async function carregarProdutos() {
  const res = await fetch(`${API_PRODUTOS}/disponiveis`);
  const produtos = await res.json();
  const tbody = document.querySelector("tbody");
  tbody.innerHTML = produtos.map(p => `
    <tr class="border-b">
      <td class="px-3 py-2">${p.nome}</td>
      <td class="px-3 py-2">${p.descricao || "-"}</td>
      <td class="px-3 py-2">${p.quantidade}</td>
      <td class="px-3 py-2">${p.dataValidade}</td>
    </tr>`).join("");
}

document.addEventListener("DOMContentLoaded", carregarProdutos);
