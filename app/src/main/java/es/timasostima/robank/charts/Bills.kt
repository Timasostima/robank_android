package es.timasostima.robank.charts

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import android.graphics.Color as colorString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.timasostima.robank.database.BillManager
import es.timasostima.robank.dto.BillData
import es.timasostima.robank.dto.CategoryData
import es.timasostima.robank.topBorder
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.LocalDate
import java.util.Locale

@Composable
fun Bills(
    billsList: List<BillData>,
    billManager: BillManager,
    categoriesList: List<CategoryData>,
    currency: String,
    months: List<String>
) {
    val chartValue by remember {
        mutableStateOf(
            billsList.map { bill ->
                val billCategory = categoriesList.find { it.id == bill.categoryId }
                Pie(
                    billCategory?.name ?: "Unknown",
                    bill.amount,
                    Color(colorString.parseColor(billCategory?.color ?: "#000000")),
                    selectedScale = 1.1f,
                    selectedPaddingDegree = 0f
                )
            }
        )
    }
    var offset by remember { mutableIntStateOf(0) }
    val calendar = Calendar.getInstance(Locale("es", "ES"))
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    var daysList = generateWeekData(calendar, offset)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
        ){
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .clickable {
                        offset -= 1
                    }
                    .size(55.dp)
                    .padding(15.dp)
            )
            Text(
                "${daysList[0].dayOfMonth} - ${daysList[6].dayOfMonth} ${months[daysList[0].month.value - 1]}",
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .clickable {
                        offset += 1
                    }
                    .size(55.dp)
                    .padding(15.dp)
                    .rotate(180f)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val groupedBills = billsList.groupBy { it.date }
            val bars =
                daysList.map { day ->
                    println("${day.dayOfMonth}-${day.month.value}-${day.year}")
                    Bars(
                        label = "%02d".format(day.dayOfMonth),
                        values = listOf(Bars.Data(
                            value = groupedBills["%02d-%02d-%d".format(
                                day.dayOfMonth,
                                day.month.value,
                                day.year
                            )]?.sumOf { it.amount } ?: 0.0,
                            color = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        ))
                    )
                }
            ColumnChart(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                data = bars,
                labelHelperProperties = LabelHelperProperties(enabled = false),
                gridProperties = GridProperties(enabled = false),
                indicatorProperties = HorizontalIndicatorProperties(enabled = false),
                labelProperties = LabelProperties(
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    rotationDegreeOnSizeConflict = 0f,
                    enabled = true,
                ),
                barProperties = BarProperties(
                    thickness = 25.dp
                )
            )
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 10.dp)
                .topBorder(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        ) {
            items(chartValue.size) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
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
                        val text = if (billsList[it].name.length > 8) {
                            billsList[it].name.substring(0, 8) + "."
                        } else {
                            billsList[it].name
                        }
                        Text(
                            text,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chartValue[it].color)
                                .padding(vertical = 8.dp)
                                .weight(1.2f)
                        )
                        Text(
                            billsList[it].date,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            billsList[it].time,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.7f)
                        )
                        Text(
                            "%.2f%s".format(billsList[it].amount, currency),
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private fun generateWeekData(calendar: Calendar, weekOffset: Int):List<LocalDate> {
    calendar.add(Calendar.DATE, weekOffset * 7)
    val list = (1..7).map {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        calendar.add(Calendar.DATE, 1)
        LocalDate.of(year, month, day)
    }
    calendar.add(Calendar.DATE, -(weekOffset * 7))
    return list
}