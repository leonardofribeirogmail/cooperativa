# Cooperativa Votação

## Descrição do Projeto

A Cooperativa Votação é uma aplicação desenvolvida para gerenciar sessões de votação em uma cooperativa. Ela permite que associados registrem seus votos em pautas específicas durante as sessões de votação. A aplicação inclui funcionalidades para criar, listar e encerrar sessões de votação, bem como validar CPF dos associados usando um serviço externo.

## Requisitos

- Docker
- Docker Compose
- Java 17
- Maven 3.8.4

## Instalação

### Clonando o Repositório

```bash
git clone https://github.com/leonardofribeirogmail/cooperativa.git
cd cooperativa
```
### Construindo a Imagem Docker
```bash
docker-compose build
```
## Configuração

### Variáveis de Ambiente

A configuração das variáveis de ambiente está definida no arquivo application.yaml. Aqui estão as principais variáveis que você pode precisar ajustar:
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- CORS_ALLOWED_ORIGINS
- CORS_ALLOWED_METHODS
- CPF_VALIDATOR_URL
- SCHEDULER_UPDATE_RATE
- RESTTEMPLATE_CONNECT_TIMEOUT
- RESTTEMPLATE_READ_TIMEOUT

#### Nota sobre CPF_VALIDATOR_URL
Durante o desenvolvimento, a URL fornecida pela documentação (`https://user-info.herokuapp.com/users/{cpf}`) não estava disponível. Portanto, foi criado um serviço de validação (validator) parametrizado pela variável de ambiente `CPF_VALIDATOR_URL`. Você pode alterar facilmente para a URL da documentação comentada, se ela estiver disponível:

```yaml
cpf:
  validator:
    url: ${CPF_VALIDATOR_URL:http://validator:8081/users/{cpf}} #https://user-info.herokuapp.com/users/{cpf}
```
Basta descomentar a URL da documentação e configurar a variável de ambiente de acordo. Como comportamento padrão, todos os serviços de docker se iniciam ao mesmo tempo. Conforme está descrito [aqui](#construindo-a-imagem-docker).
### Uso
#### Endpoints Disponíveis

AssociadoController
- POST /api/associados - Cria um novo associado.
- GET /api/associados/{id} - Obtém detalhes de um associado por ID.

PautaController
- POST /api/pautas - Cria uma nova pauta.
- GET /api/pautas - Lista todas as pautas.

ResultadoVotacaoController
- GET /api/resultados/{sessaoId} - Obtém o resultado da votação para uma sessão específica.

SessaoVotacaoController

- POST /api/sessoes - Cria uma nova sessão de votação.
- GET /api/sessoes - Lista todas as sessões de votação.
- GET /api/sessoes/{id} - Obtém detalhes de uma sessão de votação por ID.

VotoController
- POST /api/votos - Registra um novo voto.

### Exemplo de Requisição
Para criar um novo associado, você pode usar o seguinte exemplo de requisição via curl:
```bash
curl -X POST "http://localhost:8080/api/associados" -H "Content-Type: application/json" -d '{"cpf": "12345678901"}'
```
### Estrutura do Projeto
```plaintext
cooperativa-votacao
├── .mvn
│   └── wrapper
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── cooperativa
│   │   │               ├── config
│   │   │               ├── controller
│   │   │               ├── converter
│   │   │               ├── dto
│   │   │               ├── enums
│   │   │               ├── exception
│   │   │               ├── model
│   │   │               ├── repository
│   │   │               ├── service
│   │   │               └── util
│   │   └── resources
│   └── test
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── cooperativa
│       └── resources
└── validator
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── validator
│   │   └── resources
└── test
├── java
│   └── com
│       └── example
│           └── validator
└── resources
```
### Contribuição
Esse projeto não aceita outras contribuições.