package com.titin.memotin

import android.os.Bundle

class Estado {

   
    data class GameState(
        val theGame: Int,               
        val gameLevel: Int,              
        val longestSequence: String,     
        val currentSequence: String,    
        val sequenceIndex: Int,          
        val totalLength: Int,           
        val playerPosition: Int,         
        val winToneIndex: Int,           
        val razToneIndex: Int,           
        val beepDuration: Long,          
        val pauseDuration: Long,        
        val activeColors: BooleanArray,  
        val isLit: Boolean,              
        val heardButtonPress: Boolean,   
        val gameMode: Int                
    ) {
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameState

            if (theGame != other.theGame) return false
            if (gameLevel != other.gameLevel) return false
            if (longestSequence != other.longestSequence) return false
            if (currentSequence != other.currentSequence) return false
            if (sequenceIndex != other.sequenceIndex) return false
            if (totalLength != other.totalLength) return false
            if (playerPosition != other.playerPosition) return false
            if (winToneIndex != other.winToneIndex) return false
            if (razToneIndex != other.razToneIndex) return false
            if (beepDuration != other.beepDuration) return false
            if (pauseDuration != other.pauseDuration) return false
            if (!activeColors.contentEquals(other.activeColors)) return false
            if (isLit != other.isLit) return false
            if (heardButtonPress != other.heardButtonPress) return false
            if (gameMode != other.gameMode) return false

            return true
        }

        
        override fun hashCode(): Int {
            var result = theGame
            result = 31 * result + gameLevel
            result = 31 * result + longestSequence.hashCode()
            result = 31 * result + currentSequence.hashCode()
            result = 31 * result + sequenceIndex
            result = 31 * result + totalLength
            result = 31 * result + playerPosition
            result = 31 * result + winToneIndex
            result = 31 * result + razToneIndex
            result = 31 * result + beepDuration.hashCode()
            result = 31 * result + pauseDuration.hashCode()
            result = 31 * result + activeColors.contentHashCode()
            result = 31 * result + isLit.hashCode()
            result = 31 * result + heardButtonPress.hashCode()
            result = 31 * result + gameMode
            return result
        }
    }

    
    fun saveState(
        map: Bundle?,
        theGame: Int,
        gameLevel: Int,
        longestSequence: IntArray,
        longestLength: Int,
        currentSequence: IntArray,
        sequenceLength: Int,
        sequenceIndex: Int,
        totalLength: Int,
        playerPosition: Int,
        winToneIndex: Int,
        razToneIndex: Int,
        beepDuration: Long,
        pauseDuration: Long,
        activeColors: BooleanArray,
        isLit: Boolean,
        heardButtonPress: Boolean,
        gameMode: Int
    ): Bundle? {
        map?.apply {
           
            putInt(Constantes.KEY_THE_GAME, theGame)
            putInt(Constantes.KEY_GAME_LEVEL, gameLevel)
            putString(Constantes.KEY_LONGEST_SEQUENCE, parseSequenceToString(longestSequence, longestLength))
            putString("currentSequence", parseSequenceToString(currentSequence, sequenceLength))
            putInt("sequenceIndex", sequenceIndex)
            putInt("totalLength", totalLength)
            putInt("playerPosition", playerPosition)
            putInt("winToneIndex", winToneIndex)
            putInt("razToneIndex", razToneIndex)
            putLong("beepDuration", beepDuration)
            putLong("pauseDuration", pauseDuration)
            putBooleanArray("activeColors", activeColors)
            putBoolean("isLit", isLit)
            putBoolean("heardButtonPress", heardButtonPress)
            putInt("gameMode", gameMode)
        }
        return map
    }

    
    fun restoreState(map: Bundle): GameState {
        return GameState(
            theGame = map.getInt(Constantes.KEY_THE_GAME),
            gameLevel = map.getInt(Constantes.KEY_GAME_LEVEL),
            longestSequence = map.getString(Constantes.KEY_LONGEST_SEQUENCE) ?: "",
            currentSequence = map.getString("currentSequence") ?: "",
            sequenceIndex = map.getInt("sequenceIndex"),
            totalLength = map.getInt("totalLength"),
            playerPosition = map.getInt("playerPosition"),
            winToneIndex = map.getInt("winToneIndex"),
            razToneIndex = map.getInt("razToneIndex"),
            beepDuration = map.getLong("beepDuration"),
            pauseDuration = map.getLong("pauseDuration"),
            activeColors = map.getBooleanArray("activeColors") ?: BooleanArray(4),
            isLit = map.getBoolean("isLit"),
            heardButtonPress = map.getBoolean("heardButtonPress"),
            gameMode = map.getInt("gameMode")
        )
    }

   
    fun parseSequenceAsString(sequence: String): IntArray =
        IntArray(sequence.length) { i -> Character.toString(sequence[i]).toInt() }

   
    fun parseSequenceToString(array: IntArray, length: Int): String =
        StringBuilder().apply {
            for (i in 0 until length) {
                append(array[i])
            }
        }.toString()
}





















































