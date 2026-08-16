package com.qrtoolkit.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.qrtoolkit.app.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentQrBitmap: Bitmap? = null

    // Launcher for the ZXing embedded scanner
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            binding.textScanResult.text = result.contents
            binding.textScanResult.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupGenerateActions()
        setupScanActions()
    }

    // ---------- Tabs ----------

    private fun setupTabs() {
        binding.tabGenerate.setOnClickListener { showGenerateTab() }
        binding.tabScan.setOnClickListener { showScanTab() }
    }

    private fun showGenerateTab() {
        binding.panelGenerate.visibility = android.view.View.VISIBLE
        binding.panelScan.visibility = android.view.View.GONE
        binding.tabGenerate.setBackgroundResource(R.drawable.bg_segment_selected)
        binding.tabGenerate.setTextColor(getColor(R.color.bg_dark))
        binding.tabScan.background = null
        binding.tabScan.setTextColor(getColor(R.color.text_secondary))
    }

    private fun showScanTab() {
        binding.panelGenerate.visibility = android.view.View.GONE
        binding.panelScan.visibility = android.view.View.VISIBLE
        binding.tabScan.setBackgroundResource(R.drawable.bg_segment_selected)
        binding.tabScan.setTextColor(getColor(R.color.bg_dark))
        binding.tabGenerate.background = null
        binding.tabGenerate.setTextColor(getColor(R.color.text_secondary))
    }

    // ---------- Generate ----------

    private fun setupGenerateActions() {
        binding.btnGenerate.setOnClickListener {
            val text = binding.editInput.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.empty_input_toast, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bitmap = generateQrBitmap(text, 800, 800)
            currentQrBitmap = bitmap
            binding.imageQr.setImageBitmap(bitmap)
        }

        binding.btnSave.setOnClickListener {
            val bitmap = currentQrBitmap
            if (bitmap == null) {
                Toast.makeText(this, R.string.empty_input_toast, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveBitmapToGallery(bitmap)
        }

        binding.btnShare.setOnClickListener {
            val bitmap = currentQrBitmap
            if (bitmap == null) {
                Toast.makeText(this, R.string.empty_input_toast, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            shareBitmap(bitmap)
        }
    }

    /** Encodes [text] into a QR code bitmap using ZXing. */
    private fun generateQrBitmap(text: String, width: Int, height: Int): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "qr_${System.currentTimeMillis()}.png"
        val resolver = contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/QRToolkit")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show()
            }
        } else {
            val dir = getExternalFilesDir(null)
            val file = File(dir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(cacheDir, "shared_qr")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_share.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(android.content.Intent.createChooser(shareIntent, getString(R.string.btn_share)))
    }

    // ---------- Scan ----------

    private fun setupScanActions() {
        binding.btnScan.setOnClickListener {
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("")
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            scanLauncher.launch(options)
        }

        binding.btnCopy.setOnClickListener {
            val text = binding.textScanResult.text.toString()
            if (text.isEmpty() || text == getString(R.string.empty_scan)) {
                return@setOnClickListener
            }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("QR result", text))
            Toast.makeText(this, R.string.copied_toast, Toast.LENGTH_SHORT).show()
        }
    }
}
