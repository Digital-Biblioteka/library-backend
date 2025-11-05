@echo off
setlocal enableextensions enabledelayedexpansion

if not defined MINIO_ENDPOINT set MINIO_ENDPOINT=http://localhost:9000
if not defined MINIO_ROOT_USER set MINIO_ROOT_USER=minioadmin
if not defined MINIO_ROOT_PASSWORD set MINIO_ROOT_PASSWORD=minioadmin
if not defined RAW_BUCKET set RAW_BUCKET=raw
if not defined PARSED_BUCKET set PARSED_BUCKET=parsed
if not defined INDEX_BUCKET set INDEX_BUCKET=index
if not defined ES_SNAPSHOTS_BUCKET set ES_SNAPSHOTS_BUCKET=es-snapshots

echo [minio-init] MINIO_ENDPOINT=%MINIO_ENDPOINT%

pushd "%~dp0.."
call .\gradlew --no-daemon build || goto :error
call .\gradlew --no-daemon minioInit || goto :error
popd

echo [minio-init] done
exit /b 0

:error
popd
exit /b 1
