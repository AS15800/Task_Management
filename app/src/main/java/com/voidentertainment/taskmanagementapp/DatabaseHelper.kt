package com.voidentertainment.taskmanagementapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

class DatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

        companion object{
            private const val DATABASE_NAME = "taskList.db"
            private const val DATABASE_VERSION = 1

            private const val TABLE_NAME = "TaskInputs"
            private const val COLUMN_ID = "id"
            private const val COLUMN_TITLE = "taskTitle"
            private const val COLUMN_ITEM = "taskItem"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT NOT NULL,
            $COLUMN_ITEM TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //Insert a new task
    fun insertData(title:String, items: List<String>) : Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_ITEM, items.joinToString(", "))
        }
        return db.insert(TABLE_NAME, null, values)
    }

    //Retrieve all tasks
    fun getAllData(): Map<String, List<String>> {
        val data = mutableMapOf<String, List<String>>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val items = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM)).split(", ")
                data[title] = items
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    //Update an existing task
    fun updateData(id: Int, title: String, items: List<String>): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_ITEM, items.joinToString(", "))
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }


    //Get a task by ID
    fun getTaskById(id: Int): Task? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=?", arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val items = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM)).split(", ")
            Task(id = id, title = title, items = items)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun deleteData(id: Int): Int{
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}
