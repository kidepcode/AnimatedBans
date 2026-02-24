# AnimatedBans

**AnimatedBans** is a lightweight Paper 1.21.x plugin that plays a configurable **ban animation** (freeze + lift + visual effect + title/subtitle) and then executes a **server command**.

It does **not** implement bans by itself — instead, it can work with **any punishment plugin** (LiteBans, AdvancedBan, LibertyBans, Vanilla `/ban`, custom commands, etc.) by changing the command template in the configuration.

---

## Showcse
(blocking disabled)

https://github.com/user-attachments/assets/f146a83b-c5ea-468e-8c28-adb31a04ab9a

## Features

- Paper **1.21.x** support (**Java 21**)
- MiniMessage for titles and messages
- Animation flow:
  - freezes the target (movement + interactions)
  - lifts the target upwards
  - plays a particle animation (“Rune Cage”)
  - shows title/subtitle from the beginning and every interval
  - optional small end explosion
  - optional kill target before the ban command
  - executes configured ban command

---

## Requirements

- **Paper** `1.21.x`
- **Java 21**

---

## Commands

### Main
- `/animatedbans ban <player> <time> <reason...>`
- `/animatedbans reload`

### Examples
- `/animatedbans ban Notch 7d Cheating (KillAura)`
- `/animatedbans ban Steve 30m Xray`

---

## Permissions

- `animatedbans.ban` — allows running the animation ban command (default: `op`)
- `animatedbans.reload` — allows reloading configuration (default: `op`)

---

## Compatibility with any ban/punishment plugin

AnimatedBans only executes a command after the animation ends.

You can use **any** ban plugin or even custom commands by editing:

```yml
banCommand:
  template: "tempban {player} {time} {reason}"
