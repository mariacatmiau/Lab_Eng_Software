(() => {
  const pageApiOrigin =
    window.location.origin && window.location.origin.startsWith("http")
      ? window.location.origin
      : "http://localhost:8080";
  const pageApiBase = `${pageApiOrigin}/api`;
  const colspanPendentes = 6;
  const colspanAceitas = 5;

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

  function contatoMercado(doacao) {
    const email = doacao?.criadoPor?.email || "-";
    const telefone = doacao?.criadoPor?.telefone || "nao informado";
    return `<div class="text-sm leading-5"><div><span class="font-medium">E-mail:</span> ${email}</div><div><span class="font-medium">Tel:</span> ${telefone}</div></div>`;
  }

  function acoesContato(doacao) {
    const email = doacao?.criadoPor?.email;
    const telefone = String(doacao?.criadoPor?.telefone || "").replace(/\D/g, "");
    const links = [];

    if (email) {
      links.push(`<a class="text-blue-600 hover:text-blue-800 mr-3" href="mailto:${email}?subject=Retirada%20de%20doa%C3%A7%C3%A3o%20%23${doacao.id}">Enviar e-mail</a>`);
    }
    if (telefone) {
      links.push(`<a class="text-green-600 hover:text-green-800" target="_blank" rel="noopener" href="https://wa.me/55${telefone}">WhatsApp</a>`);
    }

    return links.length ? links.join("") : "Sem contato disponível.";
  }

  function setMensagem(tbody, colspan, texto, css) {
    tbody.innerHTML = `<tr><td colspan="${colspan}" class="text-center py-4 ${css}">${texto}</td></tr>`;
  }

  function renderPendentes(tbody, lista) {
    if (!lista.length) {
      setMensagem(tbody, colspanPendentes, "Nenhuma pendente.", "text-gray-500");
      return;
    }

    tbody.innerHTML = lista.map((doacao) => `
      <tr>
        <td class="px-6 py-4">${doacao.produto?.nome ?? "-"}</td>
        <td class="px-6 py-4">${doacao.criadoPor?.nome ?? "Supermercado"}</td>
        <td class="px-6 py-4">${contatoMercado(doacao)}</td>
        <td class="px-6 py-4">${fmtData(doacao.dataCriacao)}</td>
        <td class="px-6 py-4">${badge(doacao.status)}</td>
        <td class="px-6 py-4 text-right space-x-3">
          <button class="text-green-600 hover:text-green-800" onclick="aceitar(${doacao.id})" title="Aceitar"><i data-feather="check"></i></button>
          <button class="text-red-600 hover:text-red-800" onclick="recusar(${doacao.id})" title="Recusar"><i data-feather="x"></i></button>
        </td>
      </tr>
    `).join("");
  }

  function renderAceitas(tbody, lista) {
    if (!lista.length) {
      setMensagem(tbody, colspanAceitas, "-", "text-gray-500");
      return;
    }

    tbody.innerHTML = lista.map((doacao) => `
      <tr>
        <td class="px-6 py-4">${doacao.produto?.nome ?? "-"}</td>
        <td class="px-6 py-4">${doacao.criadoPor?.nome ?? "Supermercado"}</td>
        <td class="px-6 py-4">${contatoMercado(doacao)}</td>
        <td class="px-6 py-4">${badge(doacao.status)}</td>
        <td class="px-6 py-4 text-right">${acoesContato(doacao)}</td>
      </tr>
    `).join("");
  }

  async function carregarDoacoesOng() {
    const tbodyPendentes = document.getElementById("tbody-pendentes");
    const tbodyAceitas = document.getElementById("tbody-aceitas");
    const usuario = lerUsuario();
    const ongId = Number(usuario?.id || 0);

    if (!tbodyPendentes || !tbodyAceitas) {
      alert("Tabela de doações não encontrada na página.");
      return;
    }

    if (!usuario || String(usuario.tipo || "").toUpperCase() !== "ONG" || !getToken()) {
      localStorage.removeItem("usuario");
      localStorage.removeItem("token");
      window.location.replace("login.html");
      return;
    }

    if (!ongId) {
      setMensagem(tbodyPendentes, colspanPendentes, "ONG não identificada.", "text-red-500");
      setMensagem(tbodyAceitas, colspanAceitas, "ONG não identificada.", "text-red-500");
      return;
    }

    setMensagem(tbodyPendentes, colspanPendentes, "Carregando...", "text-gray-500");
    setMensagem(tbodyAceitas, colspanAceitas, "Carregando...", "text-gray-500");

    try {
      const lista = await requestJson("GET", `${pageApiBase}/doacoes/por-ong/${ongId}`);
      if (!Array.isArray(lista)) {
        throw new Error("Resposta inválida ao carregar doações.");
      }

      renderPendentes(tbodyPendentes, lista.filter((item) => item.status === "PENDENTE"));
      renderAceitas(tbodyAceitas, lista.filter((item) => item.status === "ACEITA"));

      if (window.feather) {
        window.feather.replace();
      }
    } catch (error) {
      const mensagem = String(error?.message || "Erro ao carregar doações.");
      setMensagem(tbodyPendentes, colspanPendentes, mensagem, "text-red-500");
      setMensagem(tbodyAceitas, colspanAceitas, mensagem, "text-red-500");
    }
  }

  window.aceitar = async function aceitar(id) {
    if (!confirm("Aceitar esta doação?")) return;
    try {
      await requestJson("PUT", `${pageApiBase}/doacoes/${id}/aceitar`);
      await carregarDoacoesOng();
      alert("Doação aceita!");
    } catch (error) {
      alert(String(error?.message || "Erro ao aceitar."));
    }
  };

  window.recusar = async function recusar(id) {
    if (!confirm("Recusar esta doação?")) return;
    try {
      await requestJson("PUT", `${pageApiBase}/doacoes/${id}/recusar`);
      await carregarDoacoesOng();
      alert("Doação recusada.");
    } catch (error) {
      alert(String(error?.message || "Erro ao recusar."));
    }
  };

  document.addEventListener("DOMContentLoaded", carregarDoacoesOng);
})();
