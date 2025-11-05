document.addEventListener("DOMContentLoaded", async () => {
  const tipo = localStorage.getItem("userType");
  if (!tipo) window.location.href = "login.html";

  if (tipo === "FUNCIONARIO") {
    document.getElementById("dashboardFuncionario").innerHTML = `
      <p>Bem-vindo! Aqui você pode cadastrar produtos e gerenciar doações.</p>
      <a href="produtos.html" class="text-green-600 underline">Ver produtos</a>
    `;
  } else {
    document.getElementById("dashboardOng").innerHTML = `
      <p>Bem-vindo! Veja as doações disponíveis para sua ONG.</p>
      <a href="doacoes.html" class="text-green-600 underline">Ver doações pendentes</a>
    `;
  }
});
