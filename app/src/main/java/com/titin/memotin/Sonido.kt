package com.titin.memotin


import android.content.Context
import android.media.AudioManager
import android.media.SoundPool

class Sonido(private val context: Context) {

   
    private val soundPool: SoundPool = SoundPool(Constantes.TOTAL_BUTTONS, AudioManager.STREAM_MUSIC, 0)

   
    private val soundIds = IntArray(Constantes.TOTAL_BUTTONS + 3) // Incluye sonidos para victoria, derrota y especiales

   
    private var speakerStream = 0

  
    init {
        loadSounds()
    }

    
    private fun loadSounds() {
        soundIds[Constantes.GREEN] = soundPool.load(context, R.raw.green_long, 1)
        soundIds[Constantes.RED] = soundPool.load(context, R.raw.red_long, 1)
        soundIds[Constantes.YELLOW] = soundPool.load(context, R.raw.yellow_long, 1)
        soundIds[Constantes.BLUE] = soundPool.load(context, R.raw.blue_long, 1)
        soundIds[Constantes.VICTORY_SOUND] = soundPool.load(context, R.raw.victory, 1)
        soundIds[Constantes.LOSE_SOUND] = soundPool.load(context, R.raw.lose, 1)
        soundIds[Constantes.SPECIAL_RAZZ] = soundPool.load(context, R.raw.special_razz, 1)
    }

   
    fun getSoundId(index: Int): Int {
        return if (index >= 0 && index < soundIds.size) {
            soundIds[index]
        } else {
            0
        }
    }

    
    fun doStream(soundId: Int) {
        if (soundId != 0) {
            if (speakerStream != 0) {
                soundPool.stop(speakerStream)
            }
            speakerStream = soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

   
    fun stopSound() {
        if (speakerStream != 0) {
            soundPool.stop(speakerStream)
            speakerStream = 0
        }
    }

   
    fun dispose() {
        soundPool.release()
    }
}