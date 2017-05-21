jbo-home Haussteuerung - Howto update
=====================================

1) Server stoppen
- Dienste-Dialog öffnen
  (Windows-Taste + tippen "Dienste", auswählen)
- Dienst "Apache Tomcat 6.0..."
  rechte Maustaste Kontextmenü, "Beenden"
  
2) Neue Applikationsversion kopieren
- Server-Archivdatei "soo.war" aus gewünschter Version von Google-Drive herunterladen
- Dateiexplorer öffnen
- In Verzeichnis navigieren "C:\Programme\Apache\Tomcat 6.0\webapps"
  (Der exakte Pfad variiert je nach Installation.)
- Unterverzeichnis löschen "soo"
- "soo.war" mit heruntergelandener Datei überschreiben

3) Server starten
- Im Dienste-Dialog (siehe unter 1) Dienst "Apache Tomcat..." wieder starten
  rechte Maustaste Kontextmenü, "Starten"
  
4) Im Browser testen
- Browser aufrufen "http://localhost/soo/protected.jsp"
- Auf der Seite auswählen "Launch SO App via Java Web Start (JNLP)"
- Im gestarteten Viewer unter project "jbo-home" auswählen