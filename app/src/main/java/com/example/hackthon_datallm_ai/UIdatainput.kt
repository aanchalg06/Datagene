package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.opencsv.CSVParser
import org.apache.commons.csv.CSVFormat
import java.io.InputStreamReader

class UIdatainput(val context: Context, val navController: NavController) {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
    @Composable
    fun Databaseinput() {
        var documentId by remember { mutableStateOf(TextFieldValue()) }
        var fieldName by remember { mutableStateOf(TextFieldValue()) }
        val fields = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
        var isDialogOpen by remember { mutableStateOf(false) }
        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                readCsvFromUri(context, uri, tableName = documentId.text,navController)
            }
        }
        Scaffold(

            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description",
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { fields.value.removeLast() }) {
                            if (fields.value.isNotEmpty()) {
                                Image(
                                    painter = painterResource(id = R.drawable.undo),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                                )
                            }
                        }

                    },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Add Document", color = MaterialTheme.colorScheme.background)
                    }
                )
            }
        ) {

            val Datatype =
                arrayOf(
                    "string",
                    "primaryKey",
                    "number",
                    "boolean",
                    "map",
                    "array",
                    "null",
                    "timestamp",
                )
            var expanded by remember { mutableStateOf(false) }
            var selectedText by remember { mutableStateOf(Datatype[0]) }
            var isPrimaryKeyAdded by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp, horizontal = 15.dp)
                    .clip(shape = RoundedCornerShape(50.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 80.dp, horizontal = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Input for Document ID
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                unfocusedTextColor =  MaterialTheme.colorScheme.onSecondary
                            ),
                            value = documentId,
                            onValueChange = { documentId = it },
                            label = { Text("Document Name", color = MaterialTheme.colorScheme.onBackground) }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { // Input for Field Name
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(40f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                                    cursorColor = MaterialTheme.colorScheme.onSecondary,
                                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                    unfocusedTextColor =  MaterialTheme.colorScheme.onSecondary
                                ),
                                value = fieldName,
                                onValueChange = { fieldName = it },
                                label = { Text("Field Name", color = MaterialTheme.colorScheme.onBackground) }
                            )

                            Row(
                                modifier = Modifier
                                    .weight(41f)
                                    .padding(top = 7.5.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = {
                                        expanded = !expanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .menuAnchor(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                                            cursorColor = MaterialTheme.colorScheme.onSecondary,
                                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                                            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                                            unfocusedTextColor =  MaterialTheme.colorScheme.onSecondary
                                        ),
                                        value = selectedText,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.requiredHeight(300.dp)
                                    ) {
                                        Datatype.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(text = item, color = MaterialTheme.colorScheme.onBackground) },
                                                onClick = {

                                                    selectedText = item
                                                    expanded = false

                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }


                        // Button to add field
                        Row {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                onClick = {
                                    if (fieldName.text.isNotEmpty() && documentId.text.isNotEmpty()) {

                                        if (!selectedText.equals("primaryKey") || !isPrimaryKeyAdded) {
                                            if (selectedText.equals("primaryKey")) {
                                                showToast("Added Primary key")
                                                isPrimaryKeyAdded = true
                                            }
                                            fields.value.add(fieldName.text to selectedText)
                                            fieldName = TextFieldValue()
                                        } else {
                                            showToast("Primary key already added")
                                        }
                                    }
                                },
                                enabled = fieldName.text.isNotEmpty() && documentId.text.isNotEmpty()
                            ) {
                                Text("Add Field", color = MaterialTheme.colorScheme.background)
                            }
                            Text(text = "Or", Modifier.padding(10.dp))
                            IconButton(onClick = {
                                if (documentId.text.isNotEmpty()) {
                                    filePickerLauncher.launch("*/*")
                                } else {
                                    showToast("Insert Document id")
                                }
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.adddata),
                                    contentDescription = "",
                                    modifier = Modifier.size(30.dp)
                                )

                            }
                        }
                        // Display added fields
                        Column {
                            fields.value.forEach { (name, type) ->
                                Text(

                                    "$name: $type",
                                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                        // Button to submit data
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = { isDialogOpen = true },
                            enabled = fields.value.isNotEmpty()
                        ) {
                            Text("Submit", color = MaterialTheme.colorScheme.background)
                        }

                        Column(modifier = Modifier) { // Dialog for confirmation
                            if (isDialogOpen) {
                                Dialog(
                                    onDismissRequest = { isDialogOpen = false }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .clip(
                                                shape = RoundedCornerShape(25.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.surface),
                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {
                                        Text(
                                            "Are you sure you want to submit?",
                                            Modifier.padding(15.dp)
                                        )
                                        Row(
                                            modifier = Modifier.padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                                        ) {
                                            OutlinedButton(onClick = { isDialogOpen = false }) {
                                                Text(
                                                    "Cancel",
                                                    color = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                            Button(
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondary,
                                                ),
                                                onClick = {
                                                    DatabaseHelper(context).createTable(
                                                        documentId.text,
                                                        fields.value
                                                    )
                                                    isDialogOpen = false
                                                    navController.navigate("main_screen")
                                                }
                                            ) {
                                                Text(
                                                    "Confirm",
                                                    color = MaterialTheme.colorScheme.background
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    fun readCsvFromUri(context: Context, uri: Uri, tableName: String,navController: NavController) {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val reader = InputStreamReader(stream)
            val csvParser = org.apache.commons.csv.CSVParser(reader, CSVFormat.DEFAULT)

            var isFirstLine = true
            var fieldNames: List<String>? = null
            val csvDataMap = mutableMapOf<String, MutableList<Any>>()

            for (record in csvParser) {
                if (isFirstLine) {
                    isFirstLine = false
                    fieldNames = record.toList()
                    // Initialize lists for each column name
                    fieldNames?.forEach { columnName ->
                        csvDataMap[columnName] = mutableListOf()
                    }
                    // Infer column data types
                    val columnDefinitions = inferColumnDataTypes(csvDataMap)
                    // Create table based on inferred column definitions
                    DatabaseHelper(context).createTable(tableName, columnDefinitions)
                    continue
                }

                val contentValues = ContentValues()
                if (fieldNames != null) {
                    // Process each field according to its field name
                    fieldNames.forEachIndexed { index, columnName ->
                        val fieldValue = record.get(index)
                        if (fieldValue != null) {
                            // Add data to ContentValues
                            contentValues.put("\"$columnName\"", fieldValue.toString())
                            csvDataMap[columnName]?.add(fieldValue)
                        }
                    }
                }
                // Insert row into database
                DatabaseHelper(context).insertData(tableName, contentValues)
            }

            csvParser.close()
            navController.navigate("main_screen")
        }
    }


    private fun inferColumnDataTypes(csvDataMap: Map<String, List<Any>>): MutableList<Pair<String, String>> {
        val columnDefinitions = mutableListOf<Pair<String, String>>()
        csvDataMap.forEach { (columnName, values) ->
            val dataType = inferDataType(values)
            columnDefinitions.add(columnName to dataType)
        }
        return columnDefinitions
    }

    private fun inferDataType(values: List<Any>): String {
        // Infer data type based on values
        // You can implement your logic here to determine the data type
        // For simplicity, let's assume all values are of type "TEXT"
        return "string"
    }

    private fun showToast(message: String) {
        // Use application context to show toast
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}



