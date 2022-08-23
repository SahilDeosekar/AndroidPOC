package net.srijan.swiko.ui.user.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import net.srijan.swiko.*
import net.srijan.swiko.databinding.FragmentProfileBinding
import net.srijan.swiko.model.response.profile.GetUserResponseModel
import net.srijan.swiko.ui.DateFormatConstants
import net.srijan.swiko.ui.MainActivity
import net.srijan.swiko.ui.user.HomeFragment
import net.srijan.swiko.utility.LoadingScreen
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    lateinit var appConfig: AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfig = AppConfig(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            profileViewModel.uiMessages.collect { profileUIMessage ->
                when (profileUIMessage) {
                    ProfileUiMessages.FirstNameEmpty -> {
                        binding.profileFirstNameTil.isErrorEnabled = true
                        binding.profileFirstNameTil.error = "Please enter first name"
                    }
                    ProfileUiMessages.LastNameEmpty -> {
                        binding.profileLastNameTil.isErrorEnabled = true
                        binding.profileLastNameTil.error = "Please enter last name"
                    }
                    ProfileUiMessages.DateOfBirthEmpty -> {
                        binding.profileDobTil.isErrorEnabled = true
                        binding.profileDobTil.error = "Please enter date of birth"
                    }
                    ProfileUiMessages.GenderEmpty -> {
                        binding.profileGenderErrorTv.visibility = View.VISIBLE
                    }
                    ProfileUiMessages.ShowLoading -> {
                        LoadingScreen.displayLoadingWithText(requireContext(), "Please wait...",false)
                    }
                    ProfileUiMessages.HideLoading -> {
                        LoadingScreen.hideLoading()
                    }
                    is ProfileUiMessages.ProfileFetchSuccess -> {
                        profileUIMessage.profile?.let { userProfile ->
                            bindToView(userProfile)
                        }
                    }
                    is ProfileUiMessages.ProfileFetchError -> {
                        showToast(profileUIMessage.error)
                    }
                    is ProfileUiMessages.ProfileEditSuccess -> {
                        requireActivity().hideKeyboard()
                        showToast(profileUIMessage.message)
                    }
                }
            }
        }

//        lifecycleScope.launchWhenResumed {
//            profileViewModel.fetchProfile()
//        }

        binding.profileDobTiet.setOnClickListener {
            onClickDatePicker()
        }

        binding.btnEditProfile.setOnClickListener {
            binding.profileGenderErrorTv.visibility = View.GONE
            requireActivity().hideKeyboard()
            lifecycleScope.launch {
                profileViewModel.authenticateForm(
                    binding.profileFirstNameTiet.text.toString(),
                    binding.profileLastNameTiet.text.toString(),
                    binding.profileDobTiet.text.toString(),
                    profileViewModel.selectedGender,
                    binding.emailTv.text.toString()
                )
            }
        }

        binding.profileEditIv.setOnClickListener {
            (requireActivity() as MainActivity).uploadProfilePicture {
                Glide.with(requireContext()).load(it).into(binding.profileSwikoIv)
                profileViewModel.updateImageUrl(it)
            }
        }

        binding.profileGenderRadioGroup.setOnCheckedChangeListener { _, id ->
            when (resources.getResourceEntryName(id)) {
                "profile_male_radio" -> profileViewModel.updateGender("Male")
                "profile_female_radio" -> profileViewModel.updateGender("Female")
                else -> profileViewModel.updateGender("Non-binary")
            }
        }

        binding.profileFirstNameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.profileFirstNameTil.error = null
                binding.profileFirstNameTil.isErrorEnabled = false
            }
        }

        binding.profileLastNameTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.profileLastNameTil.error = null
                binding.profileLastNameTil.isErrorEnabled = false
            }
        }

        binding.profileDobTiet.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                binding.profileDobTil.error = null
                binding.profileDobTil.isErrorEnabled = false
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
                binding.profileDobTiet.setText(selectedDateString)

            },
            year, month, day
        )
        dateDialog.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dateDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun bindToView(profile: GetUserResponseModel.SwikoUser) {
        binding.emailTv.text = profile.email
        binding.profileFirstNameTiet.setText(profile.firstName)
        binding.profileLastNameTiet.setText(profile.lastName)
        try {
            val dob = profile.dob?.convertDate(DateFormatConstants.formatTnZLong, DateFormatConstants.yearMonthDay)
            binding.profileDobTiet.setText(dob)
        } catch (e: Exception) {
            Log.e("Date Conversion Exception", "$e")
        }
        when (profile.gender?.lowercase()) {
            "male" -> {
                binding.profileGenderRadioGroup.check(R.id.profile_male_radio)
            }
            "female" -> {
                binding.profileGenderRadioGroup.check(R.id.profile_female_radio)
            }
            else -> {
                binding.profileGenderRadioGroup.check(R.id.profile_non_binary_radio)
            }
        }
        profile.imageUrl?.let {
            if (it.isNotBlank()) {
                Glide.with(requireContext()).load(it).into(binding.profileSwikoIv)
                profileViewModel.updateImageUrl(it)
            }
        }
    }
}