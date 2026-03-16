# Townguide Android Admin

Android-приложение для администрирования контента Telegram-бота.  
Клиент предназначен для внутренних админских сценариев: вход по учётной записи администратора, просмотр каталога городов, создание новых городов и добавление историй с привязкой к выбранному городу.

[![Ссылка на бота](https://img.shields.io/badge/Telegram-@Borovsk__bot-blue?logo=telegram)](https://t.me/Borovsk_bot)  
[![Репозиторий Бота](https://img.shields.io/badge/GitHub-TownGuide_App-black?logo=github)](https://github.com/gusainovt/townguide-bot)

## Содержание

1. [О проекте](#о-проекте)
2. [Разделы меню](#разделы-меню)
4. [Скриншоты](#скриншоты)
5. [Технологический стек](#технологический-стек)
6. [Архитектура](#архитектура)
7. [Backend API](#backend-api)
8. [Запуск проекта](#запуск-проекта)

## О проекте

Townguide Android Admin решает одну задачу: дать администратору быстрый мобильный интерфейс для управления туристическим контентом.

Текущий фокус приложения:

- авторизация администратора;
- сохранение JWT-токена на устройстве;
- автоматический переход в админ-панель при наличии валидной сессии;
- просмотр списка городов вместе с вложенными историями и местами;
- создание нового города;
- создание истории с предварительным выбором города.


## Разделы меню

Главное меню администратора содержит 5 разделов:

| Раздел | Назначение | Текущее состояние |
|---|---|---|
| Города | Просмотр каталога городов, историй и мест | Работает |
| Новый город | Создание новой карточки города | Работает |
| История | Добавление новой истории к выбранному городу | Работает |
| Место | Создание точки интереса | Заглушка |
| Фото | Загрузка и привязка медиа | Заглушка |

## Скриншоты

### Дашборд

<img alt="01-dashboard.png" src="docs/screenshots/01-dashboard.png" width="250"/>

### Города

<p>
  <img src="docs/screenshots/02-cities.png" width="250"/>
  <img src="docs/screenshots/03-add-city.png" width="250"/>
</p>

### История

<p>
  <img src="docs/screenshots/04-add-story-1.png" width="250"/>
  <img src="docs/screenshots/05-add-story-2.png" width="250"/>
</p>

## Технологический стек

| Язык | Kotlin |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Навигация | Navigation Compose |
| Сетевой слой | Retrofit, Gson Converter, OkHttp |
| Хранение сессии | DataStore Preferences |
| Архитектура экранов | Compose + ViewModel + StateFlow |
| Android SDK | minSdk 24 / targetSdk 36 / compileSdk 36 |
| JVM target | Java 11 |

## Архитектура

Проект разделён на два крупных слоя:

- `data`  
  Сетевые API-интерфейсы, DTO-модели, interceptor для токена, локальное хранилище авторизации.
- `ui`  
  Compose-экраны, ViewModel, навигация, общие UI-компоненты и тема приложения.

Основной runtime-поток выглядит так:

1. `SplashScreen` проверяет наличие токена.
2. Пользователь попадает либо на экран логина, либо сразу в админ-панель.
3. После успешного логина токен сохраняется в `DataStore`.
4. `AuthInterceptor` автоматически добавляет `Authorization: Bearer <token>` к backend-запросам.
5. Администратор переходит в нужный раздел через дашборд.

## Запуск проекта

### Требования

- Android Studio с поддержкой Kotlin Compose;
- Android SDK 36;
- JDK 11;
- локально запущенный backend Townguide API;
- доступный endpoint авторизации и админских API.

### Быстрый старт

1. Откройте проект в Android Studio.
2. Убедитесь, что backend доступен локально.
3. Проверьте базовый URL API в `app/build.gradle.kts`.
4. Запустите приложение на эмуляторе или устройстве.
5. Войдите под учётной записью администратора API.

### Сборка из терминала

macOS / Linux:

```bash
./gradlew assembleDebug
```

Windows:

```powershell
gradlew.bat assembleDebug
```

### Проверка компиляции Kotlin

macOS / Linux:

```bash
./gradlew :app:compileDebugKotlin
```

Windows:

```powershell
gradlew.bat :app:compileDebugKotlin
```

## Конфигурация API

По умолчанию приложение использует:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```
