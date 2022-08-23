package net.srijan.swiko.ui.user.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import net.srijan.swiko.R
import net.srijan.swiko.databinding.FragmentResetPasswordBinding
import net.srijan.swiko.ui.user.auth.SignInFragment


class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()



    companion object {
        private const val EMAIL = "email"
        private const val OTP = "otp"

        fun newInstance(email: String, otp:String) = ResetPasswordFragment().apply {
            arguments = Bundle().apply {
                putString(EMAIL, email)
                putString(OTP, otp)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

            newPasswordTiet.addTextChangedListener {
                if (!it.isNullOrBlank()) {
                    newPasswordTil.error = null
                    newPasswordTil.isErrorEnabled = false
                }
            }

            reEnterPasswordTiet.addTextChangedListener{
                if (!it.isNullOrBlank()) {
                    reenterPasswordTil.error = null
                    reenterPasswordTil.isErrorEnabled = false
                }
            }

            updatePasswordBt.setOnClickListener{

                val newPassword = newPasswordTiet.text.toString()
                val reEnteredPassword = reEnterPasswordTiet.text.toString()

                    lifecycleScope.launch{
                        forgotPasswordViewModel.updatePassword(
                            newPassword,reEnteredPassword,
                            arguments?.getString(EMAIL) ?: ""
                        )
                    }

            }

        }

        lifecycleScope.launchWhenResumed {
            forgotPasswordViewModel.uiMessages.collect { signInUiMessage ->
                when (signInUiMessage) {
                    ForgotPasswordUiMessages.HideLoading -> {
                        binding.updatePasswordBt.isVisible = true
                        binding.progressBar.isVisible = false
                    }
                    ForgotPasswordUiMessages.PasswordMisMatch -> {
                        Toast.makeText(requireContext(), "Passwords must be similar", Toast.LENGTH_SHORT).show()
                    }
                    ForgotPasswordUiMessages.ShowLoading -> {
                        binding.updatePasswordBt.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    ForgotPasswordUiMessages.PasswordUpdateSuccess -> {
                        findNavController().navigate(R.id.signInFragment)
                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                    }
                   is ForgotPasswordUiMessages.PasswordUpdateFailed -> {
                       Toast.makeText(requireContext(), signInUiMessage.error, Toast.LENGTH_SHORT).show()
                   }
                    ForgotPasswordUiMessages.NewPasswordIsEmpty -> {
                        binding.newPasswordTil.error = "Can't be blank"
                        binding.newPasswordTil.isErrorEnabled = true
                    }
                    ForgotPasswordUiMessages.ReenteredPasswordIsEmpty -> {
                        binding.reenterPasswordTil.error = "Can't be blank"
                        binding.reenterPasswordTil.isErrorEnabled = true
                    }
                    ForgotPasswordUiMessages.BothPasswordsAreEmpty -> {
                        binding.newPasswordTil.error = "Can't be blank"
                        binding.newPasswordTil.isErrorEnabled = true

                        binding.reenterPasswordTil.error = "Can't be blank"
                        binding.reenterPasswordTil.isErrorEnabled = true
                    }
                    else -> {}
                }
            }
        }

    }

}