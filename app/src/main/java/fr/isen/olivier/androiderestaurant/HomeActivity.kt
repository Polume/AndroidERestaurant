package fr.isen.olivier.androiderestaurant


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import fr.isen.olivier.androiderestaurant.basket.BasketActivity
import fr.isen.olivier.androiderestaurant.ui.theme.AndroidERestaurantTheme

enum class DishType {
    STARTER, MAIN, DESSERT;

    @Composable
    fun title(): String {
        return when(this) {
            STARTER -> stringResource(id = R.string.menu_starter)
            MAIN -> stringResource(id = R.string.menu_main)
            DESSERT -> stringResource(id = R.string.menu_dessert)
        }
    }
}

interface MenuInterface {
    fun dishPressed(dishType: DishType)
}

class HomeActivity : ComponentActivity(), MenuInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (NumbDishes.getValue()>0) GlobalCounter.setValue(NumbDishes.getValue())
        super.onCreate(savedInstanceState)
        setContent {
            //getString(R.string.menu_starter)
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(225, 180, 90)
                ) {
                    SetupView(this)
                }
            }
        }
        Log.d("lifeCycle", "Home Activity - OnCreate")
    }

    override fun dishPressed(dishType: DishType) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra(MenuActivity.CATEGROY_EXTRA_KEY, dishType)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onPause() {
        Log.d("lifeCycle", "Home Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Home Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Home Activity - onDestroy")
        super.onDestroy()
    }
}

@Composable
fun SetupView(menu: MenuInterface) {
    val context = LocalContext.current
    Row {
        Button(
            onClick = {
                val intent = Intent(context, BasketActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .padding(vertical = 10.dp)
                .offset(x = 250.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            AsyncImage(
                model = R.drawable.ajouter_au_panier,
                null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(100.dp)
                    .height(80.dp)
                    .zIndex(1f)
            )
        }
    }
    Text(text = GlobalCounter.getCurrentValue().toString(),
        color = Color.Red,
        modifier = Modifier
            .offset(x = 300.dp, y = 15.dp),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 35.dp)) {
        Image(painterResource(R.drawable.incon_app), null)
        Text(text = "Jones Restaurant",
            color = Color(180, 0, 0),
            fontSize = 30.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 50.dp)) {
            CustomButton(type = DishType.STARTER, menu)
            Divider(color = Color.Black, modifier = Modifier
                .width(100.dp)
                .height(3.dp))
            CustomButton(type = DishType.MAIN, menu)
            Divider(color = Color.Black, modifier = Modifier
                .width(100.dp)
                .height(3.dp))
            CustomButton(type = DishType.DESSERT, menu)
        }
    }
}

@Composable fun CustomButton(type: DishType, menu: MenuInterface) {
    Row {
        if(type.title() == "Entrées") {
            AsyncImage(
                model = R.drawable.entrees,
                null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(77.dp)
                    .height(120.dp)
                    .padding(vertical = 20.dp)
            )
        }
        else if(type.title() == "Plats") {
            AsyncImage(
                model = R.drawable.plats,
                null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(77.dp)
                    .height(120.dp)
                    .padding(vertical = 20.dp)
            )
        }
        else {
            AsyncImage(
                model = R.drawable.desserts,
                null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(77.dp)
                    .height(120.dp)
                    .padding(vertical = 20.dp)
            )
        }
        TextButton(onClick = { menu.dishPressed(type) }) {
            Text(type.title(),
                color = Color(20, 20, 80),
                modifier = Modifier.padding(vertical = 30.dp),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold)
        }
    }

}

object GlobalCounter {
    private var _counter = mutableIntStateOf(0)

    fun setValue(nb: Int) {
        _counter.intValue = nb
    }

    fun addValue(nb: Int) {
        _counter.intValue += nb
    }

    fun removeValue(nb: Int) {
        _counter.intValue -= nb
    }

    fun resetValue() {
        _counter.intValue = 0
    }

    fun getCurrentValue(): Int {
        return _counter.intValue
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        SetupView(HomeActivity())
    }
}