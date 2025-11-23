// js/retiradas.js
document.addEventListener("DOMContentLoaded", async () => {
  AOS.init();
  feather.replace();

  const BASE_URL = "http://44.198.34.216:8081";

  const tbodyPendentes = document.getElementById("retiradas-pendentes");
  const tbodyConcluidas = document.getElementById("retiradas-concluidas");

  // Detecta ONG x Funcionário (aceita retirada-ong ou retiradas-ong)
  const path = (window.location.pathname || "").toLowerCase();
  const isOng = /retirada[s-]?ong\.html/.test(path);
  const userType = isOng ? "ONG" : "FUNCIONARIO";
  console.log(`📦 Página carregada: Retiradas - ${userType}`);

  try {
    const resp = await fetch(`${BASE_URL}/api/doacoes`);
    if (!resp.ok) throw new Error("Erro ao buscar doações");
    const doacoes = await resp.json();
    console.log("✅ Doações recebidas:", doacoes);

    const usuario = JSON.parse(localStorage.getItem("usuario"));
    if (!usuario) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("login.html");
      return;
    }

    // Filtra por usuário logado (se ONG)
    const visiveis = isOng
      ? doacoes.filter(d => d?.ong?.id === usuario.id)
      : doacoes;

    // Normaliza status para maiúsculas
    visiveis.forEach(d => (d.status = (d.status || "").toUpperCase()));

    // Regras do seu enum:
    // Pendentes  => ACEITA
    // Concluídas => RETIRADA
    const pendentes = visiveis.filter(d => d.status === "ACEITA");
    const concluidas = visiveis.filter(d => d.status === "RETIRADA");

    console.log("🟡 Pendentes:", pendentes);
    console.log("🟢 Concluídas:", concluidas);

    renderTabela(pendentes, tbodyPendentes, userType, true, BASE_URL);
    renderTabela(concluidas, tbodyConcluidas, userType, false, BASE_URL);
  } catch (err) {
    console.error("❌ Erro ao carregar retiradas:", err);
    tbodyPendentes.innerHTML =
      `<tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
    tbodyConcluidas.innerHTML =
      `<tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
  }
});

// ---------------- RENDER ----------------
function renderTabela(lista, tbody, tipoUsuario, isPendentes, BASE_URL) {
  tbody.innerHTML = "";

  if (!lista || lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">
      Nenhuma retirada ${isPendentes ? "pendente" : "concluída"}.
    </td></tr>`;
    return;
  }

  lista.forEach(item => {
    const tr = document.createElement("tr");
    const produto = item?.produto?.nome || "—";
    // Para ONG mostramos quem criou (funcionário / supermercado),
    // para Funcionário mostramos a ONG.
    const parceiro = tipoUsuario === "ONG"
      ? (item?.criadoPor?.nome || "Supermercado")
      : (item?.ong?.nome || "ONG");

    const status = item?.status || "—";
    const data = item?.dataAtualizacao || item?.dataCriacao;

    tr.innerHTML = `
      <td class="px-6 py-4 text-sm text-gray-700">${produto}</td>
      <td class="px-6 py-4 text-sm text-gray-700">${parceiro}</td>
      ${!isPendentes
        ? `<td class="px-6 py-4 text-sm text-gray-700">${formatarData(data)}</td>`
        : ""
      }
      <td class="px-6 py-4">
        <span class="px-2 py-1 text-xs font-semibold rounded-full ${
          status === "ACEITA"
            ? "bg-yellow-100 text-yellow-800"
            : status === "RETIRADA"
              ? "bg-green-100 text-green-800"
              : "bg-gray-100 text-gray-600"
        }">${status}</span>
      </td>
      ${
        isPendentes
          // No seu fluxo, na fase ACEITA ambos podem confirmar retirada:
          ? `<td class="px-6 py-4">
               <button onclick="confirmarRetirada(${item.id})"
                 class="text-green-600 hover:text-green-800 font-medium">
                 Confirmar Retirada
               </button>
               ${
                 tipoUsuario === "FUNCIONARIO"
                   ? `<button onclick="cancelarRetirada(${item.id})"
                        class="ml-3 text-red-600 hover:text-red-800 font-medium">
                        Cancelar
                      </button>`
                   : ""
               }
             </td>`
          : `<td class="px-6 py-4 text-sm text-gray-500">✔️ Concluída</td>`
      }
    `;
    tbody.appendChild(tr);
  });
}

function formatarData(val) {
  if (!val) return "-";
  const d = new Date(val);
  return Number.isNaN(d.getTime()) ? "-" : d.toLocaleDateString("pt-BR");
}

// ---------------- AÇÕES ----------------
async function confirmarRetirada(id) {
  const BASE_URL = "http://44.198.34.216:8081";
  try {
    const r = await fetch(`${BASE_URL}/api/doacoes/${id}/retirada`, { method: "PUT" });
    if (!r.ok) throw new Error("Falha ao confirmar retirada");
    alert("✅ Retirada confirmada!");
    location.reload();
  } catch (e) {
    console.error(e);
    alert("Erro ao confirmar retirada.");
  }
}

async function cancelarRetirada(id) {
  const BASE_URL = "http://44.198.34.216:8081";
  try {
    const r = await fetch(`${BASE_URL}/api/doacoes/${id}/recusar`, { method: "PUT" });
    if (!r.ok) throw new Error("Falha ao cancelar retirada");
    alert("❌ Retirada cancelada.");
    location.reload();
  } catch (e) {
    console.error(e);
    alert("Erro ao cancelar retirada.");
  }
}
