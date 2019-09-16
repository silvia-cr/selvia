package com.tfm.digitalevidencemanager.integration_tests.digital_evidence_manager

import android.content.Context
import android.support.test.InstrumentationRegistry
import com.tfm.digitalevidencemanager.api.Utils
import com.tfm.digitalevidencemanager.entity.database_connector.DBHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File


/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(MockitoJUnitRunner::class)
class UtilsTests {

    private lateinit var helper: DBHelper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        helper = DBHelper(context, true)
        helper.cleanAndRecreate(context)
    }

    @After
    fun tearDown() {
        helper.clean(context)
    }

    @Test
    fun get_file_type_from_file() {
        val file =  File("path/new_file.txt")
        val expectedType = "txt"

        val type = Utils.getFileType(file)

        assertEquals(expectedType, type)
    }

    @Test
    fun get_file_type_without_extension() {
        val file =  File("path/new_file")
        val expectedType = "file"

        val type = Utils.getFileType(file)

        assertEquals(expectedType, type)
    }
}