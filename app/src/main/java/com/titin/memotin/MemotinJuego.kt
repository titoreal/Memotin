package com.titin.memotin


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message

class MemotinJuego(private val context: Context) {
   
    private var gameMode = Constantes.IDLE  
    private var theGame = 0  
    private var totalLength = 0  
    private var isLit = false  
    private var heardButtonPress = false  


    
    private var activeColors = BooleanArray(4) { true }  
    private val longestSequence = IntArray(32)  
    private val currentSequence = IntArray(32)  
    private val razzSequence = intArrayOf(
        Constantes.RED, Constantes.YELLOW, Constantes.BLUE, Constantes.GREEN,
        Constantes.GREEN, Constantes.GREEN, Constantes.GREEN, Constantes.RED,
        Constantes.YELLOW, Constantes.LOSE_SOUND
    ) 

    
    private var longestLength = 0  
    private var sequenceLength = 0 
    private var sequenceIndex = 0  
    private var playerPosition = 0 

   
    private var beepDuration: Long = 0  
    private var mLastUpdate: Long = 0  
    private var pauseDuration: Long = 0  
    private var winToneIndex = 0  
    private var razToneIndex = 0  

  
    private val buttonPressMap = BooleanArray(Constantes.TOTAL_BUTTONS)  
    private val listeners = mutableListOf<Listener>() 


   
    private val soundManager = Sonido(context)  
    private val stateManager = Estado() 

  
    private val RNG = java.util.Random()

    
    private val mUpdateHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constantes.UI -> this@MemotinJuego.update()  
                Constantes.TIMEOUT -> this@MemotinJuego.gameTimeoutLose()  
            }
        }

      
        fun sleep(delayMillis: Long) {
            this.removeMessages(Constantes.UI)
            sendMessageDelayed(obtainMessage(Constantes.UI), delayMillis)
        }
    }

    
    init {
        mLastUpdate = System.currentTimeMillis() 
    }

  
    fun saveState(map: Bundle?): Bundle? {
        return stateManager.saveState(map, theGame, getLevel(),
            longestSequence, longestLength, currentSequence, sequenceLength,
            sequenceIndex, totalLength, playerPosition, winToneIndex,
            razToneIndex, beepDuration, pauseDuration, activeColors,
            isLit, heardButtonPress, gameMode)
    }

  
    fun restoreState(map: Bundle) {
        gameMode = Constantes.PAUSED  

       
        val restoredState = stateManager.restoreState(map)

       
        setGame(restoredState.theGame)
        setLevel(restoredState.gameLevel)
        setLongest(restoredState.longestSequence)
        setCurrent(restoredState.currentSequence)
        sequenceIndex = restoredState.sequenceIndex
        totalLength = restoredState.totalLength
        playerPosition = restoredState.playerPosition
        winToneIndex = restoredState.winToneIndex
        razToneIndex = restoredState.razToneIndex
        beepDuration = restoredState.beepDuration
        pauseDuration = restoredState.pauseDuration
        activeColors = restoredState.activeColors
        isLit = restoredState.isLit
        heardButtonPress = restoredState.heardButtonPress

       
        mLastUpdate = System.currentTimeMillis()
        gameMode = restoredState.gameMode
    }

   
    fun getLevel(): Int = when {
        totalLength <= 8 -> 1
        totalLength <= 14 -> 2
        totalLength <= 20 -> 3
        else -> 4
    }

   
    fun setLevel(level: Int) {
        val savedTotalLength = totalLength

        totalLength = when (level) {
            1 -> 8
            2 -> 14
            3 -> 20
            4 -> 30
            else -> 4
        }

        if (totalLength != savedTotalLength) {
            gameClearTimeout()
            if (pauseDuration > 0) pauseDuration = 0
            if (isLit) playNext()
            gameMode = Constantes.IDLE
            sequenceIndex = 0
        }
    }

   
    fun setGame(level: Int) {
        theGame = level
    }

   
    fun getGame(): Int = theGame

   
    fun setLongest(sequence: String) {
        longestLength = sequence.length
        stateManager.parseSequenceAsString(sequence).forEachIndexed { i, value ->
            longestSequence[i] = value
        }
    }

   
    fun setCurrent(sequence: String) {
        sequenceLength = sequence.length
        stateManager.parseSequenceAsString(sequence).forEachIndexed { i, value ->
            currentSequence[i] = value
        }
    }

    
    fun getLongest(): String = stateManager.parseSequenceToString(longestSequence, longestLength)

   
    private fun scaleBeepDuration(index: Int) {
        beepDuration = when {
            index < 6 -> 420L   
            index < 14 -> 320L   
            else -> 220L         
        } - Constantes.TICK_COMPENSATION

        if (beepDuration < 0) beepDuration = 0
    }

    
    private fun getRandomColor(): Int {
        var color = RNG.nextInt(4)
        if (theGame == 3) {
           
            while (!activeColors[color]) color = RNG.nextInt(4)
        }
        return color
    }

    
    private fun maintainLongest() {
        if (sequenceLength > longestLength) {
            currentSequence.copyInto(longestSequence, 0, 0, sequenceLength)
            longestLength = sequenceLength
        }
    }

    
    fun gameSetTimeout() {
        mUpdateHandler.removeMessages(Constantes.TIMEOUT)
        if (!Constantes.DISABLE_TIMEOUT) {
            mUpdateHandler.sendEmptyMessageDelayed(Constantes.TIMEOUT, 3000)
        }
    }

  
    fun gameClearTimeout() {
        mUpdateHandler.removeMessages(Constantes.TIMEOUT)
    }

   
    fun update() {
        val now = System.currentTimeMillis()
        val delay = if (pauseDuration > 0) pauseDuration else calculateDelay()

        if (gameMode != Constantes.LISTENING) {
            if (now - mLastUpdate > delay) {
                playNext()
                mLastUpdate = now
            }
            mUpdateHandler.sleep(delay)
        }
    }

  
    private fun calculateDelay(): Long = when (gameMode) {
       
        Constantes.WINNING -> if (winToneIndex == 0) 20L else if (isLit) 20L else Constantes.BETWEEN_DURATION + 20L
      
        Constantes.RAZZING -> if (razToneIndex == 0) Constantes.BETWEEN_DURATION
        else if (isLit) Constantes.BETWEEN_DURATION
        else (Constantes.RAZZ_DURATION - Constantes.RAZZ_COMPENSATION).coerceAtLeast(0)
      
        else -> if (isLit) Constantes.BETWEEN_DURATION else beepDuration
    }

   
    fun playNext() {
        if (pauseDuration > 0) {
            pauseDuration = 0
            return
        }

        when (gameMode) {
            Constantes.REPLAYING, Constantes.PLAYING -> handleSequencePlayback()
            Constantes.LONG_PLAYING -> handleLongestSequencePlayback()
            Constantes.WINNING -> handleWinningSequence()
            Constantes.RAZZING -> handleRazzingSequence()
            Constantes.LOSING -> gameMode = Constantes.LOST
        }
    }

  
    private fun handleSequencePlayback() {
        if (sequenceIndex < sequenceLength) {
            if (isLit) {
                showButtonRelease(currentSequence[sequenceIndex])
                isLit = false
                sequenceIndex++

                if (sequenceIndex == sequenceLength) {
                    if (gameMode == Constantes.PLAYING) {
                        gameSetTimeout()
                        sequenceIndex = 0
                        gameMode = Constantes.LISTENING
                    } else {
                        gameMode = Constantes.IDLE
                    }
                }
            } else {
                showButtonPress(currentSequence[sequenceIndex])
                isLit = true
            }
        }
    }

   
    private fun handleLongestSequencePlayback() {
        if (isLit) {
            showButtonRelease(longestSequence[sequenceIndex])
            isLit = false
            sequenceIndex++
            return
        }

        if (sequenceIndex < longestLength) {
            showButtonPress(longestSequence[sequenceIndex])
            isLit = true
        } else {
            scaleBeepDuration(sequenceLength)
            gameMode = Constantes.IDLE
        }
    }

   
    private fun handleWinningSequence() {
        if (isLit) {
            showButtonRelease(currentSequence[sequenceLength - 1])
            isLit = false
            if (winToneIndex == 6) gameMode = Constantes.WON
        } else {
            showButtonPress(currentSequence[sequenceLength - 1])
            isLit = true
            winToneIndex++
        }
    }

   
    private fun handleRazzingSequence() {
        if (isLit) {
            showButtonRelease(razzSequence[razToneIndex])
            isLit = false
            if (razToneIndex == 9) gameLose()
            razToneIndex++
        } else {
            showButtonPress(razzSequence[razToneIndex])
            isLit = true
        }
    }

   
    fun playCurrent() {
        gameMode = Constantes.PLAYING
        sequenceIndex = 0
        update()
    }

    
    fun playLast() {
        if (gameMode in arrayOf(Constantes.IDLE, Constantes.WON, Constantes.LOST)) {
            resetAllButtons()
            gameMode = Constantes.REPLAYING
            sequenceIndex = 0
            update()
        }
    }

   
    fun playLongest() {
        if (gameMode in arrayOf(Constantes.IDLE, Constantes.WON, Constantes.LOST)) {
            resetAllButtons()
            gameMode = Constantes.LONG_PLAYING
            sequenceIndex = 0
            scaleBeepDuration(longestLength)
            update()
        }
    }

   
    private fun resetAllButtons() {
        for (index in 0 until 4) {
            showButtonRelease(index)
        }
    }

  
    fun gameStart() {
        for (i in 0 until 4) {
            activeColors[i] = true
        }

        resetAllButtons()

        if (Constantes.SHORT_GAME) totalLength = 3

        winToneIndex = 0
        razToneIndex = 0
        sequenceLength = 1
        scaleBeepDuration(1)
        playerPosition = 1
        currentSequence[0] = getRandomColor()
        playCurrent()
    }

   
    fun gameWin() {
        mLastUpdate = System.currentTimeMillis()
        pauseDuration = 800
        gameMode = if (Constantes.TEST_RAZZ) Constantes.RAZZING else Constantes.WINNING
        update()
    }

   
    fun razzWin() {
        mLastUpdate = System.currentTimeMillis()
        pauseDuration = 800
        gameMode = Constantes.RAZZING
        update()
    }

    
    fun gameTimeoutLose() {
        if (theGame == 3) {
            activeColors[currentSequence[sequenceIndex]] = false
        }
        gameLose()
    }

   
    fun gameLose() {
        soundManager.doStream(soundManager.getSoundId(Constantes.LOSE_SOUND))

        if (theGame == 3) {
            val activeColorCount = activeColors.count { it }

            if (activeColorCount == 1) {
                gameWin()
            } else {
                sequenceLength = 1
                scaleBeepDuration(1)
                currentSequence[0] = getRandomColor()
                gameCycle()
            }
        } else {
            gameMode = Constantes.LOSING
            update()
        }
    }

   
    fun gameCycle() {
        mLastUpdate = System.currentTimeMillis()
        pauseDuration = 800
        playerPosition = 1
        update()
        playCurrent()
    }

   
    fun pressButton(buttonIndex: Int) {
        if (gameMode != Constantes.LISTENING) return

        heardButtonPress = true

       
        if (playerPosition > sequenceLength) {
            currentSequence[sequenceIndex] = buttonIndex
            sequenceLength++
            playerPosition++
        }

       
        if (currentSequence[sequenceIndex] == buttonIndex) {
            maintainLongest()
            showButtonPress(buttonIndex)
        } else {
            gameClearTimeout()
            soundManager.doStream(soundManager.getSoundId(Constantes.LOSE_SOUND))

            if (theGame == 3) {
                activeColors[buttonIndex] = false
            }

            gameLose()
        }
    }

   
    fun releaseButton(buttonIndex: Int) {
        if (gameMode != Constantes.LISTENING || !heardButtonPress) return

        heardButtonPress = false
        mLastUpdate = System.currentTimeMillis()
        gameSetTimeout()

        if (sequenceIndex < sequenceLength) {
            if (currentSequence[sequenceIndex] == buttonIndex) {
                showButtonRelease(buttonIndex)
                sequenceIndex++

                if (sequenceIndex == sequenceLength) {
                    handleCompletedSequence()
                } else {
                    playerPosition++
                }
            } else {
                if (theGame == 3) {
                    activeColors[buttonIndex] = false
                }
                gameLose()
            }
        }
    }

   
    private fun handleCompletedSequence() {
        if (sequenceLength < totalLength) {
            when (theGame) {
                2 -> {
                    if (playerPosition > sequenceLength) {
                        playerPosition = 1
                        sequenceIndex = 0
                    } else {
                        playerPosition++
                    }
                }
                else -> {
                    sequenceLength++
                    playerPosition = 1
                    scaleBeepDuration(sequenceLength)
                    currentSequence[sequenceIndex] = getRandomColor()
                    gameCycle()
                }
            }
        } else {
            if (theGame == 3 && sequenceLength == 31) {
                razzWin()
            } else {
                gameWin()
            }
        }
    }

   
    fun showButtonPress(index: Int) {
        gameClearTimeout()

        if (index in 0 until Constantes.TOTAL_BUTTONS && !buttonPressMap[index]) {
            buttonPressMap[index] = true

           
            when (gameMode) {
                Constantes.WON -> soundManager.doStream(soundManager.getSoundId(Constantes.VICTORY_SOUND))
                Constantes.WINNING -> soundManager.doStream(soundManager.getSoundId(Constantes.RED))
                Constantes.LOSING -> soundManager.doStream(soundManager.getSoundId(Constantes.LOSE_SOUND))
                Constantes.LISTENING -> soundManager.doStream(soundManager.getSoundId(if (currentSequence[sequenceIndex] == index) index else Constantes.LOSE_SOUND))
                Constantes.RAZZING -> if (razToneIndex < 9) soundManager.doStream(soundManager.getSoundId(index))
                else -> soundManager.doStream(soundManager.getSoundId(index))
            }

            listeners.forEach { it.buttonStateChanged(index) }
        }
    }

 
    fun showButtonRelease(index: Int) {
        if (index in 0 until Constantes.TOTAL_BUTTONS && buttonPressMap[index]) {
            buttonPressMap[index] = false

            soundManager.stopSound()

            listeners.forEach { it.buttonStateChanged(index) }
        }
    }

   
    fun isButtonPressed(index: Int): Boolean =
        index in 0 until Constantes.TOTAL_BUTTONS && buttonPressMap[index]

   
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

   
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

   
    fun releaseAllButtons() {
        for (i in buttonPressMap.indices) {
            buttonPressMap[i] = false
        }
        listeners.forEach { it.multipleButtonStateChanged() }
    }

  
    fun dispose() {
        soundManager.dispose()
    }
}





