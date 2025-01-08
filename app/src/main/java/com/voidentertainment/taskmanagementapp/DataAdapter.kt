package com.voidentertainment.taskmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(
    private var taskList: List<Task>,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<DataAdapter.ViewHolder>(){

        inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
            val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataAdapter.ViewHolder, position: Int) {
        val task = taskList[position]

        holder.titleTextView.text = task.title

        holder.itemView.setOnClickListener {
            val fragment = TaskDetailFragment()

            //Pass data to the Fragment using bundle
            val bundle = Bundle()
            bundle.putInt("task_id", task.id)
            fragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}

data class Task(
    val id: Int,
    val title: String,
    val items: List<String>
)