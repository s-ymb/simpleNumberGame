package io.github.s_ymb.simplenumbergame.data

class SatisfiedGridData (satisfiedArray: Array<IntArray> = Array(NUM_OF_ROW){
                                    IntArray(NUM_OF_COL){
                                                                            NUM_NOT_SET
                                                                        }
                                                            }): NumbergameData(){
    private val satisfied = satisfiedArray
    /*
    正解データを正解リストからランダムに選択し、セルの位置の再配置を指定する
*/
    /*
        データ入替の種類
     */
    enum class RotateType{
        LINE_ROTATE,     //行入替
        AREA_ROTATE,     //９×９のエリア入替
    }

    enum class RotateDirection{
        ROW,        // 行入替
        COL         // 列入替
    }

    enum class RotatePattern{
        PATTERN_12,        // １行（列）と２行（列）を入替
        PATTERN_13,        // １行（列）と３行（列）を入替
        PATTERN_23         // ２行（列）と３行（列）を入替
    }

    enum class RotateArea {
        START,      // 行入替の場合、最上段のエリア、列入替の場合、左端のエリア
        MIDDLE,     // 真ん中のエリア
        END         // 行入替の場合、最下段のエリア、列入替の場合、右端のエリア
    }

    fun getRandom() : Array<IntArray>  {
        // 正解リストの１つの正解を選択
        // NumbergemeViewModelに移動
//        val satisfiedIdx: Int=  (0 until dataList.size).random()
//        val satisfiedGrid = dataList[satisfiedIdx]

        // 入れ替えるパターンをランダムに選択
        val rotateType = (RotateType.entries.toTypedArray()).random().ordinal
        val rotateDirection = (RotateDirection.entries.toTypedArray()).random().ordinal
        val rotateArea = (RotateArea.entries.toTypedArray()).random().ordinal
        val rotatePattern = (RotatePattern.entries.toTypedArray()).random().ordinal
        val offset: Array<IntArray> = getOffset(
            type = rotateType,
            direction = rotateDirection,
            pattern = rotatePattern,
            area = rotateArea
        )

        // １～９のランダムな順列を生成
        val seedArray = (1..KIND_OF_DATA).shuffled()

        // 正解リストの値を生成する数字のIndex番号として入替パターンのオフセット分ずらした位置に配置する
        val retArray: Array<IntArray> = Array(NUM_OF_ROW) { IntArray(NUM_OF_COL) { NUM_NOT_SET } }
        for (rowIdx in 0 until NUM_OF_ROW) {
            for (colIdx in 0 until NUM_OF_COL) {
                if (rotateDirection == RotateDirection.ROW.ordinal) {
                    // 行入替パターンの場合、設定元の行番号をオフセット分ずらした値を設定する
                    retArray[rowIdx][colIdx] =
                        seedArray[satisfied[rowIdx + offset[rowIdx][colIdx]][colIdx] - 1]        // 配列は０オリジンなので１ずらす
                } else {
                    // 列入替パターンの場合、設定元の列番号をオフセット分ずらした値を設定する
                    retArray[rowIdx][colIdx] =
                        seedArray[satisfied[rowIdx][colIdx + offset[rowIdx][colIdx]] - 1]        // 配列は０オリジンなので１ずらす
                }
            }
        }
        return retArray
    }

    /*
            行・列 入替のパターン毎に移動させる距離をオフセット配列に設定していく
            例.1                            例２.
                １行目と２行目を入れ替えたい場合        中央のブロックと右側のブロックを入れ替えたい場合
                 1  1  1  1  1  1  1  1  1          0  0  0  3  3  3 -3 -3 -3
                -1 -1 -1 -1 -1 -1 -1 -1 -1          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3
                 0  0  0  0  0  0  0  0  0          0  0  0  3  3  3 -3 -3 -3

     */
    private fun getOffset(type: Int, direction: Int, pattern: Int,area: Int): Array<IntArray> {
        val retOffset =
            Array(NUM_OF_ROW) { IntArray(NUM_OF_COL) { NUM_NOT_SET } }
        if (type == RotateType.LINE_ROTATE.ordinal) {
            // 行 or 列 入替の場合
            if (direction == RotateDirection.ROW.ordinal) {
                //行入替の場合
                //マスクのパターンを取得
                val maskOffset = getRowMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartRow = SQR_SIZE * area
                for (maskIdx in 0 until SQR_SIZE) {
                    for (colIdx in 0 until NUM_OF_COL) {
                        retOffset[areaStartRow + maskIdx][colIdx] = maskOffset[maskIdx][colIdx]
                    }
                }
            } else if (direction == RotateDirection.COL.ordinal) {
                //列入替の場合
                // マスクのパターンを取得
                val maskOffset = getColMask(pattern = pattern)
                //指定エリアにマスクパターンを反映する
                val areaStartCol = SQR_SIZE * area
                for (maskIdx in 0 until SQR_SIZE) {
                    for (rowIdx in 0 until NUM_OF_ROW) {
                        retOffset[rowIdx][areaStartCol + maskIdx] = maskOffset[rowIdx][maskIdx]
                    }
                }
            }
        } else if (type == RotateType.AREA_ROTATE.ordinal) {
            var swapFrom = 0
            var swapTo = 0
            // エリア入替の場合(マスクパターン毎に、ここでセット）
            when (pattern) {
                RotatePattern.PATTERN_12.ordinal
                -> {
                    // 最初エリアと途中エリアを入れ替える
                    swapFrom = RotateArea.START.ordinal
                    swapTo = RotateArea.MIDDLE.ordinal
                }

                RotatePattern.PATTERN_13.ordinal
                -> {
                    // 最初エリアと最終エリアを入れ替える
                    swapFrom = RotateArea.START.ordinal
                    swapTo = RotateArea.END.ordinal
                }

                RotatePattern.PATTERN_23.ordinal
                -> {
                    // 途中のエリアと最終のエリアを入れ替える
                    swapFrom = RotateArea.MIDDLE.ordinal
                    swapTo = RotateArea.END.ordinal
                }
            }
            // 入替元の先頭位置と移動距離を設定
            val startIndexFrom = SQR_SIZE * swapFrom
            val startIndexTo = SQR_SIZE * swapTo
            val distance = SQR_SIZE * (swapTo - swapFrom)

            if (direction == RotateDirection.ROW.ordinal) {
                // エリアの行入替の場合
                //入替元に設定するマスク
                for (rowIdx in  startIndexFrom  until  startIndexFrom + SQR_SIZE) {
                    for (colIdx in 0 until NUM_OF_COL) {
                        retOffset[rowIdx][colIdx] = distance
                    }
                }
                // 入替先に設定するマスク
                for (rowIdx in startIndexTo until startIndexTo + SQR_SIZE) {
                    for (colIdx in 0 until NUM_OF_COL) {
                        retOffset[rowIdx][colIdx] = -distance
                    }
                }
            } else if (direction == RotateDirection.COL.ordinal) {
            // エリアの列入替の場合
                for (colIdx in startIndexFrom until startIndexFrom + SQR_SIZE) {
                    for (rowIdx in 0 until NUM_OF_ROW) {
                        retOffset[rowIdx][colIdx] = distance
                    }
                }
                for (colIdx in startIndexTo until startIndexTo + SQR_SIZE) {
                    for (rowIdx in 0 until NUM_OF_ROW) {
                        retOffset[rowIdx][colIdx] = -distance
                    }
                }
            }
        }
        return retOffset
    }

    /*
            行入替の場合の移動パターンの配列を作成する
    */
    private fun getRowMask(pattern: Int): Array<IntArray>{
        // １エリア分の行数のマスク
        val retMask = Array(SQR_SIZE){IntArray(NUM_OF_COL){NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １行目と２行目の入替の場合、１行目([0])に＋１を２行目([1])にー１を設定
                for (colIdx in 0 until NUM_OF_COL) {
                    retMask[0][colIdx] = 1
                    retMask[1][colIdx] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １行目と３行目の入替の場合、１行目([0])に＋2を３行目([2])に-2を設定
                for (colIdx in 0 until NUM_OF_COL) {
                    retMask[0][colIdx] = 2
                    retMask[2][colIdx] = -2
                }
            }
            RotatePattern.PATTERN_23.ordinal -> {
                // ２行目と３行目の入替の場合、２行目([1])に＋1を３行目([2])に-1を設定
                for (colIdx in 0 until NUM_OF_COL) {
                    retMask[1][colIdx] = 1
                    retMask[2][colIdx] = -1
                }
            }
        }
        return retMask
    }

    /*
            列入替の場合の移動パターンの配列を作成する
    */
    private fun getColMask(pattern: Int): Array<IntArray> {
        val retMask = Array(NUM_OF_ROW){IntArray(SQR_SIZE){NUM_NOT_SET}}
        when(pattern) {
            RotatePattern.PATTERN_12.ordinal -> {
                // １列目と２列目の入替の場合、１列目([0])に＋１を２列目([1])にー１を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][0] = 1
                    retMask[rowIdx][1] = -1
                }
            }
            RotatePattern.PATTERN_13.ordinal -> {
                // １列目と３列目の入替の場合、１列目([0])に＋2を３行列([2])に-2を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][0] = 2
                    retMask[rowIdx][2] = -2
                }
            }
            RotatePattern.PATTERN_23.ordinal -> {
                // ２列目と３列目の入替の場合、２列目([1])に＋1を３列目([2])に-1を設定
                for (rowIdx in 0 until NUM_OF_ROW) {
                    retMask[rowIdx][1]= 1
                    retMask[rowIdx][2] = -1
                }
            }
        }
        return retMask
    }

}