# Маркетплейс

## Цель проекта
**Целью** моего **проекта является создание** единой **платформы для** бесплатного **размещения** частных **брендов от** еще не популярных **дизайнеров**.


## Моя цель как разработчика
Разработать масштабируемую и надежную headless backend-инфраструктуру для маркетплейса, обеспечивающую следующие возможности:
- управление каталогом товаров (включая поиск и фильтрацию)
- хранение фотографий товаров и брендов в облачном объектном хранилище
- обработку заказов (от создания до оплаты)
- управление пользователями и их корзинами
- систему платежей (интеграция с платежным сервисом Stripe API)
- систему отслеживания метрик и логов.

Инфраструктура должна быть документирована, тестируема и развертываться с помощью CI/CD.

## Архитектура проекта
![alt text](https://i.imgur.com/MJFfqfu.png)
В репозитории проект хранится как многомодульный maven проект, где на каждый сервис существует по модулю.

## Сервис каталога
RESTful Сервис отвечающий за предоставление данных о брендах, категориях и товарах.

### Стек технологий
Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Liquibase, Kafka, Swagger

### Модель данных
![alt text](https://i.imgur.com/RUzKal4.png)

## Сервис файлов
RESTful Сервис отвечающий за сохранение и получение файлов из облачного хранилища.
Взаимодействует с объектным хранилищем от cloud.ru

### Стек технологий
Spring Boot, Spring Cloud (spring-cloud-aws-s3-starter), Spring Security, Swagger

## Сервис пользователей
RESTful Сервис отвечающий за предоставление данных о пользователях, об их заказах и их корзинах. Также отвечает за оплату.

### Стек технологий
Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Liquibase, Kafka, Swagger, Stripe API, Test Containers

### Модель данных
![alt text](https://i.imgur.com/9VwyTHA.png)

## Полезные данные о проекте

Проект запущен на виртуальной машине и доступен по IP адресу: http://176.123.166.167  
Порты: 
- 8081 – сервис каталога, docs-endpoint: /api/v1/catalog/docs 
- 8082 – сервис пользователя, docs-endpoint: /api/v1/user-service/docs 
- 8083 – сервис изображений, docs-endpoint: /api/v1/file-service/docs 
- 3000 – сервис Grafana (логин admin пароль 12345678) 
- 5601 – сервис Kibana  

Некоторые endpoint-ы защищены basic-auth, данные для аутентификации:
- Catalog-service: login – catalog_service_user password – 19022602 
- User-service: login – user_service_user password – 19022602 
- File-service: login – file_service_user password – 19022602

## Как развернуть локально?

1. Склонировать репозиторий на свой компьютер.
2. В корне репозитория создать файл .env и заполнить его этими переменными:
  - SPRING_PROFILES_ACTIVE=prod 
  - CATALOG_DB_URL=
  - CATALOG_DB_USERNAME= 
  - CATALOG_DB_PASSWORD= 
  - CATALOG_SERVICE_USERNAME= 
  - CATALOG_SERVICE_PASSWORD={encoded password} 
  - CATALOG_SERVICE_PASSWORD_NOT_ENCODED={not encoded password} 
  - CATALOG_LOG_PATH= 
  - FILE_LOG_PATH= 
  - USER_LOG_PATH= 
  - TZ= 
  - AWS_ACCESS_KET_ID= 
  - AWS_SECRET_KEY= 
  - FILE_SERVICE_USERNAME= 
  - FILE_SERVICE_PASSWORD={encoded password} 
  - FILE_SERVICE_PASSWORD_NOT_ENCODED={not encoded password} 
  - USER_SERVICE_USERNAME= 
  - USER_SERVICE_PASSWORD={encoded password} 
  - USER_SERVICE_PASSWORD_NOT_ENCODED={not encoded password} 
  - ALERT_BOT_TOKEN= 
  - ALERT_CHAT_ID= 
  - USER_DB_URL= 
  - USER_DB_USERNAME= 
  - USER_DB_PASSWORD= 
  - CATALOG_SERVICE_URL= 
  - PAYMENT_SUCCESS_URL= 
  - PAYMENT_CANCEL_URL= 
  - PAYMENT_API_KEY= 
  - PAYMENT_CURRENCY= 
  - FILE_SERVICE_URL= 
  - PAYMENT_SIGHING_SECRET= 
  - KAFKA_HOST= 
  - KAFKA_CLUSTER_ID= 
  - ADMIN_EMAIL= 
  - ADMIN_PHONE= 
  - ADMIN_USERNAME= 
  - ADMIN_PASSWORD={encoded password} 
  - ADMIN_FIRST_NAME= 
  - ADMIN_LAST_NAME=
3. Установить docker compose на свой компьютер
4. Запустить проект выполнив команду sudo docker compose up -d (находясь в корне репозитория)