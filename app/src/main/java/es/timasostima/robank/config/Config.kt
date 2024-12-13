package es.timasostima.robank.config

import android.app.LocaleManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.timasostima.robank.R
import es.timasostima.robank.charts.buttomBorder
import es.timasostima.robank.database.CategoryData
import es.timasostima.robank.database.Database
import es.timasostima.robank.database.PreferencesData

@Composable
fun ConfigScreen(
    preferences: PreferencesData,
    changeMode: (Boolean?) -> Unit,
    db: Database
) {
    val context = LocalContext.current
    Column (modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(top = 30.dp)
    ) {
        UserRow()
        Spacer(Modifier.height(20.dp))
        val mod = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp)
            .buttomBorder(1.dp, MaterialTheme.colorScheme.onSecondaryContainer)

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ){
            val onPick: (String) -> Unit = { item ->
                val name = item.substring(0, item.length - 5)
                context
                    .getSystemService(LocaleManager::class.java)
                    .applicationLocales =
                    android.os.LocaleList(java.util.Locale(name.lowercase(), name.uppercase()))
            }
            Text(stringResource(R.string.language))
            Spacer(modifier = Modifier.weight(1f))
            Picker( //it resets every time the screen is out os the composition
                listOf(
                    stringResource(R.string.system), "EN \uD83C\uDDEC\uD83C\uDDE7", "ES \uD83C\uDDEA\uD83C\uDDF8",
                //    "UCR \uD83C\uDDFA\uD83C\uDDE6", "RO \uD83C\uDDF7\uD83C\uDDF4", "RU \uD83C\uDDF7\uD83C\uDDFA"
                ),
                onPick
            )
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ){
            Text(stringResource(R.string.currency))
            Spacer(modifier = Modifier.weight(1f))
            Picker(listOf("eur", "usd", "rub"), {})
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ){
            Text(stringResource(R.string.theme))
            Spacer(modifier = Modifier.weight(1f))

            val sun = painterResource(R.drawable.sun)
            val moon = painterResource(R.drawable.moon)
            val systemTheme = painterResource(R.drawable.system_theme)

            var theme by remember { mutableStateOf(preferences.theme) }
            val changeIcon: () -> Unit = {
                theme = when (theme) {
                    "system" -> "night"
                    "night" -> "light"
                    "light" -> "system"
                    else -> "system"
                }
            }
            val icon = when (theme) {
                "system" -> systemTheme
                "night" -> moon
                "light" -> sun
                else -> systemTheme
            }
            changeMode(when (theme) {
                "system" -> null
                "dark" -> true
                "light" -> false
                else -> null
            })

            Icon(
                painter = icon,
                contentDescription = "Theme Icon",
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        changeIcon()
                        db.changeTheme(
                            when (preferences.theme) {
                                "system" -> "night"
                                "night" -> "light"
                                "light" -> "system"
                                else -> "system"
                            }
                        )
                    }
                    .aspectRatio(1f)
                    .padding(10.dp),
            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ){
            var agreeToNotif by remember { mutableStateOf(false) }
            Text("Notifications")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = agreeToNotif,
                onCheckedChange = { agreeToNotif = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    checkedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f),
                )
            )
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ){
            Text(stringResource(R.string.change_the_password))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                stringResource(R.string.change),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { }
                    .padding(5.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()){
            Text(stringResource(R.string.coming_soon), fontSize = 40.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun UserRow(){
    val name = "Tymur Kulivar"
    val email = "tymurkulivar@gmail.com"
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 20.dp)
    ){
        Column {
            Text(name)
            Text(email)
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface (
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            modifier = Modifier.clip(CircleShape)
        ){
            Icon(
                Icons.AutoMirrored.Outlined.ExitToApp,
                "Exit",
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp),
            )
        }
    }
}

@Composable
fun Picker(
    configValues: List<String>,
    onPick: (String) -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }
    val itemPosition = rememberSaveable {
        mutableIntStateOf(0)
    }

    Box {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    isDropDownExpanded = true
                }
                .padding(10.dp)
        ) {
            Text(text = configValues[itemPosition.intValue])
            Icon(
                Icons.Default.ArrowDropDown,
                "dropdown icon",
            )
        }

        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = {
                isDropDownExpanded = false
            }) {
            configValues.forEachIndexed { index, item ->
                DropdownMenuItem(text = {
                    Text(text = item)
                },
                    onClick = {
                        isDropDownExpanded = false
                        itemPosition.intValue = index
                        onPick(item)
                    }
                )
            }
        }
    }
}


@Composable
fun CategoryPicker(
    configValues: List<CategoryData>,
    onPick: (CategoryData) -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }
    val itemPosition = rememberSaveable {
        mutableIntStateOf(0)
    }

    Box {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    if (configValues.isNotEmpty())
                        isDropDownExpanded = true
                }
                .padding(10.dp)
        ) {
            Text(
                text = if (configValues.isEmpty()) "No hay"
                    else configValues[itemPosition.intValue].name
            )
            Icon(
                Icons.Default.ArrowDropDown,
                "dropdown icon",
            )
        }

        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = {
                isDropDownExpanded = false
            }) {
            configValues.forEachIndexed { index, item ->
                DropdownMenuItem(text = {
                    Text(text = item.name)
                },
                    onClick = {
                        isDropDownExpanded = false
                        itemPosition.intValue = index
                        onPick(item)
                    }
                )
            }
        }
    }
}