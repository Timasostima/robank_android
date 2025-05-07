package es.timasostima.robank.config

enum class ThemeMode(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("night");

    companion object {
        fun fromString(value: String): ThemeMode =
            ThemeMode.entries.find { it.value == value } ?: SYSTEM

        fun toBooleanForDarkMode(mode: ThemeMode): Boolean? = when(mode) {
            SYSTEM -> null
            LIGHT -> false
            DARK -> true
        }
    }
}