package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bignerdranch.android.criminalintent.crimeAdapter.Crime
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.util.Date

//private const val TAG = "CrimeDetailFragment"
class CrimeDetailFragment : Fragment() {
    //    lateinit var crime: Crime


    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        crime = Crime(
//            id = UUID.randomUUID(),
//            title = "",
//            date = Date(),
//            isSolved = false
//        )
//        Log.d(TAG, "The crime ID is: ${args.crimeId}")
//
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Here, you add a listener that will be invoked whenever the text in the
        //EditText is changed. The lambda is invoked with four parameters, but
        //you only care about the first one, text. The text is provided as a
        //CharSequence, so to set the Crime’s title, you call toString() on it.
        //(The doOnTextChanged() function is actually a Kotlin extension
        //function on the EditText class. Do not forget to import it from the
        //androidx.core.widget package.)
        //When you are not using a parameter, like the remaining lambda parameters
        //here, you name it _. Lambda arguments named _ are ignored, which
        //removes unnecessary variables and can help keep your code tidy.

        // -------------------------------------------------------------------
        // chapter 11
        // Challenge: Formatting the Date
        // doc: https://developer.android.com/reference/android/icu/text/DateFormat
//        val date = crime.date
//        val formattedDate = DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(date)
        // в книге написано, что нужно использовать экземпляр класса android.text.format.DateFormat
        // [icu enhancement] ICU's replacement for DateFormat. Methods, fields, and other functionality specific to ICU are labeled '[icu]'.
        // верно ли я выполнила задание?


        // также на Стак Оверфлоу есть ответ: https://stackoverflow.com/questions/68518225/how-to-use-android-text-format-dateformat-with-kotlin
        // было бы интересно разобрать

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
//                crime = crime.copy(title = text.toString())
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

//            crimeDate.apply {
//                // chapter 11
//                // Challenge: Formatting the Date
//                text = formattedDate
//                isEnabled = false
//            }

            //The last change you need to make within this class is to set a listener on the
            //CheckBox that will update the isSolved property of the Crime, as
            //shown in Listing 9.10.
            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
//                crime = crime.copy(isSolved = isChecked)
                //а зачем копировать?

                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }

            }
        }

        // With the repeatOnLifecycle(…) function, you can execute coroutine
        //code while your fragment is in a specified lifecycle state.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }


        // Chapter 13. Challenge: No Untitled Crimes. Handle the back button event
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.crimeTitle.text.trim().isEmpty()) {
                Toast.makeText(context, "Please enter the title! ", Toast.LENGTH_LONG).show()
            } else {
                findNavController().popBackStack(R.id.crimeListFragment, false)
            }

        }


        binding.applyCrime.setOnClickListener {
            crimeDetailViewModel.updateCrimeInRepository()
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        // chapter 15. Challenge: Deleting Crimes

        binding.deleteCrime.setOnClickListener {
//            val crimeID =
//            val crime = crimeDetailViewModel.getOneCrime()
            viewLifecycleOwner.lifecycleScope.launch {
                crimeDetailViewModel.deleteCrimeById(args.crimeId)
            }
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    crimeDetailViewModel.crime.collect { crime ->
//                        crime?.let { crimeDetailViewModel.deleteCrime(it) }
//                    }
//                }
//            }
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()

            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }

            // chapter 14: Dialogs and DialogFragment. Challenge: More Dialogs
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }
            crimeSolved.isChecked = crime.isSolved
        }
    }
}
