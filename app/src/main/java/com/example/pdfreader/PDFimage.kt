package com.example.pdfreader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.YELLOW
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import java.util.ArrayDeque


@SuppressLint("AppCompatCustomView")
class PDFimage(context: Context, currPage: Int, totalPages: Int) : ImageView(context) {
    var last = PointF()

    val LOGNAME = "pdf_image"
    var path: myPath? = null
    var erasePath: Path = Path()
    var pagePaths: Array<MutableList<myPath?>>
    var bitmap: Bitmap? = null
    //var paint = Paint(Color.BLUE)
    var mode: Mode = Mode.DRAW

    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f
    var old_x1 = 0f
    var old_y1 = 0f
    var old_x2 = 0f
    var old_y2 = 0f
    var mid_x = -1f
    var mid_y = -1f
    var old_mid_x = -1f
    var old_mid_y = -1f
    var p1_id = 0
    var p1_index = 0
    var p2_id = 0
    var p2_index = 0

    var myMatrix = Matrix()
    var inverse = Matrix()

    var currPage:Int = 0

    //val undo = LimitedStack<Path>(5)
    val pageUndo: Array<LimitedStack<myPath>>
    val pageRedo: Array<LimitedStack<myPath>>
    //val redo = LimitedStack<Path>(5)

    init {
        this.currPage = currPage
        pagePaths = Array(totalPages) { mutableListOf<myPath?>() }
        pageUndo = Array(totalPages) { LimitedStack<myPath>(5) }
        pageRedo = Array(totalPages) { LimitedStack<myPath>(5) }
        super.setClickable(true)
        scaleType = ScaleType.MATRIX
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(mode == Mode.DRAW || mode == Mode.HIGHLIGHT) {
            var inverted = floatArrayOf()
            when (event.pointerCount) {
                1 -> {
                    p1_id = event.getPointerId(0)
                    p1_index = event.findPointerIndex(p1_id)

                    // invert using the current matrix to account for pan/scale
                    // inverts in-place and returns boolean
                    //inverse = Matrix()
                    myMatrix.invert(inverse)

                    // mapPoints returns values in-place
                    inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                    inverse.mapPoints(inverted)
                    x1 = inverted[0]
                    y1 = inverted[1]

                    //val touchPoint = floatArrayOf(event.x,event.y)
                    //inverse.mapPoints(touchPoint)
                    val touchPoint = inverted
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d(LOGNAME, "Action down")
                            path = myPath(this.mode) // <--sets style
                            pagePaths[currPage].add(path)
                            pageUndo[currPage].push(path!!)
                            pageRedo[currPage].clear()
                            path!!.moveTo(touchPoint[0], touchPoint[1])
                        }

                        MotionEvent.ACTION_MOVE -> {
                            Log.d(LOGNAME, "Action move")
                            path!!.lineTo(touchPoint[0], touchPoint[1])
                        }

                        MotionEvent.ACTION_UP -> Log.d(LOGNAME, "Action up")
                    }
                    invalidate()
                }

                2 -> {
                    // point 1
                    p1_id = event.getPointerId(0)
                    p1_index = event.findPointerIndex(p1_id)

                    // mapPoints returns values in-place
                    inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
                    inverse.mapPoints(inverted)

                    // first pass, initialize the old == current value
                    if (old_x1 < 0 || old_y1 < 0) {
                        x1 = inverted.get(0)
                        old_x1 = x1
                        y1 = inverted.get(1)
                        old_y1 = y1
                    } else {
                        old_x1 = x1
                        old_y1 = y1
                        x1 = inverted.get(0)
                        y1 = inverted.get(1)
                    }

                    // point 2
                    p2_id = event.getPointerId(1)
                    p2_index = event.findPointerIndex(p2_id)

                    // mapPoints returns values in-place
                    inverted = floatArrayOf(event.getX(p2_index), event.getY(p2_index))
                    inverse.mapPoints(inverted)

                    // first pass, initialize the old == current value
                    if (old_x2 < 0 || old_y2 < 0) {
                        x2 = inverted.get(0)
                        old_x2 = x2
                        y2 = inverted.get(1)
                        old_y2 = y2
                    } else {
                        old_x2 = x2
                        old_y2 = y2
                        x2 = inverted.get(0)
                        y2 = inverted.get(1)
                    }

                    // midpoint
                    mid_x = (x1 + x2) / 2
                    mid_y = (y1 + y2) / 2
                    old_mid_x = (old_x1 + old_x2) / 2
                    old_mid_y = (old_y1 + old_y2) / 2

                    // distance
                    val d_old =
                        Math.sqrt(
                            Math.pow(
                                (old_x1 - old_x2).toDouble(),
                                2.0
                            ) + Math.pow((old_y1 - old_y2).toDouble(), 2.0)
                        )
                            .toFloat()
                    val d = Math.sqrt(
                        Math.pow(
                            (x1 - x2).toDouble(),
                            2.0
                        ) + Math.pow((y1 - y2).toDouble(), 2.0)
                    )
                        .toFloat()

                    // pan and zoom during MOVE event
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        Log.d(LOGNAME, "Multitouch move")
                        // pan == translate of midpoint
                        val dx = mid_x - old_mid_x
                        val dy = mid_y - old_mid_y
                        myMatrix.preTranslate(dx, dy)
                        Log.d(LOGNAME, "translate: $dx,$dy")

                        // zoom == change of spread between p1 and p2
                        var scale = d / d_old
                        scale = Math.max(0f, scale)
                        myMatrix.preScale(scale, scale, mid_x, mid_y)
                        Log.d(LOGNAME, "scale: $scale")

                        //NEW:
                        imageMatrix = myMatrix
                        invalidate()
                        // reset on up
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        old_x1 = -1f
                        old_y1 = -1f
                        old_x2 = -1f
                        old_y2 = -1f
                        old_mid_x = -1f
                        old_mid_y = -1f
                    }
                }
                else -> {
                }
            }
        }

        if(mode == Mode.PAN ) {
            val curr = PointF(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    last = curr
                }
                MotionEvent.ACTION_MOVE -> {
                    var dx = curr.x - last.x
                    var dy = curr.y - last.y
                    Log.d(LOGNAME, "translate: $dx,$dy")
                    val pace = 0.3f
                    myMatrix.preTranslate(dx*pace, dy*pace)

                    imageMatrix = myMatrix
                    //last[curr.x] = curr.y
                    last = curr
                }
            }
        }

        if(mode == Mode.ERASE){
            var inverted = floatArrayOf()
            inverted = floatArrayOf(event.x, event.y)
            inverse.mapPoints(inverted)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    erasePath = Path() // <--sets style
                    erasePath.moveTo(inverted[0], inverted[1])
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d(LOGNAME, "test for erase")
                    erasePath!!.lineTo(inverted[0], inverted[1])

                    //val touchedPath = getTouchedPath(inverted[0], inverted[1])
                    val touchedPath = getTouchedPath()
                    touchedPath?.let {
                        pagePaths[currPage].remove(it)
                        pageRedo[currPage].push(it)
                    }
                }
                MotionEvent.ACTION_UP -> Log.d(LOGNAME, "Action up")
            }
        }
        return true
    }

//    private fun getTouchedPath(x: Float, y: Float): myPath? {
//        for (path in pagePaths[currPage]) {
//            val touchRect = RectF(x - 15, y - 15, x + 15, y + 15)
//            val pathRect = RectF()
//            path!!.computeBounds(pathRect, true)
//            if (RectF.intersects(touchRect, pathRect)) {
//                return path
//            }
//        }
//        return null
//    }
    private fun getTouchedPath(): myPath? {
        for (path in pagePaths[currPage]) {
            val result = Path()
            if(result.op(erasePath, path!!, Path.Op.INTERSECT)){
                if(!result.isEmpty){
                    return path
                }
            }
        }
        return null
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // apply transformations from the event handler above
        canvas.concat(myMatrix)

        // draw background
        if (bitmap != null) {
            setImageBitmap(bitmap!!)
        }

        // draw lines over it
        for (path in pagePaths[currPage]) {
            canvas.drawPath(path!!, path.paint)
        }
    }

    fun setImage(bitmap: Bitmap) {
        this.bitmap = bitmap
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sx = MeasureSpec.getSize(widthMeasureSpec).toFloat() / bitmap!!.width.toFloat()
        val sy = MeasureSpec.getSize(heightMeasureSpec).toFloat() / bitmap!!.height.toFloat()
        val s = Math.min(sx, sy)
        myMatrix.setScale(s, s)
        imageMatrix = myMatrix
    }

    fun undo(){
        if(pageUndo[currPage].isEmpty()){
            return
        }
        val path = pageUndo[currPage].pop()
        pagePaths[currPage].remove(path)
        if (path != null) {
            pageRedo[currPage].push(path)
        }
    }

    fun redo(){
        if(pageRedo[currPage].isEmpty()){
            return
        }
        val path = pageRedo[currPage].pop()
        pagePaths[currPage].add(path)
        if (path != null) {
            pageUndo[currPage].push(path)
        }
    }

    enum class Mode {
        PAN,
        DRAW,
        HIGHLIGHT,
        ERASE
    }
}

class myPath(paintStyle: PDFimage.Mode): Path(){

    val paint: Paint
    init{
        if(paintStyle == PDFimage.Mode.DRAW){
            paint = Paint().apply {
                color = BLACK
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
        }else{
            paint = Paint().apply {
                color = YELLOW
                alpha = 80 // Set the alpha (transparency) value to create a semi-transparent effect
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }
        }

    }
}
class LimitedStack<E>(private val maxSize: Int) {
    private val stack: ArrayDeque<E> = ArrayDeque(maxSize)
    fun push(element: E) {
        if (stack.size >= maxSize) {
            stack.removeFirst()
        }
        stack.addLast(element)
    }
    fun pop(): E? {
        return stack.pollLast()
    }
    fun clear() {
        stack.clear()
    }

    fun isEmpty(): Boolean {
        return stack.isEmpty()
    }
}