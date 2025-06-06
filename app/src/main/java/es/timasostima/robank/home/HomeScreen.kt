package es.timasostima.robank.home

import android.graphics.Bitmap
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
import androidx.compose.runtime.LaunchedEffect
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
import es.timasostima.robank.database.BillManager
import es.timasostima.robank.database.CategoryManager
import es.timasostima.robank.database.GoalManager
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.dto.GoalData

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    categoriesList: List<CategoryData>,
    categoryManager: CategoryManager,
    goalsList: List<GoalData>,
    goalManager: GoalManager,
    billManager: BillManager,
    currency: String
) {
    val firstGoalImageState = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(goalsList) {
        if (goalsList.isNotEmpty()) {
            val firstGoalId = goalsList.first().id
            firstGoalImageState.value = goalManager.getGoalImage(firstGoalId)
        }
    }

    val curSym = when (currency) {
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
                    categoryManager,
                    categoriesList,
                    billManager,
                    curSym,
                    firstGoalImageState
                )
            }
            composable("goals") {
                Goals(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                    goalsList,
                    goalManager,
                    curSym,
                    firstGoalImageState
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
            progress = currentProgress,
            trackColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
        )
    }
}
