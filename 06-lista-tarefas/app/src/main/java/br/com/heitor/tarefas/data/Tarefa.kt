package br.com.heitor.tarefas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarefas")
data class Tarefa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String = "",
    val concluida: Boolean = false,
    val prioridade: Int = 2,
    val dataCriacao: Long = System.currentTimeMillis()
)
