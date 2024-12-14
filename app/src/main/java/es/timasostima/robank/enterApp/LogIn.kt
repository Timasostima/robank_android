package es.timasostima.robank.enterApp

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.maxkeppeler.sheets.input.models.InputTextFieldType
import com.maxkeppeler.sheets.input.models.ValidationResult
import com.maxkeppeler.sheets.state.StateDialog
import com.maxkeppeler.sheets.state.models.State
import com.maxkeppeler.sheets.state.models.StateConfig
import es.timasostima.robank.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(
    navController: NavHostController,
    accountManager: AccountManager,
    scope: CoroutineScope,
    showCredentials: Boolean,
    changeCredVis: () -> Unit
){
    val context = LocalContext.current
    var showPassword by remember { mutableStateOf(false) }

//    LaunchedEffect(key1 = true){
//        if (showCredentials){
//            changeCredVis()
//            delay(1000)
//            val result = accountManager.logInCredentialManager()
//            if (result is LogInResult.Success){
//                navController.navigate("app")
//            }
//        }
//    }

    var showEmailDialog by remember { mutableStateOf(false) }
    var showCredentialsFailureDialog by remember { mutableStateOf(false) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "app logo",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.6f)
        ){
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            RobankTextField(
                email,
                stringResource(R.string.email),
                { email = it },
            )
            RobankTextField(
                password,
                stringResource(R.string.password),
                { password = it },
                transformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            )
            {
                if (showPassword){
                    Icon(
                        painter = painterResource(R.drawable.visibility_on),
                        contentDescription = "invalid",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(27.dp))
                            .clickable {
                                showPassword = !showPassword
                            }
                            .padding(10.dp)
                    )
                }
                else{
                    Icon(
                        painter = painterResource(R.drawable.visibility_off),
                        contentDescription = "invalid",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(27.dp))
                            .clickable {
                                showPassword = !showPassword
                            }
                            .padding(10.dp)
                    )
                }
            }

            Text(
                stringResource(R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Right,
                textDecoration = TextDecoration.Underline,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .clickable {
                        showPasswordResetDialog = true
                    }
            )
            Spacer(modifier = Modifier.size(10.dp))

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = {
                        scope.launch {
                            val result = accountManager.logIn(email, password)

                            if (result is LogInResult.Success) {
                                navController.navigate("app/${result.user.uid}")
                            }
                            else if (result is LogInResult.EmailNotVerified) {
                                showEmailDialog = true
                            }
                            else if (result is LogInResult.Failure) {
                                showCredentialsFailureDialog = true
                            }
                        }
                    }
                ) {
                    Text("Log In")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        scope.launch {
                            val result = accountManager.signInGoogle()

                            if (result is LogInResult.Success) {
                                navController.navigate("app/${result.user.uid}")
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "Google",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            val result = accountManager.signInGoogle()

                            if (result is LogInResult.Success) {
                                navController.navigate("app")
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "Github",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    stringResource(R.string.don_t_have_an_account_yet),
                    color = MaterialTheme.colorScheme.onSurface)
                Text(
                    stringResource(R.string.sign_up_ex),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Right,
                    textDecoration = TextDecoration.Underline,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.clickable {
                        navController.navigate("signUp")
                    }
                )
            }

            if (showEmailDialog){
                StateDialog(
                    state = rememberUseCaseState(
                        visible = true,
                        onCloseRequest = { showEmailDialog = false }
                    ),
                    config = StateConfig(
                        state = State.Failure(labelText = stringResource(R.string.email_not_verified)),
                    ),
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                )
            }
            if (showCredentialsFailureDialog){
                StateDialog(
                    state = rememberUseCaseState(
                        visible = true,
                        onCloseRequest = { showCredentialsFailureDialog = false }
                    ),
                    config = StateConfig(
                        state = State.Failure(labelText = stringResource(R.string.invalid_credentials)),
                    ),
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                )
            }
            var showPasswordResetConfirmation by remember { mutableStateOf(false) }
            if (showPasswordResetDialog){
                PasswordReset(context, scope, accountManager, {showPasswordResetConfirmation = true}) { showPasswordResetDialog = false }
            }
            if (showPasswordResetConfirmation){
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
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordReset(
    context: Context,
    scope: CoroutineScope,
    accountManager: AccountManager,
    confirmDialog: () -> Unit,
    onClose: () -> Unit
) {
    val emailPattern = Patterns.EMAIL_ADDRESS
    val inputOptions = listOf(
        InputTextField(
            type = InputTextFieldType.OUTLINED,
            header = InputHeader(
                title = stringResource(R.string.password_reset),
            ),
            validationListener = { value ->
                println(value)
                if (value.isNullOrBlank()) ValidationResult.Invalid(context.getString(R.string.email_is_required))
                else if (!emailPattern.matcher(value).matches()){
                    ValidationResult.Invalid(context.getString(R.string.invalid_email_address))
                }
                else ValidationResult.Valid
            },
            required = true
        )
    )
    InputDialog(
        state = rememberUseCaseState(
            visible = true,
            onCloseRequest = { onClose() }),
        selection = InputSelection(
            input = inputOptions,
            onNegativeClick = {
                onClose()
            },
            onPositiveClick = { result ->
                val emailInput = result.getString("0") ?: ""
                if (emailInput.isNotBlank()) {
                    scope.launch {
                        accountManager.resetPassword(emailInput)
                        confirmDialog()
                    }
                }
            },
        )
    )
}