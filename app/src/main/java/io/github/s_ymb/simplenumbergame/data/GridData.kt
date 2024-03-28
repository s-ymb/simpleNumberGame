package io.github.s_ymb.simplenumbergame.data

import io.github.s_ymb.simplenumbergame.data.DupErr.NO_DUP
import kotlin.random.Random

/*
        ９×９のセルを表現するクラス（操作も含む）
        基底クラス(NumbergameDataは課題で設定された初期データという概念はない)
*/

class GridData(
        val data: Array<Array<CellData>> = Array(NUM_OF_ROW) { Array(NUM_OF_COL) { CellData(0, false) } }
        ) : NumbergameData() {
    /*
        データの指定位置に指定データが設定可能か判定して可能な場合、値を設定する
        同じ数字が範囲内に重複する場合、範囲に応じたエラーを返す。
     */
    fun setData(row: Int, col: Int, newNum: Int, isInit: Boolean): DupErr {
        if(data[row][col].init){
            return DupErr.FIX_DUP      //課題として表示された列が指定されたら即エラーリターン
        }

        // いままでに設定されている値を単なる数値の２次元配列にコピー
        // 基底クラス(NumbergameData は
        val tmp: Array<Array<Int>> = Array(NUM_OF_ROW) { Array(NUM_OF_COL) { 0 } }
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx, cell) in colArray.withIndex()) {
                tmp[rowIdx][colIdx] = cell.num
            }
        }
        // コピーした配列がNumbergameのルールに沿っているかチェックする
        // チェックロジックは継承元のNumbergameDataにて実装
        val dataCheckResult: DupErr = checkData(tmp, row, col, newNum)
        if (NO_DUP == dataCheckResult) {
            //チェックOKの場合、データに反映する。
            data[row][col].num = newNum
            data[row][col].init = isInit
        }
        return dataCheckResult
    }


    /*
        新規ゲームデータの作成
        入力の課題配列(satisfiedArray)より
        指定個数の数字(fixCellSelected)を
        メンバ変数のdataに課題として設定する
    */
    fun newGame(satisfiedArray: Array<IntArray>, blankCellSelected: Int) {
        // データ全消去
        clearAll(true)

        // ランダムに指定個数データを設定
        val fixCellSelected = NUM_OF_COL * NUM_OF_ROW - blankCellSelected
        var fixCelCnt = 0
        while (fixCelCnt <= fixCellSelected) {
            val seedRow = Random.nextInt(NUM_OF_ROW)
            val seedCol = Random.nextInt(NUM_OF_COL)
            // 初期データが設定されているデータ以外の場合（偶然同じセルがランダムに選択されないように）
            if (!data[seedRow][seedCol].init) {
                //引数の課題配列をメンバ変数のdataに設定する
                data[seedRow][seedCol].num = satisfiedArray[seedRow][seedCol]
                data[seedRow][seedCol].init = true
                fixCelCnt++
            }
        }
    }

    /*
        全てのデータを初期化
            引数：withFixCell  true：全てのデータ、false:課題で設定されたデータ以外
    */
    fun clearAll (withFixCell: Boolean) {
        for ((rowIdx, colArray) in data.withIndex()) {
            for ((colIdx) in colArray.withIndex()) {
                // 全データクリアの場合もしくは初期列以外の場合
                if((withFixCell) || !data[rowIdx][colIdx].init) {
                    // データを初期化
                    data[rowIdx][colIdx].num = NUM_NOT_SET
                    data[rowIdx][colIdx].init = false
                }
            }
        }
    }
}

