# Diagrama de Caso de Uso – Visão Geral

<img width="808" height="1554" alt="image" src="https://github.com/user-attachments/assets/4382abc4-e504-4e42-b3f0-893a67dfbe3c" />


# Funcionário do Supermercado

@startuml
actor "Funcionário do Supermercado" as Func

rectangle Sistema {
  usecase "Cadastrar Produto" as UC1
  usecase "Atualizar Produto" as UC2
  usecase "Consultar Produtos Próximos ao Vencimento" as UC3
  usecase "Selecionar ONG para Doação" as UC4
  usecase "Registrar Retirada da Doação" as UC5
  usecase "Gerenciar ONGs" as UC6
  usecase "Emitir Relatórios" as UC7
}

Func --> UC1
Func --> UC2
Func --> UC3
Func --> UC4
Func --> UC5
Func --> UC6
Func --> UC7
@enduml


# Administrador do Sistema

@startuml
actor "Administrador do Sistema" as Admin

rectangle Sistema {
  usecase "Gerenciar Usuários" as UC12
  usecase "Configurar Sistema" as UC13
  usecase "Auditar Logs" as UC14
}

Admin --> UC12
Admin --> UC13
Admin --> UC14
@enduml


# Sistema de Notificação

@startuml
actor "Sistema de Notificação" as Notif

rectangle Sistema {
  usecase "Enviar Notificação Automática" as UC15
  usecase "Registrar Status de Entrega" as UC16
}

Notif --> UC15
Notif --> UC16
@enduml


# Diagrama de Classes (versão simplificada)

@startuml
class Produto {
  - id: int
  - nome: string
  - categoria: string
  - validade: date
  - quantidade: int
  - preco: float
}

class Supermercado {
  - id: int
  - nome: string
  - endereco: string
}

class ONG {
  - id: int
  - nome: string
  - contato: string
}

class Notificacao {
  - id: int
  - data: date
  - status: string
}

class Usuario {
  - id: int
  - login: string
  - senha: string
  - perfil: string
}

Supermercado "1" --> "*" Produto
Produto "1" --> "0..1" Notificacao
Supermercado "1" --> "*" ONG : doa para >
ONG "1" --> "*" Notificacao
Usuario "1" --> "*" Supermercado
@enduml

# Arquitetura (visão em camadas)

@startuml
rectangle "Interface (Front-end)" {
  [React.js]  
}

rectangle "Lógica (Back-end)" {
  [Node.js + Express]
}

rectangle "Banco de Dados" {
  [PostgreSQL]
}

rectangle "Integrações" {
  [API Notificações]
}

rectangle "Infraestrutura" {
  [AWS EC2] 
  [AWS RDS]
}

[React.js] --> [Node.js + Express]
[Node.js + Express] --> [PostgreSQL]
[Node.js + Express] --> [API Notificações]
[Node.js + Express] --> [AWS EC2]
[PostgreSQL] --> [AWS RDS]
@enduml

