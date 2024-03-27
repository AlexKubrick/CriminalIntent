package com.bignerdranch.android.criminalintent

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
//import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bignerdranch.android.criminalintent.crimeAdapter.Crime
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Locale
import java.text.DateFormat
import java.util.*




class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private var photoName: String? = null

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { parseContactSelection(it) }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    //Chapter 18. Challenge: Localizing Dates

    val defaultLocale = Locale.getDefault()
    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL, defaultLocale)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupUI()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }


//         Chapter 13. Challenge: No Untitled Crimes. Handle the back button event
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            if (binding.crimeTitle.text.trim().isEmpty()) {
//                Toast.makeText(context, "Please enter the title! ", Toast.LENGTH_LONG).show()
//            } else {
//                findNavController().popBackStack(R.id.crimeListFragment, false)
//            }
//
//        }

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
    }

    private fun setupUI() {
        binding.crimeTitle.doOnTextChanged { text, _, _, _ ->
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(title = text.toString())
            }
        }


        binding.crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(isSolved = isChecked)
            }

        }

        // chapter 15. Challenge: Deleting Crimes
        binding.deleteCrime.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                crimeDetailViewModel.deleteCrimeById(args.crimeId)
            }
            activity?.onBackPressed()
        }

        binding.crimeSuspect.setOnClickListener {
            selectSuspect.launch(null)
        }

        binding.applyCrime.setOnClickListener {
            crimeDetailViewModel.updateCrimeInRepository()
        }

        val selectSuspectIntent = selectSuspect.contract.createIntent(
            requireContext(),
            null
        )
        binding.crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)


        binding.crimeCamera.setOnClickListener {
            photoName = "IMG_${Date()}.JPG"
            val photoFile = File(requireContext().applicationContext.filesDir,
                photoName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.bignerdranch.android.criminalintent.fileprovider",
                photoFile
            )

            takePhoto.launch(photoUri)
        }

//        val captureImageIntent = takePhoto.contract.createIntent(
//            requireContext(),
//            null
//        )
//        binding.crimeCamera.isEnabled = canResolveIntent(captureImageIntent)
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


            //Chapter 18. Challenge: Localizing Dates
            crimeDate.text = formattedDate.format(Date())

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

            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject)
                    )
                }

                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }
            updatePhoto(crime.photoFileName)


            // chapter 16. Challenge: Another Implicit Intent. Button Listener
            callSuspect.setOnClickListener {
                requestPermissions()
                if (ContextCompat.checkSelfPermission(
                        it.context,
                        readContacts
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startCall()
                }
            }

            // chapter 17. Challenge: Detail Display
            crimePhoto.setOnClickListener {

                val picturePickerDialog = crime.photoFileName?.let { photoFileName ->
                    PicturePickerDialog.newInstPicturePicker(photoFileName)
                }

                if (picturePickerDialog != null) {
                    fragmentManager?.let { it1 -> picturePickerDialog.show(it1, null) }
                }

            }
        }
    }


    // chapter 16. Challenge: Another Implicit Intent. permission
    //https://medium.com/@ominoblair/android-runtime-permissions-91b42d2fa0a3
    private val readContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Read contacts permission is granted", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(context, "Read contacts permission is denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun requestPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    || context?.let { ContextCompat.checkSelfPermission(it, readContacts) } == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(context, "Read contacts permission is granted", Toast.LENGTH_SHORT)
                    .show()
            }
            shouldShowRequestPermissionRationale(readContacts) -> {
                context?.let {
                    AlertDialog.Builder(it)
                        .setTitle("Contacts Permission")
                        .setMessage("Contacts permission is needed in order to call an alleged suspect")
                        .setNegativeButton("Cancel") { dialog, _ ->
                            Toast.makeText(
                                context,
                                "Read contacts permission is denied",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK") { _, _ ->
                            readContactsPermission.launch(readContacts)
                        }
                        .show()
                }
            }
            else -> readContactsPermission.launch(readContacts)
        }
    }

//    private val selectSuspectForCall = registerForActivityResult(
//        ActivityResultContracts.PickContact()
//    ) { uri: Uri? ->
//        uri?.let { parseContactSelectionForCall(it) }
//    }
//
//    @SuppressLint("Range")
//    private fun parseContactSelectionForCall(contactUri: Uri) {
//        val queryFields = ContactsContract.CommonDataKinds.Phone._ID
//
//        val queryCursor = requireActivity().contentResolver
//            .query(contactUri, null, queryFields, null, null)
//
//        queryCursor?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                val number =
//                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//
//                val dialNumber = Intent(Intent.ACTION_DIAL)
//                dialNumber.data = Uri.parse("tel: $number")
//                startActivity(dialNumber)
//            }
//        }
//    }

    //https://forums.bignerdranch.com/t/challenge-another-implicit-intent-done/18982
    //https://forums.bignerdranch.com/t/challenge-another-implicit-intent-solution/17609
    //chapter 16. Challenge: Another Implicit Intent. get contacts
    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CODE_PHONE && data != null -> {
                val contactURI: Uri? = data.data

                //Got the phone ID
                val queryFields = ContactsContract.CommonDataKinds.Phone._ID

                //Perform Your Query - the Phone.CONTENT_URI is like a "where" clause here
                val cursor =
                    requireActivity().contentResolver
                        .query(contactURI!!, null, queryFields, null, null)

                cursor.use {
                    if (it?.count == 0) {
                        return
                    }
                    it?.moveToFirst()
                    val number =
                        it?.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    crimeDetailViewModel.updateCrime { oldCrime ->
                        oldCrime.copy(suspectNumber = number.toString())
                    }

                    val dialNumber = Intent(Intent.ACTION_DIAL)
                    dialNumber.data = Uri.parse("tel: $number")
                    startActivity(dialNumber)

                }
                cursor?.close()
            }
        }
    }

    //  chapter 16. Challenge: Another Implicit Intent. Calling
    private fun startCall() {
        val pickPhoneIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(pickPhoneIntent, REQUEST_CODE_PHONE)
    }


    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        
        //Chapter 18. Challenge: Localizing Dates
        val dateString = formattedDate.format(Date())
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryFields, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        //intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.crimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.crimePhoto.setImageBitmap(scaledBitmap)
                    binding.crimePhoto.tag = photoFileName
                }
            } else {
                binding.crimePhoto.setImageBitmap(null)
                binding.crimePhoto.tag = null
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PHONE = 0
        private const val DATE_FORMAT = "EEE, MMM, dd"
        private const val readContacts = READ_CONTACTS
    }
}
