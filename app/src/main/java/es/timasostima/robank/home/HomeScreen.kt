package es.timasostima.robank.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.timasostima.robank.database.CategoryData
import es.timasostima.robank.database.Database
import es.timasostima.robank.database.GoalData

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    categoriesList: MutableList<CategoryData>,
    goalsList: MutableList<GoalData>,
    db: Database,
    currency: String
) {
    val curSym = when (currency){
        "usd" -> "$"
        "eur" -> "€"
        "rub" -> "₽"
        else -> "$"
    }
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                Home(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                    goalsList,
                    categoriesList,
                    db,
                    curSym
                )
            }
            composable("goals") {
                Goals(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                    goalsList,
                    db,
                    curSym
                )
            }
        }
    }
}



@Composable
fun LinearDeterminateIndicator(modifier: Modifier) {
    val currentProgress by remember { mutableStateOf(0.5f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = { currentProgress },
            trackColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
        )
    }
}