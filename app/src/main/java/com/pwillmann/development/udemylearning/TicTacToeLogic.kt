package com.pwillmann.development.udemylearning

import android.util.Log

/**
 * Created by Patrick on 08.06.2017.
 */

class TicTacToeLogic(val mViewInterface: TicTacToeViewInterface){
    val TAG:String = TicTacToeLogic::class.toString()

    enum class Player {
        EMPTY, X, O
    }

    enum class WinType {
        ROW, COLUMN, DIAGONAL_TL_TO_BR, DIAGONAL_TR_TO_BL, DRAW
    }

    val mBoard: Array<Array<Player>> = Array<Array<Player>>(3) { Array<Player>(3) { Player.EMPTY } }
    var mCurrentPlayer: Player = Player.X
    var mIsFinished: Boolean = false
    val mBot = Bot(Player.O)

    fun resetGame() {
        this.mIsFinished = false
        this.mCurrentPlayer = Player.X

        for (row in 0..mBoard.size - 1) {
            for (column in 0..mBoard[row].size - 1) {
                mBoard[row][column] = Player.EMPTY
                mViewInterface.setField(row, column, Player.EMPTY)
            }
        }
        mViewInterface.isNewGame()
        mViewInterface.isActivePlayerChanged(Player.X)
    }

    fun toggleField(row: Int, column: Int) {
        Log.d(TAG, "toggleField() called")
        if (this.mIsFinished) return

        if( mBoard[row][column] === Player.EMPTY ){
           if(mViewInterface.setField(row, column, mCurrentPlayer)){
               mBoard[row][column] = mCurrentPlayer
               mCurrentPlayer = if (mCurrentPlayer == Player.X) TicTacToeLogic.Player.O else Player.X

               Log.d(TAG, "mBoard: [[$mBoard]], currentPlayer: $mCurrentPlayer")
               this.mIsFinished = isGameFinished(this.mBoard)
               mViewInterface.isActivePlayerChanged(if(this.mIsFinished) Player.EMPTY else mCurrentPlayer)
               if(this.mCurrentPlayer == mBot.mPlayer){
                   val (row, col) = mBot.toggleField(mBoard)
                   toggleField(row, col)
               }
           }
        }
    }

    fun isGameFinished(board: Array<Array<Player>>): Boolean {
       var winner: Winner = checkColumns(board)
        if(winner.player != Player.EMPTY){
            mViewInterface.isFinished(winner)
            return true
        }
        winner = checkRows(board)
        if(winner.player != Player.EMPTY){
            mViewInterface.isFinished(winner)
            return true
        }
        winner = checkDiagonals(board)
        if(winner.player != Player.EMPTY){
            mViewInterface.isFinished(winner)
            return true
        }
        if(isBoardFull(board)){
            mViewInterface.isFinished(Winner(Player.EMPTY, winType = WinType.DRAW, col = -1, row = -1))
            return true
        }
        return false

    }

    fun isBoardFull(board:Array<Array<Player>>): Boolean{
        for (row in 0..mBoard.size - 1) {
            for (column in 0..mBoard[row].size - 1) {
                if(mBoard[row][column] == Player.EMPTY){
                    return false
                }

            }
        }

        return true
    }

    /**
     * Checks on the given board whether a player wins on a row
     */
    fun checkRows(board: Array<Array<Player>>): Winner{
        val winType = WinType.ROW
        var player: Player = Player.EMPTY
        var startRow: Int = -1
        var startCol: Int = -1
        if(board[0][0] == board[0][1] && board [0][1] == board[0][2]){
            startRow = 0
            player = board[0][0]
        }else if(board[1][0] == board[1][1] && board [1][1] == board[1][2]){
            startRow = 1
            player = board[1][0]
        }else if(board[2][0] == board[2][1] && board [2][1] == board[2][2]){
            startRow = 2
            player = board[2][0]
        }
        return Winner(player, winType, startRow, startCol)
    }

    /**
     * Checks on the given board whether a player wins on a column
     */
    fun checkColumns(board: Array<Array<Player>>): Winner{
        val winType = WinType.COLUMN
        var player: Player = Player.EMPTY
        var startRow: Int = -1
        var startCol: Int = -1
        if(board[0][0] == board[1][0] && board [1][0] == board[2][0]){
            startCol = 0
            player = board[0][0]
        }else if(board[0][1] == board[1][1] && board [1][1] == board[2][1]){
            startCol = 1
            player = board[0][1]
        }else if(board[0][2] == board[1][2] && board [1][2] == board[2][2]){
            startCol = 2
            player = board[0][2]
        }

        return Winner(player, winType, startRow, startCol)
    }

    /**
     * Checks on the given Board whether a player wins on a diagonal line
     */
    fun checkDiagonals(board: Array<Array<Player>>): Winner{
        var winType = WinType.DIAGONAL_TL_TO_BR
        var player: Player = Player.EMPTY
        var startRow: Int = -1
        var startCol: Int = -1
        if(board[0][0] == board[1][1] && board [1][1] == board[2][2]){
            winType = WinType.DIAGONAL_TL_TO_BR
            player = board[0][0]
        }else if(board[0][2] == board[1][1] && board [1][1] == board[2][0]){
            winType = WinType.DIAGONAL_TR_TO_BL
            player = board[0][2]
        }
        return Winner(player, winType, startRow, startCol)
    }
}

class Bot(val mPlayer: TicTacToeLogic.Player){

    fun toggleField(board: Array<Array<TicTacToeLogic.Player>>): Field{
        var row = 0
        var col = 0

        for (rowIndex in 0..board.size - 1) {
            row = rowIndex
            for (columnIndex in 0..board[row].size - 1) {
                if(board[row][col] == TicTacToeLogic.Player.EMPTY){
                    return Field(row, col)
                }
                col = columnIndex
            }
        }

        return Field(row, col)
    }
}

data class Winner(val player:TicTacToeLogic.Player, val winType: TicTacToeLogic.WinType, val row:Int, val col: Int)
data class Field(val row:Int, val col: Int)

interface TicTacToeViewInterface{

    fun isActivePlayerChanged(newActivePlayer: TicTacToeLogic.Player)

    fun setField(row: Int, column: Int,player: TicTacToeLogic.Player):Boolean

    fun getField(row: Int, column: Int): TicTacToeLogic.Player

    fun isNewGame()

    fun isFinished(winner: Winner)



}
