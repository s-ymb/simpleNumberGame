package io.github.s_ymb.simplenumbergame.ui.home

import io.github.s_ymb.simplenumbergame.data.NumbergameData
import io.github.s_ymb.simplenumbergame.data.ScreenBtnData
import io.github.s_ymb.simplenumbergame.data.ScreenCellData

data class NumbergameUiState(
    val currentData: Array<Array<ScreenCellData>> = Array(NumbergameData.NUM_OF_ROW)
                        { Array(NumbergameData.NUM_OF_COL)
                            {
                                ScreenCellData(num = NumbergameData.NUM_NOT_SET, init = false, isSelected = false, isSameNum =  false)
                            }
                        },
    val currentBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1){ScreenBtnData(0)},
    val isGameOver: Boolean = false,
    val blankCellCnt: Int = 0,
) {

    override fun hashCode(): Int {
        var result = currentData.contentDeepHashCode()
        result = 31 * result + currentBtn.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumbergameUiState

        if (!currentData.contentDeepEquals(other.currentData)) return false
        return currentBtn.contentEquals(other.currentBtn)
    }
}
