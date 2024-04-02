package io.github.s_ymb.simplenumbergame.ui.home
import androidx.lifecycle.ViewModel
import io.github.s_ymb.simplenumbergame.data.DupErr
import io.github.s_ymb.simplenumbergame.data.GridData
import io.github.s_ymb.simplenumbergame.data.NumbergameData
import io.github.s_ymb.simplenumbergame.data.NumbergameData.Companion.IMPOSSIBLE_IDX
import io.github.s_ymb.simplenumbergame.data.NumbergameData.Companion.NUM_NOT_SET
import io.github.s_ymb.simplenumbergame.data.SatisfiedGridArrayInit
import io.github.s_ymb.simplenumbergame.data.SatisfiedGridData
import io.github.s_ymb.simplenumbergame.data.ScreenBtnData
import io.github.s_ymb.simplenumbergame.data.ScreenCellData
import io.github.s_ymb.simplenumbergame.ui.ToastUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NumbergameViewModel : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(NumbergameUiState())
    val uiState: StateFlow<NumbergameUiState> = _uiState.asStateFlow()

    // Toast UI state（toast用)
    private val _toastUiState = MutableStateFlow(ToastUiState())
    val toastUiState = _toastUiState.asStateFlow()

    private val gridData = GridData()

    private var blankCellCnt = 30                                   //空白のセルの個数
    private var selectedRow = IMPOSSIBLE_IDX                        //選択中セルの行番号
    private var selectedCol = IMPOSSIBLE_IDX                        //選択中セルの列番号

    /*
            メイン画面の初期化処理。保存詳細画面から再開機能で遷移した場合は、保存詳細画面の内容で初期化
     */
    init {
        // セルを空に設定
        clearGame()
    }

    /*
        データクラスの内容でviewModelルの情報を基にUIステートに値を設定する
    */
    private fun setGridDataToUiState() {
        //ui state にセル情報設定用の変数
        //表示するセルの中身をデータより設定
        val tmpData: Array<Array<ScreenCellData>> = Array(NumbergameData.NUM_OF_ROW)
                                                    {
                                                        Array(NumbergameData.NUM_OF_COL)
                                                        {
                                                            ScreenCellData(
                                                                num = NUM_NOT_SET,
                                                                init = false,
                                                                isSelected = false,
                                                                isSameNum = false
                                                            )
                                                        }
                                                    }
        //ui stateにボタン情報設定用の変数（番号毎にセルに出現している数）
        val tmpBtn: Array<ScreenBtnData> = Array(NumbergameData.KIND_OF_DATA + 1) {ScreenBtnData(0)}

        // ９×９の数字の配列をui state に設定する
        // 選択中のセルと同じ数字の表示を変える為に選択中のセルの数字を保存
        var selectedNum = NUM_NOT_SET                //初期値は未設定の数字(0)
        if(selectedRow != IMPOSSIBLE_IDX && selectedCol != IMPOSSIBLE_IDX){
            // 選択中のセルと同じ数字の表示を変える為に選択中のセルの数字を保存
            selectedNum = gridData.data[selectedRow][selectedCol].num
        }

        //
        //      ・セルを表示する情報を設定(番号、初期値、選択中、同じ番号が選択中等
        //      ・ボタンに表示する表示済みの数字毎の数を集計して設定
        //      ・未設定のセルの数を集計（ゲーム終了判定用）
        for(rowIdx in 0 until NumbergameData.NUM_OF_ROW){
            for(colIdx in 0 until NumbergameData.NUM_OF_COL){
                //セルの設定
                tmpData[rowIdx][colIdx].num = gridData.data[rowIdx][colIdx].num
                tmpData[rowIdx][colIdx].init = gridData.data[rowIdx][colIdx].init
                //数字ボタンの設定
                tmpBtn[gridData.data[rowIdx][colIdx].num].cnt++         //ボタンに表示する、数字毎の数をインクリメント
                // 選択中のセルの場合、セルに選択中のフラグをONにする
                tmpData[rowIdx][colIdx].isSelected =  (rowIdx == selectedRow && colIdx == selectedCol)

                // 選択中のセルと同じ数字の場合（空白以外）、UIで強調表示するためフラグを設定
                tmpData[rowIdx][colIdx].isSameNum = false
                if(selectedNum != NUM_NOT_SET) {
                    if (selectedNum == gridData.data[rowIdx][colIdx].num) {
                        // 選択中のセルと同じ番号のセルは強調表示する為にフラグを設定
                        tmpData[rowIdx][colIdx].isSameNum = true
                    }
                }
            }
        }
        //未設定セルの数が０個の場合、ゲーム終了とする
        var isGameOver = true
        gridData.data.forEach {
            it.forEach{cell ->
                isGameOver = isGameOver && (cell.num != NUM_NOT_SET)
            }
        }

        // 空白のセルが存在しない場合
        _uiState.value = NumbergameUiState(
            currentData = tmpData,
            currentBtn = tmpBtn,
            blankCellCnt = blankCellCnt,
            isGameOver = isGameOver,
        )
    }

    /*
        画面全消去
     */
    private fun clearGame(){
        // 全てのセルを消す
        gridData.clearAll(true)
        //選択中セルの初期化
        selectedCol = IMPOSSIBLE_IDX
        selectedRow = IMPOSSIBLE_IDX
        //描画データ再作成
        setGridDataToUiState()
    }

    /*
        新規ゲーム
     */
    fun newGame(){
        //正解リストより初期値を設定する
        // 正解配列をランダムに選択
        val satisfiedIdx: Int= (0 until SatisfiedGridArrayInit.data.size).random()

        // 正解配列をランダムに並べ変える、９×９のセルに初期値設定する
        val satisfiedGridData = SatisfiedGridData(SatisfiedGridArrayInit.data[satisfiedIdx])
        gridData.newGame(satisfiedGridData.getRandom(), blankCellCnt)

        //選択中セルの初期化
        selectedCol = IMPOSSIBLE_IDX
        selectedRow = IMPOSSIBLE_IDX

        //描画データ再作成
        setGridDataToUiState()

    }

    /*
        ９×９のセルがクリックされた時、セルを選択状態にする
     */
    fun onCellClicked(rowId: Int, colId: Int){
        selectedRow = rowId
        selectedCol = colId
        //描画データ再作成
        setGridDataToUiState()
    }

    /*
        番号のボタンが押された場合、選択中のセルに番号を設定する
     */
    fun onNumberBtnClicked(number: Int){
        var ret = DupErr.NOT_SELECTED       //とりあえず未選択エラー状態
        if((selectedRow != IMPOSSIBLE_IDX) && (selectedCol != IMPOSSIBLE_IDX)) {
            // 画面で数字を入力する場所が選択されていた場合、データを設定
            ret = gridData.setData(selectedRow, selectedCol, number, false)
       }
        // 数値が未選択 or 重複（数独のルールを逸脱）した場合toast で表示
        if(DupErr.NO_DUP != ret) {
            _toastUiState.value = ToastUiState(
                showToast = true,
                toastMsgId = ret.ordinal,
            )
        }
        //描画データ再作成
        setGridDataToUiState()

    }

 /*
        Toast 表示後に   _uiStateToast を初期値に更新
 */
    fun toastShown(){
        _toastUiState.value = ToastUiState()
    }

    /*
        スライダーで新規作成時の固定セルの個数が変更されたときメンバー変数に反映する
    */
    fun setBlankCellCnt(blankCnt: Int){
        blankCellCnt = blankCnt
    }

}