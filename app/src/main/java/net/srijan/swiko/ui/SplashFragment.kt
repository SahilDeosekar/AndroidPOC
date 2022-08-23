package net.srijan.swiko.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import net.srijan.swiko.*
import net.srijan.swiko.databinding.FragmentSplashBinding
import java.util.HashMap
import net.srijan.swiko.ui.forceupdate.AppVersionUiMessages
import net.srijan.swiko.ui.forceupdate.ForceUpdateDialogFragment
import net.srijan.swiko.ui.forceupdate.SplashViewModel


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    private val splashViewModel: SplashViewModel by viewModels()

    private val forceUpdateDialog: ForceUpdateDialogFragment by lazy {
        ForceUpdateDialogFragment(childFragmentManager)
    }

    lateinit var appConfig: AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfig = AppConfig(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            splashViewModel.getAppVersion()
        }

        lifecycleScope.launch {

            splashViewModel.uiMessages.collect{ appVersionUiMessage ->

                when(appVersionUiMessage) {
                    is AppVersionUiMessages.AppVersion -> {
                        if (BuildConfig.VERSION_NAME == appVersionUiMessage.appVersion) {
                            val userToken = appConfig.userToken.firstOrNull()
                            if (userToken.isNullOrEmpty()) {
                                findNavController().navigate(R.id.signInFragment,null,
                                    NavOptions.Builder().setPopUpTo(R.id.splashFragment,true).build())
                            } else {
                                findNavController().navigate(R.id.mainFragment)
                            }
                        } else {
                            forceUpdateDialog.display()
                        }
                    }
                    AppVersionUiMessages.AppVersionError -> {
                        Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }

//    private fun navigate(directions: NavDirections) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            bounce()
//            initializeCloudinaryManager()
//            delay(1000)
//            findNavController().navigate(directions)
//        }
//    }

    private fun bounce() {
        val bounceAnim: Animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.bounce)
        binding.splashIv.startAnimation(bounceAnim)
    }

    private fun initializeCloudinaryManager() {
        val config: MutableMap<String, String> = HashMap()
        config[CloudinaryConstants.CLOUD_NAME] = CloudinaryConstants.CLOUD_NAME_VALUE
        config[CloudinaryConstants.API_KEY] = CloudinaryConstants.API_KEY_VALUE
        config[CloudinaryConstants.API_SECRET] = CloudinaryConstants.API_SECRET_VALUE
        try {
            MediaManager.init(requireContext(), config)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

}