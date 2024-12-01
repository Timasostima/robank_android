package es.timasostima.robank.EnterApp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import es.timasostima.robank.R

@Composable
fun TermsConditions(
    navController: NavHostController,
    termsDecision: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 70.dp, 16.dp, 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TitleText(stringResource(R.string.terms_and_conditions))

            SubTitleText(stringResource(R.string.usage_title))
            ParagraphText(stringResource(R.string.usage_text))

            SubTitleText(stringResource(R.string.privacy_title))
            ParagraphText(stringResource(R.string.privacy_text))

            SubTitleText(stringResource(R.string.ip_title))
            ParagraphText(stringResource(R.string.ip_text))

            SubTitleText(stringResource(R.string.liability_title))
            ParagraphText(stringResource(R.string.liability_text))

            SubTitleText(stringResource(R.string.modifications_title))
            ParagraphText(stringResource(R.string.modifications_text))

            SubTitleText(stringResource(R.string.jurisdiction_title))
            ParagraphText(stringResource(R.string.jurisdiction_text))

            Spacer(modifier = Modifier.size(16.dp))
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = {
                        termsDecision(true)
                        navController.navigate("signUp") {
                            popUpTo("signUp"){
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text("I Agree")
                }
                Button(
                    onClick = {
                        termsDecision(false)
                        navController.navigate("signUp") {
                            popUpTo("signUp"){
                                inclusive = true
                            }
                        }
                    },
                ) {
                    Text("I Reject")
                }
            }
            Spacer(modifier = Modifier.size(16.dp))

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
                    navController.navigate("signUp") {
                        popUpTo("signUp") {
                            inclusive = true
                        }
                    }
                }
                .size(55.dp)
                .padding(15.dp, 15.dp)
        )
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}

@Composable
fun SubTitleText(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ParagraphText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}