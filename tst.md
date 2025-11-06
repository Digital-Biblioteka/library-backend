This doc shows how to run the adapter and query via Swagger

## Prerequisites
- Elasticsearch running at http://localhost:9200 with indices `books` and `book_content` loaded
- MinIO running at http://localhost:9000 (Console at http://localhost:9001) with buckets: `raw`, `parsed`

## 1) Start MinIO (minimal)
From `infra/` directory of this repo:
```bash
docker compose up -d
```
Create buckets (if not exist) via Java utility:
```bash
# from repo root
./gradlew build
./gradlew minioInit
```

## 2) Run the backend
From repo root:
```bash
./gradlew bootRun
```

## 3) Use Swagger to test the adapter
Open:
```
http://localhost:8080/swagger-ui/index.html
```
Login as user with password from console:
```
...
Using generated security password: ********-****-****-****-************
...
```

Endpoint:
- POST `/api/search/books`

Example BM25 request body:
```json
{ "query": "antarctic", "from": 0, "size": 5, "mode": "bm25" }
```
```json
{ "query": "Verne", "from": 0, "size": 5, "mode": "bm25" }
```
```json
{ "query": "journey to the center", "from": 0, "size": 5, "mode": "bm25" }
```

Expected response: array of `BookDoc` objects:
- `book_id, title, author, publisher, description, genres, linkToBook, source_uid, score`

Notes:
- `linkToBook` points to `s3://raw/...` if JSON were produced from MinIO; otherwise `file://...` for local ingest
- kNN mode will work after embeddings are added to ES and an `/embed` service is provided