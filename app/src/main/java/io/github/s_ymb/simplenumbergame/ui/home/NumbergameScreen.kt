package io.github.s_ymb.simplenumbergame.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.s_ymb.simplenumbergame.R
import io.github.s_ymb.simplenumbergame.data.NumbergameData
import io.github.s_ymb.simplenumbergame.data.ScreenBtnData
import io.github.s_ymb.simplenumbergame.data.ScreenCellData

/**
 * 　数独のメイン画面
 */
//@OptIn(ExperimentalMaterial3Api::class)
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
        // グリッド表示
        // 通常時は設定された数字を９×９のテキストボックスで表示する
        NumberGridLayout(
            onCellClicked = { rowId, colId -> gameViewModel.onCellClicked(rowId, colId) },
            currentData = gameUiState.currentData
        )


        // 数字ボタン表示
        NumBtnLayout(
            onNumBtnClicked = { num: Int -> gameViewModel.onNumberBtnClicked(num) },
            currentBtn = gameUiState.currentBtn,
        )
        Spacer(
            modifier = Modifier
                .size(8.dp)
            //.background(color=Color.Red)
        )

        // エラーメッセージ用のToast を表示
        if(toastUiState.showToast) {
            val context = LocalContext.current
            val toast = Toast.makeText(context, toastUiState.toastMsg, Toast.LENGTH_LONG)
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
            verticalAlignment = Alignment.Top,
        ) {
            for ((colIdx: Int, cell: ScreenCellData) in rowData.withIndex()) {
                var borderWidth: Int
                borderWidth = 2
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
                Text(
                    text = numStr,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = fWeight,
                    modifier = modifier
                        .width(38.dp)
                        .padding(0.dp)
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
                            .size(4.dp)
                    )
                }
            }
        }
        if (rowIdx % 3 == 2) {
            //平方毎にスペースを開ける
            Spacer(
                modifier = modifier
                    .size(4.dp)
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
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
    ) {
        //ボタンの編集可否を設定
        for (btnNum in 1..5) {
            // 選択中のセルの設定可否を初期設定
            var btnEnabled = true
            // 数字が９個設定してある数字ボタンは使用不可
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
            }

            // ボタン押下エラー時の処理

            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
            ) {
                Text(text = btnNum.toString())
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 9.sp,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
        //.background(color= Color.Yellow)
    ) {
        // ６～９のボタン　と　削除ボタン
        for (btnNum in 6..9) {
            // 数字が９個設定してある数字ボタンは使用不可
            var btnEnabled = true
            if (currentBtn[btnNum].cnt == NumbergameData.MAX_NUM_CNT) {
                btnEnabled = false
            }
            Button(
                onClick = { onNumBtnClicked(btnNum) },
                enabled = btnEnabled,
            ) {
                Text(text = btnNum.toString())
                Text(
                    text = currentBtn[btnNum].cnt.toString(),
                    fontSize = 9.sp,
                    textAlign = TextAlign.End,
                )
            }
        }
        //削除ボタン
        Button(
            onClick = { onNumBtnClicked(NumbergameData.NUM_NOT_SET) },
        ) {
            Text(text = stringResource(R.string.btn_num_delete))
        }
    }
}

/*
       機能ボタン（新規・クリア）を表示
 */
@Composable
fun FunBtnLayout(
    onNewGameBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            //新規ボタン
            Button(
                onClick = { onNewGameBtnClicked() }
            ) {
                Text(text = stringResource(R.string.btn_new))
            }
            //終了ボタン
            val activity = (LocalContext.current as Activity)
            Button(
                onClick = { activity.finish() }
            ) {
                Text(text = stringResource(R.string.btn_exit))
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
            fontSize = 12.sp,
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
        title = { Text(text = stringResource(R.string.congratulations)) },
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.btn_exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGameBtnClicked) {
                Text(text = stringResource(R.string.btn_new))
            }
        }
    )

}

