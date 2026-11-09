package br.com.heitor.tarefas

import br.com.heitor.tarefas.data.Tarefa
import br.com.heitor.tarefas.viewmodel.Filtro
import br.com.heitor.tarefas.viewmodel.TarefaRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TarefaRulesTest {
    @Test fun tituloEmBrancoOuLongoEhRejeitado() {
        assertFalse(TarefaRules.tituloValido("  "))
        assertFalse(TarefaRules.tituloValido("a".repeat(81)))
        assertTrue(TarefaRules.tituloValido("  Comprar pão  "))
        assertTrue(TarefaRules.tituloValido("a".repeat(80)))
    }

    @Test fun descricaoRespeitaLimite() {
        assertTrue(TarefaRules.descricaoValida("a".repeat(500)))
        assertFalse(TarefaRules.descricaoValida("a".repeat(501)))
    }

    @Test fun filtrosNaoAlteramListaOriginal() {
        val tarefas = listOf(
            Tarefa(id = 1, titulo = "Estudar", concluida = false),
            Tarefa(id = 2, titulo = "Entregar", concluida = true)
        )
        assertEquals(tarefas, TarefaRules.filtrar(tarefas, Filtro.TODAS))
        assertEquals(listOf(1L), TarefaRules.filtrar(tarefas, Filtro.PENDENTES).map { it.id })
        assertEquals(listOf(2L), TarefaRules.filtrar(tarefas, Filtro.CONCLUIDAS).map { it.id })
        assertEquals(2, tarefas.size)
    }
}
