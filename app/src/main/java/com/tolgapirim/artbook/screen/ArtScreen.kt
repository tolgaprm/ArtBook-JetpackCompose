package com.tolgapirim.artbook.screen

import android.Manifest
import android.app.Activity.MODE_PRIVATE
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.tolgapirim.artbook.R
import com.tolgapirim.artbook.ui.theme.ArtBookTheme
import java.io.ByteArrayOutputStream


@Composable
fun ArtScreen(navController: NavController, id: Int?, isFromMenu: Boolean?) {


    val context = LocalContext.current

    var artName by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.gorsel_secimi)
    val selectedImage = remember { mutableStateOf(bitmap) }


    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val intentFromResult = result.data

            intentFromResult?.let {
                val imageUri = it.data

                if (imageUri != null) {
                    var bitmap1: Bitmap? = null
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(context.contentResolver, imageUri)
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                            selectedImage.value = bitmap1
                        } else {
                            bitmap1 =
                                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                            selectedImage.value = bitmap1
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }

    }


    val permissionResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }


    Scaffold(
        topBar = { TopBar(titleTopBar = "Add Art") }
    ) {
        ArtBookTheme() {
            val db = context.openOrCreateDatabase("Artss", MODE_PRIVATE, null)
            if (!isFromMenu!!) {

                val cursor = db.rawQuery("SELECT * FROM arts WHERE id=?", arrayOf(id.toString()))

                val nameIx = cursor.getColumnIndex("artName")
                val artistIx = cursor.getColumnIndex("artistName")
                val yearIx = cursor.getColumnIndex("year")
                val imageIx = cursor.getColumnIndex("image")


                while (cursor.moveToNext()) {
                    artName = cursor.getString(nameIx)
                    artistName = cursor.getString(artistIx)
                    year = cursor.getString(yearIx)
                    val byte = cursor.getBlob(imageIx)

                    selectedImage.value = BitmapFactory.decodeByteArray(byte, 0, byte.size)

                }

                cursor.close()

            }

            Column(
                modifier = Modifier.fillMaxSize().padding(top=16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = selectedImage.value.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {

                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {

                                    permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                            } else {
                                val intentToGallery =
                                    Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                    )
                                activityResultLauncher.launch(intentToGallery)

                            }

                        }
                        .height(250.dp)
                        .width(300.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = artName,
                    onValueChange = { artName = it },
                    label = { Text(text = "Art Name") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(value = artistName,
                    onValueChange = { artistName = it },
                    label = { Text(text = "Artist Name") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(value = year,
                    onValueChange = { year = it },
                    label = { Text(text = "Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )

                Spacer(modifier = Modifier.height(24.dp))


                if(isFromMenu){
                    Button(
                        onClick = {
                            saveToDatabase(
                                selectedImage.value,
                                artName,
                                artistName,
                                year,
                                navController,
                                db
                            )
                        }
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.button.copy(fontSize = 17.sp)
                        )
                    }
                }


            }


        }
    }

}


@Composable
fun TopBar(titleTopBar: String) {
    TopAppBar {
        Text(
            text = titleTopBar, Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
        )
    }
}


fun saveToDatabase(

    selectedBitmap: Bitmap,
    artName: String,
    artistName: String,
    year: String,
    navController: NavController,
    db: SQLiteDatabase
) {

    try {


        db.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artName VARCHAR, artistName VARCHAR, year VARCHAR, image BLOB)")

        val sqlString = "INSERT INTO arts (artName, artistName, year, image) VALUES (?,?,?,?)"

        val compileStatement = db.compileStatement(sqlString)


        val smallBitmap = createSmallBitmap(selectedBitmap, 300)

        val outputStream = ByteArrayOutputStream()
        smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        val byteArray = outputStream.toByteArray()

        compileStatement.bindString(1, artName)
        compileStatement.bindString(2, artistName)
        compileStatement.bindString(3, year)
        compileStatement.bindBlob(4, byteArray)

        compileStatement.execute()

        navController.navigate("mainScreen")

    } catch (e: Exception) {
        e.printStackTrace()
    }


}


fun createSmallBitmap(selectedBitmap: Bitmap, maxValue: Int): Bitmap {
    var height = selectedBitmap.height
    var width = selectedBitmap.width

    val bitmapOrani: Double = width.toDouble() / height.toDouble()

    if (bitmapOrani > 1) {
        // landscape
        width = maxValue
        val scaleHeight = width / bitmapOrani
        height = scaleHeight.toInt()

    } else {
        // portait
        height = maxValue
        val scaleWidth = height * bitmapOrani
        width = scaleWidth.toInt()
    }

    return Bitmap.createScaledBitmap(selectedBitmap, width, height, true)

}




