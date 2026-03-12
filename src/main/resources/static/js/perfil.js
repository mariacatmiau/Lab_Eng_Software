(() => {
  const pageApiBase = window.AppCore.apiBase;

  function decomporEndereco(endereco) {
    const texto = String(endereco || "").trim();
    if (!texto) {
      return { rua: "", numero: "", bairro: "", cidade: "", estado: "" };
    }

    const partes = texto.split(",").map((parte) => parte.trim()).filter(Boolean);
    if (partes.length < 4) {
      return { rua: texto, numero: "", bairro: "", cidade: "", estado: "" };
    }

    const rua = partes[0] || texto;
    const numero = partes[1] || "";
    let bairro = partes[2] || "";
    const cidadeEstado = partes.slice(3).join(", ");
    let cidade = "";
    let estado = "";

    if (cidadeEstado.includes(" - ")) {
      const pedacos = cidadeEstado.split(" - ");
      cidade = pedacos[0]?.trim() || "";
      estado = pedacos.slice(1).join(" - ").trim().toUpperCase();
    } else if (partes.length >= 4) {
      cidade = cidadeEstado;
    }

    return { rua, numero, bairro, cidade, estado };
  }

  function montarEndereco() {
    const rua = document.getElementById("rua")?.value.trim() || "";
    const numero = document.getElementById("numero")?.value.trim() || "";
    const bairro = document.getElementById("bairro")?.value.trim() || "";
    const cidade = document.getElementById("cidade")?.value.trim() || "";
    const estado = (document.getElementById("estado")?.value || "").trim().toUpperCase();

    if (!rua || !numero || !bairro || !cidade || !estado) {
      return { valido: false, endereco: "" };
    }

    return {
      valido: true,
      endereco: `${rua}, ${numero}, ${bairro}, ${cidade} - ${estado}`,
    };
  }

  function montarLinksPerfil(tipo) {
    const tipoNormalizado = String(tipo || "").trim().toUpperCase();
    if (tipoNormalizado === "ONG") {
      return [
        { href: "dashboard-ong.html", icon: "home", label: "Dashboard" },
        { href: "doacoes-ong.html", icon: "gift", label: "Doações" },
        { href: "retiradas-ong.html", icon: "truck", label: "Retiradas" },
      ];
    }
    if (tipoNormalizado === "CLIENTE") {
      return [
        { href: "dashboard-cliente.html", icon: "home", label: "Dashboard" },
      ];
    }
    return [
      { href: "dashboard-funcionario.html", icon: "home", label: "Dashboard" },
      { href: "cadastrar-produto.html", icon: "package", label: "Cadastrar Produto" },
      { href: "produtos.html", icon: "list", label: "Produtos" },
      { href: "doacoes-funcionario.html", icon: "gift", label: "Doações" },
      { href: "retiradas-funcionario.html", icon: "truck", label: "Retiradas" },
      { href: "ongs.html", icon: "users", label: "ONGs Parceiras" },
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

    const links = montarLinksPerfil(tipo)
      .map((item) => `
        <a href="${item.href}" class="flex items-center px-3 py-2 text-sm font-medium text-gray-600 hover:text-gray-900 rounded-md">
          <i data-feather="${item.icon}" class="mr-3 h-5 w-5 text-gray-400"></i> ${item.label}
        </a>
      `)
      .join("");

    navContainer.innerHTML = `${links}
      <a href="perfil.html" class="active-item flex items-center px-3 py-2 text-sm font-medium rounded-md">
        <i data-feather="user" class="text-green-500 mr-3 h-5 w-5"></i> Perfil
      </a>
    `;

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
      const payload = {
        nome: document.getElementById("nome").value.trim(),
        email: document.getElementById("email").value.trim(),
        telefone: document.getElementById("telefone").value.trim(),
        endereco: enderecoMontado.address,
      };

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
