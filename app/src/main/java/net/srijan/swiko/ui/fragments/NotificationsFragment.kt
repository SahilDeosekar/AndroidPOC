package net.srijan.swiko.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.srijan.swiko.R
import net.srijan.swiko.databinding.FragmentGamesBinding
import net.srijan.swiko.databinding.FragmentNotificationsBinding
import net.srijan.swiko.navigate

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileBtNoti.setOnClickListener {
            navigate(this, "bottomTab/notification/resetPassword")
//            findNavController().navigate("myapp://notifications/notification-fragment-2".toUri())
        }
    }
}