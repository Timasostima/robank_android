package es.timasostima.robank.home

import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.timasostima.robank.R
import es.timasostima.robank.database.GoalManager
import es.timasostima.robank.dto.GoalData
import kotlinx.coroutines.launch

@Composable
fun ExpandableGoal(
    goal: GoalData,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    goalManager: GoalManager,
    currency: String,
    firstGoalImageState: MutableState<Bitmap?> = remember { mutableStateOf(null) }
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // State for storing the goal image
    var goalImage by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // First, immediately use the shared image if available (without setting isLoading)
    if (active && firstGoalImageState.value != null) {
        goalImage = firstGoalImageState.value
    }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isLoading = true
            coroutineScope.launch {
                try {
                    goalManager.uploadGoalImage(goal.id, it)
                    // Refresh the image after upload
                    goalImage = goalManager.getGoalImage(goal.id)
                } catch (e: Exception) {
                    Log.e("ExpandableGoal", "Failed to upload image", e)
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(goal.id) {
        if (goalImage == null) {
            isLoading = true
            try {
                val image = goalManager.getGoalImage(goal.id)
                goalImage = image

                // If this is the active goal, update the shared state too
                if (active && image != null) {
                    firstGoalImageState.value = image
                }
            } catch (e: Exception) {
                Log.e("ExpandableGoal", "Failed to load goal image", e)
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            if (goalImage != null) {
                Image(
                    bitmap = goalImage!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Fallback image
                Image(
                    painter = painterResource(id = R.drawable.buggati),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                    .padding(vertical = 5.dp, horizontal = 15.dp)
            ) {
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
                onClick = { goalManager.deleteGoal(goal.id) },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                ),
                shape = RoundedCornerShape(bottomStart = 10.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text("X", fontSize = 20.sp, color = Color(0xFFFA5555), fontWeight = FontWeight.Bold)
            }

            if (active)
                LinearDeterminateIndicator(modifier = Modifier.align(Alignment.BottomStart))
        }

        if (expanded) {
            var tempName by remember { mutableStateOf(goal.name) }
            var tempPrice by remember { mutableDoubleStateOf(goal.price) }

            // Add image edit button when expanded
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(
                        imageVector = if (goalImage == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = if (goalImage == null) "Add Image" else "Change Image"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (goalImage == null) "Add Image" else "Change Image")
                }
            }

            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("Name", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            OutlinedTextField(
                value = tempPrice.toString(),
                onValueChange = {
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
                    goalManager.updateGoal(goal.id, tempName, tempPrice)
                    expanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun BasicGoal(
    goal: GoalData,
    modifier: Modifier = Modifier,
    currency: String,
    firstGoalImageState: MutableState<Bitmap?>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var goalImage by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // First, immediately use the shared image if available
    if (firstGoalImageState.value != null) {
        goalImage = firstGoalImageState.value
    }

    // Only load from network if needed
    LaunchedEffect(key1 = goal.id) {
        if (goalImage == null) {
            isLoading = true
            try {
                val goalManager = GoalManager(context)
                goalImage = goalManager.getGoalImage(goal.id)
            } catch (e: Exception) {
                Log.e("BasicGoal", "Failed to load goal image", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = modifier
    ) {
        if (goalImage != null) {
            Image(
                bitmap = goalImage!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Fallback image
            Image(
                painter = painterResource(id = R.drawable.buggati),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .padding(vertical = 5.dp, horizontal = 15.dp)
        ) {
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
