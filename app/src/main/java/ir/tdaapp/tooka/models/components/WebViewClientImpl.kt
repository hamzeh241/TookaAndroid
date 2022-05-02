package ir.tdaapp.tooka.models.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


class WebViewClientImpl(val activity:Activity): WebViewClient()  {

  override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
    val url = request?.url.toString()
    if (url.indexOf("razhashop.com") > -1)
      return false

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    activity.startActivity(intent)
    return true
  }
}