package io.github.s_ymb.simplenumbergame.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SatisfiedGridTbl")
data class SatisfiedGridTbl(
    @PrimaryKey(autoGenerate = false)
    val gridData: String = "",
    @ColumnInfo(name = "create_dt")
    // TODO 日付データの扱いを日付型にする
    val createDt: String = "",
    @ColumnInfo(name = "create_user")
    val createUser: String = "",
)
/*
        StafisfiedGridTbl → SatisfiedGrid
*/
fun SatisfiedGridTbl.toSatisfiedGrid() :SatisfiedGrid {
    val tmpData: Array<IntArray> =
        Array(NumbergameData.NUM_OF_ROW) { IntArray(NumbergameData.NUM_OF_COL){0} }
    val tmpDataStr = this.gridData
    if (tmpDataStr != "") {
        for (rowId in 0 until NumbergameData.NUM_OF_ROW) {
            for (colId in 0 until NumbergameData.NUM_OF_COL) {
                val pos = rowId * NumbergameData.NUM_OF_COL + colId
                tmpData[rowId][colId] = tmpDataStr.substring(pos, pos + 1).toInt()
            }
        }
    }
    return SatisfiedGrid(createDt = this.createDt, createUser = this.createUser, data = tmpData)
}

/*
        SatisfiedGrid　→　StafisfiedGridTbl
*/
/* warning になるので消しておく
fun SatisfiedGrid.toSatisfiedGridTbl() :SatisfiedGridTbl{
    var gridData = ""
    for (rowId in 0 until NumbergameData.NUM_OF_ROW) {
        for (colId in 0 until NumbergameData.NUM_OF_COL) {
            gridData += this.data[rowId][colId].toString()
        }
    }
    return SatisfiedGridTbl(gridData = gridData, createDt= this.createDt, createUser = this.createUser)
}
*/

