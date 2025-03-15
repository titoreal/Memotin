package com.titin.memotin

object Constantes {
   
    const val BUTTON_GRID_SIZE = 2
   
    const val BUTTON_PADDING = 0.01f

   
    const val TOTAL_BUTTONS = 9 
    const val KEY_THE_GAME = "theGame" 
    const val KEY_GAME_LEVEL = "gameLevel" 
    const val KEY_LONGEST_SEQUENCE = "longestSequence" 
    const val DISABLE_TIMEOUT = false 
    const val TEST_RAZZ = false 
    const val SHORT_GAME = false 

    
    const val TICK_DURATION = 100L 
    const val BETWEEN_DURATION = 50L 
    val TICK_COMPENSATION = (TICK_DURATION - BETWEEN_DURATION).coerceAtLeast(0) 
    val RAZZ_DURATION = (100L - TICK_COMPENSATION).coerceAtLeast(20) 
    val RAZZ_COMPENSATION = (TICK_DURATION - BETWEEN_DURATION).coerceAtLeast(0) 
   
    const val UI = 0 
    const val TIMEOUT = 1 

   
    const val IDLE = 0 
    const val LISTENING = 1 
    const val PLAYING = 2
    const val REPLAYING = 3 
    const val LONG_PLAYING = 4 
    const val WINNING = 5 
    const val RAZZING = 6 
    const val WON = 7 
    const val LOSING = 8 
    const val LOST = 9 
    const val PAUSED = 10 

   
    const val GREEN = 0 // Sonido del botón verde
    const val RED = 1 // Sonido del botón rojo
    const val YELLOW = 2 // Sonido del botón amarillo
    const val BLUE = 3 // Sonido del botón azul
    const val VICTORY_SOUND = 4 // Sonido de victoria
    const val LOSE_SOUND = 5 // Sonido de derrota
    const val SPECIAL_RAZZ = 6 // Sonido especial 
