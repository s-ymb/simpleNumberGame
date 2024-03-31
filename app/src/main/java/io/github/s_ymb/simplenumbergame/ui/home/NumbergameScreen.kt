package io.github.s_ymb.simplenumbergame.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.s_ymb.simplenumbergame.R
import io.github.s_ymb.simplenumbergame.data.DupErr
import io.github.s_ymb.simplenumbergame.data.NumbergameData
import io.github.s_ymb.simplenumbergame.data.ScreenBtnData
import io.github.s_ymb.simplenumbergame.data.ScreenCellData

/**
 * 　数独のメイン画面
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameScreen(
    gameViewModel: NumbergameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val toastUiState by gameViewModel.toastUiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(
            modifier = Modifier
            .background(color=Color.Blue)

            ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // グリッド表示
                NumberGridLayout(
                    onCellClicked = { rowId, colId -> gameViewModel.onCellClicked(rowId, colId) },
                    currentData = gameUiState.currentData
                )
            }
        }
        Spacer(
            modifier = Modifier
                .size(8.dp)
        )
        // 数字ボタン表示
        NumBtnLayout(
            onNumBtnClicked = { num: Int -> gameViewModel.onNumberBtnClicked(num) },
            currentBtn = gameUiState.currentBtn,
        )
        Spacer(
            modifier = Modifier
                .size(8.dp)
        )

        // エラーメッセージ用のToast を表示(この画面では重複エラーのIDが設定される）
        if(toastUiState.showToast) {
            val toastMsg = when (toastUiState.toastMsgId){
                                    DupErr.ROW_DUP.ordinal -> stringResource(R.string.err_btn_row_dup)
                                    DupErr.COL_DUP.ordinal -> stringResource(R.string.err_btn_col_dup)
                                    DupErr.SQ_DUP.ordinal -> stringResource(R.string.err_btn_sq_dup)
                                    DupErr.FIX_DUP.ordinal -> stringResource(R.string.err_btn_fix_cell_selected)
                                    DupErr.NOT_SELECTED.ordinal -> stringResource(R.string.err_btn_cell_not_selected)
                                    else -> "想定外のエラー"
                                }
            val context = LocalContext.current
            val toast = Toast.makeText(context, toastMsg, Toast.LENGTH_LONG)
            // TODO 設定しても下端に出るので置いておく
            // toast.setGravity(Gravity.TOP, 0, 0);
            toast.show()
            // toast 表示済に更新
            gameViewModel.toastShown()
        }

        // スライダーを表示
        SliderLayout(
            defaultPos = gameUiState.blankCellCnt.toFloat(),
            onValueChangeFinished = { num: Int -> gameViewModel.setBlankCellCnt(num) },
        )

        // 機能ボタン表示
        FunBtnLayout(
            onNewGameBtnClicked = { gameViewModel.newGame() },
        )

        // 終了確認ダイアログを表示
        if(gameUiState.isGameOver) {
            FinalDialog(
                onNewGameBtnClicked = { gameViewModel.newGame() },
            )
        }
    }
}
/*
        ９×９の２次元グリッドを描画
 */
@Composable
fun NumberGridLayout(
    onCellClicked: (Int, Int) -> Unit,
    currentData: Array<Array<ScreenCellData>>,
    modifier: Modifier = Modifier
) {
    for ((rowIdx: Int, rowData: Array<ScreenCellData>) in currentData.withIndex()) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            for ((colIdx: Int, cell: ScreenCellData) in rowData.withIndex()) {
                var borderWidth: Int
                borderWidth = 1
                var borderColor: Color = colorResource(R.color.cell_border_color_not_selected)
                var textColor: Color= Color.Black
                var fWeight: FontWeight = FontWeight.Light
                if (cell.isSelected) {
                    // 選択済みのセルは表示枠を変更
                    borderWidth = 4
                    borderColor = colorResource(R.color.cell_border_color_selected)
                }

                if(cell.isSameNum){
                    // 選択済みのセルと同じ数字の場合、
                    // 文字を太字に設定
                    fWeight = FontWeight.ExtraBold
                    // テキストの色を設定
                    textColor = colorResource(R.color.cell_text_color_same_num)
//                    textColor = Color.Red
                }

                var bgColor: Color = colorResource(R.color.cell_bg_color_default)
                if (cell.init) {
                    //初期設定されたセルの場合は背景色をグレーに
                    bgColor = colorResource(R.color.cell_bg_color_init)
                }
                var numStr: String
                numStr = ""
                if (cell.num != NumbergameData.NUM_NOT_SET) {
                    numStr = cell.num.toString()
                }
                val contentDesc = "${rowIdx}行${colIdx}列"
                Text(
                    text = numStr,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = fWeight,
                    modifier = modifier
//                        .padding(0.dp,)
                        .width(39.dp)
                        .height(43.dp)
//                        .wrapContentHeight(Alignment.CenterVertically)//.background(bgColor)
                        .semantics{contentDescription = contentDesc}
                        .border(
                            border = BorderStroke(
                                width = borderWidth.dp,
                                color = borderColor
                            ),
                            shape = RectangleShape,
                        )
                        .background(
                            color = bgColor,
                        )
                        .clickable {
                            // クリックされたテキスト
                            onCellClicked(rowIdx, colIdx)
                        },

                )
                if (colIdx % 3 == 2 && colIdx != 8) {
                    //平方毎にスペースを開ける
                    Spacer(
                        modifier = modifier
                            .size(3.dp)
                    )
                }
            }
        }
        if (rowIdx % 3 == 2 && rowIdx != 8) {
            //平方毎にスペースを開ける
            Spacer(
                modifier = modifier
                    .size(3.dp)
            )
        }
    }
}

/*
        数字入力ボタンを表示
 */
@Composable
fun NumBtnLayout(
    onNumBtnClicked: (Int) -> Unit,
    currentBtn: Array<ScreenBtnData>,
    modifier: Modifier = Modifier,
) {
    val numBtnDescPost = stringResource(R.string.desc_btn_num)
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
    ) {
        //ボタンの編集可否を設定
        for (btnNum in 1..5) {
            // 選択中のセルの設定可否を初期設定
            var btnEnabled = true
            var btnTextColor = Color.White
            // 数字が９個設定してある数字ボタンは使用不可
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
                btnTextColor = Color.Black
            }
            val numBtnDesc = btnNum.toString() + numBtnDescPost
            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
                modifier = Modifier.semantics{contentDescription = numBtnDesc},
            ) {
                Text(
                    text = btnNum.toString(),
                    fontSize = 20.sp,
                    color = btnTextColor,
                    )
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = btnTextColor,
                )
            }
        }
    }
    Spacer(
        modifier = Modifier
            .size(4.dp)
    )
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
        //.background(color= Color.Yellow)
    ) {
        // ６～９のボタン　と　削除ボタン
        for (btnNum in 6..9) {
            // 数字が９個設定してある数字ボタンは使用不可
            var btnEnabled = true
            var btnTextColor = Color.White
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
                btnTextColor = Color.Black
            }
            val numBtnDesc = btnNum.toString() + numBtnDescPost
            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
                modifier = Modifier.semantics{contentDescription = numBtnDesc},
            ) {
                Text(
                    text = btnNum.toString(),
                    fontSize = 20.sp,
                    color = btnTextColor,
                )
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = btnTextColor,
                )
            }
        }
        //削除ボタン
        val delBtnDesc = stringResource(R.string.desc_btn_del)
        Button(
            onClick = { onNumBtnClicked(NumbergameData.NUM_NOT_SET) },
            modifier = Modifier.semantics{contentDescription = delBtnDesc},
            contentPadding = PaddingValues(
                start = 6.dp,
                top = 4.dp,
                end = 6.dp,
                bottom = 4.dp,),
        ) {
            Text(
                text = stringResource(R.string.btn_num_delete),
                fontSize = 24.sp,
            )
        }
    }
}

/*
       機能ボタン（新規・終了）を表示
 */
@Composable
fun FunBtnLayout(
    onNewGameBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val newBtnDesc = stringResource(R.string.desc_btn_new)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //新規ボタン
            Button(
                modifier = Modifier.semantics{contentDescription = newBtnDesc},
                onClick = { onNewGameBtnClicked() },
                contentPadding = PaddingValues(
                                    start = 24.dp,
                                    top = 8.dp,
                                    end = 24.dp,
                                    bottom = 8.dp,),
            ) {
                Text(
                    text = stringResource(R.string.btn_new),
                    fontSize = 24.sp,
                )
            }
            //終了ボタン
            val endBtnDesc = stringResource(R.string.desc_btn_end)
            val activity = (LocalContext.current as Activity)
            Button(
                modifier = Modifier.semantics{contentDescription = endBtnDesc},
                onClick = { activity.finish() },
                contentPadding = PaddingValues(
                                    start = 24.dp,
                                    top = 8.dp,
                                    end = 24.dp,
                                    bottom = 8.dp,),
            ) {
                Text(
                    text = stringResource(R.string.btn_exit),
                    fontSize = 24.sp,
                )
            }
        }
    }
}

/*
    固定セルの個数を選択するスライダー表示
 */
@Composable
private fun SliderLayout(
    defaultPos: Float,
    onValueChangeFinished: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    var sliderPosition by remember { mutableFloatStateOf(defaultPos) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
            Slider(
                value = sliderPosition,
                // TODO 固定セルの初期値と設定できる範囲の検討が必要
                valueRange = 30f..60f,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished ={onValueChangeFinished(sliderPosition.toInt())},
                //steps = 3,

            )
        val sliderTxt: String = stringResource(R.string.slider_title1) + sliderPosition.toInt().toString() + stringResource(R.string.slider_title2)
        Text(
            text = sliderTxt,
            fontSize = 16.sp,
        )
    }

}



/*
 * 終了確認ダイアログ(ゲーム完了時)
 */
@Composable
private fun FinalDialog(
    onNewGameBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = (LocalContext.current as Activity)
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = {
                Text(
                    text = stringResource(R.string.congratulations),
                    fontSize = 24.sp,
                )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(
                    text = stringResource(R.string.btn_exit),
                    fontSize = 24.sp,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGameBtnClicked) {
                Text(
                    text = stringResource(R.string.btn_new),
                    fontSize = 24.sp,
                )
            }
        }
    )
}

