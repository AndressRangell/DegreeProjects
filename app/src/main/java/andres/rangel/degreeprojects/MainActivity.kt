package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityMainBinding
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.bottomAppBar)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setupWithNavController(
            binding.navigationHostFragment.getFragment<NavHostFragment>().navController
        )

        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }

        binding.navigationHostFragment.getFragment<NavHostFragment>().navController
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment, R.id.projectListFragment, R.id.settingsFragment, R.id.profileFragment -> {
                        binding.bottomAppBar.visibility = View.VISIBLE
                        binding.fabNewProject.visibility = View.VISIBLE
                    }else -> {
                        binding.bottomAppBar.visibility = View.GONE
                        binding.fabNewProject.visibility = View.GONE
                    }
                }
            }

        binding.fabNewProject.setOnClickListener {
            binding.navigationHostFragment.getFragment<NavHostFragment>().navController
                .navigate(R.id.newProjectFragment)
        }
    }
}