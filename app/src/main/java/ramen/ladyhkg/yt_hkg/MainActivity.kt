package ramen.ladyhkg.yt_hkg

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 處理剪貼簿的內容
        handleClipboard()

        // 處理分享的內容
        handleShareIntent(intent)

        // 結束 Activity，讓應用在背景運行
        finish()
    }

    private fun handleClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip

        clipData?.let {
            if (it.itemCount > 0) {
                val sharedText = it.getItemAt(0).text?.toString()
                sharedText?.let { text ->
                    val modifiedUrl = modifyYouTubeUrl(text)
                    if (modifiedUrl != null) {
                        copyToClipboard(modifiedUrl)
                        showToast(text, modifiedUrl)
                    }
                }
            }
        }
    }

    private fun handleShareIntent(intent: Intent) {
        if (Intent.ACTION_SEND == intent.action && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                val modifiedUrl = modifyYouTubeUrl(sharedText)
                if (modifiedUrl != null) {
                    copyToClipboard(modifiedUrl)
                    showToast(sharedText, modifiedUrl)
                }
            }
        }
    }

    private fun modifyYouTubeUrl(url: String): String? {
        return when {
            url.startsWith("https://www.youtube.com/shorts/") -> {
                url.replace("https://www.youtube.com/shorts/", "https://youtu.be/").replace("?feature=share", "").replace("?si=", "")
            }
            url.startsWith("https://youtube.com/shorts/") -> {
                url.replace("https://youtube.com/shorts/", "https://youtu.be/").replace("?feature=share", "").replace("?si=", "")
            }
            url.startsWith("https://m.youtube.com/") -> {
                url.replace("https://m.youtube.com/", "https://youtu.be/").replace("?feature=share", "").replace("?si=", "")
            }
            url.startsWith("https://youtube.com/watch?v=") -> {
                val videoId = url.substringAfter("v=").substringBefore("?").takeIf { it.isNotEmpty() }
                videoId?.let { "https://youtu.be/$it" }
            }
            url.startsWith("https://youtu.be/") -> {
                val videoId = url.substringAfter("be/").substringBefore("?si=").takeIf { it.isNotEmpty() }
                videoId?.let { "https://youtu.be/$it" }
            }
            else -> null
        }
    }

    private fun copyToClipboard(url: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("YouTube Short URL", url)
        clipboard.setPrimaryClip(clip)
    }

    private fun showToast(originalText: String, modifiedUrl: String) {
        val message = """
        成功將
        $originalText
        
        修正為
        $modifiedUrl
        
        並複製到你嘅剪貼簿
    """.trimIndent()
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}