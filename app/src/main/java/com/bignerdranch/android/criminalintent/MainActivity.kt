package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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