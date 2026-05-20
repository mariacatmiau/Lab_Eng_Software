(() => {
  const pageApiBase = window.AppCore.apiBase;

  function montarLinksPerfil(tipo) {
    const tipoNormalizado = String(tipo || "").trim().toUpperCase();
    const perfil = { href: "perfil.html", icon: "user", label: "Perfil", active: true };
    if (tipoNormalizado === "ONG") {
      return [
        { href: "dashboard-ong.html", icon: "home", label: "Dashboard" },
        perfil,
        { href: "doacoes-ong.html", icon: "gift", label: "Doações" },
        { href: "retiradas-ong.html", icon: "truck", label: "Retiradas" },
        { href: "dashboard-impacto.html", icon: "bar-chart-2", label: "Impacto" },
      ];
    }
    if (tipoNormalizado === "CLIENTE") {
      return [
        { href: "dashboard-cliente.html", icon: "home", label: "Dashboard" },
        perfil,
        { href: "dashboard-impacto.html", icon: "bar-chart-2", label: "Impacto" },
      ];
    }
    return [
      { href: "dashboard-funcionario.html", icon: "home", label: "Dashboard" },
      perfil,
      { href: "cadastrar-produto.html", icon: "package", label: "Cadastrar Produto" },
      { href: "produtos.html", icon: "list", label: "Produtos" },
      { href: "doacoes-funcionario.html", icon: "gift", label: "Doações" },
      { href: "retiradas-funcionario.html", icon: "truck", label: "Retiradas" },
      { href: "ongs.html", icon: "users", label: "ONGs Parceiras" },
      { href: "dashboard-impacto.html", icon: "bar-chart-2", label: "Impacto" },
    ];
  }

  function preencherResumo(usuario) {
    document.getElementById("resumoNome").textContent = usuario?.nome || "Não informado";
    document.getElementById("resumoEmail").textContent = usuario?.email || "Não informado";
    document.getElementById("resumoTelefone").textContent = usuario?.telefone || "Não informado";
    document.getElementById("resumoEndereco").textContent = usuario?.endereco || "Não informado";
    document.getElementById("resumoTipo").textContent = String(usuario?.tipo || "Não informado");
  }

  function preencherFormulario(usuario) {
    document.getElementById("nome").value = usuario?.nome || "";
    document.getElementById("email").value = usuario?.email || "";
    document.getElementById("telefone").value = usuario?.telefone || "";
    window.AddressForm.fillFields(usuario?.endereco || "");
  }

  function renderSidebar(tipo) {
    const navContainer = document.getElementById("perfilNavLinks");
    if (!navContainer) return;

    navContainer.innerHTML = montarLinksPerfil(tipo)
      .map((item) => {
        const cls = item.active
          ? "active-item flex items-center px-3 py-2 text-sm font-medium rounded-md"
          : "flex items-center px-3 py-2 text-sm font-medium text-gray-600 hover:text-gray-900 rounded-md";
        const iconCls = item.active ? "text-green-500 mr-3 h-5 w-5" : "mr-3 h-5 w-5 text-gray-400";
        return `
        <a href="${item.href}" class="${cls}">
          <i data-feather="${item.icon}" class="${iconCls}"></i> ${item.label}
        </a>`;
      })
      .join("");

    if (window.feather) {
      window.feather.replace();
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    let usuario = window.AppCore.readStoredUser();

    if (!usuario?.id) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.href = "login.html";
      return;
    }

    renderSidebar(usuario.tipo);
    preencherResumo(usuario);
    preencherFormulario(usuario);

    const form = document.getElementById("formPerfil");
    const btnSalvar = document.getElementById("btnSalvar");
    const btnEditar = document.getElementById("btnEditarPerfil");
    const btnCancelar = document.getElementById("btnCancelarEdicao");
    const resumo = document.getElementById("perfilResumo");
    const msgOk = document.getElementById("msgOk");
    const msgErro = document.getElementById("msgErro");

    function abrirEdicao() {
      msgOk.classList.add("hidden");
      msgErro.classList.add("hidden");
      preencherFormulario(usuario);
      resumo.classList.add("hidden");
      form.classList.remove("hidden");
      btnEditar.textContent = "Editando";
      btnEditar.disabled = true;
    }

    function fecharEdicao() {
      form.classList.add("hidden");
      resumo.classList.remove("hidden");
      btnEditar.textContent = "Editar Dados";
      btnEditar.disabled = false;
      preencherFormulario(usuario);
    }

    btnEditar.addEventListener("click", abrirEdicao);
    btnCancelar.addEventListener("click", fecharEdicao);

    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      msgOk.classList.add("hidden");
      msgErro.classList.add("hidden");

      const enderecoMontado = window.AddressForm.buildFromFields();
      const novaSenha = document.getElementById("novaSenha").value;
      const confirmarSenha = document.getElementById("confirmarSenha").value;
      const senhaAtual = document.getElementById("senhaAtual").value;

      if (novaSenha || confirmarSenha || senhaAtual) {
        if (novaSenha !== confirmarSenha) {
          msgErro.textContent = "Nova senha e confirmação não conferem.";
          msgErro.classList.remove("hidden");
          return;
        }
        if (novaSenha.length < 6) {
          msgErro.textContent = "Nova senha deve ter no mínimo 6 caracteres.";
          msgErro.classList.remove("hidden");
          return;
        }
        if (!senhaAtual) {
          msgErro.textContent = "Informe a senha atual para trocar a senha.";
          msgErro.classList.remove("hidden");
          return;
        }
      }

      const payload = {
        nome: document.getElementById("nome").value.trim(),
        email: document.getElementById("email").value.trim(),
        telefone: document.getElementById("telefone").value.trim(),
        endereco: enderecoMontado.address,
      };

      if (novaSenha) {
        payload.senhaAtual = senhaAtual;
        payload.novaSenha = novaSenha;
      }

      if (!payload.nome || !payload.email || !payload.telefone || !enderecoMontado.valid) {
        msgErro.textContent = "Preencha rua, número, bairro, cidade, estado, além de nome, e-mail e telefone.";
        msgErro.classList.remove("hidden");
        return;
      }

      try {
        btnSalvar.disabled = true;
        btnSalvar.textContent = "Salvando...";

        const resp = await fetch(`${pageApiBase}/usuarios/${usuario.id}/perfil`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!resp.ok) {
          throw new Error(await resp.text());
        }

        const atualizado = await resp.json();
        usuario = {
          ...usuario,
          ...atualizado,
          id: atualizado?.id || usuario.id,
          tipo: atualizado?.tipo || usuario.tipo,
        };

        window.AppCore.writeStoredUser(usuario);
        preencherResumo(usuario);
        preencherFormulario(usuario);
        renderSidebar(usuario.tipo);
        document.getElementById("senhaAtual").value = "";
        document.getElementById("novaSenha").value = "";
        document.getElementById("confirmarSenha").value = "";
        fecharEdicao();
        msgOk.textContent = "Perfil atualizado com sucesso.";
        msgOk.classList.remove("hidden");
      } catch (err) {
        msgErro.textContent = err?.message || "Erro ao atualizar perfil.";
        msgErro.classList.remove("hidden");
      } finally {
        btnSalvar.disabled = false;
        btnSalvar.textContent = "Salvar Alterações";
      }
    });
  });
})();
