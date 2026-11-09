package br.com.heitor.tarefas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDao {
    @Query("SELECT * FROM tarefas ORDER BY concluida ASC, prioridade DESC, dataCriacao DESC, id DESC")
    fun observarTodas(): Flow<List<Tarefa>>

    @Insert
    suspend fun inserir(tarefa: Tarefa): Long

    @Update
    suspend fun atualizar(tarefa: Tarefa): Int

    @Delete
    suspend fun excluir(tarefa: Tarefa): Int
}
