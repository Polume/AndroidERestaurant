package fr.isen.olivier.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.isen.olivier.androiderestaurant.network.Category
import fr.isen.olivier.androiderestaurant.network.Dish
import fr.isen.olivier.androiderestaurant.network.MenuResult
import fr.isen.olivier.androiderestaurant.network.NetworkConstants
import com.google.gson.GsonBuilder
import fr.isen.olivier.androiderestaurant.basket.BasketActivity
import org.json.JSONObject

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = (intent.getSerializableExtra(CATEGROY_EXTRA_KEY) as? DishType) ?: DishType.STARTER
        setContent {
            when (type) {
                DishType.STARTER -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(144, 238, 144)
                    ) {
                        MenuView(type)
                    }
                }
                DishType.MAIN -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(255, 165, 0)
                    ) {
                        MenuView(type)
                    }
                }
                DishType.DESSERT -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(255, 182, 193)
                    ) {
                        MenuView(type)
                    }
                }
            }
        }
        Log.d("lifeCycle", "Menu Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Menu Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Menu Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Menu Activity - onDestroy")
        super.onDestroy()
    }

    companion object {
        const val CATEGROY_EXTRA_KEY = "CATEGROY_EXTRA_KEY"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuView(type: DishType) {
    val context = LocalContext.current
    val category = remember {
        mutableStateOf<Category?>(null)
    }
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
                when (type.title()) {
                    "Entrées" -> {
                        AsyncImage(
                            model = R.drawable.entrees,
                            null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(300.dp)
                                .height(80.dp)
                                .padding(vertical = 10.dp, horizontal = 65.dp)
                        )
                    }

                    "Plats" -> {
                        AsyncImage(
                            model = R.drawable.plats,
                            null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(300.dp)
                                .height(80.dp)
                                .padding(vertical = 10.dp, horizontal = 60.dp)
                        )
                    }

                    "Desserts" -> {
                        AsyncImage(
                            model = R.drawable.desserts,
                            null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(300.dp)
                                .height(80.dp)
                                .padding(vertical = 10.dp, horizontal = 60.dp)
                        )
                    }
                }
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
    Column (Modifier.offset(y = 80.dp)){
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            category.value?.let {
                items(it.items) {
                    DishRow(it)
                }
            }
        }
    }
    PostData(type, category)
}

@Composable fun DishRow(dish: Dish) {
    val context = LocalContext.current
    Card(border =  BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DISH_EXTRA_KEY, dish)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }
    ) {
        Row(Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dish.images.first())
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
            Text(dish.name,
                Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text("${dish.prices.first().price} €",
                Modifier.align(alignment = Alignment.CenterVertically))
        }
    }
}

@Composable
fun PostData(type: DishType, category: MutableState<Category?>) {
    val currentCategory = type.title()
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)

    val params = JSONObject()
    params.put(NetworkConstants.ID_SHOP, "1")

    val request = JsonObjectRequest(
        Request.Method.POST,
        NetworkConstants.URL,
        params,
        { response ->
            Log.d("request", response.toString(2))
            val result = GsonBuilder().create().fromJson(response.toString(), MenuResult::class.java)
            val filteredResult = result.data.first { categroy -> categroy.name == currentCategory }
            category.value = filteredResult
        },
        {
            Log.e("request", it.toString())
        }
    )

    queue.add(request)

}