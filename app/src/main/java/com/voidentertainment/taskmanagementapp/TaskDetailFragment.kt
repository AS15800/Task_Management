package com.voidentertainment.taskmanagementapp

import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

class TaskDetailFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var titleEditText: EditText
    private lateinit var itemContainer: LinearLayout
    private lateinit var saveButton: Button

    private var taskId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)

        val backToListButton: Button = view.findViewById(R.id.backToListButton)
        backToListButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val deleteTaskButton: Button = view.findViewById(R.id.deleteTaskButton)
        deleteTaskButton.setOnClickListener {
            deleteTask()
        }

        //Initialise DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        //Initialize views
        titleEditText = view.findViewById(R.id.detailTitleEditText)
        itemContainer = view.findViewById(R.id.itemContainer)
        saveButton = view.findViewById(R.id.saveButton)

        //Get the task ID from arguments
        taskId = arguments?.getInt("task_id") ?: 0

        //Load the task details
        loadTaskDetails()

        //Save button
        saveButton.setOnClickListener {
            saveEditedTask()
        }

        return view
    }

    private fun deleteTask() {
        val rowsDeleted = databaseHelper.deleteData(taskId)
        if (rowsDeleted > 0){
            Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()

            //Refresh the task list after deletion
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, TaskListFragment())
                .commit()

        } else
            Toast.makeText(requireContext(), "Failed to delete task", Toast.LENGTH_SHORT).show()
    }

    //Load task details by ID
    private fun loadTaskDetails() {
        val task = databaseHelper.getTaskById(taskId)
        task?.let {
            titleEditText.setText(it.title)
            itemContainer.removeAllViews()
            it.items.forEach { item ->
                addItemEditText(item)
            }
        }
    }

    private fun addItemEditText(item: String){
        val editText = EditText(requireContext())
        editText.setText(item)
        editText.setTextColor(Color.parseColor("#FFFFFF"))

        editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotBlank() && itemContainer.childCount == 1 || itemContainer.indexOfChild(editText) == itemContainer.childCount - 1){
                    addItemEditText("")
                }
            }
        })

        itemContainer.addView(editText)
    }

    private fun saveEditedTask(){
        val title = titleEditText.text.toString()
        if(title.isEmpty()){
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        val items = mutableListOf<String>()

        for(i in 0 until itemContainer.childCount){
            val editText = itemContainer.getChildAt(i) as EditText

            val text = editText.text.toString()
            if(text.isNotEmpty()){
                items.add(text)
            }
        }

        val rowsUpdated = databaseHelper.updateData(taskId, title, items)
        if(rowsUpdated > 0){
            Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Failed to update task", Toast.LENGTH_SHORT).show()
        }
    }
}