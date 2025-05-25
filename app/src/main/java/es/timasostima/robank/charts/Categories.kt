package es.timasostima.robank.charts

import android.icu.util.Calendar
import android.util.Log
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.graphics.Color as colorString
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.timasostima.robank.R
import es.timasostima.robank.dto.BillData
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.topBorder
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.util.Locale
import androidx.core.graphics.toColorInt

@Composable
fun Categories(
    billsList: List<BillData>,
    categoriesList: List<CategoryData>,
    currency: String,
    months: List<String>
) {
    val calendar = remember {Calendar.getInstance(Locale("es", "ES"))}
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)

    var chartPIEs by remember {
        mutableStateOf(
            categoriesList.map { category ->
                Log.i("Category", "Category: ${category.name}")
                Pie(
                    category.name,
                    billsList.filter { bill ->
                        Log.i("Category", "Bill: ${bill.categoryId} Category: ${category.id}")
                        bill.categoryId == category.id
                    }.sumOf { it.amount },
                    Color(category.color.toColorInt()),
                    selectedScale = 1.1f,
                    selectedPaddingDegree = 0f
                )
            }
        )
    }

    var selected: Int? by remember { mutableStateOf(null) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "${months[month]} $year",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 20.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(25.dp)

                .fillMaxWidth()
        ) {
            PieChart(
                data = chartPIEs,
                modifier = Modifier
                    .size(270.dp),
                onPieClick = { pie ->
                    val pieIndex = chartPIEs.indexOf(pie)
                    val alreadySelected = chartPIEs[pieIndex].selected

                    chartPIEs = chartPIEs.mapIndexed { mapIndex, p ->
                        if (mapIndex == pieIndex && !alreadySelected) {
                            selected = mapIndex
                            p.copy(selected = true)
                        }
                        else if (mapIndex == pieIndex) {
                            selected = null
                            p.copy(selected = false)
                        }
                        else p.copy(selected = false)
                    }
                },
                style = Pie.Style.Stroke(width = 60.dp)
            )
            Text(
                "%.2f%s".format(chartPIEs.sumOf { it.data }, currency),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }


        LazyColumn (
            modifier = Modifier
                .fillMaxHeight()
                .topBorder(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        ){
            items(chartPIEs.size) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surface)
                ) {
                    AnimatedVectorDrawable(selected == it, chartPIEs[it].color)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .buttomBorder(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            .padding(start = 10.dp, end = 20.dp)
                    ) {
                        val text = if (chartPIEs[it].label!!.length > 9) {
                            chartPIEs[it].label!!.substring(0, 9) + "."
                        } else {
                            chartPIEs[it].label.toString()
                        }
                        Text(
                            text,
                            fontSize = 20.sp,
                            color = Color(0xFF423E54),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chartPIEs[it].color)
                                .padding(10.dp)
                                .weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.7f))
                        Text(
                            "%.2f%s".format(chartPIEs[it].data, currency),
                            fontSize = 20.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVectorDrawable(atEnd: Boolean, color: Color) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.active_button)
    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = "Timer",
        modifier = Modifier.fillMaxHeight(),
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color),
    )
}