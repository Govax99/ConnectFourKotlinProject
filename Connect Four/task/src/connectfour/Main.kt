package connectfour

enum class State() { OUT_OF_BOUND, NEXT, GAME_OVER, DRAW, P1_WON, P2_WON }

class ConnectFour(var players: Pair<String,String> = Pair("P1","P2"), var rows: Int = 6, var columns: Int = 7) {


    val format = Regex("""\s*(\d+)\s*[xX]\s*(\d+)\s*""")
    var board = Array<Array<Char>> (rows) { Array<Char>(columns) {' '} }
    var turnCount = 0
    val playerSymbols = Pair<Char, Char> ('o','*')
    var playerScore = mutableListOf<Int>(0, 0)
    var maxGames = 1
    var gameCount = 1

    fun getInput() {
        println("Connect Four")
        println("First player's name:")
        val p1 = readln()
        println("Second player's name:")
        val p2 = readln()
        players = Pair(p1, p2)

        while (true) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val input = readln()
            if (input == "") {
                break
            }

            val match = format.find(input)
            if (format.matches(input) && match != null) {
                val (tmpRow, tmpCol) = match.groupValues.drop(1)
                if (tmpRow.toInt() !in 5..9) {
                    println("Board rows should be from 5 to 9")
                    continue
                }
                if (tmpCol.toInt() !in 5..9) {
                    println("Board columns should be from 5 to 9")
                    continue
                }
                rows = tmpRow.toInt()
                columns = tmpCol.toInt()
                break
            } else {
                println("Invalid input")
            }
        }

        while (true) {
            println("Do you want to play single or multiple games?")
            println("For a single game, input 1 or press Enter")
            println("Input a number of games:")
            val input = readln()
            val inputFormat = Regex("""\d+""")
            if (input == "") {
                break
            } else if (input.matches(inputFormat)) {
                if (input == "0") {
                    println("Invalid input")
                } else {
                    maxGames = input.toInt()
                    break
                }
            } else {
                println("Invalid input")
            }

        }
        println("${players.first} VS ${players.second}")
        println("$rows X $columns board")
        if (maxGames == 1) {
            println("Single Game")
        } else {
            println("Total $maxGames games")
        }
        //board = Array<Array<Char>> (rows) { Array<Char>(columns) {' '} }
        //drawBoard()
    }

    fun drawBoard() {
        (1..columns).toList().forEach { print(" $it")}
        println(" ")
        for (i in board.indices) {
            for (j in board[0].indices) {
                print("║${board[i][j]}")
            }
            println("║")
        }
        print("╚")
        (1 until columns).toList().forEach { print("═╩")}
        println("═╝")
    }

    fun takeTurn(): State {
        var currPlayer = ""
        var currSymbol = ' '
        val inputFormat = Regex("""\d+""")
        if ((turnCount + gameCount - 1) % 2 == 0) {
            currPlayer = players.first
            currSymbol = playerSymbols.first
        } else {
            currPlayer = players.second
            currSymbol = playerSymbols.second
        }
        while (true) {
            println("$currPlayer's turn:")
            val input = readln()

            if (input == "end") {
                return State.GAME_OVER
            }
            if (input.matches(inputFormat)) {
                if (input.toInt() !in 1..columns) {
                    println("The column number is out of range (1 - $columns)")
                    continue
                }
                val result = placeDisc(input.toInt() - 1, currSymbol)
                val finalResults = checkForEnd()
                if (finalResults != State.NEXT) {
                    return finalResults
                }
                if (result == State.NEXT) {
                    drawBoard()
                    turnCount++
                    return State.NEXT
                } else {
                    drawBoard()
                    return result
                }


            } else {
                println("Incorrect column number")
                continue
            }

        }

    }

    fun checkForEnd(): State {
        var flagAll = true
        first@ for (i in board.indices) {
            for (j in board[0].indices) {
                if (board[i][j] !in "*o") {
                    flagAll = false
                    break@first
                }
            }
        }
        if (flagAll) {
            return State.DRAW
        }

        for (i in board.indices) {
            for (j in board[0].indices) {
                if (board[i][j] in "*o") {
                    if (checkWinAt(i, j)) {
                        if (board[i][j] == 'o') {
                            return State.P1_WON
                        } else {
                            return State.P2_WON
                        }
                    }
                }
            }
        }
        return State.NEXT
    }

    fun buildPossible(i: Int, j: Int): Array<Array<Pair<Int, Int>>> {
        val possible = arrayOf<Array<Pair<Int, Int>>>  (
            arrayOf(Pair(i+1, j+1), Pair(i+2,j+2),Pair(i+3,j+3)),
            arrayOf(Pair(i+1, j-1), Pair(i+2,j-2),Pair(i+3,j-3)),
            arrayOf(Pair(i-1, j+1), Pair(i-2,j+2),Pair(i-3,j+3)),
            arrayOf(Pair(i-1, j-1), Pair(i-2,j-2),Pair(i-3,j-3)),
            arrayOf(Pair(i+1, j), Pair(i+2,j),Pair(i+3,j)),
            arrayOf(Pair(i-1, j), Pair(i-2,j),Pair(i-3,j)),
            arrayOf(Pair(i, j+1), Pair(i,j+2),Pair(i,j+3)),
            arrayOf(Pair(i, j-1), Pair(i,j-2),Pair(i,j-3)),
        )
        return possible
    }

    fun checkWinAt(i: Int, j: Int): Boolean {
        val possible = buildPossible(i, j)
        possible.forEach {
            if(it.all { pair ->
                  pair.first in  board.indices &&
                  pair.second in board[0].indices &&
                  board[pair.first][pair.second] == board[i][j]
                }) return true
        }
        return false
    }

    fun placeDisc(column: Int, currSymbol: Char): State {

        if (board[0][column] in "*o") {
            println("Column ${column + 1} is full")
            return State.OUT_OF_BOUND
        }

        for (i in board.indices) {
            if (board[i][column] in "*o") {
                board[i-1][column] = currSymbol
                return State.NEXT
            }
        }
        board[board.lastIndex][column] = currSymbol


        return State.NEXT
    }

    fun singleGame(): State {
        if (maxGames != 1) {
            println("Game #$gameCount")
        }
        board = Array<Array<Char>> (rows) { Array<Char>(columns) {' '} }
        drawBoard()
        var result = State.NEXT
        while (true) {
            result = takeTurn()
            if (result == State.GAME_OVER) {
                println("Game over!")
                break
            } else if (result == State.P1_WON) {
                playerScore[0]+=2
                drawBoard()
                println("Player ${players.first} won")
                break
            } else if (result == State.P2_WON) {
                playerScore[1]+=2
                drawBoard()
                println("Player ${players.second} won")
                break
            } else if (result == State.DRAW) {
                playerScore[0]+=1
                playerScore[1]+=1
                drawBoard()
                println("It is a draw")
                break
            }
        }
        gameCount++
        if (result == State.GAME_OVER) return State.GAME_OVER
        if (maxGames != 1) {
            println("Score")
            println("${players.first}: ${playerScore[0]} ${players.second}: ${playerScore[1]}")
            if (gameCount - 1 == maxGames) {
                println("Game over!")
            }
        }
        return State.NEXT
    }

    fun mainOperation() {
        getInput()
        var result = State.NEXT
        while (gameCount <= maxGames && result != State.GAME_OVER) {
            result = singleGame()
        }
    }


}

fun main() {
    val game = ConnectFour()
    game.mainOperation()

}