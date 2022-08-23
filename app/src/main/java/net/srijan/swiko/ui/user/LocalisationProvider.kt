package net.srijan.swiko.ui.user

import android.content.Context
import net.srijan.swiko.R

interface LocalisationProvider {
    fun getSignInError(): String {
        return ""
    }
    fun getSignUpError(): String {
        return ""
    }
    fun getProfileFetchError(): String {
        return ""
    }
    fun getEditProfileError(): String {
        return ""
    }
}

class AndroidLocalisationProvider(val context: Context) : LocalisationProvider {
    override fun getSignInError() = context.getString(R.string.sign_in_error)
    override fun getSignUpError() = context.getString(R.string.sign_up_error)
    override fun getProfileFetchError() = context.getString(R.string.error_profile_fetch)
    override fun getEditProfileError() = context.getString(R.string.error_edit_profile)
}
