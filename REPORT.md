# Звіт до лабораторної роботи №1

## 1. Тема, номер і варіант

Я виконав лабораторну роботу №1 з дисципліни «Кросплатформні засоби програмування» на тему
«РОЗГОРТАННЯ JAVA-ПРОЄКТУ ТА БАЗОВА ОБРОБКА ДАНИХ». Мій варіант — 22, предметна область —
«Проєктні задачі». Роботу виконав Танічкін В. О., група КІ-304; викладач — Грицько Т. Л.

Для варіанта 22 один запис має формат:

```text
title:String;
assignee:String;
estimateHours:double;
priority:int;
done:boolean
```

Я виконав роботу в середовищі Microsoft Windows, x64. Команда `ver` повернула фактичний рядок:

```text
Microsoft Windows [Version 10.0.26200.9457]
```

## 2. Мета роботи

Метою моєї роботи було пройти повний кросплатформний цикл розробки: створити Java 21
Maven-проєкт із Maven Wrapper і Git/GitHub workflow, реалізувати читання UTF-8 CSV,
валідацію записів варіанта 22, обчислення чотирьох показників і формування одного звіту для
консолі та файла. Я також реалізував CLI, unit та integration tests, статичний аналіз SpotBugs,
виконуваний JAR і GitHub Actions CI для Windows, Ubuntu та macOS.

## 3. Постановка задачі

Я реалізував обробку CSV-записів такого вигляду:

```text
title;assignee;estimateHours;priority;done
```

У записі `title` і `assignee` мають бути непорожніми. `estimateHours` має бути коректним
значенням типу `double` і не може бути від'ємним. `priority` має бути синтаксично коректним
значенням типу `int`; я не вводив штучний діапазон пріоритету, оскільки методика його не
визначає. Після `trim()` поле `done` може містити лише `true` або `false` без урахування
регістру.

Хибний рядок не зупиняє програму. Я додаю до звіту номер такого рядка та причину помилки,
а сам рядок не включаю до показників. Програма обчислює:

1. кількість коректних записів;
2. сумарний `estimateHours`;
3. середній `priority`;
4. кількість виконаних задач.

Для п'яти затверджених коректних записів я отримав очікувані результати:

```text
count = 5
total estimateHours = 27.50
average priority = 3.00
done count = 3
```

Поточний `data/input.csv` містить ці п'ять коректних записів і шість навмисно невалідних
рядків для перевірки continuation behavior.

## 4. Структура програми

Я розділив відповідальності між невеликими компонентами, щоб читання файлів, валідація,
обчислення та форматування не потрапили до одного великого методу.

| Package / class | Відповідальність | Вхід / результат |
| --- | --- | --- |
| `ua.lpnu.kzp.Main` | координує CLI та processing pipeline | аргументи CLI; друкує звіт або повідомлення про I/O помилку |
| `ua.lpnu.kzp.BuildInfo` | завантажує metadata збірки | bundled resource; повертає `Properties` для `--version` |
| `ua.lpnu.kzp.parsing.ProjectRowParser` | перевіряє один CSV-рядок | `String`; повертає `null` або причину помилки |
| `ua.lpnu.kzp.io.ProjectFileReader` | читає UTF-8 файл і розділяє валідні рядки та помилки | `Path`, `List<String> errors`; повертає `List<String> validLines` |
| `ua.lpnu.kzp.metrics.ProjectMetricsCalculator` | обчислює чотири показники | уже валідні `List<String>`; повертає числа показників |
| `ua.lpnu.kzp.report.ProjectReportFormatter` | формує український текстовий звіт | `validLines` і `errors`; повертає один `String` |
| `ua.lpnu.kzp.io.ProjectReportWriter` | записує готовий текст у UTF-8 файл | `Path` і report `String`; створює файл |

Потік даних у програмі має вигляд:

```text
data/input.csv
→ ProjectFileReader
→ ProjectRowParser
→ validLines + errors
→ ProjectMetricsCalculator
→ ProjectReportFormatter
→ report String
→ console + ProjectReportWriter
→ out/report.txt
```

`ProjectRowParser` не читає файли, `ProjectFileReader` не обчислює показники, а
`ProjectMetricsCalculator` не виконує повторну валідацію. `ProjectReportFormatter` не пише
файл, а `ProjectReportWriter` не формує текст звіту. `Main` лише координує ці компоненти.
Такий поділ робить поведінку пояснюваною й дозволить у Lab 02 замінити поточні `String` /
`String[]` власними класами без зміни зовнішньої поведінки програми.

## 5. Інфраструктура

Я використав Java 21. Фактичний локальний результат команди `java -version`:

```text
openjdk version "21.0.12.1" 2026-08-18 LTS
OpenJDK Runtime Environment Temurin-21.0.12.1+1 (build 21.0.12.1+1-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.12.1+1 (build 21.0.12.1+1-LTS, mixed mode, sharing)
```

Я налаштував Maven-проєкт із координатами `ua.lpnu.kzp:kzp-lab01-tanichkin:1.0.0`.
Для відтворюваної збірки використовую Maven Wrapper: `mvnw.cmd` у Windows та `./mvnw` у
macOS/Ubuntu. Тому для проєкту не потрібен system Maven.

У `pom.xml` я підтвердив такі залежності й plugins:

| Component | Фактична версія / роль |
| --- | --- |
| JUnit Jupiter | `5.12.2`, unit та integration tests |
| Maven Resources Plugin | `3.4.0`, UTF-8 resource filtering для build metadata |
| Maven Compiler Plugin | `3.13.0`, компіляція з `maven.compiler.release=21` |
| Maven Surefire Plugin | `3.5.2`, запуск JUnit tests |
| SpotBugs Maven Plugin | `4.9.7.0`, goal `check` у фазі `verify` |
| Maven Shade Plugin | `3.6.0`, виконуваний JAR з `ua.lpnu.kzp.Main` |

У POM я також налаштував UTF-8 для source, reporting, resources і properties. Команда
`mvnw.cmd test` компілює проєкт і запускає JUnit tests. Команда `mvnw.cmd verify` проходить
життєвий цикл до фази `verify`, тому запускає tests, packaging і SpotBugs. Команда
`mvnw.cmd package` створює виконуваний JAR, а `mvnw.cmd clean verify` спочатку очищує
`target`, після чого перевіряє відтворювану збірку.

Фактичний результат packaging — `target/kzp-lab01-tanichkin-1.0.0.jar`, який я запускаю
командою:

```text
java -jar target/kzp-lab01-tanichkin-1.0.0.jar
```

Для кросплатформності я використав `Path` / `Path.of` замість жорстко заданих Windows-шляхів,
явне UTF-8 кодування, `Locale.ROOT` для десяткових чисел і `%n` для системного роздільника
рядків. Maven Wrapper використовує відповідний скрипт для ОС.

Я налаштував GitHub Actions matrix для `ubuntu-latest`, `windows-latest` і `macos-latest` з
Temurin 21. Workflow запускає `verify`, виконує packaging, завантажує JAR artifact і використовує
Maven dependency cache. Назва artifact має шаблон
`jar-<OS>-v1.0.0-build-<CI_RUN_NUMBER>`.

Product version дорівнює `1.0.0`. Для локальної збірки `ci.build.number` має значення `local`,
тому `--version` показує `build local`. У CI `ci.build.number` отримує GitHub Actions run number;
цей номер відрізняється від product version і ідентифікує конкретний запуск workflow.

## 6. GitHub Issues і Pull Request

_Таблиця Issue → зміна → commit/PR; номери додаються лише після фактичного створення Issues._

## 7. Приклади роботи

_Автентичні вхідні дані, консольний вивід, файл звіту та повідомлення про помилки._

## 8. Тестування, CI і артефакти

_Виконані команди, фактичні результати й посилання на Actions/artifacts._

## 9. Документація

_Javadoc публічних і нетривіальних елементів._

## 10. Академічна доброчесність і використання ШІ

_Назва інструмента, ролі, запити, прийняті рекомендації, виправлені помилки, перевірка студентом і підтвердження розуміння коду._

## 11. Відповіді на контрольні питання

_Власні відповіді, пов'язані з фактичними файлами та доказами проєкту._

## 12. Висновки

_Фактичний результат і підготовка до Lab 02._
