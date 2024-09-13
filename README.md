
# Insurance API project

## Descrição
Essa API tem como objetivo fornecer um serviço o qual persiste a cotação de um seguro, e de acordo com o recebimento de 
uma mensagem, atualiza a cotação persistida com a police aprovada.

## Conteúdos

- [Design da Aplicação](#design-da-aplicação)
- [Frameworks e Tecnologias](#frameworks-e-tecnologias)
- [Ambiente de desenvolvimento](#ambiente-de-desenvolvimento)
- [Ambiente de execução](#ambiente-de-execução)
- [Utilização da API](#utilização-da-api)
- [Endpoints](#endpoints)
- [Oportunidades](#oportunidades)

# Design da Aplicação

#### Arquitetura
Para o desenvolvimento dessa aplicação foi utilizado o conceito de [Clean Architecture](https://www.amazon.com/Clean-Architecture-Craftsmans-Software-Structure/dp/0134494164) proposto por Robert C. Martin. Ela visa segregar as responsabilidades da aplicação em diferentes camadas, proporcionando maior flexibilidade, manutenção e escalabilidade. As camadas definidas são:

1. **Domínio**: Contém as regras de negócio e entidades fundamentais da aplicação. Esta camada é independente de frameworks, bibliotecas e outras implementações externas, garantindo que as regras de negócio possam ser reutilizadas e testadas isoladamente. No projeto está definido como a package `Domain`.

2. **Aplicação**: Responsável por orquestrar a lógica de aplicação, incluindo os casos de uso e fluxos que coordenam as operações entre as entidades de domínio. Esta camada também é independente de detalhes de infraestrutura e frameworks externos.  No projeto está definido como a package `Application`.

3. **Infraestrutura**: Contém implementações específicas de infraestrutura, como repositórios de dados, leitura e publicação de mensagens em uma fila. Esta camada depende das interfaces definidas nas camadas superiores (domínio e aplicação). No projeto está definido como a package `Infrastructure`.

4. **Apresentação**: Responsável pela interface do usuário e interação com o cliente. No caso dessa API são os controladores web REST. Esta camada depende da lógica de aplicação para realizar operações e fornecer dados ao usuário.  No projeto está definido como a package `Presentation`.

#### Estilo arquitetural
Outro conceito utilizado nesta aplicação foi o REST, conforme a proposta da solução. O endpoint de criação de quotação utiliza o método HTTP POST, que não é idempotente, criando um novo registro no banco de dados a cada nova requisição. 
Foi disponibilizado o endpoint `/queue/send` para enviar a mensagem de police (policy) aprovada, simulando a api Insurance Policy.

#### Banco de dados
O banco de dados escolhido para essa solução foi o H2 devido a sua leveza e facilidade de uso, e também por ser um banco relacional.
De acordo com o problema, não foi necessário utilização de um banco de dados mais robusto, o que poderia agregar complexidade desnecessária as configurações do projeto.

# Frameworks e Tecnologias
**Linguagem**:
* [Kotlin - version 1.9.24](https://kotlinlang.org/docs/releases.html#release-details)

**Frameworks**:
* [Springboot - version 3.3.2](https://spring.io/projects/spring-boot#overview)
* [Jackson - version 2.15.0](https://github.com/FasterXML/jackson)
* [Apache Log4j2 - version 2.20.0](https://logging.apache.org/log4j/2.x/index.html)
* [Junit - version 5](https://junit.org/junit5/)
* [Mockk - version 1.13.5](https://mockk.io/)
* [Mokito - version 5.2.0](https://site.mockito.org/)

**Mensageria**
* [RabbitMQ](https://www.rabbitmq.com/)

**Banco de dados**
* [H2](https://www.h2database.com/html/main.html)

**Gerenciador de dependencias**:
* [Gradle - version 8.8](https://gradle.org/releases/)

**Execução e disponibilização**:
* [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Docker](https://docs.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)

**Versionamento**
* [Git](https://git-scm.com/)
* [Github](https://github.com/)

# Ambiente de desenvolvimento

#### Pré-requisitos
Para o desenvolvimento dessa aplicação foi utilizado um comptador com as seguintes especificações abaixo.
* 16GB de memória Ram
* Processador intel core i7
* Linux Ubuntu 22.10

#### Instalações prévias
Para o ambiente de desenvolvimento, é necessário instalar os itens abaixo:
* [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Git](https://git-scm.com/)
* [Gradle - version 8.8](https://gradle.org/releases/)

#### IDE
Para esse desenvolvimento, foi utilizado a [IDE Intellij - versão Community 2024.1](https://www.jetbrains.com/idea/whatsnew/#). Pode-se utilizar outras IDE's também.

#### Clone do projeto
Para realizar o clone do projeto, acesse o seu terminal e dê o seguinte comando:

```
git clone https://github.com/rogeroli/insurance-quote.git
```

#### Configurações e dependencias do projeto

1 - Abra a IDE intellij, e carrega o projeto clonado no passo anterior como um projeto com gradle.
2 - Habilite a aba do gradle no intellij em `View -> Tool Windows -> Gradle`

3 - No terminal da aba do Gradle (Execute gradle task), execute o comando abaixo para baixar as dependencias do projeto:
```
gradle clean build
```

# Ambiente de execução
Todos os processos abaixo devem ser executados após as configurações e dependencias do projeto terem sido resolvidas anteriormente.

#### Execução pela IDE
1 - Dentro da IDE, Navegue até a classe InsuranceApplication.kt `/src/main/kotlin/com.itau.insurance-quote`, clique com o botão direito do mouse e depois na opção de RUN.

2 - No terminal de logs da IDE será exibido os logs de execução da aplicação, informando no final que a aplicação foi iniciada.

#### Execução com o docker-compose
Pelo seu terminal execute os comandos dentro do diretório raiz do projeto:

Para construir imagens Docker para todos os serviços definidos em um arquivo `docker-compose.yml` :
```
docker-compose build
```

Para executar e deixar disponivel em conteiners as imagens:
```
docker-compose up -d
```

# Utilização da API

#### Swagger
Assim que a aplicação é iniciada, é fornecido uma interface que permite o usuário interagir com a API de forma mais intuitiva, e sem precisar utilizar nenhuma ferramenta externa (como por exemplo o postman).
Para acessar o swagger, em seu browser, acesse:

```
localhost:8080/api/swagger-ui/index.html
```

# Endpoints

#### Envia mensagem de police (policy) aceita, simulando o servio de Insurance-Policy

```http
  POST /queue/send
```

| Parâmetro        | Tipo  | Descrição                                                                                                        |
|:-----------------|:------|:-----------------------------------------------------------------------------------------------------------------|
| `quotationId`    | `int` | **Obrigatório**. Id da cotação persistida no banco de dados.                                                     |
| `policyId`       | `int` | **Obrigatório**. Id da police aceita, esse valor que será atualizado no cotação informada no campo `quotationId` |

#### Envia uma cotação para ser persistida

```http
  POST /insurance/quotation
```

```Payload de exemplo 
  {
    "product_id": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
    "offer_id": "adc56d77-348c-4bf0-908f-22d402ee715c",
    "category": "HOME",
    "total_monthly_premium_amount": 60,
    "total_coverage_amount": 980000,
    "coverages": {
      "Incêndio": 400000,
          "Desastres naturais": 500000,
          "Responsabiliadade civil": 70000,
          "Roubo": 10000
    },
    "assistances": [
      "Encanador",
          "Eletricista",
          "Chaveiro 24h",
          "Assistência Funerária"
    ],
    "customer": {
      "document_number": "string",
      "name": "string",
      "type": "NATURAL",
      "gender": "FEMALE",
      "date_of_birth": "2024-09-09T22:03:48.236Z",
      "email": "string",
      "phoneNumber": 0
    }
  }
```

#### Retorna uma quotação

```http
  GET /insurance/quotation/{id}
```

| Parâmetro | Tipo  | Descrição                                                         |
|:----------|:------|:------------------------------------------------------------------|
| `id`      | `int` | **Obrigatório**. Id da cotação está persistida no banco de dados. |


# Oportunidades
De acordo com o escopo do problema proposto, e a arquitura desenvolvida, a API permite ser escalavél, tendo pontos de melhorias que já podem ser implementados. Abaixo segue alguns itens que vejo necessários, e o caminho que seguiria para avançar na solução.

#### CI/CD
Utilização de uma esteira de CI/CD, contendo os passos de build, run, run test, e deploy em um ambiente. Isso consiste também em configuração e disponibilização da API em um serviço na nuvem, o qual pode ser agnóstico.

#### Métricas
Apesar da implemtanção dos logs na aplicação, a configuração de ferramentas mais robustas para obter mais observabilidade da aplicação seria essencial. Ferramentas como o Grafana, prometheus, stack ELK (Elasticsearch, Logstash, Kibana).

#### Segurança e disponibilidade
A partir do momento que o serviço estaria na nuvem, varios conceitos devem ser aplicação para garantir a segurança e disponibilidade da api. Entre eles: uso de WAF, loadbalancer, cache, replicas de banco de dados. 
