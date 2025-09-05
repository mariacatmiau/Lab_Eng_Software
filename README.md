# Sistema de Gestão e Doação de Alimentos

## Descrição
Este projeto foi desenvolvido na disciplina **Laboratório de Engenharia de Software (2025)** da Universidade Presbiteriana Mackenzie.  
O sistema tem como objetivo **reduzir o desperdício de alimentos em supermercados**, permitindo o **monitoramento de produtos próximos ao vencimento**, envio de **notificações para ONGs** e o **registro de doações**.

---

## Funcionalidades Principais
- Cadastro e gestão de produtos (nome, validade, estoque, preço).  
- Listagem de produtos próximos ao vencimento.  
- Seleção de ONGs parceiras para doação.  
- Notificações automáticas às ONGs.  
- Registro e acompanhamento das retiradas.  
- Relatórios de impacto social (quantidade doada, ONGs beneficiadas).  

---

## Tecnologias Utilizadas
- **Front-end:** React.js  
- **Back-end:** Node.js + Express  
- **Banco de Dados:** PostgreSQL  
- **Integração/API:** Notificações (e-mail/WhatsApp)  
- **Nuvem:** AWS (EC2 + RDS)  
- **CI/CD:** GitHub Actions  

---

## Como Executar o Projeto
### Pré-requisitos
- Node.js 18+  
- PostgreSQL 15+  
- Conta AWS configurada  

### Passos
```bash
# Clonar o repositório
git clone https://github.com/seuusuario/seuprojeto.git

# Entrar na pasta do projeto
cd seuprojeto

# Instalar dependências
npm install

# Rodar aplicação
npm start
