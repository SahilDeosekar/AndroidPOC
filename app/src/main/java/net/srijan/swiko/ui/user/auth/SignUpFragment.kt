package net.srijan.swiko.ui.user.auth

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import net.srijan.swiko.AppConfig
import net.srijan.swiko.R
import net.srijan.swiko.dataStore
import net.srijan.swiko.databinding.FragmentSignUpBinding
import net.srijan.swiko.hideKeyboard
import net.srijan.swiko.ui.MainActivity
import net.srijan.swiko.ui.user.HomeFragment
import java.util.*


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    lateinit var appConfig: AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfig = AppConfig(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.firstNameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.firstNameTil.error = null
                binding.firstNameTil.isErrorEnabled = false
            }
        }

        binding.lastNameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.lastNameTil.error = null
                binding.lastNameTil.isErrorEnabled = false
            }
        }

        binding.dobTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.dobTil.error = null
                binding.dobTil.isErrorEnabled = false
            }
        }

        binding.emailTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.emailTil.error = null
                binding.emailTil.isErrorEnabled = false
            }
        }

        binding.passwordTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.passwordTil.error = null
                binding.passwordTil.isErrorEnabled = false
            }
        }

        binding.confirmPasswordTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.confirmPasswordTil.error = null
                binding.confirmPasswordTil.isErrorEnabled = false
            }
        }

        lifecycleScope.launchWhenResumed {
            signUpViewModel.uiMessages.collect { signUpUiMessage ->
                when (signUpUiMessage) {
                    SignUpUiMessages.FirstNameEmpty -> {
                        binding.firstNameTil.isErrorEnabled = true
                        binding.firstNameTil.error = "Please enter first name"
                    }
                    SignUpUiMessages.LastNameEmpty -> {
                        binding.lastNameTil.isErrorEnabled = true
                        binding.lastNameTil.error = "Please enter last name"
                    }
                    SignUpUiMessages.DateOfBirthEmpty -> {
                        binding.dobTil.isErrorEnabled = true
                        binding.dobTil.error = "Please enter date of birth"
                    }
                    SignUpUiMessages.GenderEmpty -> {
                        binding.genderErrorTv.visibility = View.VISIBLE
                    }
                    SignUpUiMessages.EmailEmpty -> {
                        binding.emailTil.isErrorEnabled = true
                        binding.emailTil.error = "Please enter email"
                    }
                    SignUpUiMessages.EmailInvalid -> {
                        binding.emailTil.isErrorEnabled = true
                        binding.emailTil.error = "Please enter a valid email"
                    }
                    SignUpUiMessages.PasswordEmpty -> {
                        binding.passwordTil.isErrorEnabled = true
                        binding.passwordTil.error = "Please enter password"
                    }
                    SignUpUiMessages.PasswordMismatch -> {
                        binding.confirmPasswordTil.isErrorEnabled = true
                        binding.confirmPasswordTil.error = "Passwords do not match"
                    }
                    SignUpUiMessages.ShowLoading -> {
                        binding.btnSignUp.visibility = View.INVISIBLE
                        binding.signupProgressBar.visibility = View.VISIBLE
                    }
                    SignUpUiMessages.HideLoading -> {
                        binding.signupProgressBar.visibility = View.GONE
                        binding.btnSignUp.visibility = View.VISIBLE
                    }
                    is SignUpUiMessages.SignUpError -> {
                        showToast(signUpUiMessage.error)
                    }
                    is SignUpUiMessages.SignUpSuccess -> {
                        showToast("Welcome ${signUpUiMessage.email}")
                        findNavController().navigate(R.id.mainFragment)
                    }
                }
            }
        }

        binding.pickImageTv.setOnClickListener {
            (requireActivity() as MainActivity).uploadProfilePicture {
                Glide.with(requireContext()).load(it).into(binding.swikoIv)
                signUpViewModel.updateImageUrl(it)
            }
        }

        binding.btnSignUp.setOnClickListener {
            binding.genderErrorTv.visibility = View.GONE
            requireActivity().hideKeyboard()
            lifecycleScope.launch {
                signUpViewModel.authenticateForm(
                    binding.firstNameTiet.text.toString(),
                    binding.lastNameTiet.text.toString(),
                    binding.dobTiet.text.toString(),
                    signUpViewModel.selectedGender,
                    binding.emailTiet.text.toString(),
                    binding.passwordTiet.text.toString(),
                    binding.confirmPasswordTiet.text.toString()
                )
            }
        }

        binding.dobTiet.setOnClickListener {
            onClickDatePicker()
        }

        binding.genderRadioGroup.setOnCheckedChangeListener { _, id ->
            when (resources.getResourceEntryName(id)) {
                "male_radio" -> signUpViewModel.updateGender("Male")
                "female_radio" -> signUpViewModel.updateGender("Female")
                else -> signUpViewModel.updateGender("Non-binary")
            }
        }

    }

    private fun onClickDatePicker() {

        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dateDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                val selectedDateString = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                binding.dobTiet.setText(selectedDateString)

            },
            year, month, day
        )
        dateDialog.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dateDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}