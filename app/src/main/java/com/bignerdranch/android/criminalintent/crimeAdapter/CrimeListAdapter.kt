package com.bignerdranch.android.criminalintent.crimeAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.databinding.ListItemCrimeBinding
import java.util.UUID
// The goal, remember, is that when the user presses an
//item in the list of crimes, they will navigate to the detail screen for that
//crime.
//You could swap out the toast printing code for some code that navigates the
//user to the detail screen. However, that would tightly couple
//CrimeHolder and CrimeListAdapter to being used in
//CrimeListFragment. That is not a good approach for building a
//maintainable codebase.
//A better approach would be to pass a lambda expression into the
//CrimeHolder and CrimeListAdapter classes to allow whatever
//class creates instances of those classes to configure what happens when the
//user presses a list item. That is the approach you will take here.
class CrimeListAdapter(private val crimes: List<Crime>,
                       private val onCrimeClicked:  (crimeId: UUID) -> Unit
): RecyclerView.Adapter<CrimeListAdapter.CrimeHolder>() {
    //onCreateViewHolder(…) is responsible for creating a
    //binding to display, wrapping the view in a view holder, and returning the
    //result. In this case, you inflate and bind a ListItemCrimeBinding and
    //pass the resulting binding to a new instance of CrimeHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val view = when (viewType) {
//            Crime.NOT_POLICE_TYPE -> CrimeHolder(ListItemCrimeBinding.inflate(inflater, parent, false))
//            Crime.POLICE_TYPE -> CrimeRequiresPoliceHolder(ListItemCrimeRequiresPoliceBinding.inflate(inflater, parent, false))
//            else -> CrimeHolder(ListItemCrimeBinding.inflate(inflater, parent, false))
//        }

        val view = CrimeHolder(ListItemCrimeBinding.inflate(inflater, parent, false))
        return view
    }

    //onBindViewHolder(…) is responsible for populating a
    //given holder with the crime from a given position. In this case, you get
    //the crime from the crime list at the requested position. You then use the title
    //and date from that crime to set the text in the corresponding text views.
    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        //RecyclerView.ViewHolder
        val crime = crimes[position]
//        when (holder) {
//            is CrimeHolder -> holder.bind(crime)
//            is CrimeRequiresPoliceHolder -> holder.bind(crime)
//        }
        holder.bind(crime, onCrimeClicked)
    }

//    override fun getItemViewType(position: Int): Int {
//        return crimes[position].requiresPolice
//    }

    class CrimeHolder(private val binding: ListItemCrimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit) {
            binding.crimeTitle.text = crime.title
            binding.crimeDate.text = crime.date.toString()

            binding.root.setOnClickListener {
                onCrimeClicked(crime.id)
            }

            binding.icCrimeSolved.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun getItemCount() = crimes.size

//    private class CrimeRequiresPoliceHolder(private val binding: ListItemCrimeRequiresPoliceBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(crime: Crime) {
//            binding.crimeTitle.text = crime.title
//            binding.crimeDate.text = crime.date.toString()
//
//            binding.root.setOnClickListener {
//                Toast.makeText(
//                    binding.root.context,
//                    "${crime.title} clicked!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }


}

