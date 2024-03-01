package fr.isen.olivier.androiderestaurant


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.isen.olivier.androiderestaurant.basket.Basket
import fr.isen.olivier.androiderestaurant.basket.BasketActivity
import fr.isen.olivier.androiderestaurant.network.Dish
import kotlin.math.max

class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dish = intent.getSerializableExtra(DISH_EXTRA_KEY) as? Dish
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.LightGray
            ) {
                val context = LocalContext.current
                val count = remember {
                    mutableIntStateOf(1)
                }
                val ingredient = dish?.ingredients?.map { it.name }?.joinToString(", ") ?: ""
                val pagerState = rememberPagerState(pageCount = {
                    dish?.images?.count() ?: 0
                })
                Row {
                    TopAppBar({
                        Button(
                            onClick = {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            AsyncImage(
                                model = R.drawable.menu,
                                null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(60.dp)
                            )
                        }
                        Text(
                            text = dish?.name ?: "",
                            color = Color(20, 20, 80),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(horizontal = 50.dp, vertical = 10.dp)
                                .fillMaxWidth(0.85f)
                                .height(50.dp)
                                .offset(x = 20.dp, y = 5.dp)
                        )
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
                                .offset(x = 230.dp),
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
                            ItemsInBrasket()
                        }
                        Text(text = GlobalCounter.getCurrentValue().toString(),
                            color = Color.Red,
                            modifier = Modifier
                                .offset(x = 280.dp, y = 15.dp),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    })
                }
                Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.offset(y = 80.dp)) {
                    HorizontalPager(state = pagerState) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(dish?.images?.get(it))
                                .build(),
                            null,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    Text(text = ingredient, modifier = Modifier.padding(horizontal = 5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            count.intValue = max(1, count.intValue - 1)
                            NumbDishes.setValue(count.intValue)
                        }) {
                            Text("-")
                        }
                        Text(count.intValue.toString())
                        OutlinedButton(onClick = {
                            count.intValue = count.intValue + 1
                            NumbDishes.setValue(count.intValue)
                        }) {
                            Text("+")
                        }
                        Spacer(Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.Absolute.SpaceEvenly) {
                        Button(onClick = {
                            if (dish != null) {
                                GlobalCounter.addValue(count.intValue)
                                Basket.current(context).add(dish, count.intValue, context)
                                NumbDishes.setValue(count.intValue)
                            }
                        }, modifier = Modifier.padding(20.dp)) {
                            Text("Commander", color = Color.Yellow)
                        }
                        Button(onClick = {
                            val intent = Intent(context, BasketActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            context.startActivity(intent)
                        }, modifier = Modifier.padding(vertical = 20.dp)) {
                            Text("Aller au panier", color = Color.Yellow)
                        }
                    }
                }
            }
        }
    }


    companion object {
        const val DISH_EXTRA_KEY = "DISH_EXTRA_KEY"
    }
}

object NumbDishes {
    private var _numbDishes = mutableIntStateOf(0)

    fun getValue(): Int {
        return _numbDishes.intValue
    }
    fun setValue(nb: Int) {
        _numbDishes.intValue = nb
    }
}

@Composable
fun ItemsInBrasket() {
    Text(
        text = GlobalCounter.getCurrentValue().toString(),
        color = Color.Red,
        modifier = Modifier
            .offset(x = 280.dp, y = 15.dp),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}