package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
    }

}