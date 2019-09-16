package com.tfm.digitalevidencemanager.integration_tests.digital_evidence_manager

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.tfm.digitalevidencemanager.api.DigitalEvidenceManager
import com.tfm.digitalevidencemanager.api.FileNotExist
import com.tfm.digitalevidencemanager.api.PriorityEnum
import com.tfm.digitalevidencemanager.api.SeverityEnum
import com.tfm.digitalevidencemanager.integration_tests.TestHelper
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createDevice
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createFile
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.deleteTestFolder
import com.tfm.digitalevidencemanager.entity.database_connector.DBHelper
import com.tfm.digitalevidencemanager.entity.model.User
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.deleteTestEvidence
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException


/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class AcquireTests {

    private lateinit var helper: DBHelper
    private lateinit var context: Context
    private lateinit var dem: DigitalEvidenceManager
    private val testPathFile = "/sdcard/Download/tests/"
    private val testFile = testPathFile + "example.txt"
    private val testFileContent = "example text"
    private val testTime = System.currentTimeMillis()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        helper = DBHelper(context, true)
        helper.cleanAndRecreate(context)
        dem = DigitalEvidenceManager(context)
    }

    @After
    fun tearDown() {
        helper.clean(context)
        deleteTestFolder(testPathFile)
        deleteTestEvidence(context, testTime)
    }

    @Test
    fun acquire_all_params_ok() {
        val user = TestHelper.createUser(context)
        val device = createDevice(context)
        val file = createFile(this.testFile, this.testFileContent)
        val dem = DigitalEvidenceManager(context)
        val expected = true

        val ok = dem.acquire(
            "evidence_name",
            PriorityEnum.LOW,
            SeverityEnum.MAJOR,
            file,
            user,
            device
        )

        assertEquals(expected, ok)
    }

    @Test
    fun acquire_all_mandatory_ok() {
        val user = TestHelper.createUser(context)
        val device = createDevice(context)
        val file = createFile(this.testFile, this.testFileContent)
        val dem = DigitalEvidenceManager(context)
        val expected = true

        val ok = dem.acquire(
            "evidence_name",
            null,
            null,
            file,
            user,
            device
        )

        assertEquals(expected, ok)
    }

    @Test
    fun acquire_not_saved_user_ok() {
        val user = User(null, "111", "name", "test_id")
        val device = TestHelper.createDevice(context)
        val file = createFile(this.testFile, this.testFileContent)
        val dem = DigitalEvidenceManager(context)
        val expected = true

        val ok = dem.acquire(
            "evidence_name",
            null,
            null,
            file,
            user,
            device
        )

        assertEquals(expected, ok)
    }

    @Test(expected = FileNotExist::class)
    fun acquire_file_not_exists_throw_exception() {
        val user = User(null, "111", "name", "test_id")
        val device = TestHelper.createDevice(context)
        val file = File(testFile + "not_found")
        val dem = DigitalEvidenceManager(context)

        dem.acquire(
            "evidence_name",
            null,
            null,
            file,
            user,
            device
        )
    }

}