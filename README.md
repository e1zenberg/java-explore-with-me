# \# Java Explore With Me — Stage 1 (Сервис статистики)

# 

Этот репозиторий содержит \*\*микросервис статистики (stats)\*\* и заглушку основного сервиса (ewm).

На первом этапе требуется поднять сервис статистики в Docker и продемонстрировать работу эндпоинтов.

# 

\## TL;DR — Быстрый старт

# 

\*\*Требования:\*\* Docker Desktop (с docker compose), для локальной разработки — JDK 21 и Maven.

# 

#### ```bash

##### \# 1) Запуск всех контейнеров

##### docker compose up -d

# 

##### \# 2) Проверка, что stats-server поднялся

##### curl http://localhost:9090/actuator/health

##### \# {"status":"UP"}

# 

##### \# 3) Отправка пары хитов

##### curl -X POST http://localhost:9090/hit \\

#####   -H "Content-Type: application/json" \\

#####   -d '{"app":"ewm-main-service","uri":"/events/1","ip":"127.0.0.1","timestamp":"2025-01-01 10:00:00"}'

# 

##### curl -X POST http://localhost:9090/hit \\

#####   -H "Content-Type: application/json" \\

#####   -d '{"app":"ewm-main-service","uri":"/events/1","ip":"127.0.0.2","timestamp":"2025-01-01 10:05:00"}'

# 

##### \# 4) Получение статистики

##### curl "http://localhost:9090/stats?start=2025-01-01%2000:00:00\&end=2025-01-02%2000:00:00\&uris=/events/1"

##### curl "http://localhost:9090/stats?start=2025-01-01%2000:00:00\&end=2025-01-02%2000:00:00\&uris=/events/1\&unique=true"

