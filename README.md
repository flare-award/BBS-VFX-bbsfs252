# BBS VFX

Cinematic VFX addon for the [BBS mod](https://github.com/mchorse/bbs) (Minecraft, Fabric).
Keyframable effects as BBS forms — volumetric explosions, energy beams, destruction physics,
wind, motion smear, impact frames and more — built for machinima and film production.

- **author:** Xavin
- **mod id:** `bbsvfx`
- **MC / Java:** 1.20.1 / 17 (multi-version: 1.20.4 via `-Pmc=1.20.4`, 1.21.1 via `-Pmc=1.21.1`)

## Features

- **Explosion** (`bbsvfx:explosion`) — volumetric raymarched fireball, ridged fire, smoke/fire shaping
- **Beam** (`bbsvfx:beam`) — energy beam, emissive in form-pass, depth prepass, heat haze
- **Dome** (`bbsvfx:dome`) — AKIRA/Fate-style climax dome, world levelling, GPU instancing
- **Wind** (`bbsvfx:wind`) — zonal wind, foliage sway, tornado vortex, presets
- **Destruction Box** — block capture, PhysX physics, baked simulations, persistent bakes
- **Smear / motion lines** — per-bone smear, arc/vector modes, texture-alpha dissolve
- **Impact frame** — post-shader: silhouette, ink burst, shockwave, presets
- **Blend modes** — whole-form and per-bone, fixed-function + shader modes, offscreen composite
- **Label overhaul** — custom fonts, blend modes, outline, tracking, text projection, gradients
- **Camera export** — After Effects `.jsx` (direct MC→AE) and Blender GLB (quaternions, animated FOV)

Effects are organized as toggleable modules (settings → VFX category).

Companion addon: [VFX LIGHTS](https://github.com/xavineditor/VFX-LIGHTS) — cinematic lighting
(point/spot/area lights, shadow maps, shaderpack patching, volumetrics).

## Build & run

```sh
./gradlew build       # -> build/libs/BBS-VFX-build-1.2-1.20.1.jar
./gradlew runClient   # dev client with the addon loaded
```

> Requires JDK 17. The BBS jar in `libs/` is what the addon compiles against.

## License

MIT — see `LICENSE`.
