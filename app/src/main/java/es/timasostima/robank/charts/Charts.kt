package es.timasostima.robank.charts

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.timasostima.robank.R
import es.timasostima.robank.database.BillManager
import es.timasostima.robank.database.CategoryManager
import es.timasostima.robank.dto.BillData
import es.timasostima.robank.dto.CategoryData

@Composable
fun Charts(
    bills: List<BillData>,
    categoriesList: List<CategoryData>,
    currency: String
) {
    val controller = rememberNavController()
    val navBackStack by controller.currentBackStackEntryAsState()

    val curSym = when (currency){
        "usd" -> "$"
        "eur" -> "€"
        "rub" -> "₽"
        else -> "$"
    }

    val months = listOf(
        stringResource(R.string.january), stringResource(R.string.february), stringResource(R.string.march),
        stringResource(R.string.april), stringResource(R.string.may), stringResource(R.string.june),
        stringResource(R.string.july), stringResource(R.string.august), stringResource(R.string.september),
        stringResource(R.string.october), stringResource(R.string.november), stringResource(R.string.december)
    )

    Column {
        ChartsNavigationBar(
            controller = controller,
            navBackStack = navBackStack
        )
        NavHost(
            navController = controller,
            startDestination = "categories"
        ){
            composable("categories"){
                if (categoriesList.isEmpty()){
                    ChartPlaceholder(true)
                }
                else{
                    Categories(bills, categoriesList, curSym, months)
                }
            }
            composable ("bills"){
                if (bills.isEmpty()){
                    ChartPlaceholder(false)
                }
                else{
                    Bills(bills, categoriesList, curSym, months)
                }
            }
        }
    }
}

@Composable
fun ChartsNavigationBar(
    controller: NavHostController,
    navBackStack: NavBackStackEntry?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .buttomBorder(1.dp, MaterialTheme.colorScheme.onSecondaryContainer)
            .padding(vertical = 10.dp)
    ){
        val navigate: (String) -> Unit = { text ->
            controller.navigate(text){
                popUpTo(controller.graph.startDestinationId){}
                launchSingleTop = true
            }
        }

        ChartsNavBarItem("categories", stringResource(R.string.categories), navBackStack, navigate, Modifier.weight(1f))
        ChartsNavBarItem( "bills", stringResource(R.string.bills), navBackStack, navigate, Modifier.weight(1f))
    }
}

@Composable
fun ChartsNavBarItem(
    navItem: String,
    text: String,
    backStack: NavBackStackEntry?,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val isSelected = navItem == backStack?.destination?.route

    val color by animateColorAsState(
        if (isSelected){
            MaterialTheme.colorScheme.primary
        }
        else{
            MaterialTheme.colorScheme.onSecondaryContainer
        }, label = "color"
    )

    val textDecoration =
        if (isSelected) TextDecoration.Underline
        else TextDecoration.None

    val fontStyle =
        if (isSelected) FontStyle.Italic
        else FontStyle.Normal

    Text(
        text,
        textAlign = TextAlign.Center,
        color = color,
        textDecoration = textDecoration,
        fontStyle = fontStyle,
        modifier = modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .clickable {
                if (!isSelected) navigate(navItem)
            }
            .padding(10.dp)
    )
}

fun Modifier.buttomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2
            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),

                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)


@Composable
fun ChartPlaceholder(isCategory: Boolean){
    val title =
        if (isCategory) stringResource(R.string.no_categories_to_show)
        else stringResource(R.string.no_bills_to_show)

    val subtitle = stringResource(R.string.you_need_to_create_them_first)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp)
    ){
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 35.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = subtitle,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }

}

