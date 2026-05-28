const API_BASE =
  window.location.origin && window.location.origin.startsWith("http")
    ? window.location.origin
    : "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
  const usuario = JSON.parse(localStorage.getItem("usuario"));
  if (!usuario || !usuario.tipo) {
    alert("Sessão expirada. Faça login novamente.");
    window.location.replace("login.html");
    return;
  }

  await carregarResumo();
  await carregarProdutosRecentes();
  await carregarPedidosPendentes();
});

async function carregarResumo() {
  try {
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    const usuarioId = usuario?.id;

    if (!usuarioId) {
      throw new Error("Usuário não autenticado");
    }

    const [produtosRes, ongsRes, doacoesRes] = await Promise.all([
      fetch(`${API_BASE}/api/produtos/por-usuario/${usuarioId}`),
      fetch(`${API_BASE}/api/ongs`),
      fetch(`${API_BASE}/api/doacoes/por-criador/${usuarioId}`)
    ]);

    const produtos = await produtosRes.json();
    const ongs = await ongsRes.json();
    const doacoes = await doacoesRes.json();

    // Contadores da dashboard
    document.getElementById("total-produtos").textContent = produtos.length;
    document.getElementById("total-ongs").textContent = ongs.length;
    document.getElementById("total-doacoes").textContent = doacoes.filter(d => d.status !== "PENDENTE").length;
  } catch (err) {
    console.error("Erro ao carregar resumo:", err);
  }
}

async function carregarProdutosRecentes() {
  try {
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    const usuarioId = usuario?.id;

    if (!usuarioId) {
      throw new Error("Usuário não autenticado");
    }

    const res = await fetch(`${API_BASE}/api/produtos/por-usuario/${usuarioId}`);
    const produtos = await res.json();

    const tabela = document.getElementById("tabela-produtos");
    tabela.innerHTML = "";

    if (!produtos.length) {
      tabela.innerHTML = `<tr><td colspan="3" class="text-center py-4 text-gray-500">Nenhum produto cadastrado.</td></tr>`;
      return;
    }

    produtos.slice(-5).reverse().forEach(produto => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${produto.nome || "-"}</td>
        <td>${produto.categoria || "-"}</td>
        <td>${produto.dataValidade || "-"}</td>
      `;
      tabela.appendChild(tr);
    });
  } catch (err) {
    console.error("Erro ao carregar produtos:", err);
  }
}

async function carregarPedidosPendentes() {
  const lista = document.getElementById("lista-pedidos-pendentes");
  const badge = document.getElementById("badge-pedidos-pendentes");
  try {
    const resp = await fetch(`${API_BASE}/api/pedidos/por-mercado`);
    if (!resp.ok) throw new Error("Erro na requisição");
    const pedidos = await resp.json();

    if (!pedidos || !pedidos.length) {
      lista.innerHTML = `<p class="text-center py-6 text-gray-500">Nenhum pedido aguardando confirmação.</p>`;
      return;
    }

    badge.textContent = pedidos.length;
    badge.classList.remove("hidden");

    lista.innerHTML = pedidos.map(p => {
      const data = p.criadoEm ? new Date(p.criadoEm).toLocaleString("pt-BR") : "-";
      const valorFormatado = typeof p.valorTotal === "number"
        ? p.valorTotal.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
        : `R$ ${p.valorTotal}`;
      const cliente = p.clienteNome ? window.AppCore.escapeHtml(p.clienteNome) : "Cliente";
      return `
        <div class="flex items-center justify-between px-6 py-4 gap-4" id="pedido-row-${p.pedidoId}">
          <div>
            <p class="text-sm font-semibold text-gray-900">Pedido #${p.pedidoId} — ${cliente}</p>
            <p class="text-xs text-gray-500 mt-0.5">${data} · <span class="font-medium text-gray-700">${valorFormatado}</span></p>
          </div>
          <button
            onclick="confirmarPagamentoPedido(${p.pedidoId}, this)"
            class="shrink-0 rounded-lg bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700 transition-colors">
            Confirmar pagamento recebido
          </button>
        </div>
      `;
    }).join("");
  } catch (err) {
    console.error("Erro ao carregar pedidos pendentes:", err);
    lista.innerHTML = `<p class="text-center py-6 text-red-500">Erro ao carregar pedidos.</p>`;
  }
}

async function confirmarPagamentoPedido(pedidoId, btn) {
  btn.disabled = true;
  btn.textContent = "Confirmando...";
  try {
    const resp = await fetch(`${API_BASE}/api/pedidos/${pedidoId}/pagar`, { method: "PUT" });
    if (!resp.ok) {
      const msg = await resp.text().catch(() => "");
      alert("Não foi possível confirmar: " + (msg || resp.status));
      btn.disabled = false;
      btn.textContent = "Confirmar pagamento recebido";
      return;
    }
    const row = document.getElementById(`pedido-row-${pedidoId}`);
    if (row) {
      row.innerHTML = `
        <div class="flex items-center gap-2 px-6 py-4">
          <span class="inline-block w-2 h-2 rounded-full bg-green-500"></span>
          <p class="text-sm text-green-700 font-medium">Pedido #${pedidoId} confirmado como pago.</p>
        </div>`;
    }
    // Atualiza badge
    const badge = document.getElementById("badge-pedidos-pendentes");
    if (badge) {
      const atual = parseInt(badge.textContent || "0", 10);
      if (atual <= 1) badge.classList.add("hidden");
      else badge.textContent = atual - 1;
    }
  } catch {
    alert("Erro ao confirmar pagamento. Tente novamente.");
    btn.disabled = false;
    btn.textContent = "Confirmar pagamento recebido";
  }
}

