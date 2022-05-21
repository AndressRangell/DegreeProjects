package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        Glide.with(this)
            .load("https://www.uts.edu.co/sitio/wp-content/uploads/2020/01/UTS_8714.jpg")
            .into(binding.imgHeader)

        Glide.with(this)
            .load("https://i0.wp.com/codigoespagueti.com/wp-content/uploads/2022/04/luffy-imagen-autor-slam-dunk.jpg?fit=1280%2C720&quality=80&ssl=1")
            .into(binding.circleImageView)

    }

}