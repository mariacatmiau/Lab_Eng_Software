// js/retiradas.js
document.addEventListener("DOMContentLoaded", async () => {
  AOS.init();
  feather.replace();

  const tbodyPendentes = document.getElementById("retiradas-pendentes");
  const tbodyConcluidas = document.getElementById("retiradas-concluidas");

  // Detecta se é ONG ou Funcionário pela URL
  const isOng = window.location.pathname.includes("retiradas-ong");
  const userType = isOng ? "ONG" : "FUNCIONARIO";
  console.log(`📦 Página carregada: Retiradas - ${userType}`);

  try {
    const response = await fetch("http://44.198.34.216:8081/api/doacoes");
    if (!response.ok) throw new Error("Erro ao buscar doações");
    const doacoes = await response.json();
    console.log("✅ Doações recebidas:", doacoes);

    const usuario = JSON.parse(localStorage.getItem("usuario"));
    if (!usuario) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("login.html");
      return;
    }

    // Filtra de acordo com o tipo de usuário
    const filtradas = isOng
      ? doacoes.filter(d => d.ong && d.ong.id === usuario.id)
      : doacoes;

    console.log(`📊 Doações filtradas (${userType}):`, filtradas);

    // Filtra pelos status
    const pendentes = filtradas.filter(d => d.status?.toUpperCase() === "ACEITA");
    const concluidas = filtradas.filter(d => d.status?.toUpperCase() === "RETIRADA");

    console.log("🟡 Pendentes:", pendentes);
    console.log("🟢 Concluídas:", concluidas);

    renderTabela(pendentes, tbodyPendentes, userType, true);
    renderTabela(concluidas, tbodyConcluidas, userType, false);
  } catch (error) {
    console.error("❌ Erro ao carregar retiradas:", error);
    tbodyPendentes.innerHTML = `
      <tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
  }
});

// ---------------- RENDERIZAÇÃO ----------------
function renderTabela(lista, tbody, tipoUsuario, isPendentes) {
  tbody.innerHTML = "";

  if (!lista || lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Nenhuma retirada ${
      isPendentes ? "pendente" : "concluída"
    }.</td></tr>`;
    return;
  }

  lista.forEach(item => {
    const tr = document.createElement("tr");
    const produto = item.produto?.nome || "—";
    const parceiro =
      tipoUsuario === "ONG"
        ? item.criadoPor?.nome || "Supermercado"
        : item.ong?.nome || "ONG";

    tr.innerHTML = `
      <td class="px-6 py-4 text-sm text-gray-700">${produto}</td>
      <td class="px-6 py-4 text-sm text-gray-700">${parceiro}</td>
      ${!isPendentes
        ? `<td class="px-6 py-4 text-sm text-gray-700">${formatarData(item.dataCriacao)}</td>`
        : ""
      }
      <td class="px-6 py-4">
        <span class="px-2 py-1 text-xs font-semibold rounded-full ${
          item.status?.toUpperCase() === "ACEITA"
            ? "bg-yellow-100 text-yellow-800"
            : item.status?.toUpperCase() === "RETIRADA"
              ? "bg-green-100 text-green-800"
              : "bg-gray-100 text-gray-600"
        }">${item.status}</span>
      </td>
      ${
        isPendentes && tipoUsuario === "FUNCIONARIO"
          ? `<td class="px-6 py-4 flex space-x-2">
              <button onclick="confirmarRetirada(${item.id})" class="text-green-600 hover:text-green-800 font-medium">Confirmar</button>
              <button onclick="cancelarRetirada(${item.id})" class="text-red-600 hover:text-red-800 font-medium">Cancelar</button>
            </td>`
          : isPendentes && tipoUsuario === "ONG"
            ? `<td class="px-6 py-4">
                <button onclick="confirmarRetirada(${item.id})" class="text-green-600 hover:text-green-800 font-medium">Confirmar Retirada</button>
              </td>`
            : `<td class="px-6 py-4 text-sm text-gray-500">✔️ Concluída</td>`
      }
    `;
    tbody.appendChild(tr);
  });
}

function formatarData(dataString) {
  if (!dataString) return "-";
  const data = new Date(dataString);
  return isNaN(data.getTime()) ? "-" : data.toLocaleDateString("pt-BR");
}

// ---------------- AÇÕES ----------------
async function confirmarRetirada(id) {
  try {
    const response = await fetch(`http://44.198.34.216:8081/api/doacoes/${id}/retirada`, {
      method: "PUT"
    });
    if (!response.ok) throw new Error("Erro ao confirmar retirada");
    alert("✅ Retirada confirmada com sucesso!");
    location.reload();
  } catch (error) {
    console.error(error);
    alert("Erro ao confirmar retirada.");
  }
}

async function cancelarRetirada(id) {
  try {
    const response = await fetch(`http://44.198.34.216:8081/api/doacoes/${id}/recusar`, {
      method: "PUT"
    });
    if (!response.ok) throw new Error("Erro ao cancelar retirada");
    alert("❌ Retirada cancelada.");
    location.reload();
  } catch (error) {
    console.error(error);
    alert("Erro ao cancelar retirada.");
  }
}
