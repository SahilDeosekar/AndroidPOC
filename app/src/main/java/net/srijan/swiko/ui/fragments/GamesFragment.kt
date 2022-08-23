package net.srijan.swiko.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import net.srijan.swiko.databinding.FragmentGamesBinding
import net.srijan.swiko.navigate
import net.srijan.swiko.stream.AppStream
import net.srijan.swiko.ui.MainViewModel
import net.srijan.swiko.ui.NavigationUIMessages
import net.srijan.swiko.ui.actor.route.Navigate

class GamesFragment : Fragment() {
    private lateinit var binding: FragmentGamesBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileBt.setOnClickListener {
            lifecycleScope.launch {
                AppStream.send(Navigate(this@GamesFragment, "bottomTab/home/profile"))
                mainViewModel.getNavigationState()
            }
        }
    }
}