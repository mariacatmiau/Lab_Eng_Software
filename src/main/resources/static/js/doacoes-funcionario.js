const API = {
  produtos: "http://localhost:8080/api/produtos",
  ongs: "http://localhost:8080/api/ongs",
  doacoes: "http://localhost:8080/api/doacoes",
  confirmar: (id) => `http://localhost:8080/api/retiradas/${id}/confirmar`,
  cancelar: (id) => `http://localhost:8080/api/retiradas/${id}/cancelar`,
};

const el = (id) => document.getElementById(id);
const tbody = el("tbody-doacoes");
const modal = el("modal");
const selProduto = el("selProduto");
const selOng = el("selOng");
const inpQtd = el("inpQtd");

// ---------------- UI helpers ----------------
function showModal() { modal.classList.remove("hidden"); modal.classList.add("flex"); }
function hideModal() { modal.classList.add("hidden"); modal.classList.remove("flex"); }

// ---------------- Carregamentos iniciais ----------------
async function carregarProdutosEOngs() {
  try {
    const [rp, ro] = await Promise.all([fetch(API.produtos), fetch(API.ongs)]);
    const [produtos, ongs] = await Promise.all([rp.json(), ro.json()]);

    selProduto.innerHTML = `<option value="">Selecione...</option>` +
      produtos.map(p => `<option value="${p.id}">${p.nome} — validade ${p.validade || '-'}</option>`).join("");

    selOng.innerHTML = `<option value="">Selecione...</option>` +
      ongs.map(o => `<option value="${o.id}">${o.nome}</option>`).join("");
  } catch (e) {
    selProduto.innerHTML = `<option>Erro ao carregar</option>`;
    selOng.innerHTML = `<option>Erro ao carregar</option>`;
  }
}

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

async function carregarDoacoes() {
  tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-gray-500">Carregando...</td></tr>`;
  try {
    const r = await fetch(API.doacoes);
    const list = await r.json();

    if (!list.length) {
      tbody.innerHTML = `<tr><td colspan="5" class="text-center py-6 text-gray-500">Nenhuma doação encontrada.</td></tr>`;
      return;
    }

    tbody.innerHTML = list.map(d => {
      const canConfirm = d.status === "ACEITA";
      const canCancel  = d.status === "PENDENTE" || d.status === "ACEITA";
      return `
        <tr>
          <td class="px-6 py-4">${d.produto?.nome ?? "-"}</td>
          <td class="px-6 py-4">${d.ong?.nome ?? "-"}</td>
          <td class="px-6 py-4">${fmtData(d.dataCriacao)}</td>
          <td class="px-6 py-4">${badge(d.status)}</td>
          <td class="px-6 py-4 text-right space-x-3">
            <button class="${canConfirm? 'text-green-600 hover:text-green-800':'text-gray-300 cursor-not-allowed'}"
              ${canConfirm? `onclick="confirmar(${d.id})"`:""} title="Confirmar retirada">
              <i data-feather="check-circle"></i>
            </button>
            <button class="${canCancel? 'text-red-600 hover:text-red-800':'text-gray-300 cursor-not-allowed'}"
              ${canCancel? `onclick="cancelar(${d.id})"`:""} title="Cancelar">
              <i data-feather="x-circle"></i>
            </button>
          </td>
        </tr>`;
    }).join("");
    feather.replace();
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-red-500">Erro ao carregar doações.</td></tr>`;
  }
}

// ---------------- Ações ----------------
async function criarDoacao() {
  const produtoId = selProduto.value;
  const ongId = selOng.value;
  const quantidade = inpQtd.value ? parseInt(inpQtd.value, 10) : null;

  if (!produtoId || !ongId) {
    alert("Selecione produto e ONG.");
    return;
  }

  const payload = { produtoId: Number(produtoId), ongId: Number(ongId), quantidade };
  try {
    const r = await fetch(API.doacoes, {
      method: "POST", headers: { "Content-Type":"application/json" }, body: JSON.stringify(payload)
    });
    if (!r.ok) throw 0;
    hideModal();
    await carregarDoacoes();
    alert("Doação criada e enviada para a ONG!");
  } catch {
    alert("Erro ao criar doação.");
  }
}

async function confirmar(id) {
  if (!confirm("Confirmar retirada desta doação?")) return;
  try {
    const r = await fetch(API.confirmar(id), { method: "PUT" });
    if (!r.ok) throw 0;
    await carregarDoacoes();
    alert("Retirada confirmada!");
  } catch { alert("Erro ao confirmar retirada."); }
}

async function cancelar(id) {
  if (!confirm("Cancelar esta doação?")) return;
  try {
    const r = await fetch(API.cancelar(id), { method: "PUT" });
    if (!r.ok) throw 0;
    await carregarDoacoes();
    alert("Doação cancelada.");
  } catch { alert("Erro ao cancelar doação."); }
}

// ---------------- Listeners ----------------
document.getElementById("btnAbrirModal").addEventListener("click", async () => { showModal(); await carregarProdutosEOngs(); });
document.getElementById("btnFecharModal").addEventListener("click", hideModal);
document.getElementById("btnCancelar").addEventListener("click", hideModal);
document.getElementById("btnSalvar").addEventListener("click", criarDoacao);

// boot
carregarDoacoes();
