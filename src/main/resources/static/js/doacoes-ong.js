const API = {
  doacoes: "http://localhost:8080/api/doacoes",
  aceitar: (id) => `http://localhost:8080/api/doacoes/${id}/aceitar`,
  recusar: (id) => `http://localhost:8080/api/doacoes/${id}/recusar`,
};

const tbPend = document.getElementById("tbody-pendentes");
const tbAceitas = document.getElementById("tbody-aceitas");

// se você tiver guardado o ID da ONG no login, filtramos por ela
const ONG_ID = Number(localStorage.getItem("ongId") || 0);

function badge(status) {
  const map = {
    PENDENTE: "bg-yellow-100 text-yellow-800",
    ACEITA: "bg-blue-100 text-blue-800",
    RECUSADA: "bg-red-100 text-red-800",
    RETIRADA_CONCLUIDA: "bg-green-100 text-green-800",
    CANCELADA: "bg-gray-100 text-gray-700",
  };
  return `<span class="px-2 py-1 text-xs rounded-full ${map[status] || "bg-gray-100 text-gray-700"}">${status}</span>`;
}

function fmtData(iso) {
  if (!iso) return "-";
  try { return new Date(iso).toLocaleString(); } catch { return iso; }
}

async function carregar() {
  tbPend.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-gray-500">Carregando...</td></tr>`;
  tbAceitas.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-gray-500">Carregando...</td></tr>`;

  try {
    const r = await fetch(API.doacoes);
    const list = await r.json();

    const filtraPorOng = (d) => (!ONG_ID || d.ong?.id === ONG_ID);

    const pendentes = list.filter(d => d.status === "PENDENTE" && filtraPorOng(d));
    const aceitas   = list.filter(d => d.status === "ACEITA" && filtraPorOng(d));

    // Pendentes
    tbPend.innerHTML = pendentes.length
      ? pendentes.map(d => `
        <tr>
          <td class="px-6 py-4">${d.produto?.nome ?? "-"}</td>
          <td class="px-6 py-4">${d.funcionario?.nome ?? "Supermercado"}</td>
          <td class="px-6 py-4">${fmtData(d.dataCriacao)}</td>
          <td class="px-6 py-4">${badge(d.status)}</td>
          <td class="px-6 py-4 text-right space-x-3">
            <button class="text-green-600 hover:text-green-800" onclick="aceitar(${d.id})" title="Aceitar"><i data-feather="check"></i></button>
            <button class="text-red-600 hover:text-red-800" onclick="recusar(${d.id})" title="Recusar"><i data-feather="x"></i></button>
          </td>
        </tr>`).join("")
      : `<tr><td colspan="5" class="text-center py-4 text-gray-500">Nenhuma pendente.</td></tr>`;

    // Aceitas
    tbAceitas.innerHTML = aceitas.length
      ? aceitas.map(d => `
        <tr>
          <td class="px-6 py-4">${d.produto?.nome ?? "-"}</td>
          <td class="px-6 py-4">${d.funcionario?.nome ?? "Supermercado"}</td>
          <td class="px-6 py-4">${badge(d.status)}</td>
          <td class="px-6 py-4 text-right text-gray-400 italic">Combine a retirada com o supermercado</td>
        </tr>`).join("")
      : `<tr><td colspan="4" class="text-center py-4 text-gray-500">—</td></tr>`;

    feather.replace();
  } catch {
    tbPend.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-red-500">Erro ao carregar.</td></tr>`;
    tbAceitas.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-red-500">Erro ao carregar.</td></tr>`;
  }
}

async function aceitar(id) {
  if (!confirm("Aceitar esta doação?")) return;
  try {
    const r = await fetch(API.aceitar(id), { method: "PUT" });
    if (!r.ok) throw 0;
    await carregar();
    alert("Doação aceita!");
  } catch { alert("Erro ao aceitar."); }
}

async function recusar(id) {
  if (!confirm("Recusar esta doação?")) return;
  try {
    const r = await fetch(API.recusar(id), { method: "PUT" });
    if (!r.ok) throw 0;
    await carregar();
    alert("Doação recusada.");
  } catch { alert("Erro ao recusar."); }
}

carregar();
