DocuSense is a Spring Boot backend + React frontend application that ingests documents, converts document chunks to embeddings (OpenAI), stores vectors, and provides semantic search + RAG chat endpoints. This README shows how to run and test the whole flow locally (Postgres + OpenAI) and how to run in a mock mode if you don’t have OpenAI quota.

Backend endpoints:

POST /api/documents/upload — upload document (title + content)

POST /api/search — semantic search (body: { "query": "...", "topK": 5 })

POST /api/chat — RAG chat (body: { "question": "..." })

Default backend port used in examples: 8081



Tech stack
Java 17, Spring Boot (Controller → Service → Repository)

Hibernate / Spring Data JPA

PostgreSQL (production), H2 (dev)

OpenAI Embeddings & Chat (for embed/search & RAG)

React (simple frontend demo)

Maven, npm







Prerequisites
Java 17+ and Maven installed

Node 16+ and npm (for frontend)

PostgreSQL running (or you can use H2 in-dev)

OpenAI API key if you want real embeddings & chat (or use mock mode)1 — Configure the environment
Create PostgreSQL DB

# create database
createdb docDB
# or in psql:
-- CREATE DATABASE docDB;
Make sure your DB user has privileges.

Add environment variables (recommended)
Windows (PowerShell / CMD):

powershell
Copy
Edit
setx OPENAI_API_KEY "sk-..."
setx SPRING_DATASOURCE_PASSWORD "your_db_password"
Mac/Linux (bash):

bash
Copy
Edit
export OPENAI_API_KEY="sk-..."
export SPRING_DATASOURCE_PASSWORD="your_db_password"
Restart terminal / IDE so environment vars are visible.

2 — application.properties.sample (place in repo)
Create a src/main/resources/application.properties.sample with placeholders (do not commit real keys):

properties
Copy
Edit
# DB (Postgres)
spring.datasource.url=jdbc:postgresql://localhost:5432/docDB
spring.datasource.username=postgres
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server port
server.port=8081

# OpenAI key (use env or replace for local dev)
openai.api.key=${OPENAI_API_KEY}

# Mock mode (true = no calls to OpenAI; useful for testing w/o quota)
app.mock-ai=true
Important: Add src/main/resources/application.properties to .gitignore to avoid committing secrets.

Recommended .gitignore snippet:

bash
Copy
Edit
/target/
/.idea/
/.vscode/
/application.properties
/src/main/resources/application.properties
3 — Build & run (backend)
From project root:

bash
Copy
Edit
# build
mvn clean install

# run
mvn spring-boot:run
Or use your IDE Run As → Spring Boot App. Confirm console shows Tomcat started on port 8081.

4 — Build & run (frontend) — if included
bash
Copy
Edit
cd frontend
npm install
npm start
Frontend should open at http://localhost:3000 and calls the backend at http://localhost:8081/api/....

5 — Postman / curl testing (end-to-end)
1) Upload a document (ingest & create embeddings)
Endpoint

bash
Copy
Edit
POST http://localhost:8081/api/documents/upload
Content-Type: application/json
Body

json
Copy
Edit
{
  "title": "AI Basics",
  "content": "Artificial Intelligence is the simulation of human intelligence in machines..."
}
curl

bash
Copy
Edit
curl -X POST http://localhost:8081/api/documents/upload \
  -H "Content-Type: application/json" \
  -d '{"title":"AI Basics","content":"Artificial Intelligence is the simulation..."}'
What happens

Document saved to DB.

Document is chunked (in DocumentService) and each chunk sent to EmbeddingService.

EmbeddingService calls OpenAI embeddings (or returns mocked vector in app.mock-ai=true).

VectorStoreService persists embedding entries (embedding JSON or vector store).

2) Semantic search
Endpoint

bash
Copy
Edit
POST http://localhost:8081/api/search
Content-Type: application/json
Body

json
Copy
Edit
{
  "query": "What is Artificial Intelligence?",
  "topK": 5
}
curl

bash
Copy
Edit
curl -X POST http://localhost:8081/api/search \
  -H "Content-Type: application/json" \
  -d '{"query":"What is Artificial Intelligence?","topK":5}'
What happens

Query text is embedded.

VectorStoreService finds top-K similar chunks by cosine similarity.

Response: JSON array of EmbeddingEntity objects (chunkText, documentId, maybe score).

3) Chat (RAG)
Endpoint

bash
Copy
Edit
POST http://localhost:8081/api/chat
Content-Type: application/json
Body

json
Copy
Edit
{
  "question": "Explain AI simply."
}
curl

bash
Copy
Edit
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"Explain AI simply."}'
What happens

The question is embedded.

Top relevant document chunks retrieved.

These chunks are assembled into a context prompt and sent to OpenAI Chat Completion (or mocked if app.mock-ai=true) to produce a concise answer.

Response example:

json
Copy
Edit
{
  "answer": "Artificial Intelligence refers to..."
}
Note on quota/errors: If OpenAI returns insufficient_quota or 429, either enable app.mock-ai=true, or top up your OpenAI billing/quota.



Security & best practices
Never commit secrets (OpenAI keys or DB passwords). Use environment variables.

For public demo: replace API key usage with secure server-side vault or GitHub Actions secrets for CI.



************************************
