package es.timasostima.robank.config

import android.app.LocaleManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.state.StateDialog
import com.maxkeppeler.sheets.state.models.State
import com.maxkeppeler.sheets.state.models.StateConfig
import es.timasostima.robank.R
import es.timasostima.robank.charts.buttomBorder
import es.timasostima.robank.database.PreferencesManager
import es.timasostima.robank.database.ThemeMode
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.enterApp.AccountManager
import es.timasostima.robank.enterApp.PasswordReset
import es.timasostima.robank.notifications.RequestNotificationPermission
import es.timasostima.robank.notifications.checkNotificationPermission
import es.timasostima.robank.notifications.createNotificationChannel
import es.timasostima.robank.notifications.showNotification
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    changeMode: (Boolean?) -> Unit,
    loginNav: NavHostController,
    accountManager: AccountManager,
    preferencesManager: PreferencesManager,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPasswordResetDialog by remember { mutableStateOf(false) }

    val user: FirebaseUser = accountManager.getCurrentUser()!!

    val theme by preferencesManager.themeState.collectAsState()
    val preferences by preferencesManager.preferencesState.collectAsState()

    LaunchedEffect(theme) {
        changeMode(ThemeMode.toBooleanForDarkMode(theme))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 30.dp)
    ) {
        UserRow(loginNav, user, accountManager)

        Spacer(Modifier.height(20.dp))

        val mod = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp)
            .buttomBorder(1.dp, MaterialTheme.colorScheme.onSecondaryContainer)

        // Language Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ) {
            val system = stringResource(R.string.system)
            val onPick: (String) -> Unit = { item ->
                val language = if (item == system) "system" else item.substring(0, item.length - 5)
                preferencesManager.updateLanguage(language)
                context
                    .getSystemService(LocaleManager::class.java)
                    .applicationLocales =
                    android.os.LocaleList(
                        java.util.Locale(
                            language.lowercase(),
                            language.uppercase()
                        )
                    )
            }
            Text(stringResource(R.string.language))
            Spacer(modifier = Modifier.weight(1f))

            // Only render if preferences data is available
            preferences?.let { prefs ->
                Picker(
                    listOf(
                        system, "EN \uD83C\uDDEC\uD83C\uDDE7", "ES \uD83C\uDDEA\uD83C\uDDF8",
                    ).sortedBy { it.lowercase().contains(prefs.language) },
                    prefs.language,
                    onPick
                )
            }
        }

        // Currency Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ) {
            val onPick: (String) -> Unit = { item ->
                preferencesManager.updateCurrency(item)
            }
            Text(stringResource(R.string.currency))
            Spacer(modifier = Modifier.weight(1f))

            preferences?.let { prefs ->
                Picker(
                    listOf("eur", "usd", "rub").sortedBy { it.contains(prefs.currency) },
                    prefs.currency,
                    onPick
                )
            }
        }

        // Theme Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ) {
            Text(stringResource(R.string.theme))
            Spacer(modifier = Modifier.weight(1f))

            val sun = painterResource(R.drawable.sun)
            val moon = painterResource(R.drawable.moon)
            val systemTheme = painterResource(R.drawable.system_theme)

            val icon = when (theme) {
                ThemeMode.SYSTEM -> systemTheme
                ThemeMode.DARK -> moon
                ThemeMode.LIGHT -> sun
            }

            Icon(
                painter = icon,
                contentDescription = "Theme Icon",
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { preferencesManager.cycleTheme() }
                    .aspectRatio(1f)
                    .padding(10.dp),
            )
        }

        // Notifications Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ) {
            var requestPermissions by rememberSaveable { mutableStateOf(false) }

            Text(stringResource(R.string.notifications))
            Spacer(modifier = Modifier.weight(1f))

            preferences?.let { prefs ->
                Switch(
                    checked = prefs.notifications,
                    onCheckedChange = { enabled ->
                        preferencesManager.updateNotifications(enabled)
                        requestPermissions = enabled
                    },
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

            if (requestPermissions) {
                if (!checkNotificationPermission(context)) {
                    RequestNotificationPermission(context)
                } else {
                    createNotificationChannel(context)
                    showNotification(context)
                }
                requestPermissions = false
            }
        }

        // Password Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = mod
        ) {
            Text(stringResource(R.string.change_the_password))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                stringResource(R.string.change),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        showPasswordResetDialog = true
                    }
                    .padding(5.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.coming_soon),
                fontSize = 40.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Password Reset Dialog
    var showPasswordResetConfirmation by remember { mutableStateOf(false) }
    if (showPasswordResetDialog) {
        PasswordReset(
            context,
            scope,
            accountManager,
            { showPasswordResetConfirmation = true },
            { showPasswordResetDialog = false }
        )
    }

    if (showPasswordResetConfirmation) {
        StateDialog(
            state = rememberUseCaseState(
                visible = true,
                onCloseRequest = { showPasswordResetConfirmation = false }
            ),
            config = StateConfig(
                state = State.Success(labelText = stringResource(R.string.password_reset_email_sent)),
            ),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

@Composable
fun UserRow(
    loginNav: NavHostController,
    user: FirebaseUser,
    accountManager: AccountManager
) {
    val name = user.displayName ?: "User"
    val email = user.email ?: ""
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var profileImage by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isLoading = true
            coroutineScope.launch {
                try {
                    accountManager.uploadProfileImage(it)
                    profileImage = accountManager.getProfileImage()
                } catch (e: Exception) {
                    Log.e("UserRow", "Failed to upload image", e)
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Load profile image when composable is first created
    LaunchedEffect(key1 = user.uid) {
        isLoading = true
        try {
            profileImage = accountManager.getProfileImage()
        } catch (e: Exception) {
            Log.e("UserRow", "Failed to load profile image", e)
        } finally {
            isLoading = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box {
                // Profile image
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(60.dp)
                ) {
                    if (isLoading) {
                        // Show loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center)
                        )
                    } else if (profileImage != null) {
                        // Show profile image
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback icon/image
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        )
                    }
                }

                // Edit button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .clickable { launcher.launch("image/*") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Text(name)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .clickable {
                        loginNav.navigate("logIn") {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = "Exit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.Center)
                )
            }
        }
        Text(email)
    }
}

@Composable
fun Picker(
    configValues: List<String>,
    picked: String,
    onPick: (String) -> Unit,
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }

    var persistedText by rememberSaveable {
        mutableStateOf(
            configValues.first { it.lowercase().contains(picked.lowercase()) }
        )
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
            Text(text = persistedText)
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
            configValues.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item)
                    },
                    onClick = {
                        isDropDownExpanded = false
                        persistedText = item
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
    pickedCategory: CategoryData,
    onPick: (CategoryData) -> Unit
) {
    var isDropDownExpanded by remember {
        mutableStateOf(false)
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
                text = pickedCategory.name,
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
            configValues.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(text = category.name)
                    },
                    onClick = {
                        isDropDownExpanded = false
                        Log.i("CategoryPicker", "Selected category: ${category.id}")
                        onPick(category)
                    }
                )
            }
        }
    }
}