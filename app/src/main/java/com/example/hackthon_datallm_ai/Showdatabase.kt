import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hackthon_datallm_ai.R
import java.awt.font.NumericShaper.Range
import java.io.File
import com.opencsv.CSVWriter
import java.io.FileWriter
import java.io.IOException

class Showdatabase {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun Showdata(context: Context, chatViewModel: ChatViewModel, navController: NavController) {
        var search by remember { mutableStateOf("") }
        var suggest by remember { mutableStateOf(emptyList<String>()) }
        var isDropdownExpanded by remember {
            mutableStateOf(false)
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let { uri ->
                    try {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            // Create CSV file
                            val writer = CSVWriter(outputStream.writer())

                            // Write header row
                            val attributes =
                                chatViewModel._attributes.map { it.first }.toTypedArray()
                            writer.writeNext(attributes)

                            // Write data rows
                            val data = DatabaseHelper(context).getDataFromCursor(
                                chatViewModel.getAllData(),
                                chatViewModel._attributes
                            )
                            data.forEach { rowData ->
                                val values =
                                    rowData.split(", ").map { it.split(": ")[1] }.toTypedArray()
                                writer.writeNext(values)
                            }

                            // Close writer
                            writer.close()

                            // Show success message
                            Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            "Error exporting database $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        val focusManager = LocalFocusManager.current
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = MaterialTheme.colorScheme.surface,
                        contentDescription = "Localized description",
                    )
                }
            },
            topBar = {
                CenterAlignedTopAppBar(
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "text/csv"
                                putExtra(Intent.EXTRA_TITLE, "database.csv")
                            }
                            exportLauncher.launch(intent)
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.upload),
                                contentDescription = "",
                                modifier = Modifier.size(25.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                tint = MaterialTheme.colorScheme.surface,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("DataGenie 📊", color = MaterialTheme.colorScheme.background)
                    }
                )
            }
        ) {
            val cursor = chatViewModel.getAllData()

            // If cursor is not null and has data
            if (cursor != null) {
                val attributes = chatViewModel._attributes.map { it.first }

                Column(
                    Modifier
                        .padding(it)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                keyboardController?.hide()
                                focusManager.clearFocus(force = true)
                            })
                        }) {
                    Column(Modifier.padding(8.dp)) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = {
                                search = it
                                isDropdownExpanded = it.isNotEmpty()
                                chatViewModel.searchInDatabase(it) { suggestions ->
                                    suggest = suggestions
                                    println(suggest)
                                }
                            },
                            label = { Text("Search") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                unfocusedTextColor =  MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
//                                imeAction= ImeAction.Previous
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                    keyboardController?.hide()
                                    focusManager.clearFocus(force = true)
                                },
                            ),
                        )

                    }
                    LazyColumn(
                        Modifier.fillMaxWidth()
                    ) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                shadowElevation = 4.dp,
                                tonalElevation = 5.dp,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Display attributes as table headers
                                    attributes.forEach { attribute ->
                                        Text(
                                            text = attribute,
                                            modifier = Modifier
                                                .weight(2f)
                                                .padding(horizontal = 4.dp),
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                            fontWeight = FontWeight.Bold,
                                            color=MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        items(
                            if (!isDropdownExpanded) {
                                DatabaseHelper(context).getDataFromCursor(
                                    cursor,
                                    chatViewModel._attributes
                                )
                            } else {
                                suggest
                            }
                        ) { rowData ->
                            Surface(
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp,
                                tonalElevation = 5.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Display data rows
                                    rowData.split(", ").forEach { entry ->
                                        val (key, value) = entry.split(": ")
                                        Text(
                                            text = value.trim(),

                                            color = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "No data available")
            }
        }
    }


}
