# Bank "Star" - Recommendation System

Сервис рекомендаций банковских продуктов для клиентов банка «Стар».
Проект включает в себя REST API, Telegram-бота, систему динамических правил и кеширование.

## 🛠 Стек технологий
- **Java 17**
- **Spring Boot 4** (Web, Data JPA, JDBC, Actuator)
- **PostgreSQL** (хранение динамических правил и статистики)
- **H2 Database** (read-only база данных с транзакциями пользователей)
- **Liquibase** (миграции БД для PostgreSQL)
- **Caffeine** (кеширование запросов к H2)
- **Telegram Bot API** (библиотека `pengrad/java-telegram-bot-api`)
- **Swagger/OpenAPI** (документация API)

## 🚀 Быстрый старт
1. Убедитесь, что запущен PostgreSQL и создана БД `teamwork_rules`.
2. Соберите проект: `.\mvnw clean package`
3. Запустите: `java -jar target/teamwork-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`

## 📚 Документация
Вся подробная документация (архитектура, требования, инструкция по развертыванию) находится в [Wiki](../../wiki) проекта.
- [Требования и User Stories](../../wiki/Requirements)
- [Архитектура](../../wiki/Architecture)
- [REST API (Swagger)](../../wiki/API)
- [Инструкция по развертыванию](../../wiki/Deployment)