package br.com.heitor.tarefas.data

class TarefasRepository(private val dao: TarefaDao) {
    val tarefas = dao.observarTodas()

    suspend fun salvar(tarefa: Tarefa) {
        if (tarefa.id == 0L) dao.inserir(tarefa)
        else check(dao.atualizar(tarefa) == 1) { "Tarefa não encontrada." }
    }

    suspend fun excluir(tarefa: Tarefa) {
        check(dao.excluir(tarefa) == 1) { "Tarefa não encontrada." }
    }
}
