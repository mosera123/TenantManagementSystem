package com.example.mytenantmanagamenntsystem2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytenantmanagamenntsystem2.databinding.ActivityMainBinding
import com.example.tenantmanagementsystemgroupa.Tenant

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastTenant: Tenant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loggedInEmail = intent.getStringExtra("EMAIL")
        if (!loggedInEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Logged in as $loggedInEmail", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            val name = binding.tenantNameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val rent = binding.rentEditText.text.toString().trim()

            var isValid = true
            if (name.isEmpty()) {
                binding.tenantNameEditText.error = "Required"
                isValid = false
            }
            if (phone.isEmpty()) {
                binding.phoneEditText.error = "Required"
                isValid = false
            }
            if (rent.isEmpty()) {
                binding.rentEditText.error = "Required"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            val tenant = Tenant(name, phone, rent)
            binding.tenant = tenant
            lastTenant = tenant
        }

        binding.callButton.setOnClickListener {
            val tenant = lastTenant
            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}"))
            startActivity(intent)
        }

        binding.shareButton.setOnClickListener {
            val tenant = lastTenant
            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, tenant.summary())
            }
            startActivity(Intent.createChooser(intent, "Share tenant"))
        }
    }
}