package es.timasostima.robank.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.color.ColorDialog
import com.maxkeppeler.sheets.color.models.ColorConfig
import com.maxkeppeler.sheets.color.models.ColorSelection
import com.maxkeppeler.sheets.color.models.ColorSelectionMode
import com.maxkeppeler.sheets.color.models.MultipleColors
import com.maxkeppeler.sheets.color.models.SingleColor
import es.timasostima.robank.R
import es.timasostima.robank.config.CategoryPicker
import es.timasostima.robank.database.BillManager
import es.timasostima.robank.database.CategoryManager
import es.timasostima.robank.dto.BillDTO
import es.timasostima.robank.dto.CategoryDTO
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.dto.GoalData
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalStdlibApi::class)
@Composable
fun Home(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    goalsList: List<GoalData>,
    categoryManager: CategoryManager,
    categoriesList: List<CategoryData>,
    billManager: BillManager,
    currency: String,
    firstGoalImageState: MutableState<Bitmap?>
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 15.dp, end = 15.dp, top = 25.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    stringResource(R.string.current_goal),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                val goal =
                    goalsList.firstOrNull() ?: GoalData(0, stringResource(R.string.example), 0.0, 0)
                with(sharedTransitionScope) {
                    BasicGoal(
                        goal = goal,
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                navController.navigate("goals") {
                                    launchSingleTop = true
                                }
                            },
                        currency = currency,
                        firstGoalImageState
                    )
                }
            }
        }

        item {
            var billName by remember { mutableStateOf("") }
            var billAmount by remember { mutableStateOf("") }

            var pickedCategory by remember {
                mutableStateOf(
                    CategoryData(0, context.getString(R.string.choose_the_category), "")
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text(
                    stringResource(R.string.add_the_transaction),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = billName,
                    onValueChange = { billName = it },
                    label = { Text(stringResource(R.string.name), fontSize = 20.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                OutlinedTextField(
                    value = billAmount,
                    onValueChange = { billAmount = it },
                    label = { Text(stringResource(R.string.amount), fontSize = 20.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                CategoryPicker(categoriesList, pickedCategory) { pickedCategory = it }

                val canCreate = billName.isNotEmpty()
                        && billAmount.isNotEmpty() && billAmount.toDoubleOrNull() != null
                        && pickedCategory.name != stringResource(R.string.choose_the_category)
                Button(
                    onClick =
                    {
                        billManager.createBill(
                            BillDTO(
                                name = billName,
                                amount = billAmount.toDouble(),
                                categoryId = pickedCategory.id,
                                date = LocalDate.now().format(dateFormatter).toString(),
                                time = LocalTime.now().format(timeFormatter).toString()
                            )
                        )
                        billName = ""
                        billAmount = ""
                        pickedCategory =
                            CategoryData(0, context.getString(R.string.choose_the_category), "")
                    },
                    enabled = canCreate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }

        item {
            val templateColors = MultipleColors.ColorsInt(
                Color(0xFFB79AE8).toArgb(),
                Color(0xFF7ED7C1).toArgb(),
                Color(0xFFFFAEBA).toArgb(),
                Color(0xFFC3E88D).toArgb(),
                Color(0xFFFFD787).toArgb(),
                Color(0xFF82AAFF).toArgb(),
                Color(0xFFF07178).toArgb(),
                Color(0xFFBBBBD4).toArgb(),
            )

            var categoryName by remember { mutableStateOf("") }
            var selectedColor by remember { mutableStateOf(Color(0xFFB79AE8)) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text(
                    stringResource(R.string.create_a_category),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text(stringResource(R.string.name), fontSize = 20.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                ColorPicker(selectedColor, templateColors) {
                    selectedColor = it
                }

                Button(
                    onClick = {
                        categoryManager.createCategory(
                            CategoryDTO(
                                name = categoryName,
                                color = "#" + selectedColor
                                    .toArgb().toHexString().drop(2).uppercase()
                            )
                        )
                        categoryName = ""
                    },
                    enabled = categoryName.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 25.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text(
                    stringResource(R.string.need_help),
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        context.sendMail(
                            to = context.getString(R.string.app_email), subject = context.getString(
                                R.string.email_subject
                            )
                        )
                    },
                ) {
                    Text(stringResource(R.string.contact_us))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun ColorPicker(
    value: Color,
    templateColors: MultipleColors.ColorsInt,
    onChangeValue: (Color) -> Unit
) {
    var open by remember { mutableStateOf(false) }

    OutlinedTextField(
        enabled = false,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { open = true },
        value = "#${value.toArgb().toHexString().uppercase()}",
        onValueChange = {},
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        trailingIcon = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(30.dp)
                    .background(value, RoundedCornerShape(5.dp))
            ) {
                Spacer(modifier = Modifier.size(30.dp))
            }
        }
    )

    if (open) {
        ColorDialog(
            state = rememberUseCaseState(visible = true, onCloseRequest = { open = false }),

            selection = ColorSelection(
                selectedColor = SingleColor(value.toArgb()),
                onSelectColor = { onChangeValue(Color(it)) },
            ),
            config = ColorConfig(
                defaultDisplayMode = ColorSelectionMode.TEMPLATE,
                templateColors = templateColors,
                allowCustomColorAlphaValues = false
            ),
        )
    }
}

fun Context.sendMail(
    to: String,
    subject: String
) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = "mailto:?subject=$subject&to=$to".toUri()
        intent.setData(data)
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    } catch (_: Throwable) {
    }
}