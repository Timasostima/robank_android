package es.timasostima.robank.home

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import es.timasostima.robank.R
import es.timasostima.robank.database.GoalManager
import es.timasostima.robank.dto.GoalDTO
import es.timasostima.robank.dto.GoalData

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Goals(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    goalsList: List<GoalData>,
    goalManager: GoalManager,
    currency: String
) {
    val context = LocalContext.current
    with(sharedTransitionScope) {
        val mod = Modifier
            .height(170.dp)
            .clip(RoundedCornerShape(10.dp))
        val modActive = Modifier
            .sharedElement(
                rememberSharedContentState(key = "image"),
                animatedVisibilityScope = animatedContentScope
            )
            .height(170.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )

        Box(modifier = Modifier.fillMaxSize()) {
            if (goalsList.isEmpty()) {
                Text(
                    stringResource(R.string.no_goals_yet),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .fillMaxWidth(),
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 15.dp, end = 15.dp, top = 70.dp)
                ) {
                    items(goalsList) {
                        ExpandableGoal(
                            it,
                            modifier = if (goalsList.indexOf(it) == 0) modActive else mod,
                            active = goalsList.indexOf(it) == 0,
                            goalManager = goalManager,
                            currency = currency
                        )
                    }
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp, 10.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .clickable {
                        navController.navigate("home") {
                            popUpTo("home") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    .size(55.dp)
                    .padding(15.dp, 15.dp)
            )
            var showGoalCreation by remember { mutableStateOf(false) }
            Button(
                onClick = { showGoalCreation = true },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .height(60.dp)
                    .width(60.dp)
            ) {
                Text("+", fontSize = 23.sp)
            }
            if (showGoalCreation) {
                CreateAGoal(
                    closeSelection = { showGoalCreation = false },
                    goalManager = goalManager,
                    context = context
                )
            }
        }
    }
}

@Composable
fun CreateAGoal(
    closeSelection: () -> Unit,
    goalManager: GoalManager,
    context: Context
) {
    var goalName by remember { mutableStateOf("") }
    var goalPrice by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    fun validateName(name: String): Boolean {
        return if (name.trim().length < 3) {
            nameError = context.getString(R.string.name_needs_to_be_at_least_3_letters_long)
            false
        } else {
            nameError = null
            true
        }
    }

    fun validatePrice(price: String): Boolean {
        return when {
            price.isBlank() -> {
                priceError = context.getString(R.string.price_is_required)
                false
            }
            price.toDoubleOrNull() == null -> {
                priceError = context.getString(R.string.price_must_be_a_number)
                false
            }
            price.toDouble() <= 0 -> {
                priceError = context.getString(R.string.price_must_be_a_positive_number)
                false
            }
            else -> {
                priceError = null
                true
            }
        }
    }

    AlertDialog(
        onDismissRequest = closeSelection,
        title = {
            Text(
                text = stringResource(R.string.add),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                Column {
                    Text(
                        text = stringResource(R.string.goal_name),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = goalName,
                        onValueChange = {
                            goalName = it
                            validateName(it)
                        },
                        isError = nameError != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    nameError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                // Price field
                Column {
                    Text(
                        text = stringResource(R.string.goal_price),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = goalPrice,
                        onValueChange = {
                            goalPrice = it
                            validatePrice(it)
                        },
                        isError = priceError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    priceError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isNameValid = validateName(goalName)
                    val isPriceValid = validatePrice(goalPrice)

                    if (isNameValid && isPriceValid) {
                        goalManager.createGoal(
                            GoalDTO(
                                name = goalName.trim(),
                                price = goalPrice.toDoubleOrNull() ?: 0.0,
                                index = 0 // The index is now calculated in GoalManager
                            )
                        )
                        closeSelection()
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = closeSelection) {
                Text(stringResource(R.string.i_reject))
            }
        }
    )
}
