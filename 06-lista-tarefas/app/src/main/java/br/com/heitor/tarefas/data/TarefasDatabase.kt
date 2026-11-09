package br.com.heitor.tarefas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tarefa::class], version = 1, exportSchema = false)
abstract class TarefasDatabase : RoomDatabase() {
    abstract fun tarefaDao(): TarefaDao

    companion object {
        @Volatile private var instance: TarefasDatabase? = null

        fun obter(context: Context): TarefasDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                TarefasDatabase::class.java,
                "tarefas.db"
            ).build().also { instance = it }
        }
    }
}
