# KeyGUI

Ein professionelles Paper-Plugin (1.21.x, Java 21) zur Verteilung von
**CoreCrates**-Keys an alle Online-Spieler ueber ein modernes Inventar-GUI.
Alle Texte unterstuetzen **PlaceholderAPI**.

## Features

- `/keygui` (Alias `/keys`, Permission `keygui.use`) oeffnet das GUI.
- Das GUI laedt **automatisch alle vorhandenen Crates** aus CoreCrates.
- Klick auf einen Crate-Key gibt **allen online Spielern genau 1 Key** dieser
  Crate – ueber die offizielle CoreCrates-API (`CrateManager#createKeyItem`).
- Das GUI bleibt nach einem Klick geoeffnet und schliesst nur, wenn der Spieler
  es selbst schliesst (ESC).
- Konfigurierbarer Broadcast, Erfolgs-/Fehlermeldungen, Sounds und Cooldown.
- Voll konfigurierbar: GUI-Titel, -Groesse, Slots, Fuell-Items, Item-Material,
  -Name, -Lore, Farben (`&`-Codes **und** Hex `&#RRGGBB`).
- Automatische Erkennung von CoreCrates und PlaceholderAPI. Fehlt eine
  Abhaengigkeit, stuerzt das Plugin nicht ab, sondern gibt eine verstaendliche
  Konsolenmeldung aus.

## Build

```bash
cd KeyGUI
mvn package
```

Die fertige Datei liegt anschliessend unter `target/KeyGUI-1.0.0.jar`.

> **Hinweis zur CoreCrates-Abhaengigkeit:** CoreCrates wird nicht in einem
> oeffentlichen Maven-Repository veroeffentlicht. Die frei verfuegbare
> Plugin-JAR liegt daher unter `libs/CoreCrates-1.2.0.jar` und wird nur zur
> Compile-Zeit als `system`-Abhaengigkeit eingebunden – sie wird **nicht** in
> die finale Plugin-JAR gepackt. Zum Aktualisieren einfach die JAR austauschen
> und die Version in der `pom.xml` anpassen.

## Installation

1. `KeyGUI-1.0.0.jar` in den `plugins/`-Ordner des Servers legen.
2. CoreCrates (Pflicht fuer die Key-Vergabe) und optional PlaceholderAPI
   installieren.
3. Server starten und `config.yml` / `messages.yml` nach Wunsch anpassen.
4. `/keygui reload` laedt die Konfiguration zur Laufzeit neu
   (Permission `keygui.admin`).

## Konfiguration

- `config.yml` – GUI (Titel, Groesse, Slots, Fuell-Items, Item-Darstellung),
  Sounds, Cooldown und Broadcast-Schalter.
- `messages.yml` – alle Chat-Nachrichten inkl. Prefix.
