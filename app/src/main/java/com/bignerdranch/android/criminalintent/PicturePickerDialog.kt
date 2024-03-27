package com.bignerdranch.android.criminalintent

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

//https://stackoverflow.com/questions/23824599/android-set-imageview-inside-dialogfragment
//https://forums.bignerdranch.com/t/challenge-detail-display-solution-and-question/17617
// chapter 17. Challenge: Detail Display
class PicturePickerDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_picture_picker, container, false)
        val imageView = view.findViewById(R.id.zoom_image) as ImageView

        val photoFileName = arguments?.getSerializable("PHOTO_URI") as String

        imageView.setImageBitmap(BitmapFactory.decodeFile(requireContext().filesDir.path + "/" + photoFileName))

        return view
    }

    companion object {
        fun newInstPicturePicker(photoFileName: String): PicturePickerDialog {
            val frag = PicturePickerDialog()
            val args = Bundle()
            args.putSerializable("PHOTO_URI", photoFileName)
            frag.arguments = args
            return frag
        }
    }

}