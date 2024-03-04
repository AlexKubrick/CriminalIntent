package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.criminalintent.crimeAdapter.Crime
import com.bignerdranch.android.criminalintent.crimeAdapter.CrimeListAdapter
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeListFragment: Fragment() {
    private val crimeListViewModel: CrimeListViewModel by viewModels()
    private var _binding: FragmentCrimeListBinding? = null
    //private var job: Job? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        //The first parameter is the same
        //LayoutInflator you used before.
        //
        //The second parameter is your view’s
        //parent, which is usually needed to configure the views properly.
        //
        //The third parameter tells the layout inflater whether to immediately add the
        //inflated view to the view’s parent. You pass in false because the
        //fragment’s view will be hosted in the activity’s container view. The
        //fragment’s view does not need to be added to the parent view immediately –
        //the activity will handle adding the view later.

        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        //Set a LinearLayoutManager as the
        //LayoutManager for your RecyclerView. It will position the items in
        //the list vertically, one after the other, like a LinearLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeListViewModel.crimes.collect { crimes ->
                    binding.crimeRecyclerView.adapter =
                        CrimeListAdapter(crimes) { crimeId ->
                            findNavController().navigate(
                                CrimeListFragmentDirections.showCrimeDetail(crimeId)
                            )
                        }
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        job = viewLifecycleOwner.lifecycleScope.launch {
//            val crimes = crimeListViewModel.loadCrimes()
//            binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
//
//        }
//    }

//    override fun onStop() {
//        super.onStop()
//        job?.cancel()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                showNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //represents the Fragment's View lifecycle
    // Namely, the lifecycle of the Fragment's View is:
    //created after onViewStateRestored(Bundle)
    //started after onStart()
    //resumed after onResume()
    //paused before onPause()
    //stopped before onStop()
    //destroyed before onDestroyView()

    //CoroutineScope tied to this LifecycleOwner's Lifecycle.
    private fun showNewCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newCrime = Crime(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false
            )
            crimeListViewModel.addCrime(newCrime)
            findNavController().navigate(
                CrimeListFragmentDirections.showCrimeDetail(newCrime.id)
            )
        }
    }



}