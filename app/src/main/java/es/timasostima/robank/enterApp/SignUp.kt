package es.timasostima.robank.enterApp

import android.util.Patterns
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.icons.LibIcons
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.state.StateDialog
import com.maxkeppeler.sheets.state.StatePopup
import com.maxkeppeler.sheets.state.models.ProgressIndicator
import com.maxkeppeler.sheets.state.models.State
import com.maxkeppeler.sheets.state.models.StateConfig
import com.maxkeppeler.sheets.state.models.StateSelection
import es.timasostima.robank.R
import es.timasostima.robank.database.SignUpResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(
    navController: NavHostController,
    accountManager: AccountManager,
    scope: CoroutineScope,
    agreesToTerms: Boolean,
    termsDecision: (Boolean) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val emailPattern = Patterns.EMAIL_ADDRESS
    val validEmail = emailPattern.matcher(email).matches()
    var password by remember { mutableStateOf("") }

    var showVerificationDialog by remember { mutableStateOf(false) }
    var showAlreadyExistsDialog by remember { mutableStateOf(false) }
    var showFailureDialog by remember { mutableStateOf(false) }

    val minLength = 6
    val maxLength = 15
    val validPassword = password.length in minLength..maxLength

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(35.dp, 0.dp)
        ) {
            Spacer(modifier = Modifier.size(17.dp))
            Image(
                painter = painterResource(R.drawable.robank_logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.31f)
            )
            Spacer(modifier = Modifier.size(3.dp))
            RobankTextField(email, stringResource(R.string.email), { email = it }, validEmail)
            {
                var showEmailInfo by remember { mutableStateOf(false) }
                TraIcon(
                    validEmail,
                    modifier = Modifier
                        .clip(RoundedCornerShape(27.dp))
                        .clickable {
                            showEmailInfo = !showEmailInfo
                        }
                        .padding(10.dp)
                )
                if (showEmailInfo) {
//                    StatePopup(
//                        state = rememberUseCaseState(
//                            visible = true,
//                            onCloseRequest = { showEmailInfo = false }
//                        ),
//                        config = StateConfig(
//                            state = State.Failure(
//                                labelText = "lasaa"
//                            ),
//                        ),
//                        properties = PopupProperties(
//                            dismissOnBackPress = true,
//                            dismissOnClickOutside = true
//                        ),
//                        alignment = Alignment.TopEnd,
//                    )
                    StateDialog(
                        state = rememberUseCaseState(
                            visible = true,
                            onCloseRequest = { showEmailInfo = false }
                        ),
                        config = StateConfig(
                            state = State.Failure(
                                labelText = stringResource(R.string.invalid_email)
                            ),
                        ),
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    )
                }
            }
            RobankTextField(
                password, stringResource(R.string.password),
                { password = it }, validPassword,
                transformation = PasswordVisualTransformation()
            )
            {
                var showPasswordInfo by remember { mutableStateOf(false) }
                TraIcon(
                    validPassword,
                    modifier = Modifier
                        .clip(RoundedCornerShape(27.dp))
                        .clickable {
                            showPasswordInfo = !showPasswordInfo
                        }
                        .padding(10.dp)
                )
                if (showPasswordInfo) {
                    StateDialog(
                        state = rememberUseCaseState(
                            visible = true,
                            onCloseRequest = { showPasswordInfo = false }
                        ),
                        config = StateConfig(
                            state =
                            if (password.length < minLength) {
                                State.Failure(labelText = stringResource(R.string.password_must_be_longer_than) + minLength)
                            } else {
                                State.Failure(labelText = stringResource(R.string.password_must_be_shorter_than) + maxLength)
                            }
                        ),
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = agreesToTerms,
                    onCheckedChange = { termsDecision(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        checkedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f),
                    ),
                )
                Text(
                    stringResource(R.string.i_accept_the),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Text(
                    stringResource(R.string.terms_and_conditions),
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("terms")
                    }
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Button(
                onClick = {
                    scope.launch {
                        val result = accountManager.signUp(email, password)

                        if (result is SignUpResult.Success) {
                            showVerificationDialog = true
                        }
                        if (result is SignUpResult.AlreadyRegistered) {
                            showAlreadyExistsDialog = true
                        }
                        if (result is SignUpResult.Failure) {
                            showFailureDialog = true
                        }
                    }
                },
                enabled = validEmail && validPassword && agreesToTerms,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Text(stringResource(R.string.sign_up))
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(5.dp, 15.dp)
                .clip(RoundedCornerShape(27.dp))
                .clickable {
                    navController.navigate("logIn") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
                .size(55.dp)
                .padding(15.dp, 15.dp)
        )

        if (showVerificationDialog) {
            var inProgress by remember { mutableStateOf(true) }
            LaunchedEffect(key1 = inProgress) {
                delay(2500)
                inProgress = false
            }
            StateDialog(
                state = rememberUseCaseState(
                    visible = true,
                    onCloseRequest = { showVerificationDialog = false }
                ),
                config = StateConfig(
                    state =
                    if (inProgress) {
                        State.Loading(
                            labelText = stringResource(R.string.sending),
                            ProgressIndicator.Circular()
                        )
                    } else {
                        State.Success(labelText = stringResource(R.string.the_email_has_been_sent))
                    }
                ),
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }
        if (showAlreadyExistsDialog) {
            StateDialog(
                state = rememberUseCaseState(
                    visible = true,
                    onCloseRequest = { showAlreadyExistsDialog = false }
                ),
                config = StateConfig(
                    state = State.Failure(
                        labelText = stringResource(R.string.already_registered)
                    ),
                ),
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }
        if (showFailureDialog) {
            StateDialog(
                state = rememberUseCaseState(
                    visible = true,
                    onCloseRequest = { showFailureDialog = false }
                ),
                config = StateConfig(
                    state = State.Failure(
                        labelText = stringResource(R.string.error)
                    ),
                ),
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }
    }
}

