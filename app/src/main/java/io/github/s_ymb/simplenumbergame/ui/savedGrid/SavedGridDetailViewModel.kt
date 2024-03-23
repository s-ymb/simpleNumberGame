package io.github.s_ymb.simplenumbergame.ui.savedGrid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.s_ymb.simplenumbergame.data.AppContainer
import io.github.s_ymb.simplenumbergame.data.SavedCellTbl
import io.github.s_ymb.simplenumbergame.data.SavedTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedGridDetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val appContainer: AppContainer,
) : ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle[SavedDetailDestination.savedIdArg])
    private val savedRepo = appContainer.savedTblRepository
    val savedTblUiState: StateFlow<SavedTblUiState> = savedRepo.getGrid(id)
            .filterNotNull()
            .map {
                SavedTblUiState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SavedTblUiState()
            )
    private val savedCellRepo = appContainer.savedCellTblRepository
    val savedCellTblUiState: StateFlow<SavedCellTblUiState> = savedCellRepo.getGrids(id)
        .filterNotNull()
        .map {
            SavedCellTblUiState(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SavedCellTblUiState()
        )
    /**
     * Deletes the item from the savedTblRepository data source.
     */
    fun deleteItem() {
        viewModelScope.launch(Dispatchers.IO) {
            appContainer.savedTblRepository.delete(id)
        }
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}



data class SavedCellTblUiState(
    val savedCellTblList: List<SavedCellTbl> = listOf()
)

data class SavedTblUiState(val savedTbl: SavedTbl = SavedTbl())
