document.addEventListener("DOMContentLoaded", async () => {
  const tabela = document.getElementById("tabela-produtos");
  const erro = document.getElementById("erro-produtos");

  try {
    // ✅ Lê o objeto completo do localStorage
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    const usuarioId = usuario?.id;

    if (!usuario || !usuarioId) {
      erro.textContent = "Usuário não identificado. Faça login novamente.";
      erro.classList.remove("hidden");
      tabela.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-gray-500">Nenhum produto carregado.</td></tr>`;
      return;
    }

    // ✅ Usa o ID diretamente do objeto do usuário
    const resp = await fetch(`http://localhost:8080/api/produtos/por-usuario/${usuarioId}`);

    if (!resp.ok) throw new Error("Erro ao buscar produtos.");

    const produtos = await resp.json();

    if (!produtos || produtos.length === 0) {
      tabela.innerHTML = `
        <tr><td colspan="5" class="text-center py-4 text-gray-500">Nenhum produto cadastrado.</td></tr>
      `;
      return;
    }

    tabela.innerHTML = produtos.map(p => {
      const validade = p.dataValidade
        ? new Date(p.dataValidade).toLocaleDateString("pt-BR")
        : "-";
      const status = p.disponivel ? "Disponível" : "Indisponível";
      const corStatus = p.disponivel ? "text-green-700" : "text-red-700";

      return `
        <tr>
          <td class="py-2 px-4 border-b">${p.nome}</td>
          <td class="py-2 px-4 border-b">${p.categoria}</td>
          <td class="py-2 px-4 border-b">${validade}</td>
          <td class="py-2 px-4 border-b">${p.quantidade}</td>
          <td class="py-2 px-4 border-b font-medium ${corStatus}">${status}</td>
        </tr>
      `;
    }).join("");

  } catch (err) {
    console.error("Erro ao carregar produtos:", err);
    erro.classList.remove("hidden");
    erro.textContent = "Erro ao carregar produtos.";
    tabela.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-red-500">Erro ao carregar produtos.</td></tr>`;
  }
});
