package net.srijan.swiko

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.getColorCompat(@ColorRes colorResId: Int) = ContextCompat.getColor(this, colorResId)

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


fun Context.openUrl(url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(this, "URL is empty", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(intent)
    } catch (t: Throwable) {
        Toast.makeText(this, "Can not open \"$url\"", Toast.LENGTH_SHORT).show()
    }
}

private val emailAddressRegex = Regex(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)
fun String?.isValidEmail() = this != null
        && this.matches(emailAddressRegex)

fun String.convertDate(inputFormat: String, outputFormat: String): String {
    val stringToDate = SimpleDateFormat(inputFormat, Locale.ENGLISH).parse(this)
    return SimpleDateFormat(outputFormat, Locale.ENGLISH).format(stringToDate)
}

fun String.formatDate(outputFormat: String): String {
    val stringToDate = SimpleDateFormat(outputFormat, Locale.ENGLISH).parse(this)
    return outputFormat.format(stringToDate)
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "swiko")
