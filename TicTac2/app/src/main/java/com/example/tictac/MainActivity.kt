package com.example.tictac
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isPlayer1Turn = true // true para el jugador 1 (rojo), false para el jugador 2 (azul)
    private lateinit var tvPlayerTurn: TextView
    private lateinit var btnRestart: Button
    private val gridCells = Array(6) { arrayOfNulls<View>(7) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPlayerTurn = findViewById(R.id.tv_player_turn)
        btnRestart = findViewById(R.id.btn_restart)
        updatePlayerTurn()

        // Configura las celdas
        for (row in 0..5) {
            for (col in 0..6) {
                val cellId = "cell_${row}_$col"
                val resID = resources.getIdentifier(cellId, "id", packageName)
                val cell = findViewById<View>(resID)

                if (cell != null) {
                    gridCells[row][col] = cell
                    cell.setOnClickListener {
                        onCellClicked(col) // Solo se pasa la columna
                    }
                } else {
                    Log.e("MainActivity", "No se encontró la celda con ID: $cellId")
                }
            }
        }

        btnRestart.setOnClickListener { restartGame() }
    }

    private fun onCellClicked(col: Int) {
        // Busca la primera fila vacía en la columna seleccionada
        var targetRow = -1
        for (r in 5 downTo 0) {
            if (gridCells[r][col]?.background is ColorDrawable &&
                (gridCells[r][col]!!.background as ColorDrawable).color == Color.WHITE) {
                targetRow = r
                break
            }
        }

        if (targetRow == -1) return // Columna llena, no se puede colocar la ficha

        // Cambia el color de la celda según el turno del jugador
        val color = if (isPlayer1Turn) Color.RED else Color.BLUE
        gridCells[targetRow][col]!!.setBackgroundColor(color)

        // Verifica si hay un ganador
        if (checkWin(targetRow, col)) {
            tvPlayerTurn.text = if (isPlayer1Turn) "Jugador 1 (Rojo) gana!" else "Jugador 2 (Azul) gana!"
            disableGrid()
            return
        }

        // Cambia el turno
        isPlayer1Turn = !isPlayer1Turn
        updatePlayerTurn()
    }

    private fun updatePlayerTurn() {
        val currentPlayer = if (isPlayer1Turn) "Turno: Jugador 1 (Rojo)" else "Turno: Jugador 2 (Azul)"
        tvPlayerTurn.text = currentPlayer
    }

    private fun checkWin(row: Int, col: Int): Boolean {
        return checkDirection(row, col, 1, 0) ||  // Horizontal
                checkDirection(row, col, 0, 1) ||  // Vertical
                checkDirection(row, col, 1, 1) ||  // Diagonal positiva
                checkDirection(row, col, 1, -1)    // Diagonal negativa
    }

    private fun checkDirection(row: Int, col: Int, deltaRow: Int, deltaCol: Int): Boolean {
        var count = 1 // Comienza contando la celda actual

        // Comprueba en una dirección
        count += countCells(row, col, deltaRow, deltaCol)
        // Comprueba en la dirección opuesta
        count += countCells(row, col, -deltaRow, -deltaCol)

        return count >= 4 // Se ganó si hay cuatro en línea
    }

    private fun countCells(row: Int, col: Int, deltaRow: Int, deltaCol: Int): Int {
        var count = 0
        val color = if (isPlayer1Turn) Color.RED else Color.BLUE

        var r = row + deltaRow
        var c = col + deltaCol

        while (r in 0..5 && c in 0..6) {
            val background = gridCells[r][c]?.background
            if (background is ColorDrawable && background.color == color) {
                count++
            } else {
                break // Se detiene si no hay coincidencia
            }
            r += deltaRow
            c += deltaCol
        }

        return count
    }

    private fun disableGrid() {
        for (row in 0..5) {
            for (col in 0..6) {
                gridCells[row][col]?.isEnabled = false // Desactiva las celdas
            }
        }
    }

    private fun restartGame() {
        for (row in 0..5) {
            for (col in 0..6) {
                gridCells[row][col]?.setBackgroundColor(Color.WHITE) // Restablece el color a blanco
                gridCells[row][col]?.isEnabled = true // Habilita las celdas
            }
        }
        isPlayer1Turn = true // Reinicia el turno
        updatePlayerTurn()
    }
}





