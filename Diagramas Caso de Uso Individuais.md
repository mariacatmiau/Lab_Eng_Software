# 📑 Casos de Uso – Sistema de Gestão e Doação de Alimentos

## Cadastrar Produto
**Objetivo:** registrar um novo item no estoque com validade e quantidade.  
**Atores:** Funcionário do Supermercado.  
**Pré:** funcionário autenticado.  
**Pós:** produto salvo (status *Em estoque*) e auditado.  

<img width="1575" height="327" alt="image" src="https://github.com/user-attachments/assets/845d3f6f-995d-47e2-918f-650c2957e63c" />

---

## Atualizar Produto

**Objetivo:** alterar dados de um produto existente (validade, preço, quantidade). 
**Atores:** Funcionário do Supermercado. 
**Pré:** produto existe; permissão de edição. 
**Pós:** produto atualizado e auditado. 


<img width="1430" height="309" alt="image" src="https://github.com/user-attachments/assets/31149d90-5d7a-4f4a-9c7d-b1469053a402" />

---

## Consultar Produtos Próximos ao Vencimento

**Objetivo:** listar itens ordenados pela urgência de vencimento, com filtros.
**Atores:** Funcionário do Supermercado.
**Pré:** produtos com datas de validade cadastradas.
**Pós:** lista exibida; usuário pode agir (doar, descontar).


<img width="1414" height="297" alt="image" src="https://github.com/user-attachments/assets/d73b5520-8f7a-437f-a978-92144f6ee425" />

---

## Selecionar ONG para Doação

**Objetivo:** escolher uma ONG para destinar itens selecionados.
**Atores:** Funcionário do Supermercado.
**Pré:** itens elegíveis; ONGs cadastradas.
**Pós:** pedido de doação criado; notificação disparada.


<img width="1815" height="433" alt="image" src="https://github.com/user-attachments/assets/657fe1f6-e4a1-41af-a2b9-e7a84542ed94" />

---

## Registrar Retirada da Doação

**Objetivo:** confirmar a coleta pela ONG e dar baixa nos itens.
**Atores:** Funcionário do Supermercado (principal), ONG/Instituição (colabora).
**Pré:** pedido de doação confirmado.
**Pós:** status dos itens = Doado; comprovante gerado.


<img width="1566" height="649" alt="image" src="https://github.com/user-attachments/assets/eb7f3c7f-1699-4cbf-9337-46d161188594" />

---

## Gerenciar ONGs

**Objetivo:** cadastrar/editar/remover ONGs parceiras.
**Atores:** Funcionário do Supermercado.
**Pré:** funcionário autenticado.
**Pós:** cadastro de ONGs atualizado.

<img width="1669" height="359" alt="image" src="https://github.com/user-attachments/assets/e8ad1282-f120-44f8-b266-d94cd93581c2" />

---

## Emitir Relatórios

**Objetivo:** gerar relatórios de impacto (kg doados, ONGs atendidas etc.).
**Atores:** Funcionário do Supermercado.
**Pré:** dados existentes.
**Pós:** relatório exibido/exportado.


<img width="1235" height="263" alt="image" src="https://github.com/user-attachments/assets/3011e2eb-6bdf-4449-957d-309a304c9dd5" />

---

## Receber Notificação de Doação

**Objetivo:** ONG receber aviso de nova doação.
**Atores:** ONG/Instituição (principal), Sistema de Notificação (apoio).
**Pré:** pedido de doação criado.
**Pós:** ONG informada.


<img width="1275" height="559" alt="image" src="https://github.com/user-attachments/assets/76f419a0-7f2c-4ea5-915d-86ed2c633013" />

---

## Confirmar Retirada

**Objetivo:** ONG confirmar interesse e agendar coleta.
**Atores:** ONG/Instituição.
**Pré:** ONG recebeu notificação.
**Pós:** pedido confirmado com data/horário.

<img width="1182" height="316" alt="image" src="https://github.com/user-attachments/assets/9a3d3d21-d646-4bc5-99f9-e87262d7825a" />

---

## Consultar Histórico de Doações

**Objetivo:** ONG ver doações anteriores e comprovantes.
**Atores:** ONG/Instituição.
**Pré:** ONG autenticada.
**Pós:** histórico exibido/exportável.


<img width="1287" height="369" alt="image" src="https://github.com/user-attachments/assets/062119ca-fcce-4ec1-9872-d07d6ade3afa" />

---

## Gerenciar Usuários

**Objetivo:** CRUD de usuários e perfis (funcionário, ONG, admin).
**Atores:** Administrador do Sistema.
**Pré:** admin autenticado.
**Pós:** usuários/permissões atualizados.


<img width="1387" height="313" alt="image" src="https://github.com/user-attachments/assets/ec7888b0-ac4c-4346-919e-3f3d1acfe744" />

---

## Configurar Sistema

**Objetivo:** ajustar parâmetros (dias de alerta, integrações, templates).
**Atores:** Administrador do Sistema.
**Pré:** admin autenticado.
**Pós:** configurações salvas e ativas.


















