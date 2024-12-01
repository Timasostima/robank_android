package es.timasostima.robank.EnterApp

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.timasostima.robank.AccountManager
import es.timasostima.robank.App

@Composable
fun EnterApp(){
    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accountManager = remember { AccountManager(context as ComponentActivity) }

    var showCredentials by rememberSaveable { mutableStateOf(true) }
    val changeCredVis:() -> Unit = {
        showCredentials = false
    }

    var agreesToTerms by rememberSaveable { mutableStateOf(false) }
    val termsDecision: (Boolean) -> Unit = { agrees ->
        agreesToTerms = agrees
    }
    NavHost(
        navController = navController,
        startDestination = "logIn",
        exitTransition = { slideOutVertically(
            animationSpec = tween(1500),
            targetOffsetY = { it }
        ) },
    ){
        composable("logIn"){
            LogIn(navController, accountManager, scope, showCredentials, changeCredVis)
        }
        composable("signUp"){
            SignUp(navController, accountManager, scope, agreesToTerms, termsDecision)
        }
        composable("terms"){
            TermsConditions(navController, termsDecision)
        }
        composable("app"){
            App()
        }
    }
}

@Composable
fun RobankTextField(
    text: String,
    placeholder: String,
    updateText: (String) -> Unit,
    isValid: Boolean,
    tfC: TextFieldColors,
    transformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {},
){

    TextField(
        text,
        onValueChange = updateText,
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = !isValid,
        colors = tfC,
        visualTransformation = transformation,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        trailingIcon = {
            trailingIcon()
        }
    )
}

@Composable
fun RobankDialog(
    title: String,
    onClose: () -> Unit,
    errors: Map<String, Boolean>? = null
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title) },
        text = {
            if (errors == null){
                Text("Check your email")
            }
            else{
                Column {
                    for (error in errors) {
                        val color = if (error.value) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary

                        Text(error.key, color = color)
                    }
                }
            }

        },
        confirmButton = {
            Button(
                onClick = onClose
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
fun TraIcon(
    valid: Boolean,
    modifier: Modifier = Modifier
){
    if (valid){
        Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = "valid",
            tint = MaterialTheme.colorScheme.primary,
        )
    } else {
        Icon(
            Icons.Outlined.Info,
            contentDescription = "invalid",
            tint = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
    }
}