# Приложение и веб-приложение для создания голосований на основе анонсов турниров спотивного ЧГК

## Сборка приложения

1. Сначала нужно собрать FatJar (запускать в пакете desktop)
```bash
mvn clean package spring-boot:repackage 
```
2. Потом нужно собрать само приложение для установки (запускать в пакете desktop)
```bash
mvn jpackage:jpackage
```
3. Собранный установщик будет в папке desktop/target/standalone - это можно изменить, поменяв значения в pom.xml модуля desktop
```xml   
   <name>chgk-desktop</name> <!-- имя приложения -->
   <type>PKG</type> <!-- тип упаковки: APP_IMAGE app-image, DMG dmg (для MacOS), PKG pkg (для MacOS),
    EXE exe (для Windows), MSI msi (для Windows), DEB deb (для Linux) -->
   <destination>${project.build.directory}/standalone</destination> <!-- место сохранения собранного приложения -->
```