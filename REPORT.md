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

### 1. Яке призначення `pom.xml` і його основних параметрів?

`pom.xml` є описом Maven-проєкту, а не файлом із Java-кодом. У моєму проєкті він визначає
coordinates `ua.lpnu.kzp:kzp-lab01-tanichkin:1.0.0`, Java 21 та UTF-8. Також я описав у ньому
JUnit 5, Maven Compiler Plugin, Surefire, SpotBugs, Maven Shade Plugin і build properties,
зокрема `ci.build.number`.

### 2. Чим відрізняються фази `test`, `verify` і `package`?

`test` виконує unit та integration tests. `verify` проходить попередні фази та запускає
додаткові перевірки; у моєму проєкті на цій фазі працює SpotBugs. `package` після попередніх
фаз створює виконуваний JAR. Команда `mvnw.cmd clean verify` спочатку видаляє каталог `target/`,
а потім виконує чисту перевірку.

### 3. Для чого потрібен Maven Wrapper?

`mvnw.cmd` у Windows і `./mvnw` у macOS або Ubuntu дають змогу запускати потрібну конфігурацію
Maven без вимоги вручну встановити однакову Maven-версію в кожній системі. Саме Wrapper я
використовую в локальних командах і GitHub Actions.

### 4. Яке призначення методу `main`?

`main(String[] args)` є entry point Java application. У `Main` він вибирає режим `--help` або
`--version`, розбирає `--input` і `--output`, визначає шляхи за замовчуванням та координує
processing pipeline. Сам `Main` не виконує валідацію рядків чи обчислення показників.

### 5. Чим відрізняються примітиви від `String`?

`int`, `double` і `boolean` є primitive values, а `String` є reference type, тобто об'єктом.
CSV спочатку читається як рядок `String`; після валідації я перетворюю `estimateHours` на
`double`, `priority` на `int`, а `done` інтерпретую як boolean.

### 6. Як правильно обчислити середнє значення з `double`?

Потрібно уникнути integer division. У `ProjectMetricsCalculator.averagePriority` я використав
фактичний рядок `return (double) totalPriority / validLines.size();`: cast робить лівий операнд
`double`, тому для суми 3 і кількості 2 результатом буде 1.5, а не 1.

### 7. Що станеться під час `Integer.parseInt("abc")`?

`Integer.parseInt("abc")` кидає `NumberFormatException`, бо текст не є цілим числом.
У validation logic я локально перехоплюю цю помилку для конкретного поля та повертаю причину
невалідності, тому один хибний рядок не завершує обробку всього файла.

### 8. Чому не можна мовчки пропускати некоректні рядки?

Користувач має знати, який рядок відхилено і чому. `ProjectFileReader` додає до причини номер
рядка, наприклад `Рядок 10: поле done має містити true або false`; таку поведінку можна
відтворити та перевірити тестами. Рядок не включається в metrics, але решта valid rows
обробляються далі.

### 9. Навіщо використовувати `split(";", -1)`?

Другий аргумент `-1` зберігає trailing empty fields. Наприклад, рядок
`title;assignee;1.0;3;` має п'яте, порожнє поле `done`, а не чотири поля. Це дає змогу
валідувати саме порожнє `done`, а не помилково повідомляти про неправильну кількість полів.

### 10. Чому `Path.of` є кросплатформним?

`Path.of("data", "input.csv")` будує шлях через API Java, який використовує правильний
separator для поточної ОС. Це переносиміше за жорстко заданий Windows-шлях
`"data\\input.csv"`; у програмі так само задається шлях `Path.of("out", "report.txt")`.

### 11. Навіщо явно вказувати UTF-8?

Я використовую `StandardCharsets.UTF_8` для читання input і запису report. Це не залежить від
default charset операційної системи та зберігає українські символи однаково на Windows, Ubuntu
і macOS.

### 12. Чим `%n` відрізняється від `\n`?

`%n` у `String.format` підставляє system line separator, тоді як `\n` є конкретним LF
character. У `ProjectReportFormatter` я застосовую `%n`, щоб formatted output не залежав від
припущення про Windows, macOS чи Linux.

### 13. Які правила validation реалізовано для варіанта 22?

Я перевіряю рівно п'ять полів. `title` і `assignee` мають бути nonblank; `estimateHours` —
коректним `double` і не меншим за нуль; `priority` — синтаксично коректним `int`; `done` —
`true` або `false` без урахування регістру після `trim()`. Я не ввів range для `priority`, бо
методичка його не встановлює.

### 14. Навіщо тестувати дробове середнє значення?

Тест лише з average 3.0 не виявив би помилки integer division. Тому я додав випадок із
priorities 1 і 2 та expected average 1.5; для `double` застосовано tolerance. Такий тест
безпосередньо підтверджує правильність explicit cast у формулі середнього.

### 15. Яка роль статичного аналізатора?

SpotBugs шукає потенційні програмні дефекти, які можуть не проявитися у звичайних tests.
У моєму Maven-проєкті він запускається під час `verify`; остання локальна перевірка дала
0 bugs і 0 errors.

### 16. Для чого використовується GitHub Actions?

GitHub Actions автоматично повторює build, tests, SpotBugs, packaging і artifact upload на
`ubuntu-latest`, `windows-latest` та `macos-latest`. Це перевіряє cross-platform behavior,
використовує Maven dependency cache і передає run number як CI build number.

### 17. Що має містити GitHub Issue для дефекту?

Issue для дефекту має містити зрозумілий title, опис проблеми, steps to reproduce, expected
result, actual result і relevant environment або context. Після виправлення Pull Request можна
пов'язати з Issue через `Closes #N`, щоб стан задачі змінювався прозоро.

### 18. Що потрібно зазначити про академічну доброчесність і AI?

Потрібно чесно вказати, чи використовувався AI, який інструмент і ролі застосовано, для яких
задач, які рекомендації прийнято, які помилки виправлено та що студент перевірив самостійно.
У розділі 10 я описав допоміжну роль AI і власну відповідальність за команди, тести, CI,
screenshots, метрики та розуміння submitted code.

### 19. Чим відрізняються ролі DevOps і Validator?

За фактичними файлами `ai/devops.md` і `ai/validator.md`, DevOps консультує щодо Maven,
Wrapper, SpotBugs, JAR, CI, artifact-ів і cross-platform build. Validator готує edge cases,
перевіряє результати й вимоги та описує лише реальні defects. Обидві ролі мають обмеження:
DevOps не змінює конфігурацію без рішення студента, а Validator не виправляє код і не вигадує
результати.

### 20. Який рядок програми найскладніше пояснити і чому?

Я обрав рядок `return (double) totalPriority / validLines.size();` з
`ProjectMetricsCalculator`. Він поєднує суму `int`, кількість valid rows, explicit cast,
запобігання integer division і повернення `double`. Я можу пояснити його тестом із
priorities 1 і 2, для якого результатом має бути 1.5.

## 12. Висновки

Я створив Java 21 Maven-проєкт для варіанта 22 «Проєктні задачі» та налаштував Maven Wrapper,
JUnit 5, SpotBugs і виконуваний JAR. Я перевірив, що `pom.xml`, Wrapper і GitHub Actions дають
змогу відтворено збирати, тестувати, перевіряти та пакувати проєкт на Windows, Ubuntu і macOS.

Я реалізував обробку UTF-8 CSV: програма перевіряє структуру запису та значення полів, не
перериває роботу через помилкові рядки й додає до повідомлення номер рядка та причину. Для
п'яти затверджених valid records я отримав count 5, total estimateHours 27.50, average priority
3.00 і done count 3. Report виводиться в консоль і записується у UTF-8 файл; CLI дає змогу
використовувати стандартні або передані користувачем шляхи.

Я перевірив програму unit та integration tests, крайовими випадками, `mvnw.cmd clean verify`,
SpotBugs і CI matrix. На поточному етапі локальна перевірка містить 57 tests без failures та
errors, а SpotBugs повідомляє 0 bugs і 0 errors. Git/GitHub workflow з Issues, feature branches,
Pull Requests, CI та JAR artifacts забезпечив контрольовану історію змін і докази
cross-platform behavior.

Я також підготував README, REPORT, Javadoc і чесний опис використання AI як допоміжного
інструмента. Цей код стане основою Lab 02: raw `String` і `String[]` буде замінено власними
classes без зміни зовнішньої поведінки програми.
