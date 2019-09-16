package com.tfm.digitalevidencemanager.integration_tests.model

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.tfm.digitalevidencemanager.integration_tests.TestHelper
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createDevice
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createEvidence
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createUser
import com.tfm.digitalevidencemanager.entity.database_connector.DBHelper
import com.tfm.digitalevidencemanager.entity.database_connector.StateDB
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
import com.tfm.digitalevidencemanager.entity.model.Evidence
import com.tfm.digitalevidencemanager.entity.model.InvalidState
import com.tfm.digitalevidencemanager.entity.model.State
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class EvidenceIntegrationTests {

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
    fun can_delete_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val server = TestHelper.createDevice(context, System.currentTimeMillis().toString(), true, "0.0.0.0")
        val evidence1 = createEvidence(context)
        val evidence2 = createEvidence(context)
        val evidence3 = createEvidence(context)
        val state1 = State(null, evidence1, StateContract.SEND_TO_SERVER, Date(), user, device, server, true)
        state1.save(context)
        val state2 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state2.save(context)
        val state3 = State(null, evidence2, StateContract.SEND_TO_SERVER, Date(), user, device, server, true)
        state3.save(context)
        val state4 = State(null, evidence3, StateContract.SEND_TO_SERVER, Date(), user, device, server, false)
        state4.save(context)
        val expectedEvidences = listOf(evidence2)

        val evidences = Evidence.getCanDeleteEvidences(context)

        assertEquals(expectedEvidences, evidences)
    }

    @Test
    fun need_sync_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val server = TestHelper.createDevice(context, System.currentTimeMillis().toString(), true, "0.0.0.0")
        val evidence1 = createEvidence(context)
        val evidence2 = createEvidence(context)
        val evidence3 = createEvidence(context)
        val state1 = State(null, evidence1, StateContract.SEND_TO_SERVER, Date(), user, device, server, true)
        state1.save(context)
        val state2 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state2.save(context)
        val state3 = State(null, evidence2, StateContract.SEND_TO_SERVER, Date(), user, device, server, true)
        state3.save(context)
        val state4 = State(null, evidence3, StateContract.SEND_TO_SERVER, Date(), user, device, server, false)
        state4.save(context)
        val expectedEvidences = listOf(evidence1)

        val evidences = Evidence.getNeedSyncEvidences(context)

        assertEquals(expectedEvidences, evidences)
    }
}