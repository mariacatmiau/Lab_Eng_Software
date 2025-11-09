document.addEventListener("DOMContentLoaded", async () => {
  const tabela = document.getElementById("lista-ongs");

  try {
    const resp = await fetch("http://localhost:8080/api/ongs");
    if (!resp.ok) throw new Error("Erro ao buscar ONGs");

    const ongs = await resp.json();

    if (!ongs.length) {
      tabela.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-gray-500">Nenhuma ONG cadastrada.</td></tr>`;
      return;
    }

    tabela.innerHTML = ongs.map(o => `
      <tr>
        <td class="py-2 px-4 border-b">${o.nome}</td>
        <td class="py-2 px-4 border-b">${o.email}</td>
        <td class="py-2 px-4 border-b">${o.telefone || "-"}</td>
        <td class="py-2 px-4 border-b">${o.endereco || "-"}</td>
      </tr>
    `).join("");

  } catch (err) {
    console.error("Erro ao carregar ONGs:", err);
    tabela.innerHTML = `<tr><td colspan="4" class="text-center text-red-500 py-4">Erro ao carregar ONGs.</td></tr>`;
  }
});
