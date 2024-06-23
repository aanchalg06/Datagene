package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.Model.ViewModelChat
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel

class MainScreen {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun MainUi(
        context: Context,
        navController: NavController,
        chatviewmodel: ChatViewModel
    ) {
        var database by remember { mutableStateOf(DatabaseHelper(context).getTableNames()) }
        var longPressed by remember {
            mutableStateOf(false)
        }
        var selectedItems by remember { mutableStateOf(emptySet<String>()) }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("input") }) {

                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Localized description",
                    )

                }
            },
            topBar = {

                CenterAlignedTopAppBar(
                    actions = {
                        IconButton(onClick = {
                            for (item in selectedItems){
                                DatabaseHelper(context).dropTable(item)
                            }
                            selectedItems=emptySet()
                            longPressed=false
                            database = DatabaseHelper(context).getTableNames()
                        }) {
                            if(selectedItems.isNotEmpty() && longPressed){
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = "Localized description",
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("DataGenie", color = MaterialTheme.colorScheme.background)
                    }
                )
            }
        ) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {



                if (database.isNotEmpty()) {
                    LazyColumn(Modifier.padding(top = 10.dp)) {
                        items(database) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),

                                modifier = Modifier
                                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
                                    .fillMaxWidth()
                            )
                            {
                                Row(Modifier.padding(end = 5.dp)) {
                                    Image(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(38.dp)
                                            .align(Alignment.Bottom)
                                            .requiredHeight(200.dp),
                                        painter = painterResource(id = R.drawable.db),
                                        contentDescription = ""
                                    )
                                    Row(

                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                            .align(Alignment.Bottom)
                                            .combinedClickable(
                                                onLongClick = { longPressed = !longPressed },
                                                onClick = {
                                                    chatviewmodel.setDatabase(it)
                                                    chatviewmodel.setattributes(
                                                        DatabaseHelper(context).getTableAttributes(
                                                            it
                                                        )
                                                    )
                                                    navController.navigate("chat")
                                                },
                                            )

                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                RoundedCornerShape(5.dp)
                                            )

                                    ) {
                                        Column(
                                            Modifier
                                                .align(Alignment.Top)
                                                .fillMaxWidth()
                                                .weight(9f)
                                        ) {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.sp,
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .align(Alignment.Start)
                                                // Add padding for better spacing
                                            )

                                        }
                                        if (!longPressed) {
                                            Icon(
                                                imageVector = Icons.Filled.KeyboardArrowRight,
                                                contentDescription = "arrow",
                                                modifier = Modifier
                                                    .weight(1f)

                                                    .align(Alignment.CenterVertically).size(40.dp)
                                            )
                                        } else {
                                            FilterChip(
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    ,
                                                onClick = { if (it in selectedItems) {
                                                    selectedItems -= it
                                                } else {
                                                    selectedItems += it
                                                } },
                                                label = {
                                                },
                                                selected = it in selectedItems,
                                                leadingIcon = if(it in selectedItems ){
                                                    {
                                                        Icon(
                                                            imageVector = Icons.Filled.Done,
                                                            contentDescription = "Done icon",
                                                            modifier = Modifier.size(
                                                                FilterChipDefaults.IconSize
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    null
                                                },
                                            )
                                        }

                                    }
                                }

                            }
                        }

                    }
                } else {
                    Text(
                        text = "Please Add the database ",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 300.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 300.dp)
                            .size(200.dp)
                    )
                }
//                Button(onClick = {
//                    val dbHelper = DatabaseHelper(context)
//                    val contentValues = dbHelper.convertSqlToContentValues("INSERT INTO hospital (patient, room, discharge) VALUES (John Doe, 101, 0)")
//                    if (contentValues != null) {
//                        dbHelper.insertData("hospital", contentValues)
//                    }
//
//                }) {
//                    Text(text = "daal do")
//                }
            }
        }
    }

}