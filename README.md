# Sistema de Gestão e Doação de Alimentos

## Equipe
- Felipe Carvalho – RA 10409804  
- Giulia Araki – RA 10408954  
- Maria Gabriela de Barros – RA 10409037  
- Raphaela Polonis - RA 10408843  
- Samuel Zheng - RA 10395781

# 🍎 DoaDoa — Sistema de Doação de Alimentos
**Projeto de TCC — Ciência da Computação**

Sistema web que conecta **estabelecimentos doadores de alimentos** com **ONGs**, reduzindo o desperdício e facilitando o processo de coleta, gerenciamento e doações.

---

# 📘 Sumário
1. Descrição Geral  
2. Objetivos do Projeto  
3. Arquitetura do Sistema  
4. Casos de Uso  
5. Diagramas de Sequência  
6. API - Documentação dos Endpoints  
7. Como Rodar o Backend  
8. Como Rodar o Frontend  
9. Documentação dos Testes
10. Tecnologias Utilizadas  
11. Estrutura do Projeto  
12. Contribuição  
13. Licença  

---

# 📌 Descrição Geral
O **DoaDoa** é um sistema desenvolvido para facilitar a doação de alimentos prestes a vencer.

Conecta:
- Funcionários de estabelecimentos comerciais  
- ONGs que recebem alimentos  

---

# 🎯 Objetivos do Projeto
- Reduzir o desperdício de alimentos  
- Facilitar doações  
- Registrar produtos e doações  
- Aumentar impacto social  

---

# 🏗 Arquitetura do Sistema
```
Frontend (HTML, CSS, JS)
   → REST API (JSON)
Backend Spring Boot
   → H2
```

---

# 🧩 Casos de Uso

## 1) Cadastro de Funcionário
Funcionário cria conta para cadastrar produtos.

## 2) Cadastro de ONG
ONG se registra no sistema.

## 3) Login
Autenticação de usuários.

## 4) Cadastro de Produto
Funcionário registra alimentos disponíveis.

## 5) Criar Doação
Funcionário doa produto a uma ONG.

## 6) Aceitar Doação
ONG aceita a doação.

## 7) Recusar Doação
ONG recusa.

## 8) Confirmar Retirada
ONG confirma retirada física.

---

# 🔄 Diagramas de Sequência

## Login
```
Usuário → Frontend → Backend → DB → Backend → Frontend → Usuário
```

## Criar Doação
```
Funcionário → Frontend → Backend → Repositórios → Backend → Frontend
```

---

# 🔌 API — Documentação dos Endpoints

## Usuários
| Método | Rota | Ação |
|-------|------|------|
| POST | /api/usuarios/register | Registrar |
| POST | /api/usuarios/login | Login |
| GET | /api/usuarios | Listar |

## Produtos
| Método | Rota | Ação |
|-------|------|------|
| GET | /api/produtos | Listar todos |
| POST | /api/produtos | Criar produto |
| GET | /api/produtos/disponiveis | Disponíveis |
| GET | /api/produtos/por-usuario/{id} | Por funcionário |
| PUT | /api/produtos/{id}/indisponivel | Marcar indisponível |

## Doações
| Método | Rota | Ação |
|-------|------|------|
| POST | /api/doacoes | Criar doação |
| GET | /api/doacoes | Listar |
| GET | /api/doacoes/por-ong/{id} | Por ONG |
| PUT | /api/doacoes/{id}/aceitar | Aceitar |
| PUT | /api/doacoes/{id}/recusar | Recusar |
| PUT | /api/doacoes/{id}/retirada | Retirada |

---

# 🚀 Como Rodar o Backend

Requisitos:
- Java 17  
- Maven  

Comandos:
```
mvn clean install
mvn spring-boot:run
```

---

# 🌐 Como Rodar o Frontend
Abrir o arquivo **login.html** ou usar:

```
npx live-server
```

# 📄 Documentação dos Casos de Teste — DoaDoa

Esta documentação descreve formalmente os **casos de teste unitários** implementados no backend do sistema **DoaDoa**, organizados por módulo, incluindo objetivo, entradas, saídas esperadas e critérios de aceitação.

> **Nota:** Casos referentes ao `FuncionarioController` foram removidos conforme solicitado.

---

## 🧪 1. Introdução

Os testes têm como objetivo validar o comportamento correto das regras de negócio e endpoints REST.  
As tecnologias utilizadas foram:

- **JUnit 5**
- **Mockito**
- **MockMvc**
- **Spring Boot Test**
- **H2 Database (modo memória)**

---

## 🧩 2. Casos de Teste por Módulo

---

## 🎯 2.1. DoacaoService

### 🧪 CT-DS01 — Criar doação com sucesso
**Objetivo:** Garantir criação de uma doação válida.  
**Entrada:** produtoId, ongId, criadoPorId, quantidade.  
**Pré-condições:** Produto, ONG e criador existem.  
**Resultado esperado:**
- Doação criada com status **PENDENTE**.
- Produto marcado como indisponível.

---

### 🧪 CT-DS02 — Falha ao criar doação (produto inexistente)
**Entrada:** produtoId inválido  
**Resultado esperado:** `404 NOT_FOUND`

---

### 🧪 CT-DS03 — Aceitar doação
**Objetivo:** Atualizar status para ACEITA  
**Resultado esperado:** Status alterado para **ACEITA**

---

### 🧪 CT-DS04 — Recusar doação
**Objetivo:** Atualizar status para RECUSADA  
**Resultado esperado:** Status **RECUSADA**

---

### 🧪 CT-DS05 — Confirmar retirada
**Objetivo:** Atualizar status para RETIRADA  
**Resultado esperado:** Status **RETIRADA**

---

## 🎯 2.2. ProdutoService

### 🧪 CT-PS01 — Criar produto
**Objetivo:** Garantir criação correta do produto  
**Entrada:** nome, categoria, validade, quantidade, usuarioId  
**Resultado esperado:** Produto criado com `disponivel = true`

---

### 🧪 CT-PS02 — Falha ao criar produto (usuário inexistente)
**Entrada:** usuarioId inválido  
**Resultado esperado:** `404 NOT_FOUND`

---

### 🧪 CT-PS03 — Listar produtos disponíveis
**Resultado esperado:** Apenas itens com `disponivel = true`

---

## 🎯 2.3. UsuarioService

### 🧪 CT-US01 — Cadastrar usuário
**Pré-condição:** email não registrado  
**Resultado esperado:** Usuário salvo com sucesso

---

### 🧪 CT-US02 — Falhar cadastro (email duplicado)
**Resultado esperado:** Lançamento de `IllegalArgumentException`

---

## 🎯 2.4. Controllers (MockMvc)

## 🧪 CT-DC01 — Criar Doação (POST /api/doacoes)
**Resultado esperado:** `200 OK` + JSON da doação

---

### 🧪 CT-DC02 — Aceitar Doação (PUT /api/doacoes/{id}/aceitar)
**Resultado esperado:** `200 OK`

---

### 🧪 CT-PC01 — Criar Produto (POST /api/produtos)
**Resultado esperado:** `200 OK`

---

### 🧪 CT-PC02 — Listar produtos disponíveis
**Resultado esperado:** `200 OK` + lista de produtos

---

### 🧪 CT-UC01 — Login com sucesso
**Resultado esperado:** `200 OK`

---

### 🧪 CT-UC02 — Login falha
**Resultado esperado:** `401 UNAUTHORIZED`

---

### 🧪 CT-OC01 — Listar ONGs
**Resultado esperado:** `200 OK`

---

## 📊 3. Matriz de Cobertura

| Módulo | Métodos cobertos | Cobertura |
|--------|------------------|------------|
| DoacaoService | criar, aceitar, recusar, confirmarRetirada | 100% |
| ProdutoService | criar, listarDisponiveis, listar | 100% |
| UsuarioService | cadastrar, buscarPorEmail | 100% |
| Controllers | principais GET/POST/PUT | 90% |

---

## 📌 4. Conclusão

Os testes unitários garantem a integridade do sistema e cobrem os principais fluxos críticos relacionados às regras de negócio e API REST.  
A cobertura obtida atende aos requisitos acadêmicos e garante maior confiabilidade ao sistema.

---




---

# 🧰 Tecnologias Utilizadas
- Java 17  
- Spring Boot 3.5.5  
- H2  
- HTML, CSS, JS  

---

# 📦 Estrutura do Projeto
```
src/
 ├── main/java/com/tcc/desperdicio_alimentos/
 ├── test/java/com/tcc/desperdicio_alimentos/
 ├── main/resources/static
```

---

# 🤝 Contribuição
PRs são bem-vindos.

---

# 📜 Licença
Uso educacional.
)  
- **CI/CD:** GitHub Actions  

---


