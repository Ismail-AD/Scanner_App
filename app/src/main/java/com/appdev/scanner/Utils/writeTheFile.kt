package com.appdev.scanner.Utils

import android.os.Environment
import com.appdev.scanner.ModelClass.QrCodeData
import com.github.jferard.fastods.OdsFactory
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import kotlin.random.Random


import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.OutputStreamWriter

fun writeExcelFileToDownloads(context: Context, data: List<QrCodeData>): File? {
    val workbook: Workbook = HSSFWorkbook()
    val sheet = workbook.createSheet("QR Codes")

    sheet.setColumnWidth(0, 9000)
    val header = sheet.createRow(0)
    header.createCell(0).setCellValue("Label")
    header.createCell(1).setCellValue("Time and Date")

    // Data rows
    data.forEachIndexed { index, qrCodeData ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(qrCodeData.qrCodeLabel)
        row.createCell(1).setCellValue(getCurrentTimeAndDate(qrCodeData.timeAndDate))
    }

    val randomNumber = Random.nextInt(100, 499)
    return try {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCodeData_$randomNumber.xls")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    workbook.write(outputStream)
                }
                workbook.close()
                File(it.path)
            }
        } else{
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "QRCodeData_$randomNumber.xls")

            FileOutputStream(file).use { fileOut ->
                workbook.write(fileOut)
            }
            workbook.close()
            file
        }
        file
    } catch (e: IOException) {
        null
    }
}

fun writeDocFileToDownloads(context: Context, data: List<QrCodeData>): File? {
    val document = XWPFDocument()

    // Create a table
    val table = document.createTable()

    // Create header row
    val headerRow = table.getRow(0) // Create the first row
    headerRow.getCell(0).text = "Label"
    headerRow.addNewTableCell().text = "Time and Date"

    // Create data rows
    data.forEach { qrCodeData ->
        val row = table.createRow()
        row.getCell(0).text = qrCodeData.qrCodeLabel
        row.getCell(1).text = getCurrentTimeAndDate(qrCodeData.timeAndDate)
    }

    // Save the file in the Downloads directory
    val randomNumber = Random.nextInt(500, 999)

    return try {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCodeData_$randomNumber.docx")
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    document.write(outputStream)
                }
                document.close()
                File(uri.path)
            }
        } else{
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "QRCodeData_$randomNumber.docx")

            FileOutputStream(file).use { fileOut ->
                document.write(fileOut)
            }
            document.close()
            file
        }
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun writeCsvFileToDownloads(context: Context, data: List<QrCodeData>): File? {
    val csvData = StringBuilder()
    csvData.append("Label,Time and Date\n") // Add the header

    // Add data rows
    data.forEach { qrCodeData ->
        val date = getCurrentTimeAndDate(qrCodeData.timeAndDate)
        csvData.append("${qrCodeData.qrCodeLabel} ; ${date}\n")
    }

    // Save the file in the Downloads directory
    val randomNumber = Random.nextInt(1000, 1499)
    return try {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCodeData_$randomNumber.csv")
                put(MediaStore.MediaColumns.MIME_TYPE, "csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(csvData.toString())
                    }
                }
                File(it.path)
            }
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val createdFile = File(downloadsDir, "QRCodeData_$randomNumber.csv")

            FileWriter(createdFile).use { fileWriter ->
                fileWriter.write(csvData.toString())
            }
            createdFile
        }
        file
    } catch (e: IOException) {
        null
    }
}


fun generateOdsFile(context: Context, qrCodeDataList: List<QrCodeData>): File? {
    val odsFactory = OdsFactory.create()
    val writer = odsFactory.createWriter()
    val document = writer.document()
    val table = document.addTable("QR_Codes")

    val headerRow = table.getRow(0)
    val headerCell1 = headerRow.getOrCreateCell(0)
    headerCell1.setStringValue("Label")
    val headerCell2 = headerRow.getOrCreateCell(3)
    headerCell2.setStringValue("Time and Date")

    val randomNumber = Random.nextInt(1500, 2000)
    for (i in qrCodeDataList.indices) {
        val (qrCodeLabel, timeAndDate) = qrCodeDataList[i]
        val dataRow = table.getRow(i + 1)
        val dataCell1 = dataRow.getOrCreateCell(0)
        dataCell1.setStringValue(qrCodeLabel)
        val dataCell2 = dataRow.getOrCreateCell(3)
        dataCell2.setStringValue(getCurrentTimeAndDate(timeAndDate))
    }

    return try {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val tempFile = File.createTempFile("temp", ".ods", context.cacheDir)
            writer.saveAs(tempFile)

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCodeData_$randomNumber.ods")
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/vnd.oasis.opendocument.spreadsheet"
                )
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    tempFile.inputStream().copyTo(outputStream)
                }
                tempFile.delete()
                File(uri.path)
            }
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outputFile = File(downloadsDir, "QRCodeData_$randomNumber.ods")
            writer.saveAs(outputFile)
            outputFile
        }
        file
    } catch (e: IOException) {
        null
    }
}


