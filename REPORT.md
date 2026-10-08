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

Я створив Issues для поділу роботи на незалежні частини. Початкову infrastructure частину я
реалізував окремими commits. Для предметної програми я використав послідовність
Issue → feature branch → focused commits → Pull Request → CI → merge → automatic Issue close.

| Issue | Завдання | Реалізація | PR / commit | Стан |
| --- | --- | --- | --- | --- |
| #1 | Налаштування лабораторної роботи №1 | початкова структура проєкту та базова інфраструктура | `30c1309 build: add Maven infrastructure and wrapper` | OPEN |
| #2 | Налаштувати Maven-проєкт і Maven Wrapper | `pom.xml`, Maven Wrapper, JUnit, SpotBugs і JAR framework | `30c1309 build: add Maven infrastructure and wrapper` | OPEN |
| #3 | Налаштувати SpotBugs та виконуваний JAR | SpotBugs у фазі `verify` і Maven Shade Plugin | `30c1309 build: add Maven infrastructure and wrapper` | OPEN |
| #4 | Налаштувати GitHub Actions для трьох ОС | matrix CI, Maven cache і artifact upload | `7767ca0 ci: add cross-platform verification workflow`; `a7375a1 ci: fix cross-platform wrapper execution` | OPEN |
| #5 | Реалізувати читання і перевірку записів варіанта 22 | parser, reader, UTF-8 reading і повідомлення з номером рядка | PR #9 `feat: implement project row validation and CSV reading` | CLOSED |
| #6 | Реалізувати обчислення показників і формування звіту | metrics, formatter, writer, `Main` і CLI | PR #10 `feat: implement project metrics, reporting and CLI` | CLOSED |
| #7 | Додати тести та перевірку крайових випадків | empty input, no valid records і pipeline edge cases | PR #11 `test: cover project processing edge cases` | CLOSED |
| #8 | Завершити README, REPORT та javadoc | поточна документаційна гілка | `feature/issue-8-documentation` | OPEN |

У першому CI run я виявив два реальні дефекти конфігурації. На Unix runner скрипт `mvnw` не мав
виконуваного біта, тому виникала помилка `Permission denied`. На Windows Maven неправильно
обробляв неекрановану property `ci.build.number`. Я перевірив діагностику, додав executable bit
для `mvnw` у Git metadata та взяв Windows property у лапки. Ці виправлення зафіксовані commit
`a7375a1`; після них CI став green.

## 7. Приклади роботи

Я виконав default запуск з кореня репозиторію командою:

```text
java -jar target/kzp-lab01-tanichkin-1.0.0.jar
```

Програма сформувала такий результат:

```text
Результати обробки проєктних задач

Кількість коректних записів: 5
Сумарна оцінка годин: 27.50
Середній пріоритет: 3.00
Кількість виконаних задач: 3
Кількість помилкових записів: 6

Помилки:
Рядок 6: поле title порожнє
Рядок 7: поле assignee порожнє
Рядок 8: поле estimateHours має некоректне значення
Рядок 9: поле estimateHours має некоректне значення
Рядок 10: поле done має містити true або false
Рядок 11: очікується 5 полів
```

Я перевірив, що default report записується у `out/report.txt` як UTF-8. Той самий `report String`
використовується для консолі й для файла, тому правила форматування не дублюються.

Для custom paths я виконав:

```text
java -jar target/kzp-lab01-tanichkin-1.0.0.jar --input data/input.csv --output out/custom-report.txt
```

Я порівняв default і custom результати для одного input та отримав фактичний результат
`REPORTS_EQUAL=True`.

Команда довідки дала:

```text
Використання:
  java -jar kzp-lab01-tanichkin-1.0.0.jar [опції]

Опції:
  --help            Показати довідку
  --version         Показати версію та номер збірки
  --input <файл>    Шлях до вхідного CSV
  --output <файл>   Шлях до вихідного звіту
```

Локальна команда `--version` дала:

```text
kzp-lab01-tanichkin 1.0.0
build local
```

Я також перевірив дружню обробку некоректних аргументів:

```text
java -jar target/kzp-lab01-tanichkin-1.0.0.jar --unknown
Невідомий аргумент: --unknown
Використайте --help для довідки.

java -jar target/kzp-lab01-tanichkin-1.0.0.jar --input
Помилка аргументів: після --input очікується шлях.
```

## 8. Тестування, CI і артефакти

Я виконав локальну перевірку командою:

```text
mvnw.cmd clean verify
```

Після Issue #7 я отримав 57 tests, 0 failures і 0 errors. SpotBugs завершився з результатом
0 bugs і 0 errors. Команда `mvnw.cmd package` також успішно створює виконуваний JAR.

Тести охоплюють parser validation, reader і нумерацію рядків, numeric validation, усі metrics,
дробовий середній пріоритет, форматування report, UTF-8 writer, CLI, empty input, no valid
records, valid-invalid-valid pipeline та український UTF-8 end-to-end scenario.

Останній підтверджений CI після завершення функціональної частини — GitHub Actions run #12:
[Merge pull request #11 from vlad158166/feature/issue-7-edge-case-tests](https://github.com/vlad158166/kzp-lab01-tanichkin/actions/runs/37779946200).
Він виконався на гілці `main` зі станом `completed / success` для `ubuntu-latest`,
`windows-latest` і `macos-latest`.

Для run #12 workflow створив такі artifacts:

```text
jar-macos-latest-v1.0.0-build-12
jar-ubuntu-latest-v1.0.0-build-12
jar-windows-latest-v1.0.0-build-12
```

Artifacts доступні на сторінці workflow run #12. Product version у цьому запуску — `1.0.0`,
а CI build number — `12`; позначення artifact не означає, що product version дорівнює 12.
Git tag `v1.0.0` на цьому етапі ще не створено.

## 9. Документація

Я оновив `README.md`: у ньому описані призначення програми, CSV format, збірка, запуск, CLI,
versioning, CI, testing і AI disclosure. Я також перевірив Javadoc для публічних і нетривіальних
елементів:

- `Main`, `main` і `runProcessing`;
- `BuildInfo` і `load`;
- `ProjectFileReader` і `readValidLines`;
- `ProjectReportWriter` і `writeReport`;
- `ProjectMetricsCalculator` та його public metrics methods;
- `ProjectRowParser` і `validateRow`;
- `ProjectReportFormatter` і `formatReport`.

Я виправив неточне формулювання в Javadoc `BuildInfo.load()`: попередній опис називав
`Properties` immutable, хоча `java.util.Properties` є mutable object. Тепер Javadoc коректно
описує повернення properties з product/build metadata.

## 10. Академічна доброчесність і використання ШІ

Я використовував AI як допоміжний інструмент для планування етапів, пояснення Java, Maven і
Git, підготовки test scenarios, аналізу помилок, допомоги з GitHub workflow та перевірки
README, REPORT і Javadoc. AI не повинен підміняти моє розуміння програми.

У repository я підготував ролі `manager`, `devops`, `developer`, `validator`, `documenter` і
`reviewer`. Вони відповідно допомагали декомпозувати вимоги на Issues, перевіряти Maven/CI,
пояснювати малий крок алгоритму, готувати edge cases, перевіряти документацію та проводити
requirements/cross-platform review. Їхні prompts і limitations збережені у `ai/`; наприклад,
роль developer не має писати повну предметну програму замість студента, а validator не має
вигадувати дефекти чи результати тестів.

Я прийняв конкретні рекомендації: використати `split(";", -1)` для збереження порожнього
останнього поля, розділити відповідальності між компонентами, форматувати числа через
`Locale.ROOT`, явно використовувати UTF-8, застосувати `@TempDir` у файлових tests, Maven
Wrapper і CI matrix. Я також перевіряв виявлені проблеми: permission для `mvnw` на Unix,
quoting Maven property у Windows та некоректний Javadoc про mutable `Properties`.

Я сам запускав команди, перевіряв результати й correctness metrics, перевіряв Pull Requests і
CI, робив screenshots та повинен пояснити весь submitted code і прийняті рішення.

## 11. Відповіді на контрольні питання

_Власні відповіді, пов'язані з фактичними файлами та доказами проєкту._

## 12. Висновки

_Фактичний результат і підготовка до Lab 02._
