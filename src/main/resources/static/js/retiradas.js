(() => {
  const pageApiOrigin =
    window.location.origin && window.location.origin.startsWith("http")
      ? window.location.origin
      : "http://localhost:8080";
  const baseUrl = pageApiOrigin;

  document.addEventListener("DOMContentLoaded", async () => {
    AOS.init();
    feather.replace();

    const tbodyPendentes = document.getElementById("retiradas-pendentes");
    const tbodyConcluidas = document.getElementById("retiradas-concluidas");
    const path = (window.location.pathname || "").toLowerCase();
    const isOng = /retiradas-ong\.html$/.test(path) || /retirada-ong\.html$/.test(path);
    const userType = isOng ? "ONG" : "FUNCIONARIO";

    try {
      const usuario = JSON.parse(localStorage.getItem("usuario") || "null");
      if (!usuario) {
        alert("Sessão expirada. Faça login novamente.");
        window.location.replace("login.html");
        return;
      }

      const endpoint = isOng
        ? `${baseUrl}/api/doacoes/por-ong/${usuario.id}`
        : `${baseUrl}/api/doacoes/por-criador/${usuario.id}`;

      const resp = await fetch(endpoint);
      if (!resp.ok) throw new Error("Erro ao buscar doações");

      const doacoes = await resp.json();
      const visiveis = Array.isArray(doacoes) ? doacoes : [];
      visiveis.forEach((item) => {
        item.status = String(item.status || "").toUpperCase();
      });

      const pendentes = visiveis.filter((item) => item.status === "ACEITA");
      const concluidas = visiveis.filter((item) => item.status === "RETIRADA");

      renderTabela(pendentes, tbodyPendentes, userType, true);
      renderTabela(concluidas, tbodyConcluidas, userType, false);
    } catch (err) {
      console.error("Erro ao carregar retiradas:", err);
      if (tbodyPendentes) {
        tbodyPendentes.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
      }
      if (tbodyConcluidas) {
        tbodyConcluidas.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Erro ao carregar dados</td></tr>`;
      }
    }
  });

  function renderTabela(lista, tbody, tipoUsuario, isPendentes) {
    if (!tbody) return;
    tbody.innerHTML = "";

    if (!lista || !lista.length) {
      tbody.innerHTML = `<tr><td colspan="4" class="text-center text-gray-500 py-4">Nenhuma retirada ${isPendentes ? "pendente" : "concluída"}.</td></tr>`;
      return;
    }

    lista.forEach((item) => {
      const tr = document.createElement("tr");
      const produto = item?.produto?.nome || "-";
      const parceiro = tipoUsuario === "ONG"
        ? (item?.criadoPor?.nome || "Supermercado")
        : (item?.ong?.nome || "ONG");
      const status = item?.status || "-";
      const data = item?.dataAtualizacao || item?.dataCriacao;

      tr.innerHTML = `
        <td class="px-6 py-4 text-sm text-gray-700">${produto}</td>
        <td class="px-6 py-4 text-sm text-gray-700">${parceiro}</td>
        ${!isPendentes ? `<td class="px-6 py-4 text-sm text-gray-700">${formatarData(data)}</td>` : ""}
        <td class="px-6 py-4">
          <span class="px-2 py-1 text-xs font-semibold rounded-full ${status === "ACEITA" ? "bg-yellow-100 text-yellow-800" : status === "RETIRADA" ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-600"}">${status}</span>
        </td>
        ${isPendentes
          ? `<td class="px-6 py-4">
               <button onclick="confirmarRetirada(${item.id})" class="text-green-600 hover:text-green-800 font-medium">Confirmar Retirada</button>
               ${tipoUsuario === "FUNCIONARIO" ? `<button onclick="cancelarRetirada(${item.id})" class="ml-3 text-red-600 hover:text-red-800 font-medium">Cancelar</button>` : ""}
             </td>`
          : `<td class="px-6 py-4 text-sm text-gray-500">✔️ Concluída</td>`}
      `;
      tbody.appendChild(tr);
    });
  }

  function formatarData(val) {
    if (!val) return "-";
    const d = new Date(val);
    return Number.isNaN(d.getTime()) ? "-" : d.toLocaleDateString("pt-BR");
  }

  window.confirmarRetirada = async function confirmarRetirada(id) {
    try {
      const r = await fetch(`${baseUrl}/api/doacoes/${id}/retirada`, { method: "PUT" });
      if (!r.ok) throw new Error("Falha ao confirmar retirada");
      alert("✅ Retirada confirmada!");
      location.reload();
    } catch (e) {
      console.error(e);
      alert("Erro ao confirmar retirada.");
    }
  };

  window.cancelarRetirada = async function cancelarRetirada(id) {
    try {
      const r = await fetch(`${baseUrl}/api/doacoes/${id}/recusar`, { method: "PUT" });
      if (!r.ok) throw new Error("Falha ao cancelar retirada");
      alert("❌ Retirada cancelada.");
      location.reload();
    } catch (e) {
      console.error(e);
      alert("Erro ao cancelar retirada.");
    }
  };
})();
