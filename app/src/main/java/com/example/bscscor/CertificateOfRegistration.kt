package com.example.bscscor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.OutputStream

class CertificateOfRegistration : AppCompatActivity() {

    private val CREATE_FILE_REQUEST_CODE = 1001
    private var selectedFormat: String = "PDF" // Default format
    private lateinit var EditCertificateLayout: View
    private lateinit var CertificateLayout: LinearLayout

    private lateinit var EditBtn: TextView
    private lateinit var saveButton: TextView
    private lateinit var PreviewBtn: TextView

    private lateinit var studentid: String
    private lateinit var username: String
    private lateinit var pasword: String

    private lateinit var StudentIdEdit: EditText
    private lateinit var StudentNameEdit: EditText
    private lateinit var StudentCourseEdit: EditText
    private lateinit var StudentDateEdit: EditText
    private lateinit var StudentYearEDit: EditText
    private lateinit var StudentLevelEdit: EditText

    private lateinit var StudentIdText: TextView
    private lateinit var StudentNameText: TextView
    private lateinit var StudentCourseText: TextView
    private lateinit var StudentDateText: TextView
    private lateinit var StudentYearText: TextView
    private lateinit var StudentLevelText: TextView

    private lateinit var codeRecyclerView: RecyclerView
    private lateinit var descriptionRecyclerview: RecyclerView
    private lateinit var unitRecyclerview: RecyclerView
    private lateinit var assessmentRecyclerview: RecyclerView
    private lateinit var assessmentRecyclerviewnumber: RecyclerView

    private lateinit var codeRecyclerView1: RecyclerView
    private lateinit var descriptionRecyclerview1: RecyclerView
    private lateinit var unitRecyclerview1: RecyclerView
    private lateinit var assessmentRecyclerview1: RecyclerView
    private lateinit var assessmentRecyclerviewnumber1: RecyclerView

    private val codeItems = mutableListOf<String>()
    private val descriptionItems = mutableListOf<String>()
    private val unitItems = mutableListOf<String>()

    private lateinit var AddSubjctBtn: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_of_registration)
        CertificateLayout = findViewById(R.id.linearLayout)
        EditCertificateLayout = findViewById(R.id.editcertiticatelayout)

        StudentIdEdit = findViewById(R.id.editstudentid)
        StudentNameEdit = findViewById(R.id.editstudentname)
        StudentCourseEdit = findViewById(R.id.editstudentcourse)
        StudentDateEdit = findViewById(R.id.editstudentdate)
        StudentYearEDit = findViewById(R.id.editstudentyear)
        StudentLevelEdit = findViewById(R.id.editstudentlevel)

        StudentIdText = findViewById(R.id.studentid)
        StudentNameText = findViewById(R.id.studentname)
        StudentCourseText = findViewById(R.id.studentcourse)
        StudentDateText = findViewById(R.id.studentdate)
        StudentYearText = findViewById(R.id.studentyear)
        StudentLevelText = findViewById(R.id.studentlevel)

        codeRecyclerView = findViewById(R.id.coderecyclerview)
        codeRecyclerView.layoutManager = LinearLayoutManager(this)
        descriptionRecyclerview = findViewById(R.id.descriptionrecyclerview)
        descriptionRecyclerview.layoutManager = LinearLayoutManager(this)
        unitRecyclerview = findViewById(R.id.unitrecyclerview)
        unitRecyclerview.layoutManager = LinearLayoutManager(this)
        assessmentRecyclerview = findViewById(R.id.assessmentrecyclerview)
        assessmentRecyclerview.layoutManager = LinearLayoutManager(this)
        assessmentRecyclerviewnumber = findViewById(R.id.assessmentrecyclerviewnumber)
        assessmentRecyclerviewnumber.layoutManager = LinearLayoutManager(this)
        codeRecyclerView1 = findViewById(R.id.coderecyclerview1)
        codeRecyclerView1.layoutManager = LinearLayoutManager(this)
        descriptionRecyclerview1 = findViewById(R.id.descriptionrecyclerview2)
        descriptionRecyclerview1.layoutManager = LinearLayoutManager(this)
        unitRecyclerview1 = findViewById(R.id.unitrecyclerview1)
        unitRecyclerview1.layoutManager = LinearLayoutManager(this)
        assessmentRecyclerview1 = findViewById(R.id.assessmentrecyclerview1)
        assessmentRecyclerview1.layoutManager = LinearLayoutManager(this)
        assessmentRecyclerviewnumber1 = findViewById(R.id.assessmentrecyclerviewnumber1)
        assessmentRecyclerviewnumber1.layoutManager = LinearLayoutManager(this)


        codeRecyclerView.adapter = AdapterSubjects(codeItems)
        descriptionRecyclerview.adapter = AdapterSubjects(descriptionItems)
        unitRecyclerview.adapter = AdapterSubjects(unitItems)
        val feeItems = listOf(
            "Development share/TF", "Athletic Fee", "Computer Fee", "Cultural Fee", "Medical Fee",
            "Library Fee", "Lab Fee"
        )
        assessmentRecyclerview.adapter = AdapterAssessmentFees(feeItems)
        val feesItems = listOf(
            "3925", "175", "275", "110", "125", "250", "250"
        )
        assessmentRecyclerviewnumber.adapter = AdapterAssessmentFees(feesItems)

        studentid = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("studentid", "").toString()
        username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", "").toString()
        pasword = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("password", "").toString()

        EditBtn = findViewById(R.id.editcertbtn)
        EditBtn.setOnClickListener{
            EditBtn.visibility = GONE
            saveButton.visibility = GONE
            PreviewBtn.visibility = VISIBLE

            EditCertificateLayout.visibility = VISIBLE
            CertificateLayout.visibility = GONE
        }

        saveButton = findViewById(R.id.downloadcert)
        saveButton.setOnClickListener {
            showSaveOptionsDialog()
        }

        PreviewBtn = findViewById(R.id.previewbtn)
        PreviewBtn.setOnClickListener {
            EditCertificateLayout.visibility = GONE
            CertificateLayout.visibility = VISIBLE

            EditBtn.visibility = VISIBLE
            saveButton.visibility = VISIBLE
            PreviewBtn.visibility = GONE

            preview(feesItems, feeItems, codeItems, descriptionItems, unitItems)
        }

        EditBtn.performClick()
        AddSubjctBtn = findViewById(R.id.addcode)
        AddSubjctBtn.setOnClickListener {
            shoaddSubject()
        }
    }

    fun preview(fees: List<String>, fee: List<String>, codeitems: MutableList<String>, descriptionitems: MutableList<String>,
                unititems: MutableList<String>){
        StudentIdText.text = StudentIdEdit.text.toString()
        StudentNameText.text = StudentNameEdit.text.toString()
        StudentCourseText.text = StudentCourseEdit.text.toString()
        StudentDateText.text = StudentDateEdit.text.toString()
        StudentYearText.text = StudentYearEDit.text.toString()
        StudentLevelText.text = StudentLevelEdit.text.toString()

        assessmentRecyclerview1.adapter = AdapterAssessmentFees(fee)
        assessmentRecyclerviewnumber1.adapter = AdapterAssessmentFees(fees)
        codeRecyclerView1.adapter = AdapterSubjects(codeitems)
        descriptionRecyclerview1.adapter = AdapterSubjects(descriptionitems)
        unitRecyclerview1.adapter = AdapterSubjects(unititems)
    }
    fun shoaddSubject() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.addsubject, null)
        val subjectCodeEditText = dialogView.findViewById<EditText>(R.id.subjectCodeEditText)
        val subjectNameEditText = dialogView.findViewById<EditText>(R.id.subjectNameEditText)
        val professorEditText = dialogView.findViewById<EditText>(R.id.professorEditText)
        val saveButton = dialogView.findViewById<Button>(R.id.savesubjectbtn)

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Handle "Save" button click
        saveButton.setOnClickListener {
            val code = subjectCodeEditText.text.toString()
            val description = subjectNameEditText.text.toString()
            val unit = professorEditText.text.toString()

            if (code.isNotEmpty()) {
                codeItems.add(code)
                codeRecyclerView.adapter?.notifyItemInserted(codeItems.size - 1)
            }

            if (description.isNotEmpty()) {
                descriptionItems.add(description)
                descriptionRecyclerview.adapter?.notifyItemInserted(descriptionItems.size - 1)
            }

            if (unit.isNotEmpty()) {
                unitItems.add(unit)
                unitRecyclerview.adapter?.notifyItemInserted(unitItems.size - 1)
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showSaveOptionsDialog() {
        val options = arrayOf("PDF", "PNG", "JPEG")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose a Format")
        builder.setItems(options) { _, which ->
            selectedFormat = options[which]
            openDirectoryChooser()
        }
        builder.show()
    }
    private fun openDirectoryChooser() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            when (selectedFormat) {
                "PDF" -> {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_TITLE, "certificate_of_registration.pdf")
                }
                "PNG" -> {
                    type = "image/png"
                    putExtra(Intent.EXTRA_TITLE, "layout_image.png")
                }
                "JPEG" -> {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_TITLE, "layout_image.jpg")
                }
            }
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                saveFile(uri)
            } ?: Toast.makeText(this, "No directory selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveFile(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                when (selectedFormat) {
                    "PDF" -> saveAsPDF(outputStream)
                    "PNG" -> saveAsImage(Bitmap.CompressFormat.PNG, outputStream)
                    "JPEG" -> saveAsImage(Bitmap.CompressFormat.JPEG, outputStream)
                }
            }
            Toast.makeText(this, "$selectedFormat saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show()
        }
    }
    private fun captureLayoutAsBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    private fun saveAsImage(format: Bitmap.CompressFormat, outputStream: OutputStream) {
        val bitmap = captureLayoutAsBitmap(findViewById(R.id.main))
        bitmap.compress(format, 100, outputStream)
    }
    private fun saveAsPDF(outputStream: OutputStream) {
        val view = findViewById<View>(R.id.main)
        val bitmap = captureLayoutAsBitmap(view)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}