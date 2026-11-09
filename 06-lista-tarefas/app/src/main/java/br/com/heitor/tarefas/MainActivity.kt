package br.com.heitor.tarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.heitor.tarefas.data.TarefasDatabase
import br.com.heitor.tarefas.data.TarefasRepository
import br.com.heitor.tarefas.ui.TarefasScreen
import br.com.heitor.tarefas.ui.TarefasTheme
import br.com.heitor.tarefas.viewmodel.TarefasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = TarefasRepository(TarefasDatabase.obter(applicationContext).tarefaDao())
        setContent {
            TarefasTheme {
                val vm: TarefasViewModel = viewModel(factory = TarefasViewModel.fabrica(repository))
                TarefasScreen(vm)
            }
        }
    }
}
