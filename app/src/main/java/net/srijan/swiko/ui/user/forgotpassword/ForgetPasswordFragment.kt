package net.srijan.swiko.ui.user.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.srijan.swiko.databinding.FragmentForgetPasswordBinding
import net.srijan.swiko.hideKeyboard

class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.forgotUsernameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.forgotUsernameTil.error = null
                binding.forgotUsernameTil.isErrorEnabled = false
            }
        }

        binding.enterOtpTiet.addTextChangedListener{
            if (!it.isNullOrBlank()) {
                binding.enterOtpTil.error = null
                binding.enterOtpTil.isErrorEnabled = false
            }
        }

        lifecycleScope.launchWhenResumed {
            forgotPasswordViewModel.uiMessages.collect { signInUiMessage ->
                when (signInUiMessage) {
                    ForgotPasswordUiMessages.EmailEmpty -> {
                        binding.forgotUsernameTil.isErrorEnabled = true
                        binding.forgotUsernameTil.error = "Please enter an email id"
                    }
                    ForgotPasswordUiMessages.InvalidEmail -> {
                        binding.forgotUsernameTil.isErrorEnabled = true
                        binding.forgotUsernameTil.error = "Please enter a valid email id"
                    }
                    is ForgotPasswordUiMessages.OtpFailure -> {
                        Toast.makeText(requireContext(), signInUiMessage.error, Toast.LENGTH_SHORT).show()
                    }
                    is ForgotPasswordUiMessages.OtpSuccess -> {
                        binding.enterEmailLl.isVisible = false
                        binding.enterOtpLl.isVisible = true

                    }
                    ForgotPasswordUiMessages.ShowLoading -> {
                        binding.enterOtpBt.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    ForgotPasswordUiMessages.HideLoading -> {
                        binding.progressBar.visibility = View.GONE
                        binding.enterOtpBt.visibility = View.VISIBLE
                    }
                    ForgotPasswordUiMessages.InvalidOtp -> {
                        binding.enterOtpTil.error = "OTP can't be blank"
                        binding.enterOtpTil.isErrorEnabled = true
                    }
                    ForgotPasswordUiMessages.OtpVerificationFailed -> {

                    }
                    ForgotPasswordUiMessages.OtpVerifiedSuccessfully -> {
//                        requireActivity().replaceFragment(
//                            ResetPasswordFragment.newInstance(
//                                binding.forgotUsernameTiet.text.toString(),
//                                binding.enterOtpTiet.text.toString()
//                            )
//                        )

                    }
                    else -> {}
                }
            }
        }

        binding.forgotPasswordBt.setOnClickListener {
            requireActivity().hideKeyboard()
            lifecycleScope.launch {
                forgotPasswordViewModel.submitEmail(binding.forgotUsernameTiet.text.toString())
            }
        }

        binding.enterOtpBt.setOnClickListener{
            requireActivity().hideKeyboard()
            lifecycleScope.launch {
                forgotPasswordViewModel.verifyOtp(
                    binding.enterOtpTiet.text.toString(),
                    binding.forgotUsernameTiet.text.toString()
                )
            }
        }

    }

}