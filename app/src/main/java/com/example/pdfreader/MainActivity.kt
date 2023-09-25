package com.example.pdfreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so we should expect people to need this.
// We may wish to provide this code.
class MainActivity : AppCompatActivity() {
    val LOGNAME = "pdf_viewer"
    val FILENAME = "shannon1948.pdf"
    val FILERESID = R.raw.shannon1948
    var currPageNum = 0

    // manage the pages of the PDF, see below
    lateinit var pdfRenderer: PdfRenderer
    lateinit var parcelFileDescriptor: ParcelFileDescriptor
    var currentPage: PdfRenderer.Page? = null

    // custom ImageView class that captures strokes and draws them over the image
    lateinit var pageImage: PDFimage

    //var pageNumText: TextView = findViewById<TextView>(R.id.currPage)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = findViewById<LinearLayout>(R.id.pdfLayout)
        layout.isEnabled = true
        try {
            openRenderer(this)
        } catch (exception: IOException) {
            Log.d(LOGNAME, "Error opening PDF")
        }

        pageImage = PDFimage(this, 0, pdfRenderer.pageCount)
        layout.addView(pageImage)
        pageImage.minimumWidth = 1000
        pageImage.minimumHeight = 2000

        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
            openRenderer(this)
            showPage(0)
            //closeRenderer()
        } catch (exception: IOException) {
            Log.d(LOGNAME, "Error opening PDF")
        }

        //Edits:
        val nextButton = findViewById<Button>(R.id.nextButton)
        nextButton.setOnClickListener {
            Log.d("nextButton", "Next Button was clicked!")
            showPage( currPageNum+1 )
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            Log.d("backButton", "Back Button was clicked!")
            showPage( currPageNum-1 )
        }

        var pageNumTextm = findViewById<TextView>(R.id.fileName)
        pageNumTextm.setText("${FILENAME}")

        //Mode selection
        val radioGroup = findViewById<RadioGroup>(com.example.pdfreader.R.id.modeSelection)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedText = radioButton.text.toString()
            Log.d("Mode Selection", "${selectedText}")
            when(selectedText){
                "Pan" -> pageImage.mode = PDFimage.Mode.PAN
                "Draw" -> pageImage.mode = PDFimage.Mode.DRAW
                "Highlight" -> pageImage.mode = PDFimage.Mode.HIGHLIGHT
                "Erase" -> pageImage.mode = PDFimage.Mode.ERASE
            }
        }

        //undo/redo
        val undoButton = findViewById<Button>(R.id.undoButton)
        undoButton.setOnClickListener {
            pageImage.undo()
        }

        val redoButton = findViewById<Button>(R.id.redoButton)
        redoButton.setOnClickListener {
            pageImage.redo()
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            closeRenderer()
        } catch (ex: IOException) {
            Log.d(LOGNAME, "Unable to close PDF renderer")
        }
    }


    @Throws(IOException::class)
    private fun openRenderer(context: Context) {
        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            val asset = this.resources.openRawResource(FILERESID)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (asset.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            asset.close()
            output.close()
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        pdfRenderer = PdfRenderer(parcelFileDescriptor)
    }

    // do this before you quit!
    @Throws(IOException::class)
    private fun closeRenderer() {
        currentPage?.close()
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }

    private fun showPage(index: Int) {
        if (index < 0 || pdfRenderer.pageCount <= index) {
            //index not valid
            return
        }
        currPageNum = index
        pageImage.currPage = currPageNum
        //Update UI
        var pageNumTextm = findViewById<TextView>(R.id.currPage)
        pageNumTextm.setText("Page ${currPageNum+1}/${pdfRenderer.pageCount}")

        // Close the current page before opening another one.
        currentPage?.close()

        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index)

        if (currentPage != null) {
            // Important: the destination bitmap must be ARGB (not RGB).
            val bitmap = Bitmap.createBitmap(currentPage!!.getWidth(), currentPage!!.getHeight(), Bitmap.Config.ARGB_8888)

            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Display the page
            pageImage.setImage(bitmap)
        }
    }
}



