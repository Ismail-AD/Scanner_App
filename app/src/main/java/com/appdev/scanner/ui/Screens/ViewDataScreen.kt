package com.appdev.scanner.ui.Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.appdev.scanner.R
import com.appdev.scanner.Utils.BarcodeScanner
import com.appdev.scanner.Utils.generateOdsFile
import com.appdev.scanner.Utils.getCurrentTimeAndDate
import com.appdev.scanner.Utils.writeCsvFileToDownloads
import com.appdev.scanner.Utils.writeDocFileToDownloads
import com.appdev.scanner.Utils.writeExcelFileToDownloads
import com.appdev.scanner.ViewModel.CustomViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    controller: NavHostController,
    customViewModel: CustomViewModel,
    barcodeScanner: BarcodeScanner
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var showRationale by remember(permissionState) {
        mutableStateOf(false)
    }
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        "Download as XLS file",
        "Download as ODS file",
        "Download as DOC file",
        "Download as CSV file"
    )

    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember {
        mutableStateOf(false)
    }
    val barcodeScannedRes =
        barcodeScanner.barCodeResults.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var loading by remember {
        mutableStateOf(false)
    }
    var message by remember {
        mutableStateOf("")
    }

    var grantedOrNot by remember(permissionState) {
        mutableStateOf(permissionState.status.isGranted)
    }

    val qrCodeList by customViewModel.getListData().collectAsStateWithLifecycle(initialValue = null)


    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            grantedOrNot = permissionState.status.isGranted
        }
    }


    LaunchedEffect(key1 = barcodeScannedRes.value) {
        barcodeScannedRes.value?.let { data ->
            playTone(context = context)
            loading = true
            customViewModel.saveQrCodeData(data) { errorOrNot ->
                loading = false
                errorOrNot?.let {
                    message = it
                }
            }
        }
    }
    LaunchedEffect(key1 = qrCodeList) {
        loading = qrCodeList == null
    }
    LaunchedEffect(key1 = message) {
        if (message.trim().isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            message = ""
            barcodeScanner.updateResult()
        }
    }


    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Scanner App",
                color = Color.Black,
                fontSize = 16.sp
            )
        }, actions = {
            Box {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.dotsblack),
                        contentDescription = "",
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(text = {
                            Text(
                                text = item,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                        }, onClick = {
                            expanded = false
                            when (item) {
                                "Download as XLS file" -> {
                                    qrCodeList?.let {
                                        scope.launch(Dispatchers.IO) {
                                            val file = writeExcelFileToDownloads(it)
                                            scope.launch(Dispatchers.Main) {
                                                if (file != null) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "File saved to Downloads",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Something went wrong !",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                }

                                "Download as ODS file" -> {
                                    qrCodeList?.let {
                                        scope.launch(Dispatchers.IO) {
                                            val file = generateOdsFile(it)
                                            scope.launch(Dispatchers.Main) {
                                                if (file != null) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "File saved to Downloads",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Something went wrong !",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                }

                                "Download as CSV file" -> {
                                    qrCodeList?.let {
                                        scope.launch(Dispatchers.IO) {
                                            val file = writeCsvFileToDownloads(it)
                                            scope.launch(Dispatchers.Main) {
                                                if (file != null) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "File saved to Downloads",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Something went wrong !",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                }

                                "Download as DOC file" -> {
                                    qrCodeList?.let {
                                        scope.launch(Dispatchers.IO) {
                                            val file = writeDocFileToDownloads(it)
                                            scope.launch(Dispatchers.Main) {
                                                if (file != null) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "File saved to Downloads",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Something went wrong !",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                }

                                else -> {

                                }
                            }
                        })
                    }
                }
            }
        })
    }, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = {
            if (grantedOrNot) {
                scope.launch(Dispatchers.IO) {
                    barcodeScanner.startScan()
                }
            } else if (permissionState.status.shouldShowRationale) {
                showRationale = true
            } else {
                permissionState.launchPermissionRequest()
            }
        }, modifier = Modifier.padding(15.dp), containerColor = Color.DarkGray) {
            Icon(
                imageVector = Icons.Filled.DocumentScanner,
                contentDescription = "BTN",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }) { pV ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pV)
        ) {
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Dialog(onDismissRequest = { /*TODO*/ }) {
                            CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            qrCodeList?.let { list ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    items(list, key = { it.timeAndDate }) { qrCodeData ->
                        Column(modifier = Modifier.padding(5.dp)) {
                            singleEntry(
                                label = qrCodeData.qrCodeLabel,
                                time = getCurrentTimeAndDate(qrCodeData.timeAndDate)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        if (showRationale) {
            AlertDialog(
                onDismissRequest = {
                    showRationale = false
                },
                title = {
                    Text(text = "Permissions required by the Application")
                },
                text = {
                    Text(text = "The Application requires the following permissions to work:\n CAMERA_ACCESS  ")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRationale = false
                            openAppSettings(context)
                        },
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showRationale = false
                        },
                    ) {
                        Text("Dismiss")
                    }
                },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun singleEntry(
    label: String,
    time: String
) {
    Column {
        Spacer(modifier = Modifier.height(7.dp))
        Card(
            onClick = {

            },
            modifier = Modifier
                .height(62.dp)
                .padding(horizontal = 7.dp),
            shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
            border = BorderStroke(
                width = 2.dp,
                color =  Color.Black
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.LightGray
            )

        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Label: " + if (label.length > 25) "${
                            label.take(
                                22
                            )
                        }..." else label,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        fontSize = 16.sp, modifier = Modifier.padding(start = 13.dp)
                    )
                    Text(
                        text = "Date : $time",
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        fontSize = 16.sp, modifier = Modifier.padding(start = 13.dp, top = 5.dp)
                    )
                }
            }
        }
    }
}


fun playTone(context: Context) {
    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.scan_complete)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        stopTone(mediaPlayer)
    }
}

fun stopTone(mediaPlayer: MediaPlayer) {
    mediaPlayer.stop()
    mediaPlayer.reset()
    mediaPlayer.release()
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", context.packageName, null)
    context.startActivity(intent)
}

