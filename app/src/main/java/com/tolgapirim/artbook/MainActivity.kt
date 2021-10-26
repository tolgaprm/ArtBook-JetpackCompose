package com.tolgapirim.artbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tolgapirim.artbook.screen.ArtScreen
import com.tolgapirim.artbook.screen.MainScreen
import com.tolgapirim.artbook.ui.theme.ArtBookTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtBookTheme {
                // A surface container using the 'background' color from the theme
                Navigation()

            }
        }
    }
}


@Composable
fun Navigation() {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "mainScreen") {

        composable("mainScreen") {
            MainScreen(navController = navController)
        }


        // isFromMenu argumenti hangi sayfadan geldiğimizi anlamak için bir kontrol
        // menuden gidersek yeni bir art ekleyeceğiz.
        // yada LazyColumn içerisinde tıkalnan art onun özelliklerini göreceğiz.
        composable("artScreen/{id}/{isFromMenu}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("isFromMenu") { type = NavType.BoolType }
            )) {
            ArtScreen(
                navController = navController,
                it.arguments?.getInt("id"),
                it.arguments?.getBoolean("isFromMenu")
            )

        }
    }
}


