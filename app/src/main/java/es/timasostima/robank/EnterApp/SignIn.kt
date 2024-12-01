package es.timasostima.robank.EnterApp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.timasostima.robank.AccountManager
import es.timasostima.robank.R
import es.timasostima.robank.SignInResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun LogIn(
    navController: NavHostController,
    accountManager: AccountManager,
    scope: CoroutineScope,
    showCredentials: Boolean,
    changeCredVis: () -> Unit
){
    var showPassword by remember { mutableStateOf(false) }

//    LaunchedEffect(key1 = true){
//        if (showCredentials){
//            changeCredVis()
//            delay(1000)
//            val result = accountManager.signInCredentialManager()
//            if (result is SignInResult.Success){
//                navController.navigate("app")
//            }
//        }
//    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "",
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
            val tfC = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                errorContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            RobankTextField(
                email,
                "email",
                { email = it },
                true,
                tfC
            )
            RobankTextField(
                password,
                "password",
                { password = it },
                true,
                tfC,
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
                "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Right,
                textDecoration = TextDecoration.Underline,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(10.dp))

            var showEmailDialog by remember { mutableStateOf(false) }
            var showCredentialsFailureDialog by remember { mutableStateOf(false) }

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = {
                        scope.launch {
                            val result = accountManager.signIn(email, password)

                            if (result is SignInResult.Success) {
                                navController.navigate("app")
                            }
                            else if (result is SignInResult.EmailNotVerified) {
                                showEmailDialog = true //Hay que cambiar el dialog
                            }
                            else if (result is SignInResult.Failure) {
                                showCredentialsFailureDialog = true //Hay que cambiar el dialog
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

                            if (result is SignInResult.Success) {
                                navController.navigate("app")
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

                            if (result is SignInResult.Success) {
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
                    "Don't have an account yet? ",
                    color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "Sign Up!",
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
                RobankDialog(
                    "Error",
                    { showEmailDialog = false },
                    mapOf("Email not verified" to true)
                )
            }
            if (showCredentialsFailureDialog){
                RobankDialog(
                    "Error",
                    { showCredentialsFailureDialog = false },
                    mapOf("Invalid credentials" to true)
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}