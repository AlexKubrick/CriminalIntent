package com.bignerdranch.android.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrimeDetailFragmentTest {
    private lateinit var scenario: FragmentScenario<CrimeDetailFragment>


    @Before
    fun setUp() {
        scenario = launchFragmentInContainer()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun isCheckBoxHookedUp() {
        onView(withId(R.id.crime_solved))
            .perform(click())

        scenario.onFragment() { fragment ->
//            onView(withId(R.id.crime_solved)).check(matches(fragment.crime.isSolved))
            assertTrue(fragment.crime.isSolved)
        }
    }

    @Test
    fun isEditTextHookedUp() {
        onView(withId(R.id.crime_title))
            .perform(typeText("Test input"))

        scenario.onFragment() { fragment ->
            //onView(withId(R.id.crime_title)) .check(matches(withText("Test input"))) --
            // java.lang.IllegalStateException: Method cannot be called on the main application thread (on: main)

            assertEquals("Test input", fragment.crime.title)
        }
    }



}