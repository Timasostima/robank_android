package es.timasostima.robank

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.timasostima.robank.charts.Charts
import es.timasostima.robank.config.ConfigScreen
import es.timasostima.robank.config.PreferencesManager
import es.timasostima.robank.config.ThemeMode
import es.timasostima.robank.database.BillData
import es.timasostima.robank.database.CategoryData
import es.timasostima.robank.database.Database
import es.timasostima.robank.database.GoalData
import es.timasostima.robank.database.PreferencesData
import es.timasostima.robank.enterApp.AccountManager
import es.timasostima.robank.home.GoalManager
import es.timasostima.robank.home.HomeScreen
import es.timasostima.robank.ui.theme.RobankTheme
import java.util.Locale

@Composable
fun App(
    changeMode: (Boolean?) -> Unit,
    db: Database,
    accountManager: AccountManager,
    loginNav: NavHostController
) {
    val userId = db.userId

    val preferencesManager = remember {
        PreferencesManager()
    }
    // Observe preferences from the PreferencesManager
    val preferences by preferencesManager.preferencesState.collectAsState()
    val theme by preferencesManager.themeState.collectAsState()

    LaunchedEffect(theme) {
        changeMode(ThemeMode.toBooleanForDarkMode(theme))
    }

    val categoriesList: MutableList<CategoryData> = mutableListOf()
    val billsList: MutableList<BillData> = mutableListOf()
    val goalManager = remember { GoalManager() }
    val goals by goalManager.goalsState.collectAsState()

    db.loadCategories(categoriesList)
    db.loadBills(billsList)

    val mainController = rememberNavController()
    val navBackStack by mainController.currentBackStackEntryAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MainNavigationBar(mainController, navBackStack) }
    ) { innerPadding ->
        NavHost(
            navController = mainController,
            startDestination = "home",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(categoriesList, goals, goalManager, db, preferences?.currency ?: "eur")
            }
            composable("config") {
                ConfigScreen(changeMode, loginNav, accountManager, preferencesManager)
            }
            composable("charts") {
                Charts(billsList, categoriesList, preferences?.currency ?: "eur")
            }
        }
    }
}

@Composable
fun MainNavigationBar(
    mainController: NavHostController,
    navBackStack: NavBackStackEntry?
) {
    NavigationBar(
        tonalElevation = 25.dp,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.topBorder(1.dp, MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        items.forEach { item ->
            val isSelected = item.title.lowercase() == navBackStack?.destination?.route
            NavigationBarItem(
                selected = isSelected,
                icon = {
                    val tint by animateColorAsState(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer, label = ""
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(item.icon),
                        contentDescription = item.title,
                        tint = tint
                    )
                },
                onClick = {
                    mainController.navigate(item.title.lowercase()) {
                        popUpTo(mainController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

data class BottomNavigation(
    val title: String,
    val icon: Int
)

val items = listOf(
    BottomNavigation("Config", R.drawable.config),
    BottomNavigation("Home", R.drawable.home),
    BottomNavigation("Charts", R.drawable.charts)
)

fun Modifier.topBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width

            drawLine(
                color = color,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = width, y = 0f),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Preview
@Composable
fun ButtomBar() {
    RobankTheme {
        MainNavigationBar(rememberNavController(), null)
    }
}
