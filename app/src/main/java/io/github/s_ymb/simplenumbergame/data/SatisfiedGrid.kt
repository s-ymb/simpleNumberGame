package io.github.s_ymb.simplenumbergame.data

data class SatisfiedGrid (
    val data: Array<IntArray> = Array(NumbergameData.NUM_OF_ROW){IntArray(NumbergameData.NUM_OF_COL){NumbergameData.NUM_NOT_SET}}
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SatisfiedGrid

        return data.contentDeepEquals(other.data)
    }

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }
}


