# Lab 01 — Кросплатформні засоби програмування

Консольний Java-проєкт для лабораторної роботи №1 «РОЗГОРТАННЯ JAVA-ПРОЄКТУ ТА БАЗОВА ОБРОБКА ДАНИХ».

## Варіант 22: Проєктні задачі

Формат одного UTF-8 запису в `data/input.csv`:

`title;assignee;estimateHours;priority;done`

Поля розділяються символом `;`. Майбутня програма перевірятиме рівно п'ять полів, непорожні `title` і `assignee`, невід'ємні `estimateHours`, цілий `priority` без штучного діапазону та строго `true`/`false` у `done`.

## Команди

Windows:

```text
mvnw.cmd test
mvnw.cmd verify
mvnw.cmd package
java -jar target/kzp-lab01-tanichkin-1.0.0.jar --version
```

macOS / Ubuntu:

```text
./mvnw test
./mvnw verify
./mvnw package
java -jar target/kzp-lab01-tanichkin-1.0.0.jar --version
```

`--version` розрізняє source/product version `1.0.0` та CI build number. Локальна Maven-збірка виводить `build local`; workflow передає GitHub Actions `github.run_number` як `ci.build.number`.

## Поточний стан

Інфраструктуру Maven, JUnit, SpotBugs, executable JAR і CI підготовлено. Предметний CSV-парсер, обчислення метрик і формування звіту навмисно ще не реалізовано: вони виконуватимуться малими студентськими checkpoint-ами.
