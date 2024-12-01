package es.timasostima.robank.EnterApp

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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import es.timasostima.robank.AccountManager
import es.timasostima.robank.R
import es.timasostima.robank.SignUpResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignUp(
    navController: NavHostController,
    accountManager: AccountManager,
    scope: CoroutineScope,
    agreesToTerms: Boolean,
    termsDecision: (Boolean) -> Unit
) {
    val tfC = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        errorContainerColor = MaterialTheme.colorScheme.secondaryContainer
    )

    var email by remember { mutableStateOf("") }
    val validEmail = email.matches(Regex("[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
    var password by remember { mutableStateOf("") }
    var showEmailInfo by remember { mutableStateOf(false) }
    var showPasswordInfo by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }

    // Hay que configurarlo en fire
    val minLength = 6
    val maxLength = 15
    val validPassword = password.length in minLength..maxLength

    Box{
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(35.dp, 0.dp)
        ){
            Spacer(modifier = Modifier.size(17.dp))
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.31f)
            )
            Spacer(modifier = Modifier.size(3.dp))
            RobankTextField(email, "email", { email = it }, validEmail, tfC)
            {
                TraIcon(
                    validEmail,
                    modifier = Modifier
                        .clip(RoundedCornerShape(27.dp))
                        .clickable {
                            showEmailInfo = !showEmailInfo
                        }
                        .padding(10.dp)
                )
            }
            RobankTextField(password, "password",
                { password = it }, validPassword, tfC,
                transformation = PasswordVisualTransformation())
            {
                TraIcon(
                    validPassword,
                    modifier = Modifier
                        .clip(RoundedCornerShape(27.dp))
                        .clickable {
                            showPasswordInfo = !showPasswordInfo
                        }
                        .padding(10.dp)
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
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
                Text("  I Accept the ",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 14.sp
                )
                Text(
                    "Terms and Conditions",
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
                    }
                },
                enabled = validEmail && validPassword && agreesToTerms,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Text("Sign Up")
            }

            if (showVerificationDialog){
                RobankDialog(
                    "Congratulation",
                    { showVerificationDialog = false }
                )
            }
            if (showEmailInfo){
                RobankDialog(
                    "Error",
                    { showEmailInfo = false },
                    mapOf("Invalid email" to true)
                )
            }
            if (showPasswordInfo){
                RobankDialog(
                    "Error",
                    { showPasswordInfo = false },
                    mapOf(
                        "Password must be longer than $minLength"
                            to (password.length < minLength),
                        "Password must be shorter than $maxLength"
                                to (password.length > maxLength))
                )
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
                        popUpTo("logIn"){
                            inclusive = true
                        }
                    }
                }
                .size(55.dp)
                .padding(15.dp, 15.dp)
        )
    }
}

