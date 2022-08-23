package net.srijan.swiko.ui

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import net.srijan.swiko.AppConfig
import net.srijan.swiko.dataStore
import net.srijan.swiko.network.ApiHelper
import net.srijan.swiko.ui.user.AndroidLocalisationProvider
import net.srijan.swiko.ui.actor.TheActor
import net.srijan.swiko.ui.user.auth.UserState
import net.srijan.swiko.ui.user.auth.userActor
import net.srijan.swiko.ui.user.forgotpassword.ResetPasswordState
import net.srijan.swiko.ui.user.forgotpassword.resetPasswordActor
import net.srijan.swiko.ui.actor.*
import net.srijan.swiko.ui.forceupdate.AppState
import net.srijan.swiko.ui.forceupdate.appActor
import net.srijan.swiko.ui.user.auth.TokenCache

class SwikoApp : Application() {

    private val scope = CoroutineScope(Dispatchers.Default + Job())


    override fun onCreate() {
        super.onCreate()
        val localisationProvider = AndroidLocalisationProvider(this)
        val appConfig = AppConfig(this.dataStore)
        val tokenCache = object : TokenCache {
            override suspend fun readToken() = appConfig.userToken.firstOrNull()


            override suspend fun saveToken(token: String) {
                appConfig.setUserToken(token)
            }

        }
        val scopeReference = ScopeReference(scope)

        with(scopeReference) {
            startActors(localisationProvider, tokenCache)
        }


    }

    fun ScopeReference.startActors(
        localisationProvider: AndroidLocalisationProvider,
        appConfig: TokenCache
    ) {

        TheActor.run {
            userActor(
                localisationProvider,
                ApiHelper.userService,
                appConfig
            ).toActor(UserState.NotSignedIn)
        }.start()

        TheActor.run {
            resetPasswordActor(ApiHelper.resetPasswordService).toActor(ResetPasswordState.OtpNotFetched)
        }.start()

        TheActor.run {
            appActor(ApiHelper.appVersionService).toActor(AppState.AppVersionNotFetched)
        }.start()
    }
}