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
                val satisfiedGrid = SatisfiedGrid(
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