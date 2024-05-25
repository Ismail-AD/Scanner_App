package com.appdev.scanner.Utils

import android.os.Environment
import com.appdev.scanner.ModelClass.QrCodeData
import com.github.jferard.fastods.OdsFactory
import com.github.jferard.fastods.Table
import com.github.jferard.fastods.TableCell
import com.github.jferard.fastods.TableRowImpl
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import kotlin.random.Random


fun writeExcelFileToDownloads(data: List<QrCodeData>): File? {

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
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "QRCodeData_$randomNumber.xls")

        FileOutputStream(file).use { fileOut ->
            workbook.write(fileOut)
        }
        workbook.close()
        file
    } catch (e: IOException) {
        null
    }
}


fun writeDocFileToDownloads(data: List<QrCodeData>): File? {
    val document = XWPFDocument()

    // Create a table
    val table: XWPFTable = document.createTable()

    // Create header row
    val headerRow: XWPFTableRow = table.getRow(0) // Create the first row
    headerRow.getCell(0).text = "Label"
    headerRow.addNewTableCell().text = "Time and Date"

    // Create data rows
    data.forEach { qrCodeData ->
        val row: XWPFTableRow = table.createRow()
        row.getCell(0).text = qrCodeData.qrCodeLabel
        row.getCell(1).text = getCurrentTimeAndDate(qrCodeData.timeAndDate)
    }

    // Save the file in the Downloads directory
    val randomNumber = Random.nextInt(500, 999)

    return try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "QRCodeData_$randomNumber.docx")

        FileOutputStream(file).use { fileOut ->
            document.write(fileOut)
        }
        document.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun writeCsvFileToDownloads(data: List<QrCodeData>): File? {
    // Create a StringBuilder to accumulate CSV data
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
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "QRCodeData_$randomNumber.csv")

        FileWriter(file).use { fileWriter ->
            fileWriter.write(csvData.toString())
        }
        file
    } catch (e: IOException) {
        null
    }
}

fun generateOdsFile(qrCodeDataList: List<QrCodeData>): File? {
    val odsFactory = OdsFactory.create()
    val writer = odsFactory.createWriter()
    val document = writer.document()
    val table: Table = document.addTable("QR_Codes")

    val headerRow: TableRowImpl = table.getRow(0)
    val headerCell1: TableCell = headerRow.getOrCreateCell(0)
    headerCell1.setStringValue("Label")
    val headerCell2: TableCell = headerRow.getOrCreateCell(3)
    headerCell2.setStringValue("Time and Date")


    val randomNumber = Random.nextInt(1500, 2000)
    for (i in qrCodeDataList.indices) {
        val (qrCodeLabel, timeAndDate) = qrCodeDataList[i]
        val dataRow: TableRowImpl = table.getRow(i + 1)
        val dataCell1: TableCell = dataRow.getOrCreateCell(0)
        dataCell1.setStringValue(qrCodeLabel)
        val dataCell2: TableCell = dataRow.getOrCreateCell(3)
        dataCell2.setStringValue(getCurrentTimeAndDate(timeAndDate))
    }


    return try {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFile = File(downloadsDir, "QRCodeData_$randomNumber.ods")
        writer.saveAs(outputFile)
        outputFile
    } catch (e: IOException) {
        null
    }
}


