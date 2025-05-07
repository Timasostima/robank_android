package es.timasostima.robank.home

import android.icu.util.Currency
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.timasostima.robank.R
import es.timasostima.robank.database.Database
import es.timasostima.robank.database.GoalData

@Composable
fun ExpandableGoal(
    goal : GoalData,
    modifier: Modifier = Modifier,
    active : Boolean = false,
    db : Database,
    currency: String
){
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ){
        Box (
            modifier = modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ){
            Image(
                painter = painterResource(id = R.drawable.buggati),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                    .padding(vertical = 5.dp, horizontal = 15.dp)
            ){
                Text(
                    goal.name,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "%.2f %s".format(goal.price, currency),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Button(
                onClick = {
//                    db.deleteGoal(goal)
                    db.deleteGoal2(goal.id)
                },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                ),
                shape = RoundedCornerShape(bottomStart = 10.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
            ){
                Text("X", fontSize = 20.sp, color = Color(0xFFFA5555), fontWeight = FontWeight.Bold)
            }
            if (active)
                LinearDeterminateIndicator(modifier = Modifier.align(Alignment.BottomStart))
        }
        if (expanded){
            var tempName by remember { mutableStateOf(goal.name) }
            var tempPrice by remember { mutableDoubleStateOf(goal.price) }
            OutlinedTextField(
                value = tempName,
                onValueChange = {tempName = it},
                label = { Text("Name", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            OutlinedTextField(
                value = tempPrice.toString(),
                onValueChange =
                {
                    tempPrice = it.toDoubleOrNull() ?: tempPrice
                },
                label = { Text(context.getString(R.string.price), fontSize = 12.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Button(
                onClick = {
//                    db.updateGoal(goal.name, tempName, tempPrice)
                    db.updateGoal2(goal.id)
                    expanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun BasicGoal(
    goal : GoalData,
    modifier: Modifier = Modifier,
    currency: String
){
    Box (
        modifier = modifier
    ){
        Image(
            painter = painterResource(id = R.drawable.buggati),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .padding(vertical = 5.dp, horizontal = 15.dp)
        ){
            Text(
                goal.name,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "%.2f %s".format(goal.price, currency),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        LinearDeterminateIndicator(modifier = Modifier.align(Alignment.BottomStart))
    }
}