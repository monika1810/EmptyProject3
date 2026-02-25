package `in`.mercuryai.emptyproject.presentation.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        EmailSignIn(
            navigateToHome = navigateToHomeScreen
        )



    }





}

@Composable
fun EmailSignIn(
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    navigateToHome: () -> Unit,
    context: Context = LocalContext.current
) {

    var emailValue by remember {
        mutableStateOf("")
    }

    var passwordValue by remember {
        mutableStateOf("")
    }


    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Email",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = emailValue,
            onValueChange = { newValue ->
                emailValue = newValue
            },
            placeholder = {
                Text(
                    text = "john.doe@example.com",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Password",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = passwordValue,
            onValueChange = { newValue ->
                passwordValue = newValue
            },
            placeholder = {
                Text(
                    text = "Enter your password",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(35.dp))

    Button(
        onClick = {
            viewModel
                .signInWithEmail(
                    emailValue,
                    passwordValue,
                    onSuccess = navigateToHome,
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                ).let {
                    it
                    Log.d("TAG", "EmailSignUp: ${it.toString()}")
                }

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Cyan
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign up",
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

}