package com.shail_singh.jetpackroomdboperations

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.R
import com.shail_singh.jetpackroomdboperations.databinding.ActivityMainBinding
import com.shail_singh.jetpackroomdboperations.databinding.DeleteRecordDialogBinding
import com.shail_singh.jetpackroomdboperations.databinding.EditRecordDialogBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.btnAddRecord?.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAll().collect {
                populateRecycleView(it, employeeDao)
            }
        }
    }

    private fun addRecord(employeeDao: EmployeeDao) {
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()
        if (name.isNotEmpty() and email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmail?.text?.clear()
            }
        } else {
            Toast.makeText(applicationContext, "Input(s) cannot be empty!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun populateRecycleView(employeeList: List<EmployeeEntity>, employeeDao: EmployeeDao) {
        if (employeeList.isNotEmpty()) {
            val itemAdapter = ItemAdapter(employeeList, { updateId ->
                updateRecordAction(updateId, employeeDao)
            }, { deleteId ->
                deleteRecordAction(deleteId, employeeDao)
            })
            binding?.rvRecordsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvRecordsList?.adapter = itemAdapter
            binding?.rvRecordsList?.visibility = View.VISIBLE
            binding?.tvRecordsPlaceholderText?.visibility = View.INVISIBLE
        } else {
            binding?.rvRecordsList?.visibility = View.INVISIBLE
            binding?.tvRecordsPlaceholderText?.visibility = View.VISIBLE
        }
    }

    private fun updateRecordAction(id: Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this, R.style.Theme_AppCompat_Dialog_Alert)
        updateDialog.setCancelable(false)
        val dialogBinding = EditRecordDialogBinding.inflate(layoutInflater)
        updateDialog.setContentView(dialogBinding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployee(id).collect { employeeEntity ->
                employeeEntity?.let {
                    dialogBinding.etName.setText(it.name)
                    dialogBinding.etEmail.setText(it.email)
                }
            }
        }

        dialogBinding.btnDialogUpdate.setOnClickListener {
            val newName = dialogBinding.etName.text.toString()
            val newEmail = dialogBinding.etEmail.text.toString()
            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id = id, name = newName, email = newEmail))
                    Toast.makeText(
                        this@MainActivity, "Record Updated", Toast.LENGTH_LONG
                    ).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(applicationContext, "Input(s) cannot be empty!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        dialogBinding.btnDialogCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    private fun deleteRecordAction(id: Int, employeeDao: EmployeeDao) {
        val deleteDialog = Dialog(this, R.style.Theme_AppCompat_Dialog_Alert)
        deleteDialog.setCancelable(false)
        val dialogBinding = DeleteRecordDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnDialogDeleteYes.setOnClickListener {
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id = id, name = "", email = ""))
                deleteDialog.dismiss()
            }
        }

        dialogBinding.btnDialogDeleteNo.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }
}