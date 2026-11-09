package br.com.heitor.tarefas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.heitor.tarefas.data.Tarefa
import br.com.heitor.tarefas.data.TarefasRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Filtro(val rotulo: String) {
    TODAS("Todas"), PENDENTES("Pendentes"), CONCLUIDAS("Concluídas")
}

data class TarefasUiState(
    val tarefas: List<Tarefa> = emptyList(),
    val total: Int = 0,
    val concluidas: Int = 0,
    val filtro: Filtro = Filtro.TODAS,
    val carregando: Boolean = true
)

object TarefaRules {
    fun tituloValido(titulo: String) = titulo.trim().isNotEmpty() && titulo.trim().length <= 80
    fun descricaoValida(descricao: String) = descricao.trim().length <= 500

    fun filtrar(tarefas: List<Tarefa>, filtro: Filtro): List<Tarefa> = tarefas.filter {
        when (filtro) {
            Filtro.TODAS -> true
            Filtro.PENDENTES -> !it.concluida
            Filtro.CONCLUIDAS -> it.concluida
        }
    }
}

class TarefasViewModel(private val repository: TarefasRepository) : ViewModel() {
    private val filtro = MutableStateFlow(Filtro.TODAS)
    private val mensagens = Channel<String>(Channel.BUFFERED)
    val eventos = mensagens.receiveAsFlow()

    val uiState = combine(repository.tarefas.catch {
        mensagens.send("Não foi possível carregar as tarefas.")
        emit(emptyList())
    }, filtro) { tarefas, filtroAtual ->
        TarefasUiState(
            tarefas = TarefaRules.filtrar(tarefas, filtroAtual),
            total = tarefas.size,
            concluidas = tarefas.count { it.concluida },
            filtro = filtroAtual,
            carregando = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TarefasUiState())

    fun definirFiltro(novo: Filtro) { filtro.value = novo }

    fun salvar(original: Tarefa?, titulo: String, descricao: String, prioridade: Int, concluida: Boolean, aoSalvar: (Boolean) -> Unit) {
        if (!TarefaRules.tituloValido(titulo)) {
            viewModelScope.launch { mensagens.send("Informe um título de até 80 caracteres.") }
            return
        }
        if (!TarefaRules.descricaoValida(descricao) || prioridade !in 1..3) {
            viewModelScope.launch { mensagens.send("Confira a descrição e a prioridade.") }
            return
        }
        viewModelScope.launch {
            try {
                repository.salvar(
                    (original ?: Tarefa(titulo = titulo.trim())).copy(
                        titulo = titulo.trim(), descricao = descricao.trim(),
                        prioridade = prioridade, concluida = concluida
                    )
                )
                aoSalvar(true)
                mensagens.send(if (original == null) "Tarefa criada." else "Tarefa atualizada.")
            } catch (erro: Exception) {
                aoSalvar(false)
                mensagens.send("Não foi possível salvar. Tente novamente.")
            }
        }
    }

    fun alternarConclusao(tarefa: Tarefa) = executar("Não foi possível atualizar a tarefa.") {
        repository.salvar(tarefa.copy(concluida = !tarefa.concluida))
    }

    fun excluir(tarefa: Tarefa) = executar("Não foi possível excluir a tarefa.") {
        repository.excluir(tarefa)
        mensagens.send("Tarefa excluída.")
    }

    private fun executar(mensagemErro: String, acao: suspend () -> Unit) {
        viewModelScope.launch {
            try { acao() } catch (erro: Exception) { mensagens.send(mensagemErro) }
        }
    }

    companion object {
        fun fabrica(repository: TarefasRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    TarefasViewModel(repository) as T
            }
    }
}
