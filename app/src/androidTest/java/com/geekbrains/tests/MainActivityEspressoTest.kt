package com.geekbrains.tests

import android.app.Application
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.search.MainActivity
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        if (BuildConfig.TYPE == MainActivity.FAKE) {
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 42")))
        } else {
            onView(isRoot()).perform(delay())
            onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 2810")))
        }
    }

    @Test
    fun totalCountTestViewBecomesVisible_Test(){
        onView(withId(R.id.totalCountTextView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.searchEditText)).perform(click(),
            replaceText("algol"),
            closeSoftKeyboard(),
            pressImeActionButton()
        )
        onView(isRoot()).perform(delay())
        onView(withId(R.id.totalCountTextView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun checkSearchEditTextHint_Test(){
        onView(withId(R.id.searchEditText)).check(matches(withHint(R.string.search_hint)))
    }

    @Test
    fun checkTextInDetailsButton_Test(){
        onView(withId(R.id.toDetailsActivityButton)).check(matches(withText(R.string.to_details)))
    }

    @Test
    fun checkViewsIsDisplayed_Test(){
        onView(withId(R.id.toDetailsActivityButton)).check(matches(isDisplayed()))
        onView(withId(R.id.searchEditText)).check(matches(isDisplayed()))
    }

    private fun delay(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $2 seconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(2000)
            }
        }
    }

    @After
    fun close() {
        scenario.close()
    }
}
