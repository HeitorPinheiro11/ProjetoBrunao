# Projeto 06 — Gerenciador de Tarefas

Aplicativo Android em **Kotlin, Jetpack Compose, Material 3 e Room**. As tarefas ficam em um banco SQLite local e continuam disponíveis após fechar e abrir o app. Não usa internet, login nem permissões especiais.

## Abrir e executar

1. Instale o Android Studio com **JDK 17**, Android SDK **API 36** e um emulador ou celular Android **API 23 ou superior**.
2. Em **File > Open**, selecione esta pasta `06-lista-tarefas`, e aguarde o Gradle Sync. A primeira sincronização requer internet para baixar as dependências.
3. Selecione o dispositivo e clique em **Run ▶**. O módulo executável é `app`.
4. Alternativamente, no terminal desta pasta, rode `./gradlew assembleDebug` (Linux/macOS) ou `gradlew.bat assembleDebug` (Windows). O APK gerado ficará em `app/build/outputs/apk/debug/`.

Se a sincronização pedir o SDK, configure o caminho em **File > Project Structure > SDK Location**. Não inclua `local.properties` no Git. Se houver problema de JDK, selecione **Gradle JDK 17** nas configurações do Android Studio.

## Como usar

- Toque em **+** para criar uma tarefa. Título é obrigatório (até 80 caracteres); descrição é opcional (até 500). Escolha prioridade baixa, média ou alta.
- Toque no círculo à esquerda de uma tarefa para concluir ou reabrir.
- Use **Editar** para alterar título, descrição, prioridade ou situação.
- Use **Excluir** e confirme a exclusão no diálogo.
- Selecione **Todas**, **Pendentes** ou **Concluídas** para filtrar. Dentro de cada situação, a maior prioridade vem primeiro; empates mostram a mais recente primeiro.
- O cabeçalho mostra a contagem geral de pendentes e concluídas, independentemente do filtro.

## Organização

| Local | Responsabilidade |
|---|---|
| `data/Tarefa.kt` | Entidade Room com ID, título, descrição, situação, prioridade e data de criação. |
| `data/TarefaDao.kt` | Consultas e operações SQLite; Flow emite atualizações automaticamente. |
| `data/TarefasDatabase.kt` | Instância única do banco local. |
| `data/TarefasRepository.kt` | Acesso aos dados e verificação de atualização/exclusão. |
| `viewmodel/TarefasViewModel.kt` | Validação, filtros, StateFlow, operações assíncronas e mensagens. |
| `ui/TarefasScreen.kt` | Lista Compose, formulário, filtros e confirmação de exclusão. |
| `ui/TarefasTheme.kt` | Esquema de cores Material 3. |

O banco é `tarefas.db`, versão 1. A prioridade é um inteiro: 1 baixa, 2 média, 3 alta. A data de criação é um timestamp em milissegundos e permanece estável ao editar. Não se usa `fallbackToDestructiveMigration`, para evitar apagar tarefas silenciosamente em futuras versões.

## Testes

No terminal: `./gradlew testDebugUnitTest` valida limites de texto e filtros. Com um dispositivo ligado: `./gradlew connectedDebugAndroidTest` executa o teste Room de criação, edição, conclusão, exclusão e ordenação. No Windows, troque `./gradlew` por `gradlew.bat`.

Roteiro manual para demonstrar na apresentação:

1. Abra o app sem dados e confira o estado vazio.
2. Tente salvar título em branco; observe a indicação de erro. Crie uma tarefa alta e outra baixa; confira a ordem.
3. Edite uma das tarefas e marque outra como concluída; confira as três opções de filtro e as contagens.
4. Feche o app completamente e abra novamente; confira a persistência.
5. Exclua uma tarefa, primeiro cancelando e depois confirmando. Confira o resultado.
6. Repita no emulador estreito e com o teclado aberto; o formulário rola verticalmente.

Para as evidências exigidas na ficha, capture telas reais do seu emulador mostrando lista/ordem, formulário, filtro, diálogo de exclusão e dados depois de reiniciar o app. Inclua as imagens na apresentação ou numa pasta `evidencias/`. Nenhuma captura foi fabricada neste projeto.

## Relação com a ficha de avaliação

| Critério | Evidência no projeto |
|---|---|
| Funcionalidades (3,5) | CRUD, conclusão, filtros, ordenação e banco Room persistente. |
| Interface e usabilidade (2,0) | Material 3, lista com cartões, estados vazio/carregando, diálogo rolável, rótulos acessíveis. |
| Organização (2,0) | Dados, repositório, ViewModel/StateFlow e interface separados. |
| Erros e testes (1,5) | Validação, confirmação de exclusão, mensagens e testes unitário/instrumentado. |
| Documentação (1,0) | Instruções de execução, uso, estrutura e roteiro de evidências neste README. |

**Limite de verificação:** a compilação e as capturas reais dependem de Android Studio/SDK e de um emulador ou aparelho. Faça o Gradle Sync e execute os testes acima antes da apresentação.
