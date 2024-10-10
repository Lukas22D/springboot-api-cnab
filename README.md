# Backend CNAB

Este é um projeto Spring Boot para processar arquivos CNAB e armazenar as transações em um banco de dados H2. A aplicação lê arquivos CNAB, processa as transações e as armazena no banco de dados, além de fornecer uma API para listar as transações agrupadas por nome da loja.

## Estrutura do Projeto

```
backend_cnab/
├── .gitignore
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── .vscode/
│   └── settings.json
├── data/
├── files/
│   └── CNAB.txt
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── app/
│   │   │       └── lucas/
│   │   │           └── backend_cnab/
│   │   │               ├── job/
│   │   │               ├── web/
│   │   │               │   ├── controller/
│   │   │               │   ├── handler/
│   │   │               │   ├── model/
│   │   │               │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/
│       └── java/
│           └── app/
│               └── lucas/
│                   └── backend_cnab/
└── target/
    ├── classes/
    ├── test-classes/
    └── ...
```

## Dependências

As principais dependências do projeto são:

- Spring Boot Starter Batch
- Spring Boot Starter Web
- Spring Boot Starter Data JDBC
- H2 Database
- Spring Boot DevTools
- Spring Boot Starter Test
- Spring Batch Test

## Configuração

### Banco de Dados

A aplicação utiliza um banco de dados H2 em modo arquivo. A configuração do banco de dados está no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/db
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

### Inicialização do Banco de Dados

O esquema do banco de dados é inicializado automaticamente a partir do arquivo `schema.sql`:

```sql
CREATE TABLE transacao (
    id SERIAL primary key,
    tipo int,
    data date,
    valor decimal,
    cpf bigint,
    cartao varchar(255),
    hora time,
    dono_loja varchar(255),
    nome_loja varchar(255)
);
```

## Executando a Aplicação

Para executar a aplicação, use o comando:

```sh
./mvnw spring-boot:run
```

## Endpoints

### Upload de Arquivo CNAB

Endpoint para fazer upload de um arquivo CNAB:

- **URL:** `/cnab/upload`
- **Método:** `POST`
- **Parâmetros:**
  - `file` (MultipartFile): Arquivo CNAB a ser processado.
- **Resposta:** `Processamento iniciado!`

### Listar Transações por Nome da Loja

Endpoint para listar as transações agrupadas por nome da loja:

- **URL:** `/transacao`
- **Método:** `GET`
- **Resposta:** Lista de transações agrupadas por nome da loja.

## Testes

Os testes estão localizados no diretório `src/test/java/app/lucas/backend_cnab/`. Para executar os testes, use o comando:

```sh
./mvnw test
```

## Exceções

A aplicação possui um handler para tratar exceções específicas, como `JobInstanceAlreadyCompleteException`, retornando um status HTTP 409 (CONFLICT) com a mensagem "Arquivo já importado".
