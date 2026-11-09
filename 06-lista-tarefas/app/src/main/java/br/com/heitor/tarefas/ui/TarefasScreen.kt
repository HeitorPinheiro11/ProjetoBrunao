package br.com.heitor.tarefas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.heitor.tarefas.data.Tarefa
import br.com.heitor.tarefas.viewmodel.Filtro
import br.com.heitor.tarefas.viewmodel.TarefaRules
import br.com.heitor.tarefas.viewmodel.TarefasViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefasScreen(vm: TarefasViewModel) {
    val estado by vm.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var editando by remember { mutableStateOf<Tarefa?>(null) }
    var formularioAberto by remember { mutableStateOf(false) }
    var paraExcluir by remember { mutableStateOf<Tarefa?>(null) }

    LaunchedEffect(vm) { vm.eventos.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Minhas tarefas", fontWeight = FontWeight.Bold) }) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { editando = null; formularioAberto = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Text("Organize seu dia", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("${estado.total - estado.concluidas} pendentes • ${estado.concluidas} concluídas",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Filtro.entries.forEach { filtro ->
                            FilterChip(
                                selected = estado.filtro == filtro,
                                onClick = { vm.definirFiltro(filtro) },
                                label = { Text(filtro.rotulo) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Pendentes primeiro • prioridade alta no topo",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
            if (estado.carregando) {
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() } }
            } else if (estado.tarefas.isEmpty()) {
                item { Vazio(estado.total > 0) }
            } else {
                items(estado.tarefas, key = { it.id }) { tarefa ->
                    TarefaCard(
                        tarefa = tarefa,
                        onToggle = { vm.alternarConclusao(tarefa) },
                        onEditar = { editando = tarefa; formularioAberto = true },
                        onExcluir = { paraExcluir = tarefa }
                    )
                }
            }
        }
    }

    if (formularioAberto) {
        TarefaDialog(
            original = editando,
            onDismiss = { formularioAberto = false },
            onSalvar = { titulo, descricao, prioridade, concluida, aoTerminar ->
                vm.salvar(editando, titulo, descricao, prioridade, concluida) { sucesso ->
                    if (sucesso) formularioAberto = false
                    aoTerminar()
                }
            }
        )
    }
    paraExcluir?.let { tarefa ->
        AlertDialog(
            onDismissRequest = { paraExcluir = null },
            title = { Text("Excluir tarefa?") },
            text = { Text("“${tarefa.titulo}” será removida definitivamente.") },
            confirmButton = {
                TextButton(onClick = { vm.excluir(tarefa); paraExcluir = null }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { paraExcluir = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Vazio(filtroSemResultado: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (filtroSemResultado) "Nenhuma tarefa neste filtro" else "Tudo começa com uma tarefa",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(if (filtroSemResultado) "Escolha outro filtro para ver suas tarefas."
                else "Toque no botão + para adicionar sua primeira tarefa.")
        }
    }
}

@Composable
private fun TarefaCard(tarefa: Tarefa, onToggle: () -> Unit, onEditar: () -> Unit, onExcluir: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(start = 8.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top) {
            IconButton(onClick = onToggle) {
                Icon(if (tarefa.concluida) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (tarefa.concluida) "Marcar ${tarefa.titulo} como pendente"
                    else "Marcar ${tarefa.titulo} como concluída",
                    tint = MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f).padding(top = 7.dp)) {
                Text(tarefa.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else null,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (tarefa.descricao.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(tarefa.descricao, style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("● ${prioridadeNome(tarefa.prioridade)}", color = prioridadeCor(tarefa.prioridade),
                        style = MaterialTheme.typography.labelMedium)
                    Text("• ${DateFormat.getDateInstance(DateFormat.SHORT).format(Date(tarefa.dataCriacao))}",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
            IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, contentDescription = "Editar ${tarefa.titulo}") }
            IconButton(onClick = onExcluir) { Icon(Icons.Default.Delete, contentDescription = "Excluir ${tarefa.titulo}") }
        }
    }
}

private fun prioridadeNome(valor: Int) = when (valor) { 3 -> "Alta"; 2 -> "Média"; else -> "Baixa" }
private fun prioridadeCor(valor: Int) = when (valor) {
    3 -> Color(0xFFAF3B38)
    2 -> Color(0xFF926007)
    else -> Color(0xFF286548)
}

@Composable
private fun TarefaDialog(
    original: Tarefa?,
    onDismiss: () -> Unit,
    onSalvar: (String, String, Int, Boolean, () -> Unit) -> Unit
) {
    var titulo by rememberSaveable(original?.id) { mutableStateOf(original?.titulo.orEmpty()) }
    var descricao by rememberSaveable(original?.id) { mutableStateOf(original?.descricao.orEmpty()) }
    var prioridade by rememberSaveable(original?.id) { mutableStateOf(original?.prioridade ?: 2) }
    var concluida by rememberSaveable(original?.id) { mutableStateOf(original?.concluida ?: false) }
    var tentouSalvar by rememberSaveable(original?.id) { mutableStateOf(false) }
    var salvando by rememberSaveable(original?.id) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!salvando) onDismiss() },
        title = { Text(if (original == null) "Nova tarefa" else "Editar tarefa") },
        text = {
            Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo, onValueChange = { titulo = it.take(81) },
                    label = { Text("Título *") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    isError = tentouSalvar && !TarefaRules.tituloValido(titulo),
                    supportingText = { Text("${titulo.trim().length}/80 caracteres") }
                )
                OutlinedTextField(
                    value = descricao, onValueChange = { descricao = it.take(501) },
                    label = { Text("Descrição (opcional)") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 2, maxLines = 4,
                    isError = tentouSalvar && !TarefaRules.descricaoValida(descricao),
                    supportingText = { Text("${descricao.trim().length}/500 caracteres") }
                )
                Text("Prioridade", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..3).forEach { valor ->
                        FilterChip(selected = prioridade == valor, onClick = { prioridade = valor },
                            label = { Text(prioridadeNome(valor)) })
                    }
                }
                if (original != null) {
                    FilterChip(selected = concluida, onClick = { concluida = !concluida },
                        label = { Text(if (concluida) "Concluída" else "Pendente") })
                }
            }
        },
        confirmButton = {
            Button(enabled = !salvando, onClick = {
                tentouSalvar = true
                if (TarefaRules.tituloValido(titulo) && TarefaRules.descricaoValida(descricao)) {
                    salvando = true
                    onSalvar(titulo, descricao, prioridade, concluida) { salvando = false }
                }
            }) { Text(if (salvando) "Salvando…" else "Salvar") }
        },
        dismissButton = { TextButton(enabled = !salvando, onClick = onDismiss) { Text("Cancelar") } }
    )
}
