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
9. Tecnologias Utilizadas  
10. Estrutura do Projeto  
11. Contribuição  
12. Licença  

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


