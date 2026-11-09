package br.com.heitor.tarefas

import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.heitor.tarefas.data.Tarefa
import br.com.heitor.tarefas.data.TarefasDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TarefaDaoTest {
    private lateinit var db: TarefasDatabase

    @Before fun criarBanco() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), TarefasDatabase::class.java
        ).build()
    }

    @After fun fecharBanco() { db.close() }

    @Test fun crudEOrdenacaoPorSituacaoEPrioridade() = runBlocking {
        val dao = db.tarefaDao()
        val baixa = dao.inserir(Tarefa(titulo = "Baixa", prioridade = 1, dataCriacao = 1))
        val alta = dao.inserir(Tarefa(titulo = "Alta", prioridade = 3, dataCriacao = 2))
        assertEquals(listOf(alta, baixa), dao.observarTodas().first().map { it.id })

        dao.atualizar(Tarefa(id = alta, titulo = "Alta editada", prioridade = 3,
            concluida = true, dataCriacao = 2))
        assertEquals(listOf(baixa, alta), dao.observarTodas().first().map { it.id })
        assertTrue(dao.observarTodas().first().last().concluida)

        dao.excluir(dao.observarTodas().first().first())
        assertEquals(listOf(alta), dao.observarTodas().first().map { it.id })
    }

    @Test fun tarefaPermaneceAoReabrirBanco() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nome = "tarefas_teste.db"
        context.deleteDatabase(nome)
        try {
            Room.databaseBuilder(context, TarefasDatabase::class.java, nome).build().use { primeiro ->
                primeiro.tarefaDao().inserir(Tarefa(titulo = "Persistente", prioridade = 3))
            }
            Room.databaseBuilder(context, TarefasDatabase::class.java, nome).build().use { segundo ->
                assertEquals("Persistente", segundo.tarefaDao().observarTodas().first().single().titulo)
            }
        } finally {
            context.deleteDatabase(nome)
        }
    }
}
