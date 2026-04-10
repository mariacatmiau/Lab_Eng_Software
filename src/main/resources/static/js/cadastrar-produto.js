document.addEventListener("DOMContentLoaded", () => {
  const API_ORIGIN =
    window.location.origin && window.location.origin.startsWith("http")
      ? window.location.origin
      : "http://localhost:8080";
  const API_BASE = `${API_ORIGIN}/api`;

  const form = document.getElementById("form-cadastro-produto");
  const msgErro = document.getElementById("mensagem-erro");
  const msgSucesso = document.getElementById("mensagem-sucesso");
  const submitBtn = form.querySelector("button[type='submit'], button");
  const tipoVenda = document.getElementById("tipoVenda");
  const tipoDoacao = document.getElementById("tipoDoacao");
  const grupoPreco = document.getElementById("grupoPreco");
  const grupoOng = document.getElementById("grupoOng");
  const precoInput = document.getElementById("preco");
  const ongSelect = document.getElementById("ongId");
  let submitting = false;

  function aplicarVisibilidadeTipoOferta() {
    const isVenda = tipoVenda.checked;
    grupoPreco.classList.toggle("hidden", !isVenda);
    grupoOng.classList.toggle("hidden", isVenda);

    if (isVenda) {
      ongSelect.value = "";
    } else {
      precoInput.value = "";
    }
  }

  async function carregarOngs() {
    try {
      const resp = await fetch(`${API_BASE}/ongs`);
      if (!resp.ok) throw new Error("Erro ao carregar ONGs");

      const ongs = await resp.json();
      ongSelect.innerHTML = "<option value=''>Selecione uma ONG</option>";

      if (!ongs.length) {
        ongSelect.innerHTML = "<option value=''>Nenhuma ONG cadastrada</option>";
        return;
      }

      ongs.forEach((o) => {
        const option = document.createElement("option");
        option.value = o.id;
        option.textContent = `${o.nome} (${o.email || "sem e-mail"})`;
        ongSelect.appendChild(option);
      });
    } catch (err) {
      ongSelect.innerHTML = "<option value=''>Erro ao carregar ONGs</option>";
    }
  }

  tipoVenda.addEventListener("change", aplicarVisibilidadeTipoOferta);
  tipoDoacao.addEventListener("change", aplicarVisibilidadeTipoOferta);
  aplicarVisibilidadeTipoOferta();
  carregarOngs();

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (submitting) return;

    // ✅ Recupera usuário logado do localStorage
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    const usuarioId = usuario?.id;

    if (!usuario || !usuarioId) {
      msgErro.textContent = "Usuário não identificado. Faça login novamente.";
      msgErro.classList.remove("hidden");
      return;
    }

    const produto = {
      nome: document.getElementById("nome").value.trim(),
      categoria: document.getElementById("categoria").value.trim(),
      dataValidade: document.getElementById("dataValidade").value,
      quantidade: parseInt(document.getElementById("quantidade").value) || 1,
      usuarioId: usuarioId,
      tipoOferta: tipoVenda.checked ? "VENDA" : "DOACAO",
      preco: tipoVenda.checked ? Number(precoInput.value) : null,
      ongId: tipoDoacao.checked ? Number(ongSelect.value) || null : null
    };

    if (produto.tipoOferta === "VENDA" && (!produto.preco || produto.preco <= 0)) {
      msgErro.textContent = "Para venda, informe um preço maior que zero.";
      msgErro.classList.remove("hidden");
      return;
    }

    if (produto.tipoOferta === "DOACAO" && !produto.ongId) {
      msgErro.textContent = "Para doação, selecione uma ONG destinatária.";
      msgErro.classList.remove("hidden");
      return;
    }

    try {
      submitting = true;
      submitBtn.disabled = true;
      submitBtn.textContent = "Cadastrando...";

      const resp = await fetch(`${API_BASE}/produtos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });

      if (!resp.ok) throw new Error(await resp.text());

      msgErro.classList.add("hidden");
      msgSucesso.classList.remove("hidden");
      msgSucesso.textContent = "Produto cadastrado com sucesso!";
      form.reset();
      aplicarVisibilidadeTipoOferta();

      setTimeout(() => window.location.href = "produtos.html", 1200);
    } catch (err) {
      console.error("Erro ao cadastrar produto:", err);
      msgSucesso.classList.add("hidden");
      msgErro.classList.remove("hidden");
      msgErro.textContent = err?.message || "Erro ao cadastrar produto. Verifique o backend.";
    } finally {
      submitting = false;
      submitBtn.disabled = false;
      submitBtn.textContent = "Cadastrar";
    }
  });
});
