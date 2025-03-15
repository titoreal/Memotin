package com.titin.memotin

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.floor


class BotonesVistas : View, Listener {

   
    private var buttonCellSize: Float = 0f
   
    private var scale: Float = 0f

  
    private val buttonBitmaps by lazy {
        val resources = context.resources
        mapOf(
           
            0 to Pair(
                BitmapFactory.decodeResource(resources, R.drawable.ex_green_on),
                BitmapFactory.decodeResource(resources, R.drawable.ex_green_off)
            ),
          
            1 to Pair(
                BitmapFactory.decodeResource(resources, R.drawable.ex_red_on),
                BitmapFactory.decodeResource(resources, R.drawable.ex_red_off)
            ),
           
            2 to Pair(
                BitmapFactory.decodeResource(resources, R.drawable.ex_yellow_on),
                BitmapFactory.decodeResource(resources, R.drawable.ex_yellow_off)
            ),
           
            3 to Pair(
                BitmapFactory.decodeResource(resources, R.drawable.ex_blue_on),
                BitmapFactory.decodeResource(resources, R.drawable.ex_blue_off)
            )
        )
    }

   
    private lateinit var model: MemotinJuego

    
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

  
    fun setSimonCloneModel(model: MemotinJuego) {
        
        if (::model.isInitialized) {
            this.model.removeListener(this)
        }
        
        this.model = model
        
        model.addListener(this)
    }

   
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

       
        scale = canvas.clipBounds.width().toFloat()

       
        canvas.save()
        
        canvas.scale(scale, scale)

       
        drawButtons(canvas)

      
        canvas.restore()
    }

    
    private fun drawButtons(canvas: Canvas) {
       
        buttonCellSize = 1.0f / Constantes.BUTTON_GRID_SIZE

       
        for (row in 0 until Constantes.BUTTON_GRID_SIZE) {
            for (col in 0 until Constantes.BUTTON_GRID_SIZE) {
                drawButton(canvas, row, col)
            }
        }
    }

  
    private fun drawButton(canvas: Canvas, row: Int, col: Int) {
        
        val buttonCellTop = row * buttonCellSize
        val buttonCellLeft = col * buttonCellSize

       
        val buttonTop = buttonCellTop + Constantes.BUTTON_PADDING
        val buttonLeft = buttonCellLeft + Constantes.BUTTON_PADDING

        
        val buttonSize = (buttonCellSize - Constantes.BUTTON_PADDING * 2)

        
        val buttonIndex = getButtonIndex(row, col)
        
        val pressed = model.isButtonPressed(buttonIndex)

        
        val bitmap = if (pressed) {
            buttonBitmaps[buttonIndex]?.first  
        } else {
            buttonBitmaps[buttonIndex]?.second 
        } ?: return 

       
        val pixelSize = canvas.clipBounds.width().toFloat()
        val bitmapScaleX = (pixelSize / bitmap.width) * buttonSize
        val bitmapScaleY = (pixelSize / bitmap.height) * buttonSize

       
        canvas.save()
       
        canvas.scale(bitmapScaleX, bitmapScaleY)
        
        canvas.drawBitmap(bitmap, buttonLeft / bitmapScaleX, buttonTop / bitmapScaleY, null)
      
        canvas.restore()
    }

   
    private fun getButtonIndex(row: Int, col: Int): Int {
        return row * Constantes.BUTTON_GRID_SIZE + col
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
       
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

       
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

      
        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)

      
        val chosenDimension = minOf(chosenWidth, chosenHeight)

       
        setMeasuredDimension(chosenDimension, chosenDimension)
    }

   
    private fun chooseDimension(mode: Int, size: Int): Int {
        return when (mode) {
           
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> size
           
            else -> getPreferredSize()
        }
    }

   
    private fun getPreferredSize(): Int {
        return 300 
    }

  
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
           
            MotionEvent.ACTION_DOWN -> {
               
                getButtonByCoords(event.x, event.y)?.let { model.pressButton(it) }
                return true
            }
           
            MotionEvent.ACTION_UP -> {
                
                getButtonByCoords(event.x, event.y)?.let { model.releaseButton(it) }
               
                model.releaseAllButtons()
                return true
            }
           
            MotionEvent.ACTION_POINTER_2_DOWN -> {
               
                getButtonByCoords(event.getX(1), event.getY(1))?.let { model.pressButton(it) }
                return true
            }
            
            MotionEvent.ACTION_POINTER_2_UP -> {
                
                getButtonByCoords(event.getX(1), event.getY(1))?.let { model.releaseButton(it) }
                return true
            }
        }
        return false 
    }

    
    private fun getButtonByCoords(x: Float, y: Float): Int? {
       
        val scaledX = x / scale
        val scaledY = y / scale

       
        val buttonCellX = floor(scaledX / buttonCellSize).toInt()
        val buttonCellY = floor(scaledY / buttonCellSize).toInt()

       
        if (buttonCellX < 0 || buttonCellX >= Constantes.BUTTON_GRID_SIZE ||
            buttonCellY < 0 || buttonCellY >= Constantes.BUTTON_GRID_SIZE) {
            return null // Coordenadas fuera de los límites
        }

       
        return getButtonIndex(buttonCellY, buttonCellX)
    }

    
    override fun buttonStateChanged(index: Int) {
       
    }

   
    override fun multipleButtonStateChanged() {
       
        invalidate()
    }
}
