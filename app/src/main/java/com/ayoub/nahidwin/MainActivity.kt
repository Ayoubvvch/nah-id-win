package com.ayoub.nahidwin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewAssetLoader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: FrameLayout
    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private var pendingApkFile: File? = null

    private val installPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.canRequestPackageInstalls()) {
                pendingApkFile?.let { file ->
                    launchInstallIntent(file)
                }
            } else {
                Toast.makeText(this, "Permission to install update was denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Automatic Immersive Sticky Fullscreen on startup
        setupImmersiveFullscreen()

        // Programmatic layout to eliminate XML inflation latency for instant startup
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#020204"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        fullscreenContainer = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        webView = WebView(this).apply {
            setBackgroundColor(Color.parseColor("#020204"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        rootLayout.addView(webView)
        rootLayout.addView(fullscreenContainer)
        setContentView(rootLayout)

        setupWebView()
        setupBackNavigation()
    }

    private fun setupImmersiveFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        // Ensure display cutouts (notches) are extended for maximum immersion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = false
            allowContentAccess = false
            cacheMode = WebSettings.LOAD_DEFAULT
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            displayZoomControls = false
        }

        // Bridge for In-App Updater
        webView.addJavascriptInterface(AndroidUpdaterBridge(), "AndroidUpdaterBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                request?.url?.let { uri ->
                    val response = assetLoader.shouldInterceptRequest(uri)
                    if (response != null) return response
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                if (url.startsWith("https://appassets.androidplatform.net/")) {
                    return false
                }
                return try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    onHideCustomView()
                    return
                }
                customView = view
                customViewCallback = callback

                webView.visibility = View.GONE
                fullscreenContainer.visibility = View.VISIBLE
                fullscreenContainer.addView(view)
                setupImmersiveFullscreen()
            }

            override fun onHideCustomView() {
                fullscreenContainer.visibility = View.GONE
                fullscreenContainer.removeAllViews()
                customView = null
                customViewCallback?.onCustomViewHidden()
                webView.visibility = View.VISIBLE
                setupImmersiveFullscreen()
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (customView != null) {
                    webView.webChromeClient?.onHideCustomView()
                } else if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // In-App APK Downloader & Installer Bridge
    inner class AndroidUpdaterBridge {
        @JavascriptInterface
        fun startDownloadAndInstall(apkUrl: String, versionName: String) {
            runOnUiThread {
                downloadAndInstallUpdate(apkUrl, versionName)
            }
        }
    }

    private fun downloadAndInstallUpdate(apkUrl: String, versionName: String) {
        Thread {
            try {
                var currentUrl = apkUrl
                var connection: HttpURLConnection
                var redirects = 0

                // Follow redirects (GitHub Releases redirect to AWS S3 CDN)
                while (true) {
                    val url = URL(currentUrl)
                    connection = url.openConnection() as HttpURLConnection
                    connection.instanceFollowRedirects = false
                    connection.connectTimeout = 15000
                    connection.readTimeout = 30000
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 NahIdWinUpdater")
                    connection.connect()

                    val status = connection.responseCode
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                        status == HttpURLConnection.HTTP_MOVED_PERM ||
                        status == HttpURLConnection.HTTP_SEE_OTHER ||
                        status == 307 || status == 308
                    ) {
                        currentUrl = connection.getHeaderField("Location")
                        connection.disconnect()
                        redirects++
                        if (redirects > 8) throw Exception("Too many redirects")
                        continue
                    }
                    break
                }

                val fileLength = connection.contentLength
                val inputStream: InputStream = connection.inputStream

                val downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: cacheDir
                val outputFile = File(downloadDir, "Nah-Id-Win-v${versionName}.apk")
                if (outputFile.exists()) {
                    outputFile.delete()
                }

                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    val percent = if (fileLength > 0) ((totalBytesRead * 100) / fileLength).toInt() else 0
                    val loadedStr = formatFileSize(totalBytesRead)
                    val totalStr = if (fileLength > 0) formatFileSize(fileLength.toLong()) else "..."

                    runOnUiThread {
                        webView.evaluateJavascript(
                            "if(window.onUpdateDownloadProgress) window.onUpdateDownloadProgress($percent, '$loadedStr', '$totalStr');",
                            null
                        )
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                runOnUiThread {
                    webView.evaluateJavascript(
                        "if(window.onUpdateDownloadComplete) window.onUpdateDownloadComplete();",
                        null
                    )
                    promptInstall(outputFile)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    val msg = e.message?.replace("'", "\\'") ?: "Unknown error"
                    webView.evaluateJavascript(
                        "if(window.onUpdateDownloadError) window.onUpdateDownloadError('$msg');",
                        null
                    )
                    Toast.makeText(this@MainActivity, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun promptInstall(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                pendingApkFile = apkFile
                Toast.makeText(this, "Please allow Nah, I'd Win to install updates", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                installPermissionLauncher.launch(intent)
                return
            }
        }
        launchInstallIntent(apkFile)
    }

    private fun launchInstallIntent(apkFile: File) {
        try {
            val contentUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                apkFile
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(installIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to launch installer: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun formatFileSize(bytes: Long): String {
        val mb = bytes.toDouble() / (1024 * 1024)
        return String.format("%.1f MB", mb)
    }

    override fun onResume() {
        super.onResume()
        setupImmersiveFullscreen()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
