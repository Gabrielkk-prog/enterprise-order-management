# Enterprise Order Management

Sistema de gerenciamento de pedidos desenvolvido com **Java 21 e Spring Boot**, simulando uma aplicação empresarial responsável pelo gerenciamento de clientes, produtos, estoque e pedidos.

O projeto foi construído com foco em fundamentos de desenvolvimento backend, arquitetura em camadas, regras de negócio, persistência relacional, SQL avançado, testes automatizados, integração frontend/backend, migrations e containerização.

---

## 🎯 Objetivo

O objetivo deste projeto é demonstrar, em um cenário de negócio coerente, conhecimentos de tecnologias utilizadas no desenvolvimento de sistemas corporativos.

O sistema permite:

- cadastrar clientes;
- cadastrar produtos;
- consultar clientes e produtos;
- controlar estoque;
- criar pedidos;
- associar produtos aos pedidos;
- calcular o valor total dos pedidos;
- validar disponibilidade de estoque;
- impedir pedidos de produtos inativos;
- controlar o status dos pedidos;
- registrar alterações de status;
- executar consultas e operações diretamente no banco;
- executar testes automatizados da camada de negócio.

---

# 🏗️ Arquitetura

A aplicação utiliza uma arquitetura em camadas:

```text
                    FRONTEND
              HTML / CSS / JavaScript
                         │
                         │ HTTP / JSON
                         ▼
                  ┌──────────────┐
                  │  Controller  │
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │   Service    │
                  │              │
                  │ Regras de    │
                  │ negócio      │
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │ Repository   │
                  └──────┬───────┘
                         │
                         ▼
                ┌──────────────────┐
                │ JPA / Hibernate  │
                └────────┬─────────┘
                         │
                         ▼
                ┌──────────────────┐
                │   PostgreSQL     │
                │                  │
                │ SQL              │
                │ Views            │
                │ Functions        │
                │ Procedures       │
                │ Triggers         │
                └──────────────────┘

                         ▲
                         │
                       Docker
```

A separação das responsabilidades permite que cada camada tenha uma função clara:

- **Controller:** recebe requisições HTTP e devolve respostas;
- **Service:** concentra as regras de negócio;
- **Repository:** realiza o acesso aos dados;
- **Entity:** representa o modelo persistido;
- **DTO:** controla os dados que entram e saem da API;
- **Database:** mantém os dados e também executa recursos SQL específicos.

---

# 🔄 Fluxo de criação de um pedido

Um dos principais fluxos do sistema é a criação de um pedido.

```text
Cliente envia pedido
        │
        ▼
POST /orders
        │
        ▼
OrderController
        │
        ▼
OrderService
        │
        ├── verifica cliente
        │
        ├── verifica produto
        │
        ├── verifica se produto está ativo
        │
        ├── verifica estoque
        │
        ├── verifica quantidade disponível
        │
        ├── cria OrderItem
        │
        ├── registra preço atual
        │
        ├── calcula subtotal
        │
        ├── calcula total
        │
        ├── reduz estoque
        │
        ▼
OrderRepository
        │
        ▼
PostgreSQL
```

A operação de criação do pedido utiliza `@Transactional`.

Isso permite que as alterações relacionadas à operação sejam tratadas como uma única transação. Se uma regra de negócio impedir a conclusão do pedido, a transação pode ser revertida.

---

# 📦 Domínio

O modelo principal do sistema é:

```text
CUSTOMER
   │
   │ 1:N
   ▼
 ORDERS
   │
   │ 1:N
   ▼
ORDER_ITEM
   │
   │ N:1
   ▼
PRODUCT
   │
   │ 1:1
   ▼
 STOCK
```

## Customer

Representa os clientes.

Principais atributos:

- `id`
- `name`
- `email`
- `document`

## Product

Representa os produtos.

Principais atributos:

- `id`
- `name`
- `description`
- `price`
- `active`

## Stock

Representa o estoque disponível de cada produto.

## Order

Representa um pedido.

Principais atributos:

- cliente;
- status;
- valor total;
- data de criação;
- data de atualização;
- itens.

## OrderItem

Representa um produto dentro de um pedido.

Principais atributos:

- produto;
- quantidade;
- preço unitário;
- subtotal.

---

# 💰 Valores monetários

O sistema utiliza `BigDecimal` para valores monetários.

Além disso, o `OrderItem` armazena o preço do produto no momento da compra.

Exemplo:

```text
Produto
price = R$ 4.500,00

Pedido
quantity = 2
unitPrice = R$ 4.500,00
subtotal = R$ 9.000,00
```

Se o produto posteriormente passar a custar R$ 5.000,00, o pedido antigo continuará registrando o preço de R$ 4.500,00.

Isso preserva o histórico financeiro da operação.

---

# 📦 Controle de estoque

Durante a criação de um pedido, o sistema verifica a quantidade disponível.

Exemplo:

```text
Estoque atual:       10
Quantidade solicitada: 2
Novo estoque:         8
```

O pedido é rejeitado quando:

- o produto não possui estoque;
- a quantidade é inválida;
- o estoque é insuficiente;
- o produto está inativo.

---

# 🌐 API REST

A aplicação disponibiliza uma API REST utilizando JSON.

## Customers

### Criar cliente

```http
POST /customers
Content-Type: application/json
```

Exemplo:

```json
{
  "name": "João Gabriel",
  "email": "joao@email.com",
  "document": "12345678900"
}
```

### Buscar cliente

```http
GET /customers/{id}
```

---

## Products

### Criar produto

```http
POST /products
Content-Type: application/json
```

Exemplo:

```json
{
  "name": "Notebook Pro",
  "description": "Notebook para desenvolvimento",
  "price": 4500.00
}
```

### Buscar produto

```http
GET /products/{id}
```

---

## Orders

### Criar pedido

```http
POST /orders
Content-Type: application/json
```

Exemplo:

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

Resposta:

```json
{
  "id": 1,
  "customerId": 1,
  "status": "PENDING",
  "totalAmount": 9000.00,
  "createdAt": "...",
  "items": [
    {
      "productId": 1,
      "productName": "Notebook Pro",
      "quantity": 2,
      "unitPrice": 4500.00,
      "subtotal": 9000.00
    }
  ]
}
```

---

# 🧩 DTOs

A API não expõe diretamente as entidades JPA.

O fluxo utiliza DTOs:

```text
HTTP Request
     │
     ▼
Request DTO
     │
     ▼
Service
     │
     ▼
Entity
     │
     ▼
Repository
```

Na resposta:

```text
Entity
   │
   ▼
Response DTO
   │
   ▼
JSON
```

Isso reduz o acoplamento entre o modelo interno da aplicação e o contrato da API.

---

# ✅ Validação

As requisições utilizam Bean Validation.

Entre as validações utilizadas estão:

```java
@NotBlank
@Email
@NotNull
@Positive
@DecimalMin
@NotEmpty
@Valid
```

Exemplo de requisição inválida:

```json
{
  "name": "",
  "email": "email-invalido",
  "document": ""
}
```

A API retorna um erro estruturado informando os campos inválidos.

---

# 🚨 Tratamento global de exceções

A aplicação utiliza `@RestControllerAdvice` para centralizar o tratamento de exceções.

Exemplo:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Cliente não encontrado: 999999",
  "path": "/customers/999999"
}
```

São tratados cenários como:

- recurso inexistente;
- regra de negócio violada;
- erro de validação.

---

# 🗄️ SQL avançado

O PostgreSQL não é utilizado somente como armazenamento de dados.

O projeto também demonstra recursos de SQL avançado.

## VIEW

Foi criada a view:

```text
vw_order_summary
```

Ela combina informações de pedidos e clientes para facilitar consultas relacionadas a relatórios.

Exemplo:

```sql
SELECT *
FROM vw_order_summary;
```

---

## FUNCTION

Foi criada a função:

```text
fn_total_orders_by_status()
```

Ela calcula o valor total dos pedidos de determinado status.

Exemplo:

```sql
SELECT fn_total_orders_by_status('DELIVERED');
```

---

## STORED PROCEDURE

Foi criada a procedure:

```text
sp_restock_product()
```

Ela permite realizar uma operação de reposição de estoque diretamente no banco.

Exemplo:

```sql
CALL sp_restock_product(1, 20);
```

---

## TRIGGER

Foi criado um trigger para registrar alterações no status dos pedidos.

```text
orders
  │
  │ UPDATE status
  ▼
TRIGGER
  │
  ▼
order_status_history
```

Quando o status de um pedido é alterado, o banco registra:

- pedido;
- status anterior;
- novo status;
- data da alteração.

---

# 🧪 Testes automatizados

A camada de negócio possui testes automatizados utilizando:

- JUnit 5;
- Mockito.

Os testes cobrem cenários de sucesso e falha.

### CustomerService

- criação de cliente;
- e-mail duplicado;
- documento duplicado;
- cliente existente;
- cliente inexistente.

### ProductService

- criação de produto;
- preço inválido;
- produto existente;
- produto inexistente.

### OrderService

- criação de pedido;
- cálculo do total;
- redução de estoque;
- produto inativo;
- estoque insuficiente;
- produto sem estoque.

Executar:

```bash
mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

---

# 🗃️ Database Migration

O projeto utiliza **Flyway** para controlar a evolução do banco.

As migrations estão em:

```text
src/main/resources/db/migration/
```

Exemplo:

```text
V1__create_initial_schema.sql
V2__create_advanced_sql.sql
```

Durante a inicialização:

```text
Spring Boot
     │
     ▼
Flyway
     │
     ▼
Executa migrations pendentes
     │
     ▼
PostgreSQL
     │
     ▼
Hibernate valida o schema
```

O Hibernate utiliza:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Dessa forma, o Hibernate valida o schema em vez de alterá-lo automaticamente.

---

# 🐳 Docker

O PostgreSQL é executado através do Docker Compose.

Iniciar:

```bash
docker compose up -d
```

Verificar:

```bash
docker ps
```

Parar:

```bash
docker compose down
```

Remover também os dados persistidos:

```bash
docker compose down -v
```

---

# 🖥️ Frontend

O projeto possui uma interface web simples desenvolvida com:

- HTML5;
- CSS3;
- JavaScript.

Estrutura:

```text
frontend/
├── index.html
├── css/
│   └── style.css
└── js/
    └── app.js
```

O JavaScript utiliza a Fetch API para consumir o backend:

```text
JavaScript
     │
     │ fetch()
     ▼
Spring Boot REST API
     │
     ▼
PostgreSQL
```

---

# 🛠️ Stack tecnológica

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- Maven

## Banco de dados

- PostgreSQL
- ANSI SQL
- PL/pgSQL
- Views
- Functions
- Stored Procedures
- Triggers

## Frontend

- HTML5
- CSS3
- JavaScript
- Fetch API
- REST
- JSON

## Testes

- JUnit 5
- Mockito

## Infraestrutura

- Docker
- Docker Compose

## Migrations

- Flyway

## Controle de versão

- Git

---

# 📁 Estrutura do projeto

```text
order-management/
│
├── frontend/
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/enterprise/orders/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__create_initial_schema.sql
│   │       │       └── V2__create_advanced_sql.sql
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# ▶️ Como executar

## Pré-requisitos

Instale:

- Java 21;
- Docker;
- Docker Compose.

O projeto utiliza o Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

---

## 1. Clonar o projeto

```bash
git clone <URL_DO_SEU_REPOSITORIO>
```

Entrar na pasta:

```bash
cd order-management
```

---

## 2. Iniciar o PostgreSQL

```bash
docker compose up -d
```

---

## 3. Executar a aplicação

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

---

## 4. Executar os testes

Windows:

```bash
mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

---

# 🔐 Configuração

As credenciais do banco podem ser configuradas através de variáveis de ambiente.

Exemplo:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

A aplicação possui valores padrão para ambiente local.

Em ambientes reais, credenciais e informações sensíveis não devem ser armazenadas diretamente no código-fonte.

---

# 📈 Evolução planejada

Este projeto representa a primeira etapa de uma arquitetura maior.

A ideia é utilizar o domínio desenvolvido aqui como base para uma futura evolução para sistemas distribuídos.

```text
                  PROJETO 1
              Modular Monolith
                     │
                     ▼
              Microservices
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
    Order Service         Stock Service
          │                     │
          └──────────┬──────────┘
                     ▼
              Kafka / RabbitMQ
                     │
                     ▼
               Event-driven
                     │
                     ▼
                 Docker
                     │
                     ▼
                Kubernetes
                     │
                     ▼
              Cloud / AWS
```

Possíveis evoluções:

- autenticação e autorização;
- documentação com OpenAPI/Swagger;
- CI/CD;
- observabilidade;
- mensageria;
- microsserviços;
- Kubernetes;
- integração com APIs de LLM;
- funcionalidades orientadas a IA.

---

# 🎓 Objetivos técnicos demonstrados

Este projeto foi desenvolvido para consolidar conhecimentos em:

- Java;
- Spring Boot;
- desenvolvimento de APIs REST;
- JSON;
- arquitetura em camadas;
- DTOs;
- JPA;
- Hibernate;
- PostgreSQL;
- modelagem relacional;
- regras de negócio;
- transações;
- validação;
- tratamento de exceções;
- testes unitários;
- Mockito;
- SQL avançado;
- Views;
- Functions;
- Stored Procedures;
- Triggers;
- Flyway;
- Docker;
- JavaScript;
- Git.

---

# 👨‍💻 Autor

**João Gabriel**

Projeto desenvolvido como parte do portfólio de desenvolvimento em tecnologia, com foco em backend Java, sistemas empresariais e evolução arquitetural.

