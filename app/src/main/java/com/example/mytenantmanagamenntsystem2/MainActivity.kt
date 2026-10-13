package com.example.mytenantmanagamenntsystem2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mytenantmanagamenntsystem2.databinding.ActivityMainBinding
import com.example.tenantmanagementsystemgroupa.Tenant

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val name = binding.tenantNameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val rent = binding.rentEditText.text.toString().trim()

            if (name.isBlank()) {
                binding.tenantNameEditText.error = "Tenant name is required"
                return@setOnClickListener
            }

            binding.tenant = Tenant(name, phone, rent)

            binding.tenantNameEditText.text?.clear()
            binding.phoneEditText.text?.clear()
            binding.rentEditText.text?.clear()
        }
    }
}