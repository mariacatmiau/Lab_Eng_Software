document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-cadastro-produto");
  const msg = document.getElementById("mensagem");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const produto = {
      nome: document.getElementById("nome").value.trim(),
      categoria: document.getElementById("categoria").value.trim(),
      dataValidade: document.getElementById("dataValidade").value,
      quantidade: parseInt(document.getElementById("quantidade").value)
    };

    try {
      const resp = await fetch("http://44.198.34.216:8081/api/produtos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });

      if (!resp.ok) throw new Error("Erro ao cadastrar produto");

      msg.textContent = "✅ Produto cadastrado com sucesso!";
      msg.className = "text-green-600 text-center mt-4";

      form.reset();
    } catch (err) {
      console.error(err);
      msg.textContent = "❌ Erro ao cadastrar produto. Verifique o backend.";
      msg.className = "text-red-600 text-center mt-4";
    }
  });
});
