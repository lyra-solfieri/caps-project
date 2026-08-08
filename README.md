# 🩺 CAPS Project - Sistema de Gestão de Pacientes com OCR

Sistema desenvolvido como Trabalho de Conclusão de Curso (TCC) para o **cadastro de pacientes**, **gerenciamento de prontuários** e **processamento OCR de documentos** em Centros de Atenção Psicossocial (CAPS). A API REST permite digitalização, organização e automação de processos, incluindo a geração de **RAAS** (Registro de Ações Ambulatoriais de Saúde) a partir de documentos digitalizados.

---

## 📋 Sumário

1. [Visão geral e arquitetura](#-visão-geral-e-arquitetura)
2. [Stack de tecnologias](#-stack-de-tecnologias)
3. [Como executar o projeto](#-como-executar-o-projeto)
4. [Autenticação e autorização](#-autenticação-e-autorização)
5. [Documentação dos endpoints](#-documentação-dos-endpoints)
   - [Auth](#auth)
   - [Pacientes](#pacientes)
   - [Agendamentos](#agendamentos)
   - [Anamneses](#anamneses)
   - [Evoluções](#evoluções)
   - [RAAS](#raas)
   - [RAAS via OCR](#raas-via-ocr)
   - [Relatórios](#relatórios)
   - [Auditoria](#auditoria)
   - [Usuários](#usuários)
6. [Pipeline OCR passo a passo](#-pipeline-ocr-passo-a-passo)
7. [Mecanismo de auditoria](#-mecanismo-de-auditoria)
8. [Tratamento de erros](#-tratamento-de-erros)
9. [Referência de enums](#-referência-de-enums)

---

## 🏗️ Visão geral e arquitetura

A API é **stateless** (sem sessão), protegida por **JWT** e exposta na porta **8080**. O processamento de OCR é **assíncrono**: o upload de documentos é feito via API, o arquivo é armazenado no **MinIO** e o processamento é enfileirado no **RabbitMQ**. Um **worker OCR externo** (não parte deste projeto) consome a fila de requisição, processa o documento e publica o resultado na fila de resposta, que a aplicação consome e persiste.

```
                    ┌──────────────────────────────────────────────┐
                    │                  CAPS API                    │
                    │  (Spring Boot, porta 8080)                   │
                    │                                              │
  Frontend/Angular  │  ┌──────────┐   ┌───────────┐   ┌─────────┐  │
  (localhost:4200)──┼─▶│ Controller│──▶│  Service  │──▶│   JPA   │──┼──▶ MySQL
      ┌────────────┐ │  └──────────┘   └─────┬─────┘   └─────────┘  │
      │ JWT Bearer │ │                       │                     │
      │   token    │ │                       ▼                     │
      └────────────┘ │            ┌──────────────────┐             │
                     │            │ MinIO (arquivos) │             │
                     │            └──────────────────┘             │
                     └──────────────────┬──────────────────────────┘
                                        │  publica em
                                        ▼
                    ┌──────────────────────────────────────┐
                    │   RabbitMQ                            │
                    │   fila: raas.ocr.request              │
                    └──────────────────┬───────────────────┘
                                       │ consome
                                       ▼
                    ┌───────────────────────────────┐
                    │    WORKER OCR EXTERNO         │
                    │  (Docling / OCRmyPDF /      │
                    │   Ocr-service)           │
                    └───────────────┬───────────────┘
                                    │ publica em
                                    ▼
                    ┌──────────────────────────────────────┐
                    │   RabbitMQ                            │
                    │   fila: raas.ocr.response            │
                    └──────────────────┬───────────────────┘
                                       │ consome (OcrCallbackConsumer)
                                       ▼
                          OcrProcessamento atualizado no banco
```

---

## 🚀 Stack de tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.4.3 |
| Persistência | Spring Data JPA + Hibernate, MySQL 8 |
| Migrações | Flyway (presente, mas desabilitado por padrão) |
| Segurança | Spring Security + JWT (com.auth0/java-jwt) |
| Autenticação | Stateless, token Bearer (HMAC256, expira em 2h) |
| Mapeamento | MapStruct 1.5.5 + Lombok 1.18.36 |
| Mensageria | RabbitMQ (filas OCR request/response) |
| Armazenamento | MinIO 8.5.7 (bucket `raas-uploads`) |
| OCR | Tess4J + JavaCV (dependências para o worker) |
| Validação | Spring Boot Validation + Hibernate Validator |
| Build | Maven, Docker multi-stage |
| Frontend | Angular (CORS liberado para `http://localhost:4200`) |

> **Nota:** embora o README anterior mencionasse Swagger e PostgreSQL, **não há dependência Swagger/springdoc no `pom.xml`** e o driver de banco configurado é apenas **MySQL**. A documentação é esta.

---

## ⚙️ Como executar o projeto

### 1. Pré-requisitos

- **Java 17** e **Maven 3.8+**
- **MySQL** rodando (ex: `localhost:3306`)
- **RabbitMQ** rodando (ex.: `localhost:5672`, usuário/senha `guest/guest`)
- **MinIO** rodando (ex.: `http://localhost:9000`, acesso `minioadmin/minioadmin`)
- Worker OCR externo consumindo a fila `raas.ocr.request` (para o fluxo completo de OCR)

### 2. Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do MySQL |
| `DB_SCHEMA` | `CAPS` | Nome do banco |
| `DB_USER` | `root` | Usuário do banco |
| `DB_PASSWORD` | `1234` | Senha do banco |
| `JWT_SECRET` | `my-secret-key` | Chave de assinatura do JWT (**troque em produção!**) |
| `ADMIN_EMAIL` | `lyrasolfieri@gmail.com` | E-mail do admin criado no startup |
| `ADMIN_PASSWORD` | `12345678#` | Senha do admin criado no startup |
| `RABBITMQ_HOST` | `localhost` | Host do RabbitMQ |
| `MINIO_ENDPOINT` | `http://localhost:9000` | Endpoint do MinIO |
| `MINIO_ACCESS_KEY` | `minioadmin` | Access key do MinIO |
| `MINIO_SECRET_KEY` | `minioadmin` | Secret key do MinIO |
| `MINIO_BUCKET` | `raas-uploads` | Bucket para os arquivos de OCR |

Existe um arquivo `.env.example` na raiz com um subconjunto dessas variáveis.

### 3. Executar com Maven

```bash
# clonar o repositório
git clone https://codeberg.org/lyrasolfieri/caps-project.git
cd caps-project

# configurar as variáveis de ambiente (opcional, há defaults)
export JWT_SECRET=um-segredo-forte
export ADMIN_EMAIL=admin@caps.com
export ADMIN_PASSWORD=SuaSenha@2025

# subir
./mvnw spring-boot:run
```

Ao subir, um usuário **ADMIN** é criado automaticamente (`AdminInitializer`) com as credenciais de `ADMIN_EMAIL`/`ADMIN_PASSWORD`, caso não exista nenhum admin no banco.

### 4. Executar com Docker

```bash
docker build -t caps .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e JWT_SECRET=um-segredo-forte \
  caps
```

### 5. Configurações relevantes (`application.properties`)

- `spring.jpa.hibernate.ddl-auto=update` — o schema é ajustado automaticamente pelo Hibernate
- `spring.flyway.enabled=false` — migrações Flyway desabilitadas por padrão
- Filas OCR: `ocr.queue.request=raas.ocr.request` e `ocr.queue.response=raas.ocr.response`

---

## 🔐 Autenticação e autorização

### Fluxo de autenticação

1. **O admin registra** usuários em `POST /auth/register` (exclusivo de `ADMIN`, podendo escolher a role `ADMIN` ou `USER`; default `USER`).
2. **Fazer login** em `POST /auth/login` para obter o JWT.
3. Enviar o token no header `Authorization: Bearer <token>` em todas as demais requisições.

O token JWT contém: emissor `auth-api`, `subject` = e-mail do usuário, claim `role`, expiração em **2 horas** (fuso UTC-3).

### Proteção por rota

| Rota | Regra |
|---|---|
| `POST /auth/login`, `OPTIONS` | **Públicas** |
| `/api/pacientes`, `/api/agendamentos`, `/api/anamneses`, `/api/evolucao`, `/api/raas`, `/api/raas/ocr`, `/api/relatorios` | `ADMIN` ou `USER` |
| `/api/auditoria`, `/api/usuarios` | somente `ADMIN` |
| `POST /auth/register` | somente `ADMIN` |
| qualquer outra | autenticado (qualquer token válido) |

### Rate limiting no login

O `LoginRateLimitingFilter` limita o login por IP: **máximo de 5 tentativas em 15 minutos**. Ao estourar, a API responde `429`:

```json
{ "message": "Muitas tentativas de login. Tente novamente em 15 minutos." }
```

### Nota sobre roles

O sistema possui apenas duas roles: `USER` e `ADMIN`. O `ADMIN` recebe a authority `ROLE_ADMIN` e o `USER` recebe `ROLE_USER`. Ambos acessam os endpoints de negócio; `/api/auditoria`, `/api/usuarios` e `POST /auth/register` são restritos ao `ADMIN`.

---

## 📖 Documentação dos endpoints

Todas as rotas, exceto as de autenticação, exigem o header `Authorization: Bearer <token>`.

### Auth

#### `POST /auth/register`

Exclusivo de `ADMIN` (requer `Authorization: Bearer <token>` de um admin). Cria um usuário com a role informada (`ADMIN` ou `USER`); se `role` for omitida, assume `USER`.

**Corpo:**
```json
{
  "email": "profissional@caps.com",
  "password": "Abcd@1234",
  "nome": "Maria da Silva",
  "profissao": "Psicóloga",
  "role": "USER"
}
```

Validações da senha: 8 a 100 caracteres, com letra maiúscula, minúscula, número e caractere especial.

**Respostas:**
- `200 OK` — usuário criado
- `400 Bad Request` — e-mail já cadastrado ou validação falhou

> Registra log de auditoria `CRIACAO` em `User`.

#### `POST /auth/login`

Público. Autentica e retorna o JWT.

**Corpo:**
```json
{
  "email": "profissional@caps.com",
  "password": "Abcd@1234"
}
```

**Resposta `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Erros:**
- `401 Unauthorized` — credenciais inválidas
- `429 Too Many Requests` — excedeu o rate limit (5 tentativas / 15 min)

> Registra log de auditoria `LOGIN` em `User`, incluindo o IP do cliente (via `X-Forwarded-For` → `X-Real-IP` → `getRemoteAddr`).

---

### Pacientes

Base: `/api/pacientes` · Roles: `ADMIN`, `USER`

#### `POST /api/pacientes`

Cria um paciente. Verifica duplicidade de CPF.

**Corpo (exemplo resumido — todos os campos abaixo são aceitos):**
```json
{
  "nome": "João Pedro Santos",
  "email": "joao@email.com",
  "telefone": "(11) 99999-0000",
  "cpf": "123.456.789-09",
  "endereco": {
    "cep": "01000-000",
    "logradouro": "Rua das Flores",
    "numero": "100",
    "complemento": "Apto 2",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "pais": "Brasil"
  },
  "genero": "MASCULINO",
  "dataNascimento": "1990-01-15",
  "nomeMae": "Ana Santos",
  "nomePai": "Carlos Santos",
  "profissao": "Pedreiro",
  "numeroCartaoSUS": "123456789012345",
  "raca": "PARDA",
  "numeroProntuario": "PRT-001",
  "infantojuvenil": false,
  "emSituacaoSocioeconomica": true,
  "codIBGEMunicipio": "3550308",
  "etniaIndigena": "",
  "nacionalidade": "Brasileira",
  "escolaridade": "Fundamental completo",
  "rg": "12.345.678-9",
  "profissaoMae": "Costureira",
  "profissaoPai": "Aposentado",
  "rendaFamiliar": "2 salários",
  "auxilioOuBeneficioSocial": "Bolsa Família",
  "responsavelLegal": "",
  "parentescoResponsavel": "",
  "telefoneResponsavelLegal": "",
  "profissaoResponsavelLegal": "",
  "instituicaoEnsino": "",
  "religiao": "",
  "unidadeBasicaSaude": "UBS Centro",
  "agenteSaude": "Agente 05",
  "contextoVulnerabilidadeSocioeconomica": "Desemprego"
}
```

**Resposta `200 OK`** (`PacienteResponseDto` — subconjunto de campos):
```json
{
  "id": 1,
  "nome": "João Pedro Santos",
  "email": "joao@email.com",
  "telefone": "(11) 99999-0000",
  "cpf": "123.456.789-09",
  "endereco": { "cidade": "São Paulo" },
  "genero": "MASCULINO",
  "dataNascimento": "1990-01-15",
  "raca": "PARDA",
  "numeroProntuario": "PRT-001",
  "infantojuvenil": false,
  "emSituacaoSocioeconomica": true
}
```

**Erros:**
- `409 Conflict` — CPF já cadastrado
- `500 Internal Server Error` — erro interno

> Registra auditoria `CRIACAO` em `Paciente`.

#### `GET /api/pacientes/{id}`

Busca por ID.

**Resposta `200 OK`:** `PacienteResponseDto` (mesmo formato do POST).
**Erros:** `500` (paciente não encontrado vira erro interno).

#### `GET /api/pacientes`

Lista paginada. Parâmetros: `pageNo` (0), `pageSize` (10), `sortBy` (id), `direction` (asc).

**Resposta `200 OK`:**
```json
{
  "content": [ { "id": 1, "nome": "João Pedro Santos" } ],
  "pageable": { "pageNumber": 0, "pageSize": 10 },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

#### `PUT /api/pacientes/{id}`

Atualiza parcialmente. Corpo: mesmo `PacienteDto` do POST.

**Resposta `204 No Content`** · **Erros:** `500`.

> Registra auditoria `ATUALIZACAO` em `Paciente`.

#### `DELETE /api/pacientes/{id}`

Exclui um paciente.

**Resposta `204 No Content`** · **Erros:** `500`.

> Registra auditoria `EXCLUSAO` em `Paciente`.

#### `GET /api/pacientes/filtrar`

Filtra por nome e/ou CPF (busca por trecho, `LIKE`), paginado. Parâmetros: `pageNo`, `pageSize`, `sortBy`, `direction`, `nome`, `cpf`.

**Exemplo:** `GET /api/pacientes/filtrar?nome=João&cpf=123`

**Resposta `200 OK`:** `Page<PacienteResponseDto>` (mesmo formato do listar).

---

### Agendamentos

Base: `/api/agendamentos` · Roles: `ADMIN`, `USER`

#### `POST /api/agendamentos`

Cria um agendamento vinculado ao usuário logado e (opcionalmente) a um paciente. O status inicial é `AGENDADO`.

**Corpo:**
```json
{
  "destinatario": "(11) 99999-0000",
  "message": "Lembrete da consulta de amanhã",
  "tipoComunicacao": "WHATSAPP",
  "dataHoraAtendimento": "2026-08-03T10:00:00",
  "pacienteId": 1
}
```

`tipoComunicacao`: `SMS`, `EMAIL` ou `WHATSAPP`.

**Resposta `200 OK`:**
```json
{
  "message": "Agendamento realizado com sucesso!"
}
```

**Erros:** `500`.

> Registra auditoria `CRIACAO` em `Agendamento`.

#### `GET /api/agendamentos`

Lista todos.

**Resposta `200 OK`:**
```json
[
  {
    "id": 1,
    "destinatario": "(11) 99999-0000",
    "message": "Lembrete da consulta de amanhã",
    "tipoComunicacao": "WHATSAPP",
    "dataHoraAtendimento": "2026-08-03T10:00:00",
    "statusAgendamento": "AGENDADO",
    "createAt": "2026-08-02T09:00:00",
    "paciente": { "id": 1, "nome": "João Pedro Santos", "email": "joao@email.com", "cpf": "123.456.789-09" },
    "user": { "id": 1, "nome": "Maria da Silva", "email": "profissional@caps.com", "role": "USER" }
  }
]
```

#### `PUT /api/agendamentos/{id}`

Atualiza destinatário, mensagem, tipo, data/hora e paciente.

**Resposta `200 OK`:** `{ "message": "Agendamento atualizado com sucesso!" }`
**Erros:** `500`.

> Registra auditoria `ATUALIZACAO` em `Agendamento`.

#### `DELETE /api/agendamentos/{id}`

**Resposta `204 No Content`** · **Erros:** `500`.

> Registra auditoria `EXCLUSAO` em `Agendamento`.

#### `GET /api/agendamentos/{id}`

Busca por ID. O método no service é protegido com `hasRole('ADMIN')`.

**Resposta `200 OK`:** mesmo formato do listar.

#### `GET /api/agendamentos/alertar/{id}`

Verifica se há agendamento para o ID.

**Resposta `200 OK`:** texto puro — `"Agendamento atual"` (se existir) ou `"Sem agendamento"` (se não).

---

### Anamneses

Base: `/api/anamneses` · Roles: `ADMIN`, `USER`

A anamnese é o instrumento de entrevista clínica do paciente, com dezenas de campos (queixa, história, antecedentes, hábitos, desenvolvimento infantil, sono, alimentação, escola, comportamento).

#### `POST /api/anamneses`

**Corpo (exemplo resumido — todos os campos descritos na classe `AnamneseDto` são aceitos):**
```json
{
  "pacienteId": 1,
  "dataRegistro": "2026-08-02T10:00:00",
  "profissionalResponsavel": "Maria da Silva",
  "queixaPrincipal": "Ansiedade e insônia",
  "historiaDoencaAtual": "Há 3 meses apresenta insônia...",
  "encaminhamentos": "Psiquiatria",
  "antecedentesFamiliares": "Mãe com depressão",
  "habitosDeVida": "Sedentário",
  "observacoesGerais": "",
  "alergias": "Penicilina",
  "cirurgias": "Nenhuma",
  "medicamentosUsados": "Sertralina 50mg",
  "historicoDeInternacao": "Nenhum",
  "projetoTerapeuticoSingular": "Acompanhamento semanal"
}
```

**Resposta `201 Created`:** `AnamneseDto` preenchido (com `id`).
**Erros:** `500`.

#### `GET /api/anamneses/{id}`

**Resposta `200 OK`:** `AnamneseDto`.

#### `GET /api/anamneses/paciente/{pacienteId}`

Busca a anamnese de um paciente.

**Resposta `200 OK`:** `AnamneseDto`.

#### `PUT /api/anamneses/{id}`

Atualiza a anamnese.

**Resposta `200 OK`:** `AnamneseDto` atualizado.

#### `DELETE /api/anamneses/{id}`

**Resposta `204 No Content`.**

#### `GET /api/anamneses`

Lista paginada (usando `Pageable` padrão do Spring — `page`, `size`, `sort`).

**Resposta `200 OK`:** `List<AnamneseDto>` (apenas o conteúdo da página).

#### `GET /api/anamneses/quantidade`

**Resposta `200 OK`:** número total de anamneses.
```json
42
```

---

### Evoluções

Base: `/api/evolucao` · Roles: `ADMIN`, `USER`

#### `POST /api/evolucao`

**Corpo:**
```json
{
  "pacienteId": 1,
  "codProntuario": 12345,
  "nomeProfissional": "Maria da Silva",
  "texto": "Paciente relata melhora na qualidade do sono. Mantém acompanhamento."
}
```

O texto deve ter entre 10 e 1000 caracteres.

**Resposta `200 OK`** (preenchido com id e nome do paciente):
```json
{
  "id": 1,
  "pacienteId": 1,
  "pacienteNome": "João Pedro Santos",
  "codProntuario": 12345,
  "dataHoraEdicao": null,
  "nomeProfissional": "Maria da Silva",
  "texto": "Paciente relata melhora na qualidade do sono. Mantém acompanhamento."
}
```

#### `PUT /api/evolucao/{id}`

**Resposta `200 OK`:** `EvolucaoDto` atualizado.

#### `DELETE /api/evolucao/{id}`

**Resposta `204 No Content`.**

#### `GET /api/evolucao/{id}`

**Resposta `200 OK`:** `EvolucaoDto`.

#### `GET /api/evolucao`

Lista paginada, com filtro opcional por paciente via query param `pacienteId`.

**Exemplos:**
- `GET /api/evolucao`
- `GET /api/evolucao?pacienteId=1&page=0&size=10`

**Resposta `200 OK`:** `List<EvolucaoDto>`.

---

### RAAS

Base: `/api/raas` · Roles: `ADMIN`, `USER`

O **RAAS** (Registro de Ações Ambulatoriais de Saúde) é gerado a partir de um paciente + atendimento já cadastrados, com suas ações executadas.

#### `POST /api/raas`

Gera um RAAS. Valida que o atendimento pertence ao paciente informado.

**Corpo:**
```json
{
  "pacienteId": 1,
  "atendimentoId": 1,
  "nomeEstabelecimento": "CAPS Centro",
  "cnes": "1234567",
  "acoes": [
    {
      "codigoAcao": "A",
      "quantidade": 2,
      "data": "2026-07-20",
      "cid10": "F32",
      "servico": "Ambulatorial",
      "classificacao": "Acúmulo de procedimentos",
      "cboExecutor": "223905",
      "cartaoProfissional": "CRM-SP 000000",
      "codigoEquipe": "EQ001",
      "caps": true,
      "territorio": false
    }
  ]
}
```

**Resposta `200 OK`:** entidade `Raas` persistida (JSON com os campos do RAAS, incluindo `status: true`, `enviadoSUS: false`, `dataGeracao` e a lista de `acoes`).

> Registra auditoria `CRIACAO` em `Raas`.

#### `GET /api/raas/{id}`

**Resposta `200 OK`:** entidade `Raas`.

#### `GET /api/raas/paciente/{pacienteId}`

Lista os RAAS de um paciente.

**Resposta `200 OK`:** `List<Raas>`.

#### `DELETE /api/raas/{id}`

**Resposta `204 No Content`.**

> Registra auditoria `EXCLUSAO` em `Raas`.

#### `GET /api/raas/{id}/pdf`

Gera o PDF do RAAS (`application/pdf`), com `Content-Disposition: inline`.

> **⚠️ Aviso:** este endpoint está **sem implementação funcional** — o método `gerarPdf` do `RaasService` retorna `null` (a geração via JasperReports está comentada no código). Chamá-lo resultará em erro interno.

---

### RAAS via OCR

Base: `/api/raas/ocr` · Roles: `ADMIN`, `USER`

Módulo de **digitalização automática**: o documento é processado por OCR de forma assíncrona, os dados extraídos ficam disponíveis para revisão e o RAAS só é persistido quando o usuário **aceita** os dados revisados.

#### `POST /api/raas/ocr/upload` — envia documento para OCR

Multipart. Parâmetros: `file` (obrigatório) e `pacienteId` (opcional).

```
POST /api/raas/ocr/upload
Content-Type: multipart/form-data
file: <PDF>
pacienteId: 1
```

**Fluxo:** o serviço gera um `correlationId` (UUID), salva o arquivo no MinIO (bucket `raas-uploads`, caminho `<correlationId>/<nome-do-arquivo>`), publica a mensagem na fila `raas.ocr.request` e registra um `OcrProcessamento` com status `EM_PROCESSAMENTO`.

**Resposta `200 OK`** (`RaasOcrUploadResponse`):
```json
{
  "correlationId": "9f1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d",
  "processamentoId": 1,
  "status": "EM_PROCESSAMENTO",
  "mensagem": "Documento enviado para processamento OCR"
}
```

**Erros:** `500` com corpo `"Erro ao processar upload: <mensagem>"`.

#### `GET /api/raas/ocr/status/{correlationId}`

Consulta o status do processamento. Retorna a entidade `OcrProcessamento`:

**Resposta `200 OK`:**
```json
{
  "id": 1,
  "correlationId": "9f1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d",
  "originalFileName": "raas-paciente.pdf",
  "pacienteId": 1,
  "pacienteCriadoId": null,
  "atendimentoCriadoId": null,
  "raasCriadoId": null,
  "status": "EM_PROCESSAMENTO",
  "confidenceGeral": null,
  "dadosExtraidosJson": null,
  "resultadoJson": null,
  "dataCriacao": "2026-08-02T10:00:00",
  "dataConclusao": null
}
```

Possíveis `status`: `EM_PROCESSAMENTO`, `CONCLUIDO`, `REVISAO_MANUAL`, `FALHA`.

**Erros:** `404 Not Found` (correlationId inexistente).

#### `GET /api/raas/ocr/resultado/{correlationId}`

Retorna o resultado do OCR desserializado em `OcrResultMessage` (apenas quando já há `resultadoJson`):

**Resposta `200 OK`:**
```json
{
  "correlationId": "9f1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d",
  "status": "CONCLUIDO",
  "confidenceGeral": 0.92,
  "dadosExtraidos": {
    "nomePaciente": "João Pedro Santos",
    "cartaoSus": "123456789012345",
    "cnes": "1234567"
  },
  "confidences": { "nomePaciente": 0.98, "cartaoSus": 0.85 },
  "camposNaoReconhecidos": [],
  "camposBaixaConfianca": ["cnes"],
  "erros": []
}
```

**Erros:** `404 Not Found` (se não houver resultado).

#### `POST /api/raas/ocr/aceitar`

Aplica os dados revisados, **persistindo** Paciente (se necessário), Atendimento e RAAS de forma transacional. Nada é persistido no momento do callback do OCR — somente aqui.

**Pré-condições:**
- `correlationId` existente
- o processamento **não** pode estar `EM_PROCESSAMENTO` ou `FALHA`
- o RAAS ainda não pode ter sido aceito (`raasCriadoId` deve ser nulo)

**Corpo (`RaasAceitarDTO`):**
```json
{
  "correlationId": "9f1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d",
  "nomeEstabelecimento": "CAPS Centro",
  "cnes": "1234567",
  "numeroProntuario": "PRT-001",
  "nomePaciente": "João Pedro Santos",
  "cartaoSus": "123456789012345",
  "sexo": "M",
  "dataNascimento": "1990-01-15",
  "nomeMae": "Ana Santos",
  "nacionalidade": "Brasileira",
  "racaCor": "Parda",
  "etniaIndigena": "",
  "nomeResponsavel": "",
  "municipio": "São Paulo",
  "uf": "SP",
  "codigoIbge": "3550308",
  "cep": "01000-000",
  "endereco": "Rua das Flores",
  "complemento": "",
  "telefoneCelular": "(11) 99999-0000",
  "telefoneContato": "",
  "dataAdmissao": "2026-07-01",
  "dataConclusao": "2026-07-31",
  "mesAtendimento": "07/2026",
  "numeroAutorizacao": 12345,
  "usoDrogas": "Nega uso",
  "alcool": false,
  "crack": false,
  "outrasDrogas": false,
  "origemDemanda": true,
  "origemAtencaoBasica": false,
  "origemUrgencia": false,
  "origemCaps": false,
  "origemHospital": false,
  "origemPsiquiatrico": false,
  "cidPrincipal": "F32",
  "descricaoCidPrincipal": "Episódio depressivo",
  "cidSecundario": "",
  "descricaoCidSecundario": "",
  "esf": "Ambulatório",
  "cnesAtendimento": "1234567",
  "encaminhamentoCaps": false,
  "encaminhamentoAB": true,
  "alta": false,
  "obito": false,
  "acoes": [
    {
      "codigoAcao": "A",
      "quantidade": 2,
      "data": "2026-07-20",
      "cid10": "F32",
      "servico": "Ambulatorial",
      "classificacao": "Acúmulo de procedimentos",
      "cboExecutor": "223905",
      "cartaoProfissional": "CRM-SP 000000",
      "codigoEquipe": "EQ001",
      "caps": true,
      "territorio": false
    }
  ]
}
```

**Regras do aceite:**
- Se o `cartaoSus` já existir em um paciente, o paciente é **reutilizado** (dados só são preenchidos onde estiverem nulos).
- Caso contrário, um novo `Paciente` é criado (com endereço, se informado).
- São criados o `Atendimento` e o `Raas` (status `true`, `enviadoSUS` `false`), com as ações mapeadas.
- O `OcrProcessamento` é atualizado com os IDs criados (`pacienteCriadoId`, `atendimentoCriadoId`, `raasCriadoId`).

**Resposta `200 OK`:** entidade `Raas` persistida.

**Erros:**
- `400 Bad Request` — `correlationId` obrigatório, processamento não encontrado, ainda em processamento/falhou, ou RAAS já aceito. Corpo: texto da mensagem de erro.
- `500` — erro interno.

---

### Relatórios

Base: `/api/relatorios` · Roles: `ADMIN`, `USER`

Ambos os endpoints aceitam período opcional via query params `inicio` e `fim` no formato `yyyy-MM-dd`.

#### `GET /api/relatorios/indicadores`

Exemplo: `GET /api/relatorios/indicadores?inicio=2026-07-01&fim=2026-07-31`

**Resposta `200 OK`** (`IndicadoresDesempenhoDto`):
```json
{
  "totalPacientes": 150,
  "totalAgendamentosPeriodo": 60,
  "agendamentosRealizados": 45,
  "agendamentosCancelados": 5,
  "agendamentosPendentes": 10,
  "totalAtendimentos": 90,
  "taxaComparecimento": 75.0,
  "mediaAtendimentosPorPaciente": 0.6
}
```

#### `GET /api/relatorios/atendimentos/por-profissional`

Exemplo: `GET /api/relatorios/atendimentos/por-profissional?inicio=2026-07-01&fim=2026-07-31`

**Resposta `200 OK`** — atendimentos agrupados por mês:
```json
[
  { "mes": "07/2026", "quantidade": 30 },
  { "mes": "08/2026", "quantidade": 12 }
]
```

---

### Auditoria

Base: `/api/auditoria` · Role: **somente `ADMIN`**

#### `GET /api/auditoria`

Lista logs paginados, ordenados por data desc. Filtros opcionais: `page`, `size` (máx. 100), `acao`, `entidade`, `usuario`, `dataInicio`, `dataFim`.

Exemplo: `GET /api/auditoria?page=0&size=15&acao=CRIACAO&entidade=Paciente`

**Resposta `200 OK`** (`Page<AuditoriaLogResponse>`):
```json
{
  "content": [
    {
      "id": 1,
      "usuario": "profissional@caps.com",
      "acao": "CRIACAO",
      "entidade": "Paciente",
      "entidadeId": 1,
      "detalhes": "Paciente criado: João Pedro Santos",
      "ip": "192.168.0.10",
      "dataHora": "2026-08-02T10:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

#### `GET /api/auditoria/usuarios`

Lista os e-mails distintos que possuem logs.

**Resposta `200 OK`:**
```json
[ "profissional@caps.com", "lyrasolfieri@gmail.com" ]
```

#### `GET /api/auditoria/entidades`

Lista as entidades distintas que possuem logs.

**Resposta `200 OK`:**
```json
[ "User", "Paciente", "Agendamento", "Raas" ]
```

---

### Usuários

Base: `/api/usuarios` · Role: **somente `ADMIN`**

#### `GET /api/usuarios`

Lista todos os usuários.

**Resposta `200 OK`** (`List<UserDto>`):
```json
[
  {
    "id": 1,
    "nome": "Administrador do Sistema",
    "email": "admin@caps.com",
    "role": "ADMIN"
  }
]
```

---

## 🔄 Pipeline OCR passo a passo

O fluxo completo de digitalização de um RAAS é:

1. **Upload** — o frontend envia o PDF em `POST /api/raas/ocr/upload`.
2. **Armazenamento** — `OcrGatewayService` salva o arquivo no **MinIO** (bucket `raas-uploads`), em `<correlationId>/<nome-original>`.
3. **Enfileiramento** — publica uma mensagem JSON na fila `raas.ocr.request` com `correlationId`, `filePath`, `originalFileName`, `pacienteId` e `timestamp`.
4. **Registro** — cria um `OcrProcessamento` com status `EM_PROCESSAMENTO` e retorna `correlationId` e `processamentoId`.
5. **Processamento externo** — o worker OCR consome a fila, extrai o texto do PDF (Tesseract/OCRmyPDF) e reconhece os campos.
6. **Resultado** — o worker publica na fila `raas.ocr.response` um JSON (`OcrResultMessage`) com status, `confidenceGeral`, `dadosExtraidos`, `confidences`, `camposNaoReconhecidos`, `camposBaixaConfianca` e `erros`.
7. **Callback** — `OcrCallbackConsumer` recebe a mensagem, atualiza o `OcrProcessamento` (status, `confidenceGeral`, `dadosExtraidosJson`, `resultadoJson`, `dataConclusao`).
8. **Consulta** — o frontend faz *polling* em `GET /api/raas/ocr/status/{correlationId}` e, quando pronto, em `GET /api/raas/ocr/resultado/{correlationId}`.
9. **Revisão** — o usuário confere/corrige os dados extraídos no formulário.
10. **Aceite** — `POST /api/raas/ocr/aceitar` persiste, de forma **transacional**, o `Paciente` (novo ou reutilizado pelo cartão SUS), o `Atendimento` e o `Raas` com as ações.

> **Importante:** nada é persistido no momento do callback do OCR; a persistência dos dados de negócio ocorre **somente no aceite**.

---

## 🧾 Mecanismo de auditoria

Toda operação relevante é registrada em `auditoria_log` pelo `AuditoriaService`, contendo: usuário (ou IP se anônimo), ação, entidade, ID da entidade, detalhes, IP do cliente e data/hora.

**Ações registradas por operação:**

| Operação | Ação | Entidade |
|---|---|---|
| Registro de usuário | `CRIACAO` | `User` |
| Login | `LOGIN` | `User` |
| Criar paciente | `CRIACAO` | `Paciente` |
| Atualizar paciente | `ATUALIZACAO` | `Paciente` |
| Excluir paciente | `EXCLUSAO` | `Paciente` |
| Criar agendamento | `CRIACAO` | `Agendamento` |
| Atualizar agendamento | `ATUALIZACAO` | `Agendamento` |
| Excluir agendamento | `EXCLUSAO` | `Agendamento` |
| Gerar RAAS | `CRIACAO` | `Raas` |
| Excluir RAAS | `EXCLUSAO` | `Raas` |

Os logs podem ser consultados e filtrados pelos endpoints de auditoria (exclusivo `ADMIN`).

---

## ⚠️ Tratamento de erros

O `GlobalExceptionHandler` padroniza os erros:

- **Validação (`MethodArgumentNotValidException`)** → `400 Bad Request`:
  ```json
  { "message": "CPF inválido" }
  ```
- **Qualquer `RuntimeException` não tratada** → `500 Internal Server Error`:
  ```json
  { "message": "Ocorreu um erro interno no servidor." }
  ```

Além disso, alguns controllers tratam erros localmente e podem responder `409 Conflict` (CPF duplicado), `400` (regras do aceite OCR) e `429` (rate limit no login), com o corpo variando conforme o endpoint.

---

## 📑 Referência de enums

**`Role`** — `USER`, `ADMIN`
**`Genero`** — `MASCULINO`, `FEMININO`, `NAO_BINARIO`, `OUTRO`
**`Raca`** — `BRANCA`, `PRETA`, `PARDA`, `AMARELA`, `INDIGENA`, `NAO_DECLARADO`
**`StatusAgendamento`** — `PENDENTE`, `REALIZADO`, `CANCELADO`, `AGENDADO`
**`TipoComunicacao`** — `SMS`, `EMAIL`, `WHATSAPP`
**`OcrStatus`** — `EM_PROCESSAMENTO`, `CONCLUIDO`, `REVISAO_MANUAL`, `FALHA`
**`AcaoAuditoria`** — `LOGIN`, `LOGOUT`, `CRIACAO`, `ATUALIZACAO`, `EXCLUSAO`, `ACESSO`, `CONFIGURACAO`
**`OrigemPaciente`** — `HOSPITAL_GERAL`, `HOSPITAL_PSIQUIATRICO`, `SERVICO_URGENCIA`, `ATENCAO_BASICA`, `OUTRO_CAPS`, `DEMANDA_ESPONTANEA`

---

## 📝 Notas e limitações conhecidas

- **Sem Swagger/OpenAPI** configurado; esta documentação é a referência.
- `GET /api/raas/{id}/pdf` está sem implementação (JasperReports comentado no `RaasService`).
- O sistema possui apenas duas roles (`USER` e `ADMIN`); ambos acessam os endpoints de negócio e `/api/auditoria`, `/api/usuarios` e `POST /auth/register` são exclusivos do `ADMIN`.
- O schema é gerenciado por `ddl-auto=update` (Flyway presente, porém desabilitado).
- O fluxo OCR depende de um **worker externo** consumindo `raas.ocr.request` e publicando em `raas.ocr.response` — não faz parte deste repositório.
