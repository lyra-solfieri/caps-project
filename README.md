# 🩺 CAPS Project

API REST para **gestão de pacientes** em Centros de Atenção Psicossocial (CAPS), com cadastro, prontuários, agendamentos, relatórios e **OCR de documentos RAAS** (via microserviço Python). Desenvolvido como TCC.

---

## 🚀 Tecnologias

- **Java 17** + Spring Boot 3.4.3 (Maven)
- **MySQL 8** + Spring Data JPA
- **Spring Security** + JWT
- **Microserviço de OCR**: Python (FastAPI + Docling), chamado via REST
- Frontend: Angular (`localhost:4200`)

---

## ⚙️ Como executar

### Pré-requisitos

- Java 17 e Maven 3.8+
- MySQL rodando (ex: `localhost:3306`)
- Microserviço de OCR em `http://localhost:8000` (repositório `../ocr-service`)

### Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do MySQL |
| `DB_SCHEMA` | `CAPS` | Nome do banco |
| `DB_USER` / `DB_PASSWORD` | `root` / `1234` | Credenciais do banco |
| `JWT_SECRET` | `my-secret-key` | Chave JWT (**troque em produção!**) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | ver `.env.example` | Credenciais do admin criado no startup |
| `OCR_SERVICE_URL` | `http://localhost:8000` | URL do serviço de OCR |

### Rodando

```bash
git clone https://codeberg.org/lyrasolfieri/caps-project.git
cd caps-project
./mvnw spring-boot:run
```

A API sobe na porta **8080**. Um usuário **ADMIN** é criado automaticamente no primeiro startup (se não existir nenhum).

Com Docker:

```bash
docker build -t caps .
docker run -p 8080:8080 -e DB_HOST=host.docker.internal caps
```

> O schema é criado/atualizado automaticamente pelo Hibernate (`ddl-auto=update`). Não é preciso criar tabelas manualmente.

---

## 🔐 Autenticação

1. Um **admin** cria usuários via `POST /auth/register`.
2. Faça login em `POST /auth/login` para receber o token JWT.
3. Envie o token no header: `Authorization: Bearer <token>`.

- O token expira em **2 horas**.
- Roles: `USER` e `ADMIN`.
- Login limitado a **5 tentativas por IP a cada 15 minutos** (`429` ao exceder).

| Rotas | Acesso |
|---|---|
| `/auth/login` | Público |
| `/api/pacientes`, `/api/agendamentos`, `/api/anamneses`, `/api/evolucao`, `/api/raas`, `/api/relatorios` | `USER` ou `ADMIN` |
| `/api/auditoria`, `/api/usuarios`, `/auth/register` | Somente `ADMIN` |

---

## 📖 Endpoints principais

### Auth

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/login` | Autentica e retorna o JWT |
| POST | `/auth/register` | Cria usuário (somente ADMIN; role default: `USER`) |

### Pacientes — `/api/pacientes`

CRUD completo + filtros:

- `POST` — cria paciente (valida CPF duplicado → `409`)
- `GET` — lista paginada (`pageNo`, `pageSize`, `sortBy`, `direction`)
- `GET /{id}` — busca por ID
- `PUT /{id}` — atualização parcial
- `DELETE /{id}` — exclui
- `GET /filtrar?nome=&cpf=` — busca por trecho de nome e/ou CPF

### Agendamentos — `/api/agendamentos`

Lembretes de consulta (SMS, e-mail ou WhatsApp) vinculados a um paciente:

- `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- `GET /alertar/{id}` — verifica se há agendamento ativo

### Anamneses — `/api/anamneses`

Entrevista clínica do paciente:

- `POST`, `GET`, `GET /{id}`, `GET /paciente/{pacienteId}`, `PUT /{id}`, `DELETE /{id}`
- `GET /quantidade` — total de anamneses

### Evoluções — `/api/evolucao`

Notas de evolução do paciente (texto entre 10 e 1000 caracteres):

- `POST`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- `GET?pacienteId=` — lista paginada com filtro opcional por paciente

### RAAS — `/api/raas`

Geração de RAAS (Registro de Ações Ambulatoriais de Saúde) a partir de paciente + atendimento:

- `POST` — gera o RAAS com suas ações
- `GET /{id}` e `GET /paciente/{pacienteId}` — consultas
- `DELETE /{id}` — exclui
- `GET /{id}/pdf` — geração de PDF (**ainda não implementada**)

### RAAS via OCR — `/api/raas/ocr`

Digitalização automática do formulário RAAS em PDF:

1. `POST /upload` (multipart: `file`, `pacienteId`) — envia o PDF ao serviço de OCR e retorna um `correlationId`
2. `GET /status/{correlationId}` — status do processamento (`EM_PROCESSAMENTO`, `CONCLUIDO`, `REVISAO_MANUAL`, `FALHA`)
3. `GET /resultado/{correlationId}` — dados extraídos com nível de confiança
4. `POST /aceitar` — após revisão do usuário, persiste Paciente + Atendimento + RAAS de uma só vez

> O processamento é **síncrono** e nada é salvo no banco até o **aceite**.

### Relatórios — `/api/relatorios`

Filtros opcionais `inicio` e `fim` (`yyyy-MM-dd`):

- `GET /indicadores` — totais de pacientes, atendimentos, comparecimento etc.
- `GET /atendimentos/por-profissional` — atendimentos agrupados por mês

### Auditoria — `/api/auditoria` (somente ADMIN)

- `GET` — logs paginados com filtros (`acao`, `entidade`, `usuario`, `dataInicio`, `dataFim`)
- `GET /usuarios` e `GET /entidades` — valores distintos para os filtros

Operações auditadas: login, criação/atualização/exclusão de pacientes, agendamentos, usuários e RAAS.

### Usuários — `/api/usuarios` (somente ADMIN)

- `GET` — lista todos os usuários

---

## 📑 Enums de referência

| Enum | Valores |
|---|---|
| `Role` | `USER`, `ADMIN` |
| `Genero` | `MASCULINO`, `FEMININO`, `NAO_BINARIO`, `OUTRO` |
| `Raca` | `BRANCA`, `PRETA`, `PARDA`, `AMARELA`, `INDIGENA`, `NAO_DECLARADO` |
| `StatusAgendamento` | `PENDENTE`, `REALIZADO`, `CANCELADO`, `AGENDADO` |
| `TipoComunicacao` | `SMS`, `EMAIL`, `WHATSAPP` |
| `OcrStatus` | `EM_PROCESSAMENTO`, `CONCLUIDO`, `REVISAO_MANUAL`, `FALHA` |
| `OrigemPaciente` | `HOSPITAL_GERAL`, `HOSPITAL_PSIQUIATRICO`, `SERVICO_URGENCIA`, `ATENCAO_BASICA`, `OUTRO_CAPS`, `DEMANDA_ESPONTANEA` |

---

## 📝 Limitações conhecidas

- Sem Swagger/OpenAPI — esta documentação é a referência.
- `GET /api/raas/{id}/pdf` não está implementado (JasperReports comentado no código).
- A extração OCR depende de calibração com um PDF real do formulário RAAS da região (veja `../ocr-service/README.md`).
