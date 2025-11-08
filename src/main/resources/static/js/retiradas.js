document.addEventListener("DOMContentLoaded", async () => {
  AOS.init();
  feather.replace();

  const tbodyPendentes = document.getElementById("retiradas-pendentes");
  const tbodyConcluidas = document.getElementById("retiradas-concluidas");

  // Detecta se é ONG ou Funcionário pela URL
  const isOng = window.location.pathname.includes("retiradas-ong");
  const userType = isOng ? "ONG" : "FUNCIONARIO";

  console.log(`Página carregada: Retiradas - ${userType}`);

  try {
    const response = await fetch("/api/retiradas"); // ajuste se sua rota for diferente
    if (!response.ok) throw new Error("Erro ao buscar retiradas");
    const retiradas = await response.json();

    // Filtra por status
    const pendentes = retiradas.filter(r => r.status === "PENDENTE");
    const concluidas = retiradas.filter(r => r.status === "CONCLUIDA");

    renderTabela(pendentes, tbodyPendentes, userType, true);
    renderTabela(concluidas, tbodyConcluidas, userType, false);

  } catch (error) {
    console.error("Erro ao carregar retiradas:", error);
    tbodyPendentes.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
  }
});

// Função para renderizar as tabelas
function renderTabela(lista, tbody, tipoUsuario, isPendentes) {
  tbody.innerHTML = "";

  if (!lista || lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Nenhuma retirada ${isPendentes ? "pendente" : "concluída"}.</td></tr>`;
    return;
  }

  lista.forEach(item => {
    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td class="px-6 py-4 text-sm text-gray-700">${item.produtoNome || "—"}</td>
      <td class="px-6 py-4 text-sm text-gray-700">${item.parceiroNome || (tipoUsuario === "ONG" ? item.supermercado || "Supermercado X" : "ONG X")}</td>
      ${!isPendentes
        ? `<td class="px-6 py-4 text-sm text-gray-700">${formatarData(item.dataConclusao)}</td>`
        : ""
      }
      <td class="px-6 py-4">
        <span class="px-2 py-1 text-xs font-semibold rounded-full ${
          item.status === "PENDENTE"
            ? "bg-yellow-100 text-yellow-800"
            : "bg-green-100 text-green-800"
        }">${item.status}</span>
      </td>
      ${
        isPendentes
          ? `<td class="px-6 py-4 flex space-x-2">
              ${getAcoes(tipoUsuario, item.id)}
            </td>`
          : tipoUsuario === "ONG"
            ? `<td class="px-6 py-4 text-sm text-gray-500">✔️ Retirada confirmada</td>`
            : `<td class="px-6 py-4 text-sm text-gray-500">✔️ Entregue</td>`
      }
    `;

    tbody.appendChild(tr);
  });
}

// Formata data
function formatarData(dataString) {
  if (!dataString) return "-";
  const data = new Date(dataString);
  return data.toLocaleDateString("pt-BR");
}

// Ações dinâmicas
function getAcoes(tipoUsuario, id) {
  if (tipoUsuario === "FUNCIONARIO") {
    return `
      <button onclick="confirmarRetirada(${id})" class="text-green-600 hover:text-green-800 font-medium">Confirmar</button>
      <button onclick="cancelarRetirada(${id})" class="text-red-600 hover:text-red-800 font-medium">Cancelar</button>
    `;
  } else {
    return `
      <button onclick="confirmarRetirada(${id})" class="text-green-600 hover:text-green-800 font-medium">Confirmar Retirada</button>
    `;
  }
}

// Ações da API
async function confirmarRetirada(id) {
  try {
    const response = await fetch(`/api/retiradas/${id}/confirmar`, { method: "PUT" });
    if (!response.ok) throw new Error("Erro ao confirmar retirada");
    alert("Retirada confirmada com sucesso!");
    location.reload();
  } catch (error) {
    console.error(error);
    alert("Erro ao confirmar retirada.");
  }
}

async function cancelarRetirada(id) {
  try {
    const response = await fetch(`/api/retiradas/${id}/cancelar`, { method: "PUT" });
    if (!response.ok) throw new Error("Erro ao cancelar retirada");
    alert("Retirada cancelada.");
    location.reload();
  } catch (error) {
    console.error(error);
    alert("Erro ao cancelar retirada.");
  }
}
