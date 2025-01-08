package com.voidentertainment.taskmanagementapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class TaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var titleInput: EditText
    private lateinit var taskList: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backToListFragment = view.findViewById<Button>(R.id.backToListButton)
        backToListFragment.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //Initialize the database helper
        databaseHelper = DatabaseHelper(requireContext())

        titleInput = view.findViewById(R.id.titleInput)
        taskList = view.findViewById(R.id.taskList)

        addNewItemEditText()

        val saveButton: Button = view.findViewById(R.id.saveTaskButton)

        saveButton.setOnClickListener{
            saveDataToDatabase()
        }
    }

    private fun addNewItemEditText(){
        val itemEditText = EditText(requireContext())
        itemEditText.setTextColor(Color.parseColor("#FFFFFF"))
        itemEditText.hint = "New task"
        itemEditText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        itemEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotBlank() && taskList.childCount == 1 || taskList.indexOfChild(itemEditText) == taskList.childCount - 1){
                    addNewItemEditText()
                }
            }
        })

        //Add the EditText to the Linear Layout
        taskList.addView(itemEditText)
    }

    private fun saveDataToDatabase(){
        val title = titleInput.text.toString().trim()

        //Validate the text
        if(title.isEmpty()){
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        //Collect all item inputs
        val items = mutableListOf<String>()
        for(i in 0 until taskList.childCount){
            val itemEditText = taskList.getChildAt(i) as EditText
            val itemText = itemEditText.text.toString().trim()
            if(itemText.isNotEmpty()){
                items.add(itemText)
            }
        }

        //Save to Database
        val rowId = databaseHelper.insertData(title, items)
        if(rowId != -1L) {
            Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
        else
            Toast.makeText(requireContext(), "Error saving data", Toast.LENGTH_SHORT).show()
    }
}