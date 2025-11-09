const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
  const usuario = JSON.parse(localStorage.getItem("usuario"));
  if (!usuario || !usuario.tipo) {
    alert("Sessão expirada. Faça login novamente.");
    window.location.replace("login.html");
    return;
  }

  console.log("Usuário logado:", usuario);

  await carregarResumo();
  await carregarProdutosRecentes();
});

async function carregarResumo() {
  try {
    const [produtosRes, ongsRes, doacoesRes] = await Promise.all([
      fetch(`${API_BASE}/api/produtos`),
      fetch(`${API_BASE}/api/ongs`),
      fetch(`${API_BASE}/api/doacoes`)
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
    const res = await fetch(`${API_BASE}/api/produtos`);
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

function logout() {
  localStorage.removeItem("usuario");
  window.location.replace("login.html");
}
