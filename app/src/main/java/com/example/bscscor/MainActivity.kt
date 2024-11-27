package com.example.bscscor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

class MainActivity : AppCompatActivity() {

    private lateinit var Username: EditText
    private lateinit var Password: EditText
    private lateinit var SignUpBtn: TextView

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

            if(savedpassword.isEmpty() && savedusername.isEmpty()){
                username = Username.text.toString()
                password = Password.text.toString()

            } else {
                username = savedusername
                password = savedpassword
            }

            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password!", Toast.LENGTH_SHORT).show()
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
                                if (storedPassword == password) {
                                    // Credentials are correct, proceed to next activity
                                    Toast.makeText(this@MainActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@MainActivity, CertificateOfRegistration::class.java))
                                    return
                                } else {
                                    // Password is incorrect
                                    Toast.makeText(this@MainActivity, "Incorrect password!", Toast.LENGTH_SHORT).show()
                                    return
                                }
                            }
                        } else {
                            // User does not exist
                            Toast.makeText(this@MainActivity, "Account not found!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        if(savedpassword.isNotEmpty() && savedusername.isNotEmpty()){
            loginButton.performClick()
        }


        SignUpBtn = findViewById(R.id.signupbtn)
        SignUpBtn.setOnClickListener {
            // Inflate the custom layout
            val dialogView = layoutInflater.inflate(R.layout.signup, null)

            // Initialize the dialog inputs
            val studentIdEditText = dialogView.findViewById<EditText>(R.id.studentIdEditText)
            val usernameEditText = dialogView.findViewById<EditText>(R.id.usernameEditText)
            val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
            val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)
            val saveAccountButton = dialogView.findViewById<Button>(R.id.saveAccountButton)

            // Create the dialog
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Set up the button click listener
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
                    // Save data to Firebase
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
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.putString("studentid", studentId)
                    editor.apply()


                    // Save the user to Firebase
                    if (userId != null) {
                        usersRef.child(userId).setValue(user)
                            .addOnSuccessListener {
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