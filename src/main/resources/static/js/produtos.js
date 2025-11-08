document.addEventListener("DOMContentLoaded", async () => {
  const tabela = document.getElementById("tabela-produtos");

  try {
    const resp = await fetch("http://localhost:8080/api/produtos/disponiveis");
    if (!resp.ok) throw new Error("Erro ao carregar produtos");

    const produtos = await resp.json();

    if (produtos.length === 0) {
      tabela.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-gray-500">Nenhum produto cadastrado.</td></tr>`;
      return;
    }

    tabela.innerHTML = produtos.map(p => {
      const validade = p.dataValidade ? new Date(p.dataValidade) : null;
      const hoje = new Date();
      let status = "Em estoque";
      let cor = "green";

      if (validade) {
        const diff = Math.ceil((validade - hoje) / (1000 * 60 * 60 * 24));
        if (diff < 0) { status = "Vencido"; cor = "red"; }
        else if (diff <= 7) { status = "Próximo ao vencimento"; cor = "yellow"; }
      }

      return `
        <tr>
          <td class="px-4 py-2">${p.nome}</td>
          <td class="px-4 py-2">${p.categoria}</td>
          <td class="px-4 py-2">${validade ? validade.toLocaleDateString("pt-BR") : "-"}</td>
          <td class="px-4 py-2">${p.quantidade}</td>
          <td class="px-4 py-2 text-${cor}-700">${status}</td>
        </tr>`;
    }).join("");
  } catch (err) {
    console.error("Erro:", err);
    tabela.innerHTML = `<tr><td colspan="5" class="text-center text-red-600 py-4">Erro ao carregar produtos.</td></tr>`;
  }
});
