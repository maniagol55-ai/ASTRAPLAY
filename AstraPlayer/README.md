# MichiTV 🛰

App Android IPTV completa con login, canales, favoritos, control parental y EPG.

## Características
- ✅ Login con validación XML (enabled=true/false)
- ✅ Lista de 189 canales desde el JSON
- ✅ Búsqueda y filtro por categorías
- ✅ Favoritos guardados localmente
- ✅ Control parental con PIN para canales adultos
- ✅ EPG (guía de programación genérica)
- ✅ Reproductor ExoPlayer (HLS/HTTP)
- ✅ Sesión guardada (no necesitas loguearte cada vez)

## Cómo compilar

### Requisitos
- Android Studio Hedgehog o superior
- JDK 8+
- Android SDK 34

### Pasos
1. Descomprime el ZIP
2. Abre **Android Studio** → *Open an Existing Project* → carpeta `AstraPlayer`
3. Espera que sincronice Gradle (~2-3 minutos primera vez)
4. **Build → Build APK(s)**
5. APK en: `app/build/outputs/apk/debug/app-debug.apk`

## Canales adultos (control parental)
Se detectan automáticamente por nombre: playboy, venus, sextreme, adult, xxx, erotic, private, penthouse, hustler, vivid.
