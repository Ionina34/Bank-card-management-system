# Система управления банковскими картами

Это приложение на Spring Boot для управления банковскими картами, использующее PostgreSQL в качестве базы данных и Liquibase для миграций схемы. Приложение контейнеризовано с помощью Docker и оркестрируется с использованием Docker Compose.

## Предварительные требования

Убедитесь, что у вас установлены следующие инструменты:
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Git](https://git-scm.com/downloads)

## Настройка и запуск приложения

Следуйте этим шагам, чтобы клонировать репозиторий и запустить приложение:

1. **Клонирование репозитория**

   Клонируйте проект на ваш локальный компьютер:

   ```bash
   git clone https://github.com/Ionina34/Bank-card-management-system
   cd bank-card-management-system
   ```
   Для запуска приложения:

   ```
   docker-compose -f docker-compose.yaml up --build
   ```
