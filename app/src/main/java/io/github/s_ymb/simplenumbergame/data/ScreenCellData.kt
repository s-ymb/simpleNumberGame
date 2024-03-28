package io.github.s_ymb.simplenumbergame.data

/*
    画面表示用の９×９のセル１つの情報
    CellData クラスにuiに表示する、
        isSelected（選択中のセルは赤枠）
        isSameNum（選択中のセルと同じ数字は強調表示）
    の情報を追加
*/
data class ScreenCellData(
    var num: Int,
    var init: Boolean,                  //初期データか？
    var isSelected : Boolean,           //選択中のセルか？
    var isSameNum : Boolean,            //選択中のセルの数字と同じか？
)
