# Casos de Uso por Tela – Sistema de Gestão e Doação de Alimentos

> Catálogo de ações do sistema (pensado por “telinha”).  
> Papéis: **Funcionário do Supermercado**, **ONG/Instituição**, **Administrador do Sistema**.  
> Cada UC traz: **Descrição**, **Pré**, **Pós**, **Passos**.

---



<img width="538" height="2372" alt="image" src="https://github.com/user-attachments/assets/41ccdccd-3f6e-453c-be37-848c29c7468f" />




## Sumário
- [A. Acesso e Conta](#a-acesso-e-conta)
- [B. Produtos / Estoque (Funcionário)](#b-produtos--estoque-funcionário)
- [C. Vencimento e Promoção (Funcionário)](#c-vencimento-e-promoção-funcionário)
- [D. Doações (Funcionário ↔ ONG)](#d-doações-funcionário--ong)
- [E. ONGs (Funcionário)](#e-ongs-funcionário)
- [F. Relatórios (Funcionário/Admin)](#f-relatórios-funcionárioadmin)
- [G. Conta e Perfil (ONG)](#g-conta-e-perfil-ong)
- [H. Administração (Admin)](#h-administração-admin)
- [I. Integrações e Notificações](#i-integrações-e-notificações)

---

## A. Acesso e Conta

### UC-A01 — **Entrar no sistema (Login)**
- **Descrição:** autenticar usuário para acessar as telas.
- **Pré:** usuário cadastrado/ativo.
- **Pós:** sessão iniciada; redireciona ao painel conforme perfil.
- **Passos:** informar e-mail/senha → enviar → sistema valida e cria sessão.

### UC-A02 — **Sair do sistema (Logout)**
- **Descrição:** encerrar sessão atual.
- **Pré:** sessão ativa.
- **Pós:** sessão encerrada; volta ao login.
- **Passos:** clicar **Sair** → confirmar → sessão invalida.

### UC-A03 — **Recuperar senha**
- **Descrição:** solicitar link de redefinição de senha.
- **Pré:** e-mail válido cadastrado.
- **Pós:** link enviado; token registrado.
- **Passos:** “Esqueci minha senha” → informar e-mail → sistema envia link.

### UC-A04 — **Primeiro acesso / Definir nova senha**
- **Descrição:** criar senha pelo link recebido.
- **Pré:** token válido.
- **Pós:** senha definida; usuário pode logar.
- **Passos:** abrir link → definir senha → confirmar.

> Observação: **cadastro de contas** no supermercado e **conta da ONG** geralmente é criado por **Admin** (UC-H02/H03). Opcionalmente, manter:
### UC-A05 — **Solicitar cadastro de ONG**
- **Descrição:** ONG preenche formulário público para solicitar acesso.
- **Pré:** formulário disponível.
- **Pós:** solicitação registrada (pendente de aprovação do Admin).
- **Passos:** preencher dados → enviar → sistema cria “pedido de cadastro”.

---

## B. Produtos / Estoque (Funcionário)

### UC-P01 — **Cadastrar produto**
- **Descrição:** inserir item no estoque (nome, validade, qtd, preço, lote).
- **Pré:** funcionário autenticado.
- **Pós:** produto salvo (*Em estoque*), auditado.
- **Passos:** abrir **Cadastrar Produto** → preencher → validar → salvar.

### UC-P02 — **Editar produto**
- **Descrição:** alterar dados (validade, preço, quantidade).
- **Pré:** produto existente; permissão de edição.
- **Pós:** produto atualizado; auditoria criada.
- **Passos:** abrir item → editar → salvar.

### UC-P03 — **Excluir produto**
- **Descrição:** remover produto (sem movimentos/pendências).
- **Pré:** não vinculado a pedido de doação/retirada.
- **Pós:** item removido; auditoria registrada.
- **Passos:** abrir item → **Excluir** → confirmar.

### UC-P04 — **Consultar estoque**
- **Descrição:** listar/filtrar produtos (nome, categoria, status).
- **Pré:** dados cadastrados.
- **Pós:** lista exibida com paginação/ordenar.
- **Passos:** abrir **Estoque** → aplicar filtros → ver detalhes.

### UC-P05 — **Importar produtos (CSV)**
- **Descrição:** subir planilha para carga em lote.
- **Pré:** arquivo CSV no padrão.
- **Pós:** itens importados; relatório de erros.
- **Passos:** selecionar arquivo → validar prévia → confirmar importação.

### UC-P06 — **Exportar estoque (CSV)**
- **Descrição:** baixar planilha do estoque filtrado.
- **Pré:** lista carregada.
- **Pós:** arquivo gerado para download.
- **Passos:** clicar **Exportar** → sistema gera CSV.

---

## C. Vencimento e Promoção (Funcionário)

### UC-V01 — **Ver “Próximos ao Vencimento”**
- **Descrição:** visão priorizada por dias para vencer.
- **Pré:** limiar configurado (ex.: ≤ 5 dias).
- **Pós:** lista exibida com criticidade.
- **Passos:** abrir **Vencimento** → filtros → ordenar.

### UC-V02 — **Destinar item para doação**
- **Descrição:** marcar itens para fluxo de doação.
- **Pré:** item elegível; disponibilidade de ONGs.
- **Pós:** item muda para *Destinado à doação* (pendente de pedido).
- **Passos:** selecionar item → **Destinar** → confirmar.

### UC-V03 — **Aplicar desconto promocional**
- **Descrição:** definir preço promocional para itens próximos ao vencimento.
- **Pré:** política/percentual permitido.
- **Pós:** preço atualizado; etiqueta/flag de promoção.
- **Passos:** escolher item → **Aplicar desconto** → informar %/preço → salvar.

### UC-V04 — **Descartar item com justificativa**
- **Descrição:** registrar descarte (quebra, contaminação etc.).
- **Pré:** motivo de descarte válido.
- **Pós:** item sai do estoque; registro em relatório de perdas.
- **Passos:** selecionar item → **Descartar** → motivo → confirmar.

---

## D. Doações (Funcionário ↔ ONG)

### UC-D01 — **Criar pedido de doação**
- **Descrição:** agrupar itens destinados e criar pedido para uma ONG.
- **Pré:** itens marcados (UC-V02); ONG ativa.
- **Pós:** **Pedido** criado (*Aguardando confirmação*).
- **Passos:** **Nova Doação** → escolher itens → escolher ONG → definir janela → salvar.

### UC-D02 — **Enviar notificação à ONG**
- **Descrição:** disparo automático ao salvar pedido (pode reenviar).
- **Pré:** pedido criado; canal configurado.
- **Pós:** notificação enviada e registrada.
- **Passos:** sistema envia → registra status (ver UC-I03).

### UC-D03 — **Acompanhar status do pedido**
- **Descrição:** acompanhar confirmação, agendamento, retirada.
- **Pré:** pedido criado.
- **Pós:** timeline do pedido atualizada.
- **Passos:** abrir **Doações** → ver status e histórico.

### UC-D04 — **Confirmar/agendar retirada (ONG)**
- **Descrição:** ONG confirma interesse e escolhe data/horário.
- **Pré:** notificação recebida; janelas disponíveis.
- **Pós:** pedido *Confirmado* com **Agendamento**.
- **Passos:** abrir link/portal → escolher janela → confirmar.

### UC-D05 — **Reagendar/Cancelar retirada (ONG)**
- **Descrição:** alterar ou cancelar compromisso.
- **Pré:** pedido confirmado; política de prazo.
- **Pós:** agendamento atualizado / pedido cancelado (com motivo).
- **Passos:** abrir pedido → reagendar/cancelar → confirmar.

### UC-D06 — **Registrar retirada (Supermercado)**
- **Descrição:** dar baixa no ato da coleta.
- **Pré:** pedido confirmado; ONG presente.
- **Pós:** itens *Doado*; comprovante gerado; relatórios atualizados.
- **Passos:** abrir pedido do dia → conferir → registrar retirada → emitir comprovante.

---

## E. ONGs (Funcionário)

### UC-O01 — **Cadastrar ONG**
- **Descrição:** inclusão de nova instituição (CNPJ, contato, endereço, raio).
- **Pré:** funcionário com permissão.
- **Pós:** ONG ativa; pode receber pedidos.
- **Passos:** **ONGs** → **Nova ONG** → preencher → salvar.

### UC-O02 — **Editar/Desativar ONG**
- **Descrição:** atualizar dados ou suspender acesso.
- **Pré:** ONG existente; sem pendências críticas para desativar.
- **Pós:** dados atualizados / status “Suspensa”.
- **Passos:** abrir ONG → editar/desativar → salvar.

### UC-O03 — **Consultar ONGs**
- **Descrição:** buscar por nome, raio, categoria de atendimento, disponibilidade.
- **Pré:** cadastro existente.
- **Pós:** lista exibida; acesso a detalhes e histórico.
- **Passos:** abrir **ONGs** → filtrar → visualizar.

---

## F. Relatórios (Funcionário/Admin)

### UC-R01 — **Relatório de doações**
- **Descrição:** itens/quantidade/valor estimado doado por período.
- **Pré:** histórico de pedidos.
- **Pós:** relatório exibido/exportado (CSV/PDF).
- **Passos:** **Relatórios** → “Doações” → filtros → gerar.

### UC-R02 — **Relatório de desperdício evitado**
- **Descrição:** cálculo de %/kg/valor economizado por doação x descarte.
- **Pré:** dados de estoque + doações + descartes.
- **Pós:** métricas exibidas/exportadas.
- **Passos:** **Relatórios** → “Impacto” → filtros → gerar.

### UC-R03 — **Relatório de desempenho de ONGs**
- **Descrição:** volume por ONG, tempo de resposta, comparecimento.
- **Pré:** histórico de doações.
- **Pós:** tabela/gráficos; export.
- **Passos:** **Relatórios** → “ONGs” → filtros → gerar.

---

## G. Conta e Perfil (ONG)

### UC-G01 — **Atualizar dados da ONG**
- **Descrição:** a própria ONG edita contato, endereço, docs.
- **Pré:** ONG autenticada.
- **Pós:** dados atualizados (pendências sinalizadas).
- **Passos:** **Meu Perfil** → editar → salvar.

### UC-G02 — **Ver histórico de doações**
- **Descrição:** listar doações recebidas (com comprovantes).
- **Pré:** ONG autenticada.
- **Pós:** lista exibida; pode exportar.
- **Passos:** **Histórico** → filtros → visualizar/exportar.

### UC-G03 — **Mensagens e notificações**
- **Descrição:** caixa com mensagens, reenvio de notificação, lembretes.
- **Pré:** doações e eventos gerados.
- **Pós:** status lidos/reenviados.
- **Passos:** **Notificações** → abrir mensagem → acionar reenvio (se permitido).

---

## H. Administração (Admin)

### UC-H01 — **Painel do Admin**
- **Descrição:** visão geral (usuários, lojas, ONGs, falhas).
- **Pré:** perfil Admin.
- **Pós:** indicadores exibidos.
- **Passos:** **Admin** → painel.

### UC-H02 — **Gerenciar usuários**
- **Descrição:** criar/editar/remover usuários e **perfis** (funcionário, ONG, admin).
- **Pré:** admin autenticado.
- **Pós:** usuários/permissões atualizados; convites/reset enviados.
- **Passos:** **Usuários** → novo/editar/remover → salvar.

### UC-H03 — **Gerenciar filiais/lojas**
- **Descrição:** cadastrar lojas e atribuir usuários.
- **Pré:** admin autenticado.
- **Pós:** lojas configuradas; escopos aplicados.
- **Passos:** **Lojas** → nova loja → salvar → vincular usuários.

### UC-H04 — **Configurar parâmetros do sistema**
- **Descrição:** limiar de vencimento, templates, políticas, timezone.
- **Pré:** admin autenticado.
- **Pós:** config publicada (versionada).
- **Passos:** **Configurações** → editar → validar → publicar.

### UC-H05 — **Auditar logs**
- **Descrição:** consultar quem fez o quê e quando.
- **Pré:** logs gerados.
- **Pós:** auditoria exibida/exportável.
- **Passos:** **Logs** → filtros → visualizar/exportar.

---

## I. Integrações e Notificações

### UC-I01 — **Configurar provedor de e-mail/WhatsApp**
- **Descrição:** definir chaves/token e remetentes.
- **Pré:** admin autenticado.
- **Pós:** provedor ativo/testado.
- **Passos:** **Integrações** → selecionar provedor → inserir chaves → **Testar** → salvar.

### UC-I02 — **Enviar notificação automática**
- **Descrição:** disparo por evento (pedido criado, lembrete, urgência).
- **Pré:** evento gerado; provedor configurado.
- **Pós:** notificação enviada; ID de envio registrado.
- **Passos:** sistema detecta evento → monta payload → envia → registra.

### UC-I03 — **Registrar status de entrega**
- **Descrição:** armazenar entregue/falhou/reenviado (callback/polling).
- **Pré:** notificação enviada.
- **Pós:** status atualizado; retentativa se falhou.
- **Passos:** receber retorno → atualizar status → reagendar se falha.

### UC-I04 — **Monitorar fila de notificações**
- **Descrição:** dashboard de envios/erros; reenvio manual.
- **Pré:** módulo de notificações.
- **Pós:** itens reprocessados; KPIs exibidos.
- **Passos:** **Fila** → filtrar → reenviar/descartar → ver métricas.

---

