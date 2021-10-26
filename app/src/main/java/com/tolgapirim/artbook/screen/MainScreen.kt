package com.tolgapirim.artbook.screen

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {

    val artNameList: ArrayList<String> = arrayListOf()
    val idList: ArrayList<Int> = arrayListOf()

    val context = LocalContext.current

    Scaffold(
        topBar = { TopBar(navController = navController) }
    ) {
        Surface(color = MaterialTheme.colors.background) {
            /* TODO art name Listesini yazdıracağız */

            getData(context, artNameList, idList)

            LazyColumn {
                items(artNameList.size) { index ->
                    Text(
                        text = artNameList[index],
                        style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("artScreen/${idList[index]}/${false}")
                            }
                            .padding(16.dp)

                    )

                }
            }

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
            navController.navigate("artScreen/0/${true}")
        }) {
            Text(text = "Add Art")
        }

    }


}

fun getData(context: Context, nameList: ArrayList<String>, idList: ArrayList<Int>) {
    try {
        val db = context.openOrCreateDatabase("Artss", MODE_PRIVATE, null)

        val cursor = db.rawQuery("SELECT * FROM arts", null)

        val idIx = cursor.getColumnIndex("id")
        val nameIx = cursor.getColumnIndex("artName")

        nameList.clear()
        idList.clear()
        while (cursor.moveToNext()) {
            val artName = cursor.getString(nameIx)
            val id = cursor.getInt(idIx)

            nameList.add(artName)
            idList.add(id)
        }

        cursor.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }

}



