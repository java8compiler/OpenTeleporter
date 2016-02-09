# OpenTeleporter
This addon adds computer-driven teleporters.

## Usage
1. Place two teleporters, connect them to computers. (They can even be connected to different networks!)
2. Do the magic!

## Funcs
### Teleporter
* `teleporter.teleport(destTPAddress: string)` - teleports all the entities on the teleporter to another teleporter with the given name.
* `teleporter.teleportById(uuid: string)` - teleports entity with the given UUID.
* `teleporter.getEntitiesId()` - returns entities on the teleporter and their UUIDs.
* `teleporter.getEnergyToTeleport(destTPAddress: string)` - returns the amount of energy for teleporting.
* `teleporter.getDistance(destTPAddress: string)` - returns the distance between teleporters.
