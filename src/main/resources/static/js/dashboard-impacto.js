const API_BASE =
  window.location.origin && window.location.origin.startsWith("http")
    ? window.location.origin
    : "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
  const usuario = JSON.parse(localStorage.getItem("usuario"));
  if (!usuario || !usuario.tipo) {
    window.location.replace("login.html");
    return;
  }

  // Set "Voltar ao Painel" link based on user type
  const tipo = String(usuario.tipo).toUpperCase();
  const linkVoltar = document.getElementById("linkVoltar");
  if (linkVoltar) {
    const destinos = {
      FUNCIONARIO: "dashboard-funcionario.html",
      ONG: "dashboard-ong.html",
      CLIENTE: "dashboard-cliente.html"
    };
    linkVoltar.href = destinos[tipo] || "login.html";
  }

  await carregarDashboard();
});

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

const STATUS_BADGES = {
  PENDENTE: '<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">Pendente</span>',
  ACEITA: '<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">Aceita</span>',
  RECUSADA: '<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">Recusada</span>',
  RETIRADA: '<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">Retirada</span>'
};

async function carregarDashboard() {
  try {
    const token = localStorage.getItem("token");
    const res = await fetch(`${API_BASE}/api/dashboard/impacto`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!res.ok) throw new Error("Erro ao carregar dados");

    const data = await res.json();
    preencherKPIs(data);
    preencherStatus(data);
    preencherBarraConclusao(data);
    preencherRankingOngs(data.rankingOngs);
    preencherRankingMercados(data.rankingMercados);
    preencherDoacoesRecentes(data.doacoesRecentes);
  } catch (err) {
    console.error("Erro ao carregar dashboard de impacto:", err);
  }
}

function preencherKPIs(data) {
  document.getElementById("kpi-total-doacoes").textContent = data.totalDoacoes;
  document.getElementById("kpi-retiradas").textContent = data.doacoesRetiradas;
  document.getElementById("kpi-itens-doados").textContent = data.itensDoados;
  document.getElementById("kpi-produtos").textContent = data.totalProdutosCadastrados;
  document.getElementById("kpi-ongs").textContent = data.totalOngs;
  document.getElementById("kpi-mercados").textContent = data.totalMercados;
}

function preencherStatus(data) {
  document.getElementById("status-pendentes").textContent = data.doacoesPendentes;
  document.getElementById("status-aceitas").textContent = data.doacoesAceitas;
  document.getElementById("status-retiradas").textContent = data.doacoesRetiradas;
  document.getElementById("status-recusadas").textContent = data.doacoesRecusadas;
}

function preencherBarraConclusao(data) {
  const total = data.totalDoacoes || 0;
  const retiradas = data.doacoesRetiradas || 0;
  const taxa = total > 0 ? Math.round((retiradas / total) * 100) : 0;

  document.getElementById("taxa-conclusao").textContent = taxa + "%";
  const barra = document.getElementById("barra-conclusao");
  // Animate after a short delay
  setTimeout(() => {
    barra.style.width = taxa + "%";
  }, 200);
}

function preencherRankingOngs(ranking) {
  const tbody = document.getElementById("tabela-ranking-ongs");
  tbody.innerHTML = "";

  if (!ranking || ranking.length === 0) {
    tbody.innerHTML = '<tr><td colspan="4" class="text-center py-4 text-gray-500">Nenhuma ONG com doações ainda.</td></tr>';
    return;
  }

  ranking.forEach((ong, i) => {
    const medal = i === 0 ? "🥇" : i === 1 ? "🥈" : i === 2 ? "🥉" : (i + 1);
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="px-6 py-3 text-sm">${medal}</td>
      <td class="px-6 py-3 text-sm font-medium text-gray-900">${escapeHtml(ong.nome)}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${ong.retiradasConcluidas}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${ong.totalRecebidas}</td>
    `;
    tbody.appendChild(tr);
  });
}

function preencherRankingMercados(ranking) {
  const tbody = document.getElementById("tabela-ranking-mercados");
  tbody.innerHTML = "";

  if (!ranking || ranking.length === 0) {
    tbody.innerHTML = '<tr><td colspan="4" class="text-center py-4 text-gray-500">Nenhum mercado com doações ainda.</td></tr>';
    return;
  }

  ranking.forEach((m, i) => {
    const medal = i === 0 ? "🥇" : i === 1 ? "🥈" : i === 2 ? "🥉" : (i + 1);
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="px-6 py-3 text-sm">${medal}</td>
      <td class="px-6 py-3 text-sm font-medium text-gray-900">${escapeHtml(m.nome)}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${m.totalDoacoes}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${m.itensDoados}</td>
    `;
    tbody.appendChild(tr);
  });
}

function preencherDoacoesRecentes(doacoes) {
  const tbody = document.getElementById("tabela-doacoes-recentes");
  tbody.innerHTML = "";

  if (!doacoes || doacoes.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-gray-500">Nenhuma doação registrada ainda.</td></tr>';
    return;
  }

  doacoes.forEach((d) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="px-6 py-3 text-sm text-gray-900">${escapeHtml(d.produto)}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${escapeHtml(d.mercado)}</td>
      <td class="px-6 py-3 text-sm text-gray-700">${escapeHtml(d.ong)}</td>
      <td class="px-6 py-3 text-sm">${STATUS_BADGES[d.status] || escapeHtml(d.status)}</td>
      <td class="px-6 py-3 text-sm text-gray-500">${escapeHtml(d.data)}</td>
    `;
    tbody.appendChild(tr);
  });
}
