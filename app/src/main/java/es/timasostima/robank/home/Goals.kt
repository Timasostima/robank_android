package es.timasostima.robank.home

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.maxkeppeler.sheets.input.models.InputTextFieldType
import com.maxkeppeler.sheets.input.models.ValidationResult
import es.timasostima.robank.R
import es.timasostima.robank.database.Database
import es.timasostima.robank.database.GoalData

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Goals(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    goalsList: MutableList<GoalData>,
    db: Database
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

        Box (modifier = Modifier.fillMaxSize()){
            goalsList.forEach {
                print(it.name + " goal ")
            }
            if (goalsList.isEmpty()) {
                Text(
                    "No goals yet",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .fillMaxWidth(),
                )
            }
            else{
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 15.dp, end = 15.dp, top = 70.dp)
//                    .sharedBounds(
//                        sharedContentState = rememberSharedContentState(key = "${list[0].name}"),
//                        animatedVisibilityScope = animatedContentScope
//                    )
                ){
                    items(goalsList) {
                        ExpandableGoal(
                            it,
                            modifier = if (goalsList.indexOf(it) == 0) modActive else mod,
                            active = goalsList.indexOf(it) == 0,
                            db
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
            ){
                Text("+", fontSize = 23.sp)
            }
            if (showGoalCreation){
                CreateAGoal(
                    closeSelection = { showGoalCreation = false },
                    db,
                    goalsList,
                    context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateAGoal(
    closeSelection: () -> Unit,
    db: Database,
    goalsList: MutableList<GoalData>,
    context: Context
) {
    val inputOptions = listOf(
        InputTextField(
            type = InputTextFieldType.OUTLINED,
            header = InputHeader(
                title = "Goal name:",
            ),
            validationListener = { value ->
                if ((value?.trim()?.length ?: 0) < 3) ValidationResult.Invalid(context.getString(R.string.name_needs_to_be_at_least_3_letters_long))
                else ValidationResult.Valid
            },
            required = true
        ),
        InputTextField(
            type = InputTextFieldType.OUTLINED,
            header = InputHeader(
                title = "Goal price:", ////////////////////////////////////////////////////////////////
            ),
            validationListener = { value ->
                if (value.isNullOrBlank()) ValidationResult.Invalid(context.getString(R.string.price_is_required))
                else if (value.toDoubleOrNull() == null) ValidationResult.Invalid(
                    context.getString(
                        R.string.price_must_be_a_number
                    ))
                else if (value.toDouble() <= 0) ValidationResult.Invalid(
                    context.getString(R.string.price_must_be_a_positive_number)
                )
                else ValidationResult.Valid
            },
            required = true
        )
    )

    InputDialog(
        state = rememberUseCaseState(visible = true, onCloseRequest = { closeSelection() }),
        selection = InputSelection(
            input = inputOptions,
            onPositiveClick = { result ->
                val name = result.getString("0") ?: ""
                val price = result.getString("1") ?: ""
                if (name.isNotBlank() && price.isNotBlank()) {
                    db.createGoal(GoalData(
                        name.trim(),
                        price.toDoubleOrNull() ?: 0.0,
                        (goalsList.maxByOrNull { it.index }?.index ?: 0) + 1
                    ))
                }
            },
        )
    )
}