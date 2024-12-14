package es.timasostima.robank

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp.initializeApp
import es.timasostima.robank.enterApp.EnterApp
import es.timasostima.robank.ui.theme.RobankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            initializeApp(this)
            var darkmode: Boolean? by rememberSaveable { mutableStateOf(null) }
            val changeMode: (Boolean?) -> Unit = {
                darkmode = it
            }
            RobankTheme(darkTheme = darkmode ?: isSystemInDarkTheme()) {
                val showSystemUi by remember { mutableStateOf(false) }
                val view = LocalView.current
                val window = (view.context as Activity).window
                val insetsController = WindowCompat.getInsetsController(window, view)
                if (!view.isInEditMode) {
                    if (!showSystemUi) {
                        insetsController.apply {
                            hide(WindowInsetsCompat.Type.systemBars())
                            systemBarsBehavior =
                                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        }
                    } else {
                        insetsController.apply { show(WindowInsetsCompat.Type.systemBars()) }
                    }
                }
//                App(changeMode)
                EnterApp(changeMode)
            }

//            MainScreen(this)
        }
    }

    override fun onStart() {
//        println("lalalalala")
        super.onStart()
    }

    override fun onResume() {
//        println("jajajaja")
        super.onResume()
    }

    override fun onPause() {
//        println("sasasas")
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

