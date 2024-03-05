package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            //  https://stackoverflow.com/questions/51173002/how-to-change-start-destination-of-a-navigation-graph-programmatically
//        lifecycleScope.launch {
//            CrimeRepository.get().getCrimes().collect {
//                if (it.isEmpty()) {
//                    findNavController(R.id.nav_graph).navigate(CrimeListFragmentDirections.showEmptyFragment())
//                }
//            }
//        }
        val navController = Navigation.findNavController(this, R.id.nav_graph)
        navController.navigate(CrimeListFragmentDirections.showCrimeDetail(UUID.randomUUID()))
//        findNavController(R.id.nav_graph).navigate(CrimeListFragmentDirections.showEmptyFragment())
            //CrimeListFragment()
        //CrimeDetailFragment()
//        if (savedInstanceState == null) {
//            val fragment = CrimeListFragment()
//
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.fragment_container, fragment, CRIME_FRAGMENT_TAG)
//                .commit()
//        }
    }

    companion object {
        private const val CRIME_FRAGMENT_TAG = "CrimeFragment"
    }
}