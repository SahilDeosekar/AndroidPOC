package net.srijan.swiko.ui.user.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.srijan.swiko.*
import net.srijan.swiko.databinding.FragmentSignInBinding
import net.srijan.swiko.ui.user.HomeFragment
import net.srijan.swiko.ui.user.forgotpassword.ForgetPasswordFragment


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val userViewModel: UserViewModel by viewModels()

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
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usernameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.usernameTil.error = null
                binding.usernameTil.isErrorEnabled = false
            }
        }

        binding.passwordTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.passwordTil.error = null
                binding.passwordTil.isErrorEnabled = false
            }
        }

        lifecycleScope.launchWhenResumed {
            userViewModel.uiMessages.collect { signInUiMessage ->
                when (signInUiMessage) {
                    SignInUiMessages.PasswordEmpty -> {
                        binding.passwordTil.isErrorEnabled = true
                        binding.passwordTil.error = "Please enter password"
                    }
                    SignInUiMessages.EmailEmpty -> {
                        binding.usernameTil.isErrorEnabled = true
                        binding.usernameTil.error = "Please enter email address"
                    }
                    SignInUiMessages.EmailInvalid -> {
                        binding.usernameTil.isErrorEnabled = true
                        binding.usernameTil.error = "Please enter a valid email"
                    }
                    is SignInUiMessages.SignInError -> {
                        showToast(signInUiMessage.error)
                    }
                    is SignInUiMessages.SignInSuccess -> {
                        showToast("Welcome ${signInUiMessage.username}")
                        findNavController().navigate(R.id.mainFragment)
                    }
                    SignInUiMessages.HideLoading -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signinBt.visibility = View.VISIBLE
                    }
                    SignInUiMessages.ShowLoading -> {
                        binding.signinBt.visibility = View.INVISIBLE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.signinBt.setOnClickListener {
            requireActivity().hideKeyboard()
            lifecycleScope.launch {
                userViewModel.authenticateUser(binding.usernameTiet.text.toString(),
                    binding.passwordTiet.text.toString())
            }
        }

        binding.forgetPasswordTv.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.signUpTv.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}