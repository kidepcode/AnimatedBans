# AnimatedBans

Paper 1.21.x plugin that plays an animation sequence for a target player and then executes a configurable tempban command.

## Commands
- `/animatedbans ban <player> <time> <reason...>`
- `/animatedbans reload`

## Permissions
- `animatedbans.ban` (default: op)
- `animatedbans.reload` (default: op)

## Build
Requires Java 21.

```bash
./gradlew clean build
