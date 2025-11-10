document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-cadastro-produto");
  const msgErro = document.getElementById("mensagem-erro");
  const msgSucesso = document.getElementById("mensagem-sucesso");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

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
      usuarioId: usuarioId // ✅ usa o id direto
    };

    try {
      const resp = await fetch("http://44.198.34.216:8081/api/produtos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });

      if (!resp.ok) throw new Error(await resp.text());

      msgErro.classList.add("hidden");
      msgSucesso.classList.remove("hidden");
      msgSucesso.textContent = "Produto cadastrado com sucesso!";
      form.reset();

      setTimeout(() => window.location.href = "produtos.html", 1200);
    } catch (err) {
      console.error("Erro ao cadastrar produto:", err);
      msgSucesso.classList.add("hidden");
      msgErro.classList.remove("hidden");
      msgErro.textContent = "Erro ao cadastrar produto. Verifique o backend.";
    }
  });
});
