package com.pwillmann.development.udemylearning

import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.pwillmann.development.udemylearning.TicTacToeLogic.Player
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TicTacToeViewInterface {
    val TAG: String = MainActivity::class.java.simpleName

    lateinit var mGameLogic: TicTacToeLogic

    var mXWins = 0
    var mOWins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mToolbar: Toolbar = toolbar
        setSupportActionBar(mToolbar)
        val mActionBar: ActionBar? = supportActionBar
        mActionBar?.title = "Tic Tac Toe"
        mGameLogic = TicTacToeLogic(this)
        isActivePlayerChanged(Player.X)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (menu?.getItem(0) != null) {
            val icon = menu.getItem(0).icon

            icon.mutate()

            val color = ContextCompat.getColor(this, R.color.white_transparent_75)
            icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var selectionHandled = false

        when (item?.itemId) {
            R.id.action_restart -> {
                restartGame()
                selectionHandled = true
            }
        }
        return selectionHandled
    }

    fun restartGame() {
        mGameLogic.resetGame()
        this.mXWins = 0
        this.mOWins = 0

        score_o.text = this.mOWins.toString()
        score_x.text = this.mXWins.toString()
    }

    fun toggleField(v: View) {
        Log.d(TAG, "toggleField() called")
        var rowIndex = -1
        var columnIndex: Int = -1
        when (v.id) {
            R.id.row0col0 -> {
                rowIndex = 0
                columnIndex = 0
            }
            R.id.row0col1 -> {
                rowIndex = 0
                columnIndex = 1
            }
            R.id.row0col2 -> {
                rowIndex = 0
                columnIndex = 2
            }
            R.id.row1col0 -> {
                rowIndex = 1
                columnIndex = 0
            }
            R.id.row1col1 -> {
                rowIndex = 1
                columnIndex = 1
            }
            R.id.row1col2 -> {
                rowIndex = 1
                columnIndex = 2
            }
            R.id.row2col0 -> {
                rowIndex = 2
                columnIndex = 0
            }
            R.id.row2col1 -> {
                rowIndex = 2
                columnIndex = 1
            }
            R.id.row2col2 -> {
                rowIndex = 2
                columnIndex = 2
            }
        }

        if (v is AppCompatImageView) {
            this.mGameLogic.toggleField(rowIndex, columnIndex)
        }

    }

    fun updateScore(winner: Player) {
        if (winner == Player.X) {
            this.mXWins++
        } else {
            this.mOWins++
        }
        score_o.text = this.mOWins.toString()
        score_x.text = this.mXWins.toString()
    }

    fun setFieldImage(iv: AppCompatImageView, player: Player) {
        val icon: Drawable? = when (player) {
            Player.X -> AnimatedVectorDrawableCompat.create(this, R.drawable.vec_ic_animated_cross)
            Player.EMPTY -> null
            Player.O -> AnimatedVectorDrawableCompat.create(this, R.drawable.vec_ic_animated_ring)
        }
        iv.setImageDrawable(icon)
        if (icon != null) {
            val anim: Drawable = iv.drawable
            if (anim is Animatable) {
                anim.start()
            }
        }
    }

    override fun isActivePlayerChanged(newActivePlayer: Player) {
        indicator_x.visibility = View.GONE
        indicator_o.visibility = View.GONE
        when (newActivePlayer) {
            Player.X -> indicator_x.visibility = View.VISIBLE
            Player.O -> indicator_o.visibility = View.VISIBLE
            Player.EMPTY -> {}
        }
    }

    override fun setField(row: Int, column: Int, player: TicTacToeLogic.Player): Boolean {
        var success: Boolean = false
        val str: String = "row${row}col${column}"

        val id: Int = getResources().getIdentifier(str, "id", packageName)
        val view = this.findViewById(id)

        if (id > 0 && view is AppCompatImageView) {
            val iv: AppCompatImageView = view

            setFieldImage(iv, player)
            success = true
        }

        return success
    }

    override fun getField(row: Int, column: Int): TicTacToeLogic.Player {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isNewGame() {
        winning_overlay.clear()

    }


    override fun isFinished(winner: Winner) {
        if(winner.player != Player.EMPTY && winner.winType != TicTacToeLogic.WinType.DRAW){
            drawWinningLine(winner.row, winner.col, winner.winType, winner.player)
            updateScore(winner.player)
        }

        Handler().postDelayed({
            mGameLogic.resetGame()
        }, 1500)



    }

    fun drawWinningLine(row: Int, col: Int, winType: TicTacToeLogic.WinType, winner: Player) {
        val lineColor: Int = if (winner == Player.X) R.color.grey_dark else R.color.grey_light
        var startX = getLeftOfTable()
        var startY = getTopOfTable()
        var endX = getRightOfTable()
        var endY = getBottomOfTable()

        when (winType) {
            TicTacToeLogic.WinType.COLUMN -> {
                startY = getTopOfTable()
                endY = getBottomOfTable()


                startX = when (col) {
                    0 -> getLeftOfTable() + ((getRightOfTable() - getLeftOfTable()) / 6)
                    1 -> getLeftOfTable() + ((getRightOfTable() - getLeftOfTable()) / 2)
                    2 -> getRightOfTable() - ((getRightOfTable() - getLeftOfTable()) / 6)
                    else -> getLeftOfTable() + ((getRightOfTable() - getLeftOfTable()) / 6)
                }
                endX = startX
            }
            TicTacToeLogic.WinType.ROW -> {
                startX = getLeftOfTable()
                endX = getRightOfTable()

                startY = when (row) {
                    0 -> getTopOfTable() + ((getBottomOfTable() - getTopOfTable()) / 6)
                    1 -> getTopOfTable() + ((getBottomOfTable() - getTopOfTable()) / 2)
                    2 -> getBottomOfTable() - ((getBottomOfTable() - getTopOfTable()) / 6)
                    else -> getTopOfTable() + ((getBottomOfTable() - getTopOfTable()) / 6)
                }
                endY = startY
            }
            TicTacToeLogic.WinType.DIAGONAL_TL_TO_BR -> {
                startX = getLeftOfTable()
                startY = getTopOfTable()
                endX = getRightOfTable()
                endY = getBottomOfTable()
            }
            TicTacToeLogic.WinType.DIAGONAL_TR_TO_BL -> {
                startX = getRightOfTable()
                startY = getTopOfTable()
                endX = getLeftOfTable()
                endY = getBottomOfTable()
            }
        }

        winning_overlay.drawLine(startX, startY, endX, endY, lineColor)
    }


    val statusbarHeight: Int by lazy {
        val rectangle = Rect()
        val window = window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        rectangle.top
    }

    fun getTopOfTable(): Float {
        val rect: Rect = Rect()
        row0col0.getGlobalVisibleRect(rect)

        return rect.top.toFloat() - statusbarHeight
    }

    fun getBottomOfTable(): Float {
        val rect: Rect = Rect()
        row2col0.getGlobalVisibleRect(rect)

        return rect.bottom.toFloat() - statusbarHeight
    }

    fun getLeftOfTable(): Float {
        val rect: Rect = Rect()
        row0col0.getGlobalVisibleRect(rect)

        return rect.left.toFloat()
    }

    fun getRightOfTable(): Float {
        val rect: Rect = Rect()
        row0col2.getGlobalVisibleRect(rect)

        return rect.right.toFloat()
    }
}


