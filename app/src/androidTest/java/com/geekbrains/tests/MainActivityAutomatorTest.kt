package com.geekbrains.tests

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class MainActivityAutomatorTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName
    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(getInstrumentation())
        device.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }

    @Test
    fun deviceNotNull_Test() {
        Assert.assertNotNull(device)
    }

    @Test
    fun appPackageNotNull_Test() {
        Assert.assertNotNull(packageName)
    }

    @Test
    fun mainActivityIntentNotNull_Test() {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        Assert.assertNotNull(intent)
    }


    @Test
    fun mainActivityIsStarted_Test() {
        val editText =  device.findObject(By.res(packageName, "searchEditText"))
        Assert.assertNotNull(editText)
    }

    @Test
    fun searchIsPositive_Test(){
        val editText = device.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"
        Espresso.onView(withId(R.id.searchEditText))
            .perform(ViewActions.pressImeActionButton())
        val changedText = device.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        Assert.assertEquals(changedText.text.toString(), TEST_NUMBER_OF_RESULTS_MINUS_696)
    }

    @Test
    fun searchIsPositiveWithoutEspresso_Test(){
        val editText = device.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"
        device.findObject(By.text("FIND")).click()
        val changedText = device.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        Assert.assertEquals(changedText.text.toString(), TEST_NUMBER_OF_RESULTS_MINUS_696)
    }

    @Test
    fun DetailsScreenCheckData_Test(){
        val editText = device.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"
        device.findObject(By.text("FIND")).click()
        val changedText = device.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        device.findObject(By.res(packageName,"toDetailsActivityButton")).clickAndWait(
            Until.newWindow(), TIMEOUT
        )
        val totalCountTextView = device.findObject(By.res(packageName, "totalCountTextView"))
        Assert.assertEquals(totalCountTextView.text.toString(), TEST_NUMBER_OF_RESULTS_MINUS_696)
    }

    @Test
    fun DetailsScreenCheckButtons_Test(){
        device.findObject(By.res(packageName,"toDetailsActivityButton")).clickAndWait(
            Until.newWindow(), TIMEOUT
        )
        val minusButton = device.findObject(By.res(packageName, "decrementButton"))
        val plusButton = device.findObject(By.res(packageName, "incrementButton"))
        val totalCountTextView = device.findObject(By.res(packageName, "totalCountTextView"))
        minusButton.click()
        Assert.assertEquals(totalCountTextView.text.toString(), TEST_NUMBER_OF_RESULTS_MINUS_1)
        plusButton.click()
        Assert.assertEquals(totalCountTextView.text.toString(), TEST_NUMBER_OF_RESULTS_ZERO)
    }

    @Test
    fun emptySearch_Test(){

        device.findObject(By.res(packageName, "btnFind")).click()
        val res = By.res(packageName, "totalCountTextView")
        device.wait(Until.gone(By.res(packageName, "progressBar")), TIMEOUT)
        val totalCount = device.findObject(res)
        Assert.assertNull(totalCount)

    }

    companion object{
        private const val TIMEOUT = 5000L
    }
}