# UltimateDeathSwap

Paper-Plugin für Minecraft 1.21.11, Java 21 und Gradle 9.1.0.

## Bauen

Mit installiertem Java 21 und Internetverbindung:

```bash
./gradlew build
```

Unter Windows:

```bat
gradlew.bat build
```

Die Plugin-JAR liegt anschließend in `build/libs/UltimateDeathSwap-1.0.0.jar`.

Das enthaltene Bootstrap-Skript `gradlew` lädt beim ersten Start Gradle 9.1.0 herunter (es ist ein leichtes Download-Skript und nicht das offizielle Gradle-Wrapper-JAR). Die Paper-API wird beim Build automatisch aus dem PaperMC Maven-Repository bezogen.

## Installation

1. `UltimateDeathSwap-1.0.0.jar` in den `plugins`-Ordner des Paper-Servers legen.
2. Server starten.
3. Spieler treten mit `/deathswap join` bei.
4. Ein OP startet mit `/deathswap start`.
5. Mit `/effekte` öffnet ein Spieler seine drei Effekt-Slots.

## Befehle

- `/deathswap join` – dem nächsten Spiel beitreten
- `/deathswap leave` – Spiel verlassen
- `/deathswap start` – Spiel starten (OP)
- `/deathswap stop` – Spiel stoppen (OP)
- `/deathswap restart` – Spiel neu starten (OP)
- `/deathswap setlives <spieler> <anzahl>` – Leben setzen (OP)
- `/deathswap reload` – Konfiguration neu laden (OP)
- `/effekte` – Effekt-Inventar öffnen

## Technische Hinweise

- Das Spiel rotiert die gespeicherten Positionen der lebenden, online befindlichen Teilnehmer. Dadurch bekommt bei gerader und ungerader Spielerzahl jeder einen anderen Zielort; niemand wird sich selbst zugewiesen.
- Der Teleport nutzt die vor dem Swap gespeicherten Positionen und `teleportAsync`, damit parallele Teleports nicht gegenseitig die Zielposition verändern.
- Die drei Effekt-Slots sind ein serverseitiges GUI. Eine echte Erweiterung der Vanilla-Hotbar um zusätzliche Slots ist ohne Client-Mod nicht möglich. Die normale Hotbar und das Inventar werden daher nicht verändert.
- Spielerleben werden in `plugins/UltimateDeathSwap/players.yml` gespeichert.
- Der GitHub-Workflow baut bei Push und Pull Request und lädt die JAR als Artifact hoch.
