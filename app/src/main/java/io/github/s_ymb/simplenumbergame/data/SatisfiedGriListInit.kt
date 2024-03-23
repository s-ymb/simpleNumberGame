package io.github.s_ymb.simplenumbergame.data

class SatisfiedGriListInit
{
    companion object {

        /*
                SatisfiedGridArrayInit に登録されている初期データより初期データリストを作成する
         */
        fun getInitialListData(): MutableList<SatisfiedGridData> {
            val initData: MutableList<SatisfiedGridData> = mutableListOf()
            SatisfiedGridArrayInit.data.forEach {
//                val formatter = SimpleDateFormat("yyyy/mm/dd")

                //TODO 初期登録のパラメータの保存場所の管理方法 初期登録時データの日付は固定
                val dateString = "2024/02/01"       //初期データは2024/02/01 に作ったことにしておく
//                val date = formatter.parse(dateString)
                val satisfiedGrid = SatisfiedGrid(
                    createDt = dateString,
                    createUser = "初期登録の正解",
                    data = it
                )
                initData.add(
                    SatisfiedGridData(
                        satisfiedGrid
                    )
                )
            }
            return initData
        }
    }
}