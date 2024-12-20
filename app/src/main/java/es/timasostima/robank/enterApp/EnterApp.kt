package es.timasostima.robank.enterApp

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.timasostima.robank.App
import es.timasostima.robank.R
import es.timasostima.robank.database.Database

@Composable
fun EnterApp(
    changeMode: (Boolean?) -> Unit
) {
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
        composable("app/{userID}"){
            val userId = it.arguments?.getString("userID") ?: "Error"
            val db = Database(userId)
            App(changeMode, db, accountManager)
        }
    }
}

@Composable
fun RobankTextField(
    text: String,
    placeholder: String,
    updateText: (String) -> Unit,
    isValid: Boolean = true,
    tfC: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        errorContainerColor = MaterialTheme.colorScheme.surface
    ),
    transformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {},
){
    val options = if (placeholder == stringResource(R.string.email)) KeyboardOptions(keyboardType = KeyboardType.Email)
    else KeyboardOptions.Default
    TextField(
        text,
        onValueChange = updateText,
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = !isValid,
        colors = tfC,
        keyboardOptions = options,
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