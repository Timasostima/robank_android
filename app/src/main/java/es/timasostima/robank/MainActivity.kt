package es.timasostima.robank

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import es.timasostima.robank.ui.theme.RobankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobankTheme {
                var showSystemUi by remember { mutableStateOf(false) }
                val view = LocalView.current
                val window = (view.context as Activity).window
                val insetsController = WindowCompat.getInsetsController(window, view)
                if (!view.isInEditMode) {
                    if (!showSystemUi) {
                        insetsController.apply {
                            hide(WindowInsetsCompat.Type.systemBars())
                            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        }
                    } else { insetsController.apply { show(WindowInsetsCompat.Type.systemBars()) } }
                }

                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = { MyBar() }) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MyBar(){
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly){
        Button(onClick = {}) { }
        Button(onClick = {}) { }
        Button(onClick = {}) { }

    }
}

@Composable
fun App(modifier: Modifier = Modifier){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize().background(Color(41,43,63))
    ){
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "",
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f)
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.6f)
        ){
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                "",
                onValueChange = {},
                placeholder = { Text("name") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                "",
                onValueChange = {},
                placeholder = { Text("password") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(20.dp))

            Button(onClick = {}, modifier = Modifier.fillMaxWidth(0.5f)) {
                Text("Log In")
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RobankTheme {
        App()
    }
}