# bbs-template-addon — контекст проекта

> Этот файл автоматически загружается Claude Code в начале каждой сессии в этой папке.
> Держи его в актуальном состоянии.

## Что это за проект

**Темплейт аддона к моду BBS** (`bbs-fs` / `bbs_mod`). Это чистый скелет: рабочая сборка,
обе точки входа, один пример миксина, заготовка `ISourcePack` и runtime-локализация. На нём
дальше нарастает фактический функционал.

- **mod id:** `xavin`
- **package:** `org.xavin.xavin`
- **target:** Minecraft 1.20.1, Fabric, Java 17
- **зависимость на BBS:** `libs/bbs-2.5.2-1.20.1.jar` (подключён как `modImplementation files(...)`)

Архитектура скопирована с рабочего аддона `resfreshed-addon` (соседняя папка) — там же лежат
полноценные примеры миксинов/настроек/ассетов, если нужен образец.

## Точки расширения (что где трогать)

| Что | Файл | Зачем |
|---|---|---|
| **Addon entry (common)** | `main/.../XavinAddon.java` | `BBSAddonMod`, `@Subscribe`-события BBS, хранит статические настройки. Регистрируется через entrypoint `bbs-addon`. |
| **Client entry** | `client/.../XavinClient.java` | `ClientModInitializer`. Регистрирует строки l10n. |
| **Пример миксина** | `main/.../mixin/BBSSettingsMixin.java` | Добавляет группу настроек `xavin` в категорию personalization. Канонический паттерн `@Inject`. |
| **Source pack (ассеты)** | `main/.../resources/XavinAssetsSourcePack.java` | `ISourcePack` для подачи/override ассетов в `AssetProvider` BBS. Сейчас пустой (ничего не отдаёт). |
| **Локализация** | `client/.../XavinStrings.java` | Ставит content lang-ключей в рантайме на `L10nReloadEvent`, без своих lang-файлов. |
| **Конфиги миксинов** | `main/resources/xavin.mixins.json` (common) + `xavin.client.mixins.json` (client, пока пустой) | Список миксинов. Клиентские миксины добавлять во второй. |
| **Access widener** | `main/resources/xavin.accesswidener` | Пустой заголовок. Добавлять строки по мере нужды. |

### Как добавлять функционал
1. **Самодостаточный код** → обычные классы аддона (не миксины).
2. **Точечная правка BBS** (цвет/ветка/значение) → `@Inject` / `@ModifyExpressionValue` / `@Redirect`. Устойчиво к апстрим-апдейтам.
3. **Целиком переписанный метод** → `@Overwrite` (хрупко, привязка к версии BBS — держать список коротким).
4. **Новые/override ассеты** → через `XavinAssetsSourcePack` (см. доки в файле: `register` vs `registerFirst`).
5. **Клиентский миксин** → класс в `mixin/client/` + имя в `xavin.client.mixins.json`.

> Не угадывать API BBS — сверяться с исходником в `bbs-fs` и примерами в `resfreshed-addon`.

## Окружение сборки

- Fabric Loom `1.15-SNAPSHOT`, Gradle `9.2.0` (wrapper), MC `1.20.1`, yarn `1.20.1+build.10`,
  loader `0.16.14`, fabric-api `0.92.1+1.20.1`, Java 17.
- Split source sets (`main` + `client`) — общий код в `main`, клиент-только в `client`.

```
./gradlew build            # собрать jar → build/libs/xavin-0.1.0-1.20.x.jar
./gradlew runClient        # запустить dev-клиент с аддоном
```

## Источники (абсолютные пути)

| Роль | Путь |
|---|---|
| **Образец-аддон (полные примеры)** | `C:\Users\Qualet\Documents\Project\Minecraft\BBS\resfreshed-addon` |
| **Исходник BBS (upstream)** | `C:\Users\Qualet\Documents\Project\Minecraft\BBS\bbs-fs` |
| **Этот проект** | `C:\Users\Qualet\Documents\Project\Minecraft\BBS\bbs-template-addon` |

## Договорённости

- **Поэтапно.** Одна фича = отдельная проверка в игре + коммит. **Коммит — только по явному «ОК».**
- **Базу BBS не трогаем** — все правки живут в аддоне через миксины/события.
- **Лог правок** — терсово, на английском (как в коде).
