package `in`.mercuryai.emptyproject.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import `in`.mercuryai.chat.presentation.util.BebasNeueFont
import `in`.mercuryai.chat.presentation.util.CustomDrawerState
import `in`.mercuryai.chat.presentation.util.FontSize
import `in`.mercuryai.chat.presentation.util.IconPrimary
import `in`.mercuryai.chat.presentation.util.Surface
import `in`.mercuryai.chat.presentation.util.SurfaceLighter
import `in`.mercuryai.chat.presentation.util.TextPrimary
import `in`.mercuryai.emptyproject.presentation.util.getScreenWidth
import `in`.mercuryai.chat.presentation.util.isOpened
import `in`.mercuryai.chat.presentation.util.opposite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MercuryAITopAppBar(
    modifier: Modifier= Modifier,
    title: String="Mercury Ai",
    onSearchClick: () -> Unit,
    navigationIcon:@Composable () -> Unit={}
) {


    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }
    val animatedOffSet by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )

    val animateScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animateRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )



    CenterAlignedTopAppBar(
        title = {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append(title.split(" ").first())
                    }
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("${title.split(" ").last()}")
                    }
                },
                fontSize = FontSize.LARGE,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = BebasNeueFont()
            )
        },
        actions = {
            IconButton(onClick = {
                onSearchClick()
            }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Surface,
            scrolledContainerColor = Surface,
            navigationIconContentColor = IconPrimary,
            titleContentColor = TextPrimary,
            actionIconContentColor = IconPrimary,
        ),
        navigationIcon = {
            AnimatedContent(
                targetState = drawerState
            ) { drawer ->
                if (drawer.isOpened()) {
                    IconButton(
                        onClick = {
                            drawerState = drawerState.opposite()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                            tint = IconPrimary
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            drawerState = drawerState.opposite()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Icon",
                            tint = Color.White
                        )
                    }
                }

            }

        },

    )
}

























