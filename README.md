# Robank Android

[//]: # (![Robank Logo]&#40;../robank_frontend/public/Robank_Logo_Small.png&#41;)

Aplicación nativa de Android para el sistema Robank de gestión de finanzas personales.

## Arquitectura

Aplicación desarrollada siguiendo el patrón MVVM con Jetpack Compose:

```
└── src/main/
    ├── java/es/timasostima/robank/
    │   ├── enterApp/     # Autenticación
    │   ├── home/         # Dashboard principal
    │   ├── charts/       # Visualizaciones
    │   ├── ui/theme/     # Estilos Material 3
    │   └── AppContent.kt # Navegación principal
    └── res/
        ├── values/       # Recursos
        └── values-es/    # Localización español
```

## Funcionalidades

- **Material Design 3**: Interfaz moderna con temas dinámicos
- **Firebase Authentication**: Inicio de sesión seguro
- **Multi-idioma**: Soporte para español e inglés
- **Gestión de Gastos**: Control categorizado de gastos
- **Visualización de Datos**: Gráficos interactivos
- **Temas**: Soporte para modo claro y oscuro

> [!IMPORTANT]
> La aplicación requiere conexión a internet para sincronización con el backend.

## Tecnologías

- Kotlin 1.9
- Jetpack Compose
- Firebase Authentication
- Retrofit para comunicación API
- Material 3

## Requisitos

- Android 13+ (SDK 33)
- Conexión a internet

## Configuración

1. Clonar el repositorio
2. Configurar Firebase:
   - Crear proyecto en Firebase Console
   - Añadir app Android al proyecto
   - Descargar `google-services.json` y colocarlo en `/app`

> [!TIP]
> Para desarrollo local, puedes configurar la URL de la API en `Config.kt`:
> ```kotlin
> object ApiConfig {
>     const val BASE_URL = "http://192.168.1.100:8080/api/"
> }
> ```

## Autor

**Tymur Kulivar Shymanskyi**
- GitHub: [Timasostima](https://github.com/Timasostima)
- Email: contact@tymurkulivar.dev

## Licencia

Este proyecto está disponible bajo la Licencia MIT.

---

*Desarrollado como parte del TFG para el ciclo de Desarrollo de Aplicaciones Multiplataforma.*
