# BBS VFX — актуальная память проекта

> Этот файл — рабочая память для ассистента. `CLAUDE.md` и `README.md` сейчас частично устарели:
> они описывают проект как чистый template/skeleton, но в коде уже лежит полноценный VFX toolkit.

## Что это

**BBS VFX** — Fabric-аддон к моду **BBS** (`bbs_mod`).

- mod id: `xavin`
- package: `org.xavin.xavin`
- root этого аддона: `D:/BBS VFX/bbs-template-addon`
- рядом лежит upstream BBS source: `D:/BBS VFX/bbs-fs`
- папка с идеями/концептами/референсами: `D:/concept`
- target по умолчанию: Minecraft `1.20.1`, Fabric, Java `17`
- сборка: `./gradlew build`
- запуск dev-клиента: `./gradlew runClient`
- multi-version в `build.gradle`: `-Pmc=1.20.1`, `-Pmc=1.20.4`, `-Pmc=1.21.1` (для 1.21.1 нужен Java 21)

## Главные договорённости

- Базу BBS (`bbs-fs`) не трогаем. Все правки делаем в аддоне через миксины/события/формы/рендер.
- Работаем поэтапно и минимально.
- Коммит только по явному “ОК” от пользователя.
- Не угадывать API BBS — сверяться с исходником `bbs-fs` и уже существующими миксинами аддона.
- Старые абсолютные пути из `CLAUDE.md` вида `C:\Users\Qualet\...` считать неактуальными; актуальный корень — `D:/BBS VFX`.

## Вкус и референсы

- `D:/concept/puss-in-boots-puss-in-boots-wolf.mp4` — пользователю особенно понравился момент, где волк проходит сквозь “портал”: тёмный силуэт + стильная contour/rim outline вокруг тела, magenta/cyan палитра, glitch/chromatic streaks, отражение на полу. Хороший ориентир для aura/portal/impact эффекта.

## Связанные проекты

- `D:/VFX LIGHTS` — sibling-проект, публичный аддон искусственного освещения для BBS. У него есть свой авторитетный `D:/VFX LIGHTS/CLAUDE.md`; при работе с ним доверять ему и коду.
  - mod id `vfxlights`, package `org.xavin.vfxlights`, MC `1.20.1`, Java `17`.
  - Подключается к BBS VFX как модуль через entrypoint `vfx-module` (`VfxLightsModule`), поэтому `bbs-template-addon` для него — core hub.
  - Composite build: `D:/VFX LIGHTS/settings.gradle` включает `../BBS VFX/bbs-template-addon`.
  - Главная идея: свет — не только формы; эффекты должны подавать эфемерные источники каждый кадр через публичный API `VfxLights` (`point/sphere/spot/rect/tube/ambientZone`).
  - Важная лицензионная граница: декомпилы IRLite/Photonics в `D:/concept/light` — только референс механизмов; код оттуда не заимствовать.

## Текущий функционал BBS VFX

Кратко: это VFX toolkit для BBS — destruction/physics, explosion, beam, dome, wind, 3D curves,
smear frames, motion lines, blend modes, label/text overhaul, impact frames, camera export.

### Модульная система

- `org.xavin.xavin.module.VfxModule` — контракт внешнего VFX-модуля.
- `org.xavin.xavin.module.VfxModules` — hub/registry модулей.
- Внешний мод подключается через Fabric entrypoint `vfx-module`.
- Для каждого модуля есть toggle в настройках VFX.
- При выключении модуль должен проверять `VfxModules.isEnabled(moduleId)` и гасить свои hooks.

### Формы

- `DestructionBoxForm` — захваченная структура блоков, которая “рассыпается” по параметру `destruction`.
  - режимы: обычный blast, point destruction, PhysX physics;
  - направление, радиальный разлёт, random scatter, tumble, gravity, friction, bounciness;
  - world colliders, ground plane, explosion impulse, shaped cone blast;
  - persistent bake/cache логика живёт вокруг destruction-кода аддона.
- `ExplosionForm` — наследник Destruction Box.
  - скан блоков сферой/полусферой вокруг эпицентра;
  - blast wind, sustained wind, suction/negative phase;
  - fire, smoke, dust, mushroom cloud / sphere fireball, sparks.
  - **Sparks fountain**: отдельная текстура `fx/spark` (длинная тонкая полоса), kind `K_SPARK`, emissive pass. Искры вылетают из ядра взрыва в верхнее полушарие, летят по дуге под гравитацией + air drag, живут до 30 с (default 12 с). Длинный motion-streaked шлейф + яркое белое ядро поверх. Параметры: `spark_count`, `spark_size`, `spark_speed`, `spark_gravity`, `spark_life`, `spark_spread`, `spark_color_*`.
- `BeamForm` — энергетический луч.
  - core/glow/helix/rings/dashes/ground glow;
  - всё closed-form от `progress`, scrub точный;
  - опционально “выедает” террейн в месте удара.
- `DomeForm` — expanding energy dome.
  - beam tail lead-in, white-out flash, shockwave ring, lightning, dust;
  - ground cracks, rising smoke;
  - world-levelling destruction по фронту волны.
- `WindForm` — зональный ambient wind.
  - directional / storm / tornado параметры;
  - visible wind: streaks, leaves, dust;
  - foliage sway через scan + cut-and-replace.
- `CurveForm` — 3D Catmull-Rom curve.
  - line / solid tube / swept block extrusion;
  - taper, trim, per-point width, animatable control points;
  - связанная камера `follow_curve`.

### Дополнительные системы

- Smear frames / motion lines:
  - keyframe factories регистрируются в `XavinAddon`;
  - client editors регистрируются в `XavinClient`.
- Blend modes:
  - `blend_mode` legacy channel;
  - `blend` per-bone pose channel;
  - много blend-миксинов в client renderer.
- Label/text overhaul:
  - `XavinFontManager`, `XavinFontTexture`, label overlay/blend/projection classes.
- Impact frames:
  - camera clip/modifier `xavin:impact`;
  - full-screen post pass через `XavinImpactShader` и связанные миксины.
- Camera export:
  - `XavinAeTracker` — After Effects `.jsx` camera export;
  - `XavinGlbWriter` — Blender `.glb` animated camera export.
- Shader/post pipeline:
  - custom core shaders для destruction, beam heat, dome volume, cracks, smoke, wind, explosion smoke;
  - depth capture на `WorldRenderEvents.LAST` / `AFTER_ENTITIES` для совместимости с Sodium/Iris/shaderpacks;
  - OIT через `XavinOIT`.

## Ключевые файлы

- `src/main/java/org/xavin/xavin/XavinAddon.java` — common BBS addon entry, source pack registration, keyframe factories.
- `src/client/java/org/xavin/xavin/client/XavinClient.java` — client wiring форм, renderer’ов, шейдеров, camera clips, depth capture.
- `src/main/java/org/xavin/xavin/module/VfxModules.java` — registry/toggles VFX-модулей.
- `src/main/java/org/xavin/xavin/module/VfxModule.java` — интерфейс VFX-модуля.
- `src/main/java/org/xavin/xavin/forms/*` — common form data/models.
- `src/client/java/org/xavin/xavin/client/*` — renderers, UI editors, shaders/post FX, export, capture, physics.
- `src/main/resources/xavin.mixins.json` — common mixins.
- `src/main/resources/xavin.client.mixins.json` — client mixins.
- `src/main/resources/fabric.mod.json` — entrypoints, dependencies, описание мода.
- `src/main/resources/assets/xavin/shaders/core/*` — custom shaders.

## Что считать устаревшим

- `CLAUDE.md` — полезен как старая инструкция по расширению, но НЕ отражает текущий объём функционала.
- `README.md` — тоже описывает проект как skeleton/template.
- Если расходятся `CLAUDE.md`/`README.md` и этот `MEMORY.md` — доверять `MEMORY.md` и коду.

## VFX LIGHTS — статус багфиксов (2026-07-26)

Сборки для проверки лежат в `D:/BBS VFX/test-kit-physicsmod/`:
- `BBS-VFX-build-1.2-1.20.1.jar` — core hub,
- `VFX-LIGHTS-0.1.0-1.20.1.jar` — модуль света.

| Баг | Статус | Фикс |
|---|---|---|
| Тени мира пропадают, если на актёре в bodypart висит свет | применён | `LightFormRenderer.gizmosVisible()` теперь возвращает `false` во время собственного теневого прохода (`ShadowMapper.isActive()`), чтобы wireframe гизмо лампы не резал мировые тени. |
| Volumetric beam / haze пропадает при включённом rim light | применён (компенсация) | Rim dial теперь передаётся в `light_volumetric.fsh` через `LightExtra.w`; shader умножает air glow на `1.0 + rim * 0.5`, чтобы beam оставался читаемым на LDR-экране, который rim specular может забить до белого. |
| При большом количестве источников у части пропадают тени | уже было | `ShadowAtlas.TILES_ACROSS` уже `8` (64 тайла), что даёт ~10 point/area lights до переполнения. |
| После закрытия редактора свет вспыхивает ярче на пару секунд | уже было | `LightRegistry.clearAllPersisted()` + `MinecraftClientMixin` сбрасывает persisted-свет при переходе `UIScreen → не UIScreen`. |

### Что ещё надо проверить / довести

- **Rim + beam:** фикс — компенсация яркости, а не устранение root-cause. Если на реальной сборке beam всё ещё «пропадает полностью», нужна диагностика: запустить с `VFXLIGHTS_PROBE=1`, посмотреть, уходит ли beam на уровне Java (`VolumetricPass` не рисует ни одного draw) или в шейдере (draw есть, но цвет чёрный/белый). Возможно, требуется HDR-composite или clamping surface specular, а не boost volumetric.
- **Bodypart light + тени:** если после фикса гизмо тени мира всё ещё ломаются, искать дублирование источника в `CharacterMask.capture()` / `FormLightCollector.collect()` или конфликт теневых тайлов.
- **Много источников:** если 10 point/area lights недостаточно, атлас придётся расти дальше (`TILES_ACROSS`/`TILE_SIZE`) — вопрос памяти vs. качество.
