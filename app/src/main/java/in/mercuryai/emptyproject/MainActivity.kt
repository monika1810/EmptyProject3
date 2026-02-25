package `in`.mercuryai.emptyproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import `in`.mercuryai.emptyproject.presentation.navigation.SetUpNavGraph3
import `in`.mercuryai.emptyproject.ui.theme.EmptyProjectTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmptyProjectTheme {


                val snackbarHostState = remember { SnackbarHostState() }


                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->

                    val navController = rememberNavController()


                    SetUpNavGraph3(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        snackbarHostState = snackbarHostState
                    )
                   // Text(text = "hi", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


