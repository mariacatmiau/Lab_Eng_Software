(() => {
  const pageApiOrigin =
    window.location.origin && window.location.origin.startsWith("http")
      ? window.location.origin
      : "http://localhost:8080";
  const pageApiBase = `${pageApiOrigin}/api`;

  function lerUsuario() {
    try {
      return JSON.parse(localStorage.getItem("usuario") || "null");
    } catch {
      return null;
    }
  }

  function getToken() {
    return localStorage.getItem("token") || "";
  }

  function requestJson(method, url) {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.open(method, url, true);
      xhr.responseType = "text";
      xhr.timeout = 12000;
      xhr.setRequestHeader("Accept", "application/json");

      const token = getToken();
      if (token) {
        xhr.setRequestHeader("Authorization", `Bearer ${token}`);
      }

      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          const text = xhr.responseText || "null";
          try {
            resolve(JSON.parse(text));
          } catch {
            reject(new Error("Resposta inválida do servidor."));
          }
          return;
        }

        if (xhr.status === 401) {
          localStorage.removeItem("usuario");
          localStorage.removeItem("token");
          window.location.replace("login.html");
          return;
        }

        reject(new Error(xhr.responseText || `Erro HTTP ${xhr.status}`));
      };

      xhr.onerror = () => reject(new Error("Falha de rede ao carregar doações."));
      xhr.ontimeout = () => reject(new Error("Tempo de resposta excedido ao carregar doações."));
      xhr.send();
    });
  }

  function badge(status) {
    const map = {
      PENDENTE: "bg-yellow-100 text-yellow-800",
      ACEITA: "bg-blue-100 text-blue-800",
      RECUSADA: "bg-red-100 text-red-800",
      RETIRADA: "bg-green-100 text-green-800",
      CANCELADA: "bg-gray-100 text-gray-700",
    };
    const css = map[status] || "bg-gray-100 text-gray-700";
    return `<span class="px-2 py-1 text-xs rounded-full ${css}">${status || "-"}</span>`;
  }

  function fmtData(iso) {
    if (!iso) return "-";
    try {
      return new Date(iso).toLocaleString();
    } catch {
      return iso;
    }
  }

  function setMensagem(tbody, texto, css) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 ${css}">${texto}</td></tr>`;
  }

  function renderLista(tbody, lista) {
    if (!lista.length) {
      setMensagem(tbody, "Nenhuma doação encontrada.", "text-gray-500");
      return;
    }

    tbody.innerHTML = lista.map((doacao) => {
      const canConfirm = doacao.status === "ACEITA";
      const canCancel = doacao.status === "PENDENTE" || doacao.status === "ACEITA";

      return `
        <tr>
          <td class="px-6 py-4">${doacao.produto?.nome ?? "-"}</td>
          <td class="px-6 py-4">${doacao.ong?.nome ?? "-"}</td>
          <td class="px-6 py-4">${fmtData(doacao.dataCriacao)}</td>
          <td class="px-6 py-4">${badge(doacao.status)}</td>
          <td class="px-6 py-4 text-right space-x-3">
            <button class="${canConfirm ? "text-green-600 hover:text-green-800" : "text-gray-300 cursor-not-allowed"}" ${canConfirm ? `onclick="confirmar(${doacao.id})"` : ""} title="Confirmar retirada">
              <i data-feather="check-circle"></i>
            </button>
            <button class="${canCancel ? "text-red-600 hover:text-red-800" : "text-gray-300 cursor-not-allowed"}" ${canCancel ? `onclick="cancelar(${doacao.id})"` : ""} title="Cancelar">
              <i data-feather="x-circle"></i>
            </button>
          </td>
        </tr>
      `;
    }).join("");
  }

  async function carregarDoacoesFuncionario() {
    const tbody = document.getElementById("tbody-doacoes");
    const usuario = lerUsuario();
    const usuarioId = Number(usuario?.id || 0);

    if (!tbody) {
      alert("Tabela de doações não encontrada na página.");
      return;
    }

    if (!usuario || String(usuario.tipo || "").toUpperCase() !== "FUNCIONARIO" || !getToken()) {
      localStorage.removeItem("usuario");
      localStorage.removeItem("token");
      window.location.replace("login.html");
      return;
    }

    if (!usuarioId) {
      setMensagem(tbody, "Usuário não autenticado.", "text-red-500");
      return;
    }

    setMensagem(tbody, "Carregando...", "text-gray-500");

    try {
      const lista = await requestJson("GET", `${pageApiBase}/doacoes/por-criador/${usuarioId}`);
      if (!Array.isArray(lista)) {
        throw new Error("Resposta inválida ao carregar doações.");
      }

      renderLista(tbody, lista);

      if (window.feather) {
        window.feather.replace();
      }
    } catch (error) {
      setMensagem(tbody, String(error?.message || "Erro ao carregar doações."), "text-red-500");
    }
  }

  window.confirmar = async function confirmar(id) {
    if (!confirm("Confirmar retirada desta doação?")) return;
    try {
      await requestJson("PUT", `${pageApiBase}/doacoes/${id}/retirada`);
      await carregarDoacoesFuncionario();
      alert("Retirada confirmada!");
    } catch (error) {
      alert(String(error?.message || "Erro ao confirmar retirada."));
    }
  };

  window.cancelar = async function cancelar(id) {
    if (!confirm("Cancelar esta doação?")) return;
    try {
      await requestJson("PUT", `${pageApiBase}/doacoes/${id}/recusar`);
      await carregarDoacoesFuncionario();
      alert("Doação cancelada.");
    } catch (error) {
      alert(String(error?.message || "Erro ao cancelar doação."));
    }
  };

  document.addEventListener("DOMContentLoaded", carregarDoacoesFuncionario);
})();
