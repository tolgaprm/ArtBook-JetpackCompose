package com.tolgapirim.artbook.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {

    Scaffold(
        topBar = { TopBar(navController = navController) }
    ) {
        Surface(color = MaterialTheme.colors.background) {
             /* TODO art name Listesini yazdıracağız */

        }
    }

}


@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Art Name",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 10.dp)
            )
        },
        actions = {
            TopAppBarDropdownMenu(navController = navController)
        },

        )
}


@Composable
fun TopAppBarDropdownMenu(navController: NavController) {

    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = {
            expanded.value = true
        }) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
        }
    }


    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {

        // ADD ART Screen
        DropdownMenuItem(onClick = {
            expanded.value = false
            navController.navigate("artScreen")
        }) {
            Text(text = "Add Art")
        }

    }

}



