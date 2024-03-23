package io.github.s_ymb.simplenumbergame.data

data class ScreenCellData(
    var num: Int,
    var init: Boolean,                  //初期データか？
    var isSelected : Boolean,           //選択中のセルか？
    var isSameNum : Boolean             //選択中のセルの数字と同じか？
)
