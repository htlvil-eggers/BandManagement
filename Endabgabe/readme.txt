Da wir keinen Webspace zur Verfügung hatten, müssen vor dem Starten des Projekts die Ziel-IP-Adressen der Clients für die Webservices eingestellt werden.

Man findet so folgendermaßen:

Android-Client: pkgModel.WebserviceManager
Java-Client: pkgModel.WebserviceManager
WPF-Client: Model.WebserviceManager (gilt auch für den Spatial-Client)
Web-Client: form.js in der ersten Zeile die Variable "url"