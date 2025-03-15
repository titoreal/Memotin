package com.titin.memotin


import android.media.AudioManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

   
    private lateinit var model: MemotinJuego
    private lateinit var levelDisplay: TextView
    private lateinit var gameDisplay: TextView

   
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

    
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

      
        model = MemotinJuego(this)

        
        val grid = findViewById<BotonesVistas>(R.id.button_grid)
        grid.setSimonCloneModel(model)

       
        gameDisplay = findViewById(R.id.game)
        levelDisplay = findViewById(R.id.level)

       
        val lastButton = findViewById<Button>(R.id.last)
        lastButton.setOnClickListener {
            model.playLast()
        }

        val longestButton = findViewById<Button>(R.id.longest)
        longestButton.setOnClickListener {
            model.playLongest()
        }

       
        val startButton = findViewById<Button>(R.id.start)
        startButton.setOnClickListener {
            model.gameStart()
        }

       
        volumeControlStream = AudioManager.STREAM_MUSIC

       
        if (savedInstanceState == null) {
            val settings = getPreferences(0)
            model.setLevel(settings.getInt(Constantes.KEY_GAME_LEVEL, 1))
            model.setGame(settings.getInt(Constantes.KEY_THE_GAME, 1))
            model.setLongest(settings.getString(Constantes.KEY_LONGEST_SEQUENCE, "")!!)
            levelDisplay.text = model.getLevel().toString()
            gameDisplay.text = model.getGame().toString()
        } else {
            model.restoreState(savedInstanceState)
        }
    }

    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        model.saveState(outState)
    }

    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

   
    override fun onPause() {
        super.onPause()
        val settings = getPreferences(0)
        val editor = settings.edit()

        editor.putInt(Constantes.KEY_GAME_LEVEL, model.getLevel())
        editor.putInt(Constantes.KEY_THE_GAME, model.getGame())
        editor.putString(Constantes.KEY_LONGEST_SEQUENCE, model.getLongest())

        editor.apply()
    }

    
    private fun showLevelDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.set_level)
        builder.setSingleChoiceItems(R.array.level_choices, model.getLevel() - 1) { _, whichButton ->
            model.setLevel(whichButton + 1)
            levelDisplay.text = (whichButton + 1).toString()
        }
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

  
    private fun showGameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.set_game)
        builder.setSingleChoiceItems(R.array.game_choices, model.getGame() - 1) { _, whichButton ->
            model.setGame(whichButton + 1)
            gameDisplay.text = (whichButton + 1).toString()
        }
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    
    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.about)
        builder.setMessage(R.string.long_about)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    
    private fun showHelpDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.help)
        builder.setMessage(R.string.long_help)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

  
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.set_level -> {
                showLevelDialog()
                true
            }
            R.id.set_game -> {
                showGameDialog()
                true
            }
            R.id.about -> {
                showAboutDialog()
                true
            }
            R.id.help -> {
                showHelpDialog()
                true
            }
            R.id.clear_longest -> {
                model.setLongest("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
   
    override fun onDestroy() {
        super.onDestroy()
        model.dispose()  
    }
}
