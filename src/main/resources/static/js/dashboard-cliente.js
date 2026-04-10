(() => {
  const pageApiBase = window.AppCore.apiBase;
  const cartStorageKey = "carrinhoCliente";
  let ofertasAtuais = [];
  let carrinho = carregarCarrinho();
  let localizacaoAtual = null;
  let ultimoPedidoFinalizado = null;

  function carregarCarrinho() {
    try {
      const salvo = JSON.parse(localStorage.getItem(cartStorageKey) || "{}");
      return salvo && typeof salvo === "object" ? salvo : {};
    } catch {
      return {};
    }
  }

  function persistirCarrinho() {
    localStorage.setItem(cartStorageKey, JSON.stringify(carrinho));
  }

  function atualizarPainelPedidoFinalizado() {
    const box = document.getElementById("pedidoFinalizadoBox");
    const resumo = document.getElementById("pedidoFinalizadoResumo");
    const mercadosEl = document.getElementById("pedidoFinalizadoMercados");
    if (!box || !resumo || !mercadosEl) {
      return;
    }

    if (!ultimoPedidoFinalizado) {
      box.classList.add("hidden");
      resumo.textContent = "";
      mercadosEl.innerHTML = "";
      return;
    }

    box.classList.remove("hidden");
    resumo.textContent = `Pedido #${ultimoPedidoFinalizado.pedidoId} aguardando pagamento. Total: ${window.AppCore.formatCurrency(ultimoPedidoFinalizado.valorTotal)}.`;
    mercadosEl.innerHTML = (ultimoPedidoFinalizado.mercados || [])
      .map((mercado) => {
        const whatsappNumero = formatarTelefoneWhatsapp(mercado.mercadoTelefone);
        const whatsappHref = whatsappNumero
          ? `https://wa.me/${whatsappNumero}?text=${encodeURIComponent(mercado.mensagemWhatsapp || "")}`
          : "";

        return `
          <section class="rounded-xl border border-green-200 bg-white px-4 py-4">
            <div class="flex items-start justify-between gap-3">
              <div>
                <p class="text-sm font-bold text-slate-900">${window.AppCore.escapeHtml(mercado.mercadoNome || "Mercado")}</p>
                <p class="mt-1 text-sm text-slate-600">${window.AppCore.escapeHtml(mercado.mercadoEndereco || "Endereço não informado")}</p>
              </div>
              <p class="text-sm font-extrabold text-green-700">${window.AppCore.formatCurrency(mercado.total)}</p>
            </div>
            ${whatsappHref
              ? `<a href="${whatsappHref}" target="_blank" rel="noopener noreferrer" class="primary-btn mt-3 w-full inline-flex items-center justify-center">Enviar mensagem para o mercado</a>`
              : `<p class="mt-3 text-sm text-amber-800">Esse mercado não tem telefone cadastrado para WhatsApp.</p>`}
          </section>
        `;
      })
      .join("");
  }

  function obterQuantidadeSelecionada(produtoId) {
    return Number(carrinho[String(produtoId)]?.quantidade || 0);
  }

  function normalizarQuantidade(valor, maximo) {
    const numero = Number(valor);
    if (!Number.isFinite(numero)) {
      return 1;
    }
    return Math.max(1, Math.min(Math.floor(numero), Math.max(1, Number(maximo) || 1)));
  }

  function formatarTelefoneWhatsapp(telefone) {
    const digits = String(telefone || "").replace(/\D/g, "");
    if (!digits) {
      return "";
    }
    return digits.startsWith("55") ? digits : `55${digits}`;
  }

  function formatarDistancia(distanciaKm) {
    const numero = Number(distanciaKm);
    if (!Number.isFinite(numero)) {
      return "Distância indisponível";
    }
    if (numero < 1) {
      return `${Math.round(numero * 1000)} m de você`;
    }
    return `${numero.toLocaleString("pt-BR", { minimumFractionDigits: 1, maximumFractionDigits: 1 })} km de você`;
  }

  function atualizarStatusLocalizacao(mensagem, tipo = "neutro") {
    const status = document.getElementById("statusLocalizacao");
    if (!status) {
      return;
    }

    status.textContent = mensagem;
    status.className = "text-sm";
    if (tipo === "sucesso") {
      status.classList.add("text-green-700");
      return;
    }
    if (tipo === "erro") {
      status.classList.add("text-red-700");
      return;
    }
    status.classList.add("text-gray-600");
  }

  function obterPosicaoAtual() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error("Geolocalização não é suportada neste navegador."));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => resolve(position),
        (error) => reject(error),
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 300000 }
      );
    });
  }

  async function solicitarLocalizacao() {
    const botao = document.getElementById("btnUsarLocalizacao");
    try {
      if (botao) {
        botao.disabled = true;
        botao.textContent = "Localizando...";
      }

      atualizarStatusLocalizacao("Obtendo sua localização para ordenar os mercados mais próximos...");
      const position = await obterPosicaoAtual();
      localizacaoAtual = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        fonte: "gps",
      };
      atualizarStatusLocalizacao("Localização ativada via GPS. As ofertas priorizam os mercados mais próximos.", "sucesso");
      await carregarOfertas();
    } catch (error) {
      // Mantém coordenadas do perfil se já existiam
      if (!localizacaoAtual) {
        const mensagem = error?.code === 1
          ? "Permissão de localização negada. Você ainda pode usar o campo de referência manualmente."
          : "Não foi possível obter sua localização agora. Tente novamente em instantes.";
        atualizarStatusLocalizacao(mensagem, "erro");
      } else {
        const mensagem = error?.code === 1
          ? "Permissão de GPS negada. Usando localização do seu endereço cadastrado."
          : "GPS indisponível. Usando localização do seu endereço cadastrado.";
        atualizarStatusLocalizacao(mensagem, "sucesso");
      }
    } finally {
      if (botao) {
        botao.disabled = false;
        botao.textContent = "Usar minha localização";
      }
    }
  }

  function inicializarLocalizacaoComPerfil() {
    const usuario = window.AppCore.readStoredUser();
    if (usuario?.latitude != null && usuario?.longitude != null) {
      localizacaoAtual = {
        latitude: usuario.latitude,
        longitude: usuario.longitude,
        fonte: "perfil",
      };
      atualizarStatusLocalizacao(
        "Usando localização do seu endereço cadastrado. Para maior precisão, clique em \"Usar minha localização\".",
        "sucesso"
      );
      return true;
    }
    return false;
  }

  async function inicializarLocalizacao() {
    // 1. Usa coordenadas do perfil como fallback imediato
    const temPerfil = inicializarLocalizacaoComPerfil();

    if (!navigator.geolocation) {
      if (!temPerfil) {
        atualizarStatusLocalizacao("Seu navegador não suporta geolocalização. Use o campo de referência para priorizar ofertas.", "erro");
      }
      return;
    }

    // 2. Se permissão de GPS já está concedida, atualiza com coordenadas mais precisas
    if (!navigator.permissions?.query) {
      return;
    }

    try {
      const resultado = await navigator.permissions.query({ name: "geolocation" });
      if (resultado.state === "granted") {
        await solicitarLocalizacao();
      }
    } catch {
      // Ignora falhas do Permissions API e mantém coordenadas do perfil.
    }
  }

  function gerarMensagemWhatsapp(mercado) {
    const linhas = [
      `Olá, ${mercado.nome}.`,
      "Tenho interesse nestes produtos da DoaDoa:",
      "",
    ];

    mercado.itens.forEach((item) => {
      linhas.push(
        `- ${item.produtoNome} | qtd: ${item.quantidadeSelecionada} | unitário: ${window.AppCore.formatCurrency(item.preco)} | subtotal: ${window.AppCore.formatCurrency(item.subtotal)}`
      );
    });

    linhas.push("");
    linhas.push(`Total estimado: ${window.AppCore.formatCurrency(mercado.total)}`);
    linhas.push("Pode me enviar a chave Pix para pagamento antes da retirada ou no momento da entrega?");

    return linhas.join("\n");
  }

  function agruparCarrinhoPorMercado() {
    const grupos = new Map();

    Object.values(carrinho).forEach((itemCarrinho) => {
      const quantidadeSelecionada = Number(itemCarrinho?.quantidade || 0);
      if (!quantidadeSelecionada) {
        return;
      }

      const mercadoId = String(itemCarrinho.mercadoId || "sem-mercado");
      if (!grupos.has(mercadoId)) {
        grupos.set(mercadoId, {
          mercadoId,
          nome: itemCarrinho.mercadoNome || "Mercado",
          telefone: itemCarrinho.mercadoTelefone || "",
          endereco: itemCarrinho.mercadoEndereco || "Endereço não informado",
          itens: [],
          total: 0,
        });
      }

      const grupo = grupos.get(mercadoId);
      const precoUnitario = Number(itemCarrinho.preco);
      const subtotal = Number.isFinite(precoUnitario) && precoUnitario > 0
        ? precoUnitario * quantidadeSelecionada
        : 0;

      grupo.itens.push({
        produtoId: itemCarrinho.produtoId,
        produtoNome: itemCarrinho.produtoNome || "Produto",
        quantidadeSelecionada,
        preco: itemCarrinho.preco,
        subtotal,
      });
      grupo.total += subtotal;
    });

    return Array.from(grupos.values());
  }

  function construirPayloadPedido() {
    return {
      itens: Object.values(carrinho)
        .map((item) => ({
          produtoId: item.produtoId,
          quantidade: Number(item.quantidade || 0),
        }))
        .filter((item) => item.produtoId && item.quantidade > 0),
    };
  }

  function sincronizarCarrinhoComOfertas() {
    const ofertasPorProduto = new Map(ofertasAtuais.map((oferta) => [String(oferta.produtoId), oferta]));
    let alterou = false;

    Object.keys(carrinho).forEach((produtoId) => {
      const oferta = ofertasPorProduto.get(String(produtoId));
      if (!oferta) {
        delete carrinho[produtoId];
        alterou = true;
        return;
      }

      const maximo = Math.max(0, Number(oferta.quantidade) || 0);
      if (maximo <= 0) {
        delete carrinho[produtoId];
        alterou = true;
        return;
      }

      const quantidadeAtual = Number(carrinho[produtoId]?.quantidade || 0);
      const quantidadeAjustada = Math.min(quantidadeAtual, maximo);
      if (quantidadeAjustada <= 0) {
        delete carrinho[produtoId];
        alterou = true;
        return;
      }

      if (quantidadeAjustada !== quantidadeAtual) {
        carrinho[produtoId].quantidade = quantidadeAjustada;
        alterou = true;
      }
    });

    if (alterou) {
      persistirCarrinho();
    }
  }

  async function finalizarPedido() {
    const botao = document.getElementById("btnFinalizarPedido");
    const payload = construirPayloadPedido();
    if (!payload.itens.length) {
      alert("Adicione pelo menos um item ao carrinho antes de finalizar o pedido.");
      return;
    }

    try {
      if (botao) {
        botao.disabled = true;
        botao.textContent = "Finalizando...";
      }

      const resp = await fetch(`${pageApiBase}/pedidos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      let dados = null;
      try {
        dados = await resp.json();
      } catch {
        dados = null;
      }

      if (!resp.ok) {
        throw new Error(dados?.message || "Não foi possível finalizar o pedido.");
      }

      ultimoPedidoFinalizado = dados;
      carrinho = {};
      persistirCarrinho();
      atualizarResumoCarrinho();
      atualizarPainelPedidoFinalizado();
      await carregarOfertas();
    } catch (error) {
      alert(error?.message || "Erro ao finalizar o pedido.");
    } finally {
      if (botao) {
        botao.disabled = false;
        botao.textContent = "Finalizar pedido no sistema";
      }
    }
  }

  function atualizarResumoCarrinho() {
    const resumo = document.getElementById("resumoCarrinho");
    const estadoVazio = document.getElementById("estadoCarrinhoVazio");
    const totalItens = document.getElementById("carrinhoTotalItens");
    const totalValor = document.getElementById("carrinhoTotalValor");
    const mercadosEl = document.getElementById("carrinhoMercados");
    const grupos = agruparCarrinhoPorMercado();

    const quantidadeTotal = grupos.reduce(
      (acc, grupo) => acc + grupo.itens.reduce((subtotal, item) => subtotal + item.quantidadeSelecionada, 0),
      0
    );
    const valorTotal = grupos.reduce((acc, grupo) => acc + grupo.total, 0);

    if (!grupos.length) {
      resumo.classList.add("hidden");
      estadoVazio.classList.remove("hidden");
      mercadosEl.innerHTML = "";
      totalItens.textContent = "0";
      totalValor.textContent = "R$ 0,00";
      document.getElementById("btnFinalizarPedido")?.setAttribute("disabled", "disabled");
      return;
    }

    resumo.classList.remove("hidden");
    estadoVazio.classList.add("hidden");
    totalItens.textContent = String(quantidadeTotal);
    totalValor.textContent = window.AppCore.formatCurrency(valorTotal);
    document.getElementById("btnFinalizarPedido")?.removeAttribute("disabled");
    mercadosEl.innerHTML = grupos
      .map((grupo) => {
        const whatsappNumero = formatarTelefoneWhatsapp(grupo.telefone);
        const whatsappHref = whatsappNumero
          ? `https://wa.me/${whatsappNumero}?text=${encodeURIComponent(gerarMensagemWhatsapp(grupo))}`
          : "";

        return `
          <section class="rounded-xl border border-slate-200 bg-white px-4 py-4 shadow-sm">
            <div class="flex items-start justify-between gap-3">
              <div>
                <p class="text-base font-bold text-slate-900">${window.AppCore.escapeHtml(grupo.nome)}</p>
                <p class="text-sm text-slate-600 mt-1">${window.AppCore.escapeHtml(grupo.endereco)}</p>
                <p class="text-sm text-slate-600">WhatsApp: ${window.AppCore.escapeHtml(grupo.telefone || "Não informado")}</p>
              </div>
              <p class="text-base font-extrabold text-green-700">${window.AppCore.formatCurrency(grupo.total)}</p>
            </div>

            <div class="mt-3 space-y-2">
              ${grupo.itens
                .map(
                  (item) => `
                    <div class="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-700">
                      <p class="font-semibold text-slate-900">${window.AppCore.escapeHtml(item.produtoNome)}</p>
                      <p>Quantidade: ${item.quantidadeSelecionada}</p>
                      <p>Subtotal: ${window.AppCore.formatCurrency(item.subtotal)}</p>
                    </div>
                  `
                )
                .join("")}
            </div>

            ${whatsappHref
              ? `<a href="${whatsappHref}" target="_blank" rel="noopener noreferrer" class="primary-btn mt-4 w-full inline-flex items-center justify-center">Pedir chave Pix no WhatsApp</a>`
              : `<p class="mt-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-amber-800">Esse mercado não tem telefone cadastrado para gerar o link do WhatsApp.</p>`}
          </section>
        `;
      })
      .join("");
  }

  function alterarItemCarrinho(oferta, quantidade) {
    const produtoId = String(oferta.produtoId);
    const maximo = Math.max(1, Number(oferta.quantidade) || 1);
    const quantidadeNormalizada = normalizarQuantidade(quantidade, maximo);

    carrinho[produtoId] = {
      produtoId: oferta.produtoId,
      produtoNome: oferta.produtoNome,
      preco: oferta.preco,
      mercadoId: oferta.mercadoId,
      mercadoNome: oferta.mercadoNome,
      mercadoTelefone: oferta.mercadoTelefone,
      mercadoEndereco: oferta.mercadoEndereco,
      quantidade: quantidadeNormalizada,
    };

    persistirCarrinho();
    atualizarResumoCarrinho();
    renderizarOfertas();
  }

  function removerItemCarrinho(produtoId) {
    delete carrinho[String(produtoId)];
    persistirCarrinho();
    atualizarResumoCarrinho();
    renderizarOfertas();
  }

  function renderizarOfertas() {
    const estadoVazio = document.getElementById("estadoVazio");
    const lista = document.getElementById("listaOfertas");
    const total = document.getElementById("totalResultados");

    total.textContent = `${ofertasAtuais.length} oferta${ofertasAtuais.length === 1 ? "" : "s"}`;

    if (!ofertasAtuais.length) {
      estadoVazio.classList.remove("hidden");
      estadoVazio.textContent = "Nenhuma oferta encontrada com os filtros informados.";
      lista.classList.add("hidden");
      lista.innerHTML = "";
      return;
    }

    estadoVazio.classList.add("hidden");
    lista.classList.remove("hidden");

    lista.innerHTML = ofertasAtuais
      .map((o) => {
        const venc = o?.diasParaVencer == null
          ? "Validade não informada"
          : o.diasParaVencer < 0
            ? "Validade vencida"
            : `Vence em ${o.diasParaVencer} dia(s)`;
        const quantidadeSelecionada = obterQuantidadeSelecionada(o.produtoId);
        const maximo = Math.max(1, Number(o.quantidade) || 1);

        return `
          <article class="p-5 grid grid-cols-1 lg:grid-cols-5 gap-4">
            <div class="lg:col-span-3">
                <h3 class="text-lg font-extrabold text-gray-900">${window.AppCore.escapeHtml(o.produtoNome || "Produto sem nome")}</h3>
                <p class="text-sm text-gray-600 mt-1">Categoria: ${window.AppCore.escapeHtml(o.categoria || "-")}</p>
              <p class="text-sm text-gray-600">Quantidade disponível: ${o.quantidade ?? "-"}</p>
                <p class="text-sm text-green-700 font-bold">Preço unitário: ${window.AppCore.formatCurrency(o.preco)}</p>
              <p class="text-sm font-semibold ${o.diasParaVencer != null && o.diasParaVencer <= 2 ? "text-red-700" : "text-amber-700"}">${venc}</p>
            </div>

            <div class="lg:col-span-2 space-y-3">
              <div>
                <p class="text-sm text-gray-500">Mercado</p>
                  <p class="text-base font-bold text-gray-900">${window.AppCore.escapeHtml(o.mercadoNome || "Mercado")}</p>
                  <p class="text-sm text-gray-600 mt-1">${window.AppCore.escapeHtml(o.mercadoEndereco || "Endereço não informado")}</p>
                  <p class="text-sm text-gray-600">Contato: ${window.AppCore.escapeHtml(o.mercadoTelefone || "Não informado")}</p>
                ${o.distanciaKm != null
                  ? `<p class="text-sm font-bold text-blue-700 mt-1">${formatarDistancia(o.distanciaKm)}</p>`
                  : localizacaoAtual
                    ? `<p class="text-sm font-semibold text-amber-700 mt-1">Endereço do mercado não pôde ser validado para cálculo de distância.</p>`
                    : ""}
              </div>

              <div class="rounded-xl border border-slate-200 bg-slate-50 p-3">
                <label for="qtd-${o.produtoId}" class="block text-sm font-bold text-gray-800 mb-2">Quantidade para o carrinho</label>
                <div class="flex flex-col sm:flex-row gap-2">
                  <input id="qtd-${o.produtoId}" data-role="quantidade" data-produto-id="${o.produtoId}" type="number" min="1" max="${maximo}" value="${quantidadeSelecionada || 1}" class="w-full rounded-md px-3 py-2" />
                  <button data-role="adicionar-carrinho" data-produto-id="${o.produtoId}" class="primary-btn w-full sm:w-40" type="button">${quantidadeSelecionada ? "Atualizar" : "Adicionar"}</button>
                </div>
                ${quantidadeSelecionada
                  ? `<div class="mt-3 flex items-center justify-between gap-3 text-sm">
                      <span class="font-semibold text-slate-700">No carrinho: ${quantidadeSelecionada}</span>
                      <button data-role="remover-carrinho" data-produto-id="${o.produtoId}" type="button" class="text-red-700 font-bold">Remover</button>
                    </div>`
                  : ""}
              </div>
            </div>
          </article>
        `;
      })
      .join("");
  }

  async function carregarOfertas() {
    const busca = (document.getElementById("busca")?.value || "").trim();
    const tipoBusca = (document.getElementById("tipoBusca")?.value || "PRODUTO").trim();
    const referencia = (document.getElementById("referencia")?.value || "").trim();

    const params = new URLSearchParams();
    if (busca) params.set("busca", busca);
    if (tipoBusca) params.set("tipoBusca", tipoBusca);
    if (referencia) params.set("referencia", referencia);

    const estadoVazio = document.getElementById("estadoVazio");

    estadoVazio.classList.remove("hidden");
    estadoVazio.textContent = "Buscando ofertas...";
    document.getElementById("listaOfertas").classList.add("hidden");
    document.getElementById("listaOfertas").innerHTML = "";

    try {
      const query = params.toString();
      const endpoint = query
        ? `${pageApiBase}/produtos/ofertas?${query}`
        : `${pageApiBase}/produtos/ofertas`;
      const url = new URL(endpoint, window.location.origin);
      if (localizacaoAtual) {
        url.searchParams.set("latitude", String(localizacaoAtual.latitude));
        url.searchParams.set("longitude", String(localizacaoAtual.longitude));
      }

      const resp = await fetch(url.toString());
      if (!resp.ok) {
        throw new Error("Não foi possível carregar as ofertas.");
      }

      ofertasAtuais = await resp.json();
      sincronizarCarrinhoComOfertas();
      renderizarOfertas();
      atualizarResumoCarrinho();
    } catch (err) {
      document.getElementById("totalResultados").textContent = "0 ofertas";
      estadoVazio.classList.remove("hidden");
      estadoVazio.textContent = err?.message || "Erro ao carregar ofertas.";
    }
  }

  document.addEventListener("DOMContentLoaded", async () => {
    if (window.feather) {
      window.feather.replace();
    }

    const usuario = window.AppCore.readStoredUser();
    const tipo = String(usuario?.tipo || "").trim().toUpperCase();
    if (!usuario || tipo !== "CLIENTE") {
      alert("Faça login como cliente para acessar esta área.");
      window.location.replace("login.html");
      return;
    }

    const nomeEl = document.querySelector("[data-user-name]");
    if (nomeEl && usuario.nome) {
      nomeEl.textContent = usuario.nome;
    }

    const form = document.getElementById("filtroOfertas");
    form?.addEventListener("submit", async (event) => {
      event.preventDefault();
      await carregarOfertas();
    });

    document.getElementById("listaOfertas")?.addEventListener("click", (event) => {
      const target = event.target instanceof Element ? event.target.closest("[data-role]") : null;
      if (!target) {
        return;
      }

      const produtoId = target.getAttribute("data-produto-id");
      const oferta = ofertasAtuais.find((item) => String(item.produtoId) === String(produtoId));
      if (!oferta) {
        return;
      }

      if (target.getAttribute("data-role") === "adicionar-carrinho") {
        const input = document.getElementById(`qtd-${produtoId}`);
        alterarItemCarrinho(oferta, input?.value);
      }

      if (target.getAttribute("data-role") === "remover-carrinho") {
        removerItemCarrinho(produtoId);
      }
    });

    document.getElementById("btnLimparCarrinho")?.addEventListener("click", () => {
      carrinho = {};
      persistirCarrinho();
      atualizarResumoCarrinho();
      renderizarOfertas();
    });

    document.getElementById("btnFinalizarPedido")?.addEventListener("click", async () => {
      await finalizarPedido();
    });

    document.getElementById("btnUsarLocalizacao")?.addEventListener("click", async () => {
      await solicitarLocalizacao();
    });

    atualizarResumoCarrinho();
  atualizarPainelPedidoFinalizado();
    await inicializarLocalizacao();

    await carregarOfertas();
  });
})();
