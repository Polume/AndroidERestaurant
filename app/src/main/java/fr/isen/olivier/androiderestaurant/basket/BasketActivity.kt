package fr.isen.olivier.androiderestaurant.basket

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.isen.olivier.androiderestaurant.GlobalCounter
import fr.isen.olivier.androiderestaurant.HomeActivity
import fr.isen.olivier.androiderestaurant.R

class BasketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasketView()
        }
    }
}

object ItemsPrice {
    private var _itemsPrice = mutableDoubleStateOf(0.0)

    fun setValue(nb: Double) {
        _itemsPrice.doubleValue += nb
    }

    fun addValue(nb: Double) {
        _itemsPrice.doubleValue += nb
    }

    fun resetValue() {
        _itemsPrice.doubleValue = 0.0
    }

    fun getCurrentValue(): Double {
        return _itemsPrice.doubleValue
    }
}

@Composable fun BasketView() {
    val rememberedDictionary = remember {
        mutableStateMapOf<Double, Int>()
    }
    val context = LocalContext.current
    val basketItems = remember {
        mutableStateListOf<BasketItem>()
    }
    Surface {
        Column {
            TopBasket(rememberedDictionary)
            Row(horizontalArrangement = Arrangement.Absolute.SpaceEvenly) {
                Button(onClick = {
                    rememberedDictionary.clear()
                    GlobalCounter.resetValue()
                    Basket.current(context).items
                    basketItems.clear()
                    Basket.current(context).deleteAll(context)
                },
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)) {
                    Text("Annuler", color = Color.Yellow)
                }
                Button(onClick = {/*TODO*/},
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)) {
                    Text("Paiement", color = Color.Yellow)
                }
            }
            LazyColumn {
                items(basketItems) {
                    BasketItemView(it, basketItems)
                    rememberedDictionary[it.dish.prices.first().price.toDouble()] = it.count
                }
                ItemsPrice.resetValue()
                rememberedDictionary.forEach { (key, value) ->
                    ItemsPrice.addValue(key * value)
                }
            }
        }
    }
    basketItems.addAll(Basket.current(context).items)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun TopBasket(rememberedDictionary: SnapshotStateMap<Double, Int>) {
    val context = LocalContext.current
    TopAppBar({
        Row {
            Button(
                onClick = {
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                AsyncImage(
                    model = R.drawable.menu,
                    null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(100.dp)
                        .height(80.dp)
                        .zIndex(1f)
                )
            }
            Text(
                "Total de la commande :",
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .fillMaxWidth(0.73f)
                    .height(60.dp)
                    .offset(y = 10.dp)
            )
            Text(
                text = ItemsPrice.getCurrentValue().toString(),
                modifier = Modifier.padding(vertical = 25.dp),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            )
            rememberedDictionary.clear()
            Text(
                "€",
                modifier = Modifier.padding(vertical = 25.dp),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

        }


    })
}
@Composable fun BasketItemView(item: BasketItem, basketItems: MutableList<BasketItem>) {
    Card {
        val context = LocalContext.current
        Card(border =  BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.dish.images.first())
                        .build(),
                    null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(10))
                        .padding(8.dp)
                )
                Column(
                    Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                ) {
                    Text(item.dish.name)
                    Text("${item.dish.prices.first().price} €")
                }

                Spacer(Modifier.weight(1f))
                Text(item.count.toString(),
                    Modifier.align(alignment = Alignment.CenterVertically))
                TextButton(onClick = {
                    // delete item and redraw view
                    GlobalCounter.removeValue(item.count)
                    if (GlobalCounter.getCurrentValue() < 0) GlobalCounter.resetValue()
                    Basket.current(context).delete(item, context)
                    basketItems.clear()
                    basketItems.addAll(Basket.current(context).items)
                }) {
                    Text("X", color = Color.Red)
                }
            }
        }
    }
}