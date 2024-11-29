package com.example.bscscor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.storage
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

val supabase = createSupabaseClient(
    supabaseUrl = "https://zdabqmaoocqiqjlbjymi.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpkYWJxbWFvb2NxaXFqbGJqeW1pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI4NTQyODcsImV4cCI6MjA0ODQzMDI4N30.m0Mi4G4Henu9nt_E4P0TqJVKe_Q1S6ZhC7UkLRWpTsA"
) {
    install(Postgrest)
}


class MainActivity : AppCompatActivity() {

    private lateinit var Username: EditText
    private lateinit var Password: EditText
    private lateinit var SignUpBtn: TextView
    private lateinit var Profile: ImageView
    private lateinit var openimagebtn: Button
    private val IMAGE_PICK_REQUEST = 1001

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Username = findViewById(R.id.username)
        Password = findViewById(R.id.password)
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        var username = ""
        var password = ""
        val savedusername = sharedPreferences.getString("username", "").toString()
        val savedpassword = sharedPreferences.getString("password", "").toString()

        val loginButton = findViewById<Button>(R.id.loginbtn)
        loginButton.setOnClickListener {
            if (savedpassword.isEmpty() && savedusername.isEmpty()) {
                username = Username.text.toString()
                password = Password.text.toString()
            } else {
                username = savedusername
                password = savedpassword
            }

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Reference to Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users")

            // Query the database for the username
            usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val storedPassword = userSnapshot.child("password").value.toString()
                                val storedId = userSnapshot.child("studentid").value.toString()
                                if (storedPassword == password) {
                                    // Credentials are correct, proceed to next activity
                                    Toast.makeText(
                                        this@MainActivity, "Login successful!", Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(Intent(this@MainActivity, CertificateOfRegistration::class.java)
                                        .apply {
                                            putExtra("username", username)
                                            putExtra("password", password)
                                            putExtra("studentid", storedId)
                                            editor.putString("studentid", storedId)
                                            editor.putString("username", username)
                                            editor.putString("password", password)
                                            editor.apply()
                                        }
                                    )
                                    return
                                } else {
                                    // Password is incorrect
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Incorrect password!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }
                            }
                        } else {
                            // User does not exist
                            Toast.makeText(
                                this@MainActivity,
                                "Account not found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        if (savedpassword.isNotEmpty() && savedusername.isNotEmpty()) {
            loginButton.performClick()
        }

        SignUpBtn = findViewById(R.id.signupbtn)
        SignUpBtn.setOnClickListener {
            // Check if user is already signed up
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val savedStudentId = sharedPreferences.getString("studentid", null)
            val savedUsername = sharedPreferences.getString("username", null)

            // If any of these are not null, it means the user has already signed up
            if (savedStudentId != null && savedUsername != null) {
                Toast.makeText(this, "You are already signed up!", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with showing the signup dialog if the user has not signed up yet
                val dialogView = layoutInflater.inflate(R.layout.signup, null)
                val studentIdEditText = dialogView.findViewById<EditText>(R.id.studentIdEditText)
                val usernameEditText = dialogView.findViewById<EditText>(R.id.usernameEditText)
                val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
                val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)
                val saveAccountButton = dialogView.findViewById<Button>(R.id.saveAccountButton)

                val dialog = AlertDialog.Builder(this).setView(dialogView).create()

                saveAccountButton.setOnClickListener {
                    val studentId = studentIdEditText.text.toString()
                    val username = usernameEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    val confirmPassword = confirmPasswordEditText.text.toString()

                    if (studentId.isBlank() || username.isBlank() || password.isBlank()) {
                        Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                    } else if (password != confirmPassword) {
                        Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Save data to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("username", username)
                        editor.putString("password", password)
                        editor.putString("studentid", studentId)
                        editor.apply()

                        // Save the user to Firebase
                        val database = FirebaseDatabase.getInstance()
                        val usersRef = database.getReference("Users")

                        // Generate a unique key for the user
                        val userId = usersRef.push().key

                        // Create a user map
                        val user = mapOf(
                            "studentid" to studentId,
                            "username" to username,
                            "password" to password
                        )

                        // Save the user to Firebase
                        if (userId != null) {
                            usersRef.child(userId).setValue(user).addOnSuccessListener {
                                Toast.makeText(this, "Account saved successfully!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save account: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                dialog.show()
            }
        }
    }
}