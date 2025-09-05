# 📑 Casos de Uso – Sistema de Gestão e Doação de Alimentos

Este documento descreve os principais casos de uso do sistema.  
Cada caso de uso inclui: **objetivo, atores, pré-condições, pós-condições e fluxos principais/alternativos**.

---

## UC-01 – Cadastrar Produto
**Atores:** Funcionário do Supermercado  
**Objetivo:** Registrar um novo produto no estoque.  
**Pré-condições:** Funcionário autenticado.  
**Pós-condições:** Produto cadastrado e disponível para monitoramento.  
**Fluxo Principal:**  
1. Funcionário acessa a opção “Cadastrar Produto”.  
2. Preenche nome, categoria, quantidade, validade e preço.  
3. O sistema valida os dados.  
4. O sistema salva e confirma o cadastro.  
**Fluxos Alternativos:** Dados inválidos → mensagem de erro.

---

## UC-02 – Selecionar ONG para Doação
**Atores:** Funcionário do Supermercado  
**Objetivo:** Destinar produtos próximos ao vencimento para uma ONG.  
**Pré-condições:** Produtos cadastrados no sistema.  
**Pós-condições:** Pedido de doação registrado e ONG notificada.  
**Fluxo Principal:**  
1. Funcionário abre lista de produtos próximos ao vencimento.  
2. Seleciona itens.  
3. Escolhe ONG cadastrada.  
4. O sistema registra a doação e envia notificação.  

---

## UC-03 – Confirmar Retirada
**Atores:** ONG  
**Objetivo:** Confirmar e agendar a retirada da doação.  
**Pré-condições:** ONG notificada.  
**Pós-condições:** Pedido confirmado com data e horário definidos.  
**Fluxo Principal:**  
1. ONG acessa notificação.  
2. Informa data, horário e responsável pela retirada.  
3. O sistema confirma o agendamento.  

---

## UC-04 – Registrar Retirada
**Atores:** Funcionário, ONG  
**Objetivo:** Confirmar a entrega física dos alimentos.  
**Pré-condições:** Pedido confirmado.  
**Pós-condições:** Status do produto atualizado para “Doado”.  
**Fluxo Principal:**  
1. ONG comparece ao supermercado.  
2. Funcionário confere e registra a retirada.  
3. Sistema gera comprovante.  

---

*(Outros casos de uso podem ser detalhados conforme o desenvolvimento avança.)*
