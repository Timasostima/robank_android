# Robank Android

Aplicación nativa de Android para el sistema Robank de gestión de finanzas personales.

## 🏗️ Arquitectura

Aplicación desarrollada siguiendo el patrón MVVM con Jetpack Compose:

```
└── src/main/
    ├── java/es/timasostima/robank/
    │   ├── enterApp/     # Autenticación y registro
    │   ├── home/         # Funcionalidades principales
    │   ├── charts/       # Visualizaciones financieras
    │   ├── config/       # Ajustes y configuración
    │   ├── database/     # Gestión de datos
    │   ├── dto/          # Objetos de transferencia
    │   ├── ui/theme/     # Sistema de diseño
    │   ├── notifications/ # Sistema de notificaciones
    │   ├── AppContent.kt # Navegación principal
    │   └── MainActivity.kt # Punto de entrada
    └── res/
        ├── drawable/     # Recursos gráficos
        ├── values/       # Recursos (EN)
        └── values-es/    # Localización español
```

## ✨ Funcionalidades

- **Autenticación completa**: Login con email y Google
- **Material Design 3**: Temas dinámicos adaptables
- **Multi-idioma**: Soporte para español e inglés
- **Gestión de gastos**: Registro y categorización
- **Metas financieras**: Seguimiento con progreso visual
- **Gráficos interactivos**: Análisis de gastos por categoría
- **Preferencias persistentes**: Tema, idioma y notificaciones
- **Perfil de usuario**: Gestión de datos personales y avatar

## 🛠️ Tecnologías

- **Kotlin 1.9**: Lenguaje principal
- **Jetpack Compose**: UI declarativa
- **Material 3**: Sistema de diseño
- **Firebase Auth**: Autenticación segura
- **Retrofit**: Comunicación API REST
- **Coil**: Carga y caché de imágenes
- **Compose Charts**: Gráficos interactivos
- **Corrutinas**: Operaciones asíncronas
- **Flow**: Programación reactiva
- **Accompanist**: Utilidades para Compose

## 📱 Requisitos

- Android 13+ (SDK 33)
- Google Play Services
- Conexión a internet

## ⚙️ Configuración

1. Clonar el repositorio
2. Configurar Firebase:
   - Crear proyecto en Firebase Console
   - Añadir app Android al proyecto
   - Descargar `google-services.json` y colocarlo en `/app`
3. Crear archivo `local.properties` con las siguientes propiedades:
   ```properties
   gcm_defaultSenderId=TU_SENDER_ID
   google_api_key=TU_API_KEY
   google_app_id=TU_APP_ID
   google_crash_reporting_api_key=TU_CRASH_KEY
   google_storage_bucket=TU_STORAGE_BUCKET
   google_server_client_id=TU_SERVER_CLIENT_ID
   firebase_database_url=TU_DATABASE_URL
   ```

4. Compilar y ejecutar la aplicación:
   ```bash
   ./gradlew assembleDebug
   ```

> [!TIP]
> Para desarrollo local, puedes configurar la URL de la API en `Config.kt`:
> ```kotlin
> object ApiConfig {
>     const val BASE_URL = "http://192.168.1.100:8080/api/"
> }
> ```

## 📌 Características destacadas

- **Temas dinámicos**: Adaptación automática al tema del sistema
- **Navegación fluida**: Transiciones suaves entre pantallas
- **Compartir datos**: Envío de información a otras apps
- **UI/UX optimizada**: Componentes reutilizables
- **Inyección de dependencias**: Gestión eficiente de servicios
- **Gestión de estados**: Arquitectura unidireccional de datos

## 👨‍💻 Autor

**Tymur Kulivar Shymanskyi**
- GitHub: [Timasostima](https://github.com/Timasostima)
- Email: contact@tymurkulivar.dev

---

*Desarrollado como parte del TFG para el ciclo de Desarrollo de Aplicaciones Multiplataforma.*
