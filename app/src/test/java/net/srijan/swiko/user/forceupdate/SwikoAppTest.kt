package net.srijan.swiko.user.forceupdate

import app.cash.turbine.test
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.srijan.swiko.BuildConfig
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.forceupdate.AppState
import net.srijan.swiko.ui.forceupdate.AppVersionRequest
import net.srijan.swiko.ui.forceupdate.GetAppVersionState
import net.srijan.swiko.ui.forceupdate.AppVersionUiMessages
import net.srijan.swiko.ui.forceupdate.SplashViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class SwikoAppTest {

    val appVersion = BuildConfig.VERSION_NAME

    val appVersionDummy = "2.1"

    @Test
    fun isTheAppVersionSame() = runTest {
        val splashViewModel = SplashViewModel()

        launch {
            AppStream.messages.test {
                assertEquals(AppVersionRequest,awaitItem())
                val message = awaitItem()
                if (message is GetAppVersionState) {
                    message.state.complete(AppState.AppVersion(appVersion))
                }
            }
        }
        advanceUntilIdle()

        splashViewModel.uiMessages.test {
            splashViewModel.getAppVersion()
            assertEquals(AppVersionUiMessages.AppVersion(appVersion), awaitItem())
            cancelAndConsumeRemainingEvents()
        }

    }

}