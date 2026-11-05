# Кросплатформні засоби програмування — лабораторна робота №2

Варіант 22 — «Проєктні задачі». Lab 02 продовжує verified стан Lab 01 (`v1.0.0`) у тому самому репозиторії. Мета перебудови — замінити рядки й роботу з індексами CSV на інкапсульовану модель Java без зміни зовнішнього звіту.

## Модель даних

Один UTF-8 CSV-рядок має формат:

```text
title;assignee;estimateHours;priority;done
```

`ProjectTask` є незмінним класом із `private final` полями та конструкторними інваріантами:

| Поле | Тип | Правило |
|---|---|---|
| `title` | `String` | непорожнє після trim |
| `assignee` | `String` | непорожнє після trim |
| `estimateHours` | `double` | скінченне й не від'ємне |
| `priority` | `int` | не від'ємне |
| `done` | `boolean` | у CSV лише `true` або `false`, без урахування регістру |

`ProjectTask.fromCsv(String)` застосовує `split(";", -1)`, тому порожнє останнє поле не зникає. `EstimatePriority` — record із `totalEstimateHours` та `totalPriority`, який переносить проміжний агрегат метрик.

## Typed pipeline Lab 02

```text
CSV text → ProjectTask.fromCsv → List<ProjectTask>
         → EstimatePriority / metrics → formatted report → console + UTF-8 file
```

`ProjectFileReader` читає файл і додає номер рядка до помилки. `ProjectMetricsCalculator` не знає синтаксису CSV: він отримує лише `List<ProjectTask>`. Застарілий `ProjectRowParser` вилучено, щоб не підтримувати дві незалежні реалізації валідації.

Для контрольного набору Lab 01 зовнішній результат лишається таким:

```text
Кількість коректних записів: 5
Сумарна оцінка годин: 27.50
Середній пріоритет: 3.00
Кількість виконаних задач: 3
```

## Збірка і запуск

Потрібна Java 21. Maven Wrapper зберігається з Lab 01.

Windows:

```text
mvnw.cmd test
mvnw.cmd verify
mvnw.cmd package
```

macOS / Ubuntu:

```text
./mvnw test
./mvnw verify
./mvnw package
```

Після package виконуваний JAR запускається так:

```text
java -jar target/kzp-lab01-tanichkin-1.0.0.jar
```

Підтримуються `--help`, `--version`, `--input <файл>` і `--output <файл>`. За замовчуванням використовуються `data/input.csv` і `out/report.txt`.

## Перевірка та CI

JUnit-тести перевіряють `ProjectTask`, `fromCsv`, `EstimatePriority`, typed reader, метрики та сумісність повного звіту з еталоном Lab 01. GitHub Actions виконує `verify`, packaging і upload JAR artifact на `ubuntu-latest`, `windows-latest` та `macos-latest`.

Фактичний статус перевірок і CI для цієї Lab 02 гілки фіксується тільки після реальних запусків у GitHub Actions та в Pull Request.

## Академічна доброчесність

Codex використовувався як консультант і виконавець малих погоджених checkpoint-ів: студент окремо переглянув `ProjectTask`, `fromCsv`, `EstimatePriority` і typed pipeline перед переходом до наступного етапу. Повний журнал і межі відповідальності збережено в [ai/LAB_02_AI_USAGE.md](ai/LAB_02_AI_USAGE.md); студент відповідає за розуміння та пояснення кожного рядка поданого коду.
