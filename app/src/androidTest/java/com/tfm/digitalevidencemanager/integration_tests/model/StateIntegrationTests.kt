package com.tfm.digitalevidencemanager.integration_tests.model

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createDevice
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createEvidence
import com.tfm.digitalevidencemanager.integration_tests.TestHelper.createUser
import com.tfm.digitalevidencemanager.entity.database_connector.DBHelper
import com.tfm.digitalevidencemanager.entity.database_connector.StateDB
import com.tfm.digitalevidencemanager.entity.interfaces.StateContract
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
class StateIntegrationTests {

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
    fun state_acquired_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(
            null,
            evidence,
            StateContract.ACQUIRED,
            Date(),
            user,
            device,
            "path"
        )
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test(expected = InvalidState::class)
    fun state_acquired_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.MODIFIED, Date(), user, device, "path")

        state.save(context)
    }

    @Test
    fun state_deleted_file_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.DELETED_FILE, Date(), user, device)
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test
    fun state_deleted_se_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.DELETED_FILE, Date(), user, device)
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test(expected = InvalidState::class)
    fun state_deleted_file_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.ACQUIRED, Date(), user, device)

        state.save(context)
    }

    @Test
    fun state_send_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val destinationDevice = createDevice(context)
        val state = State(null, evidence, StateContract.SEND, Date(), user, device, destinationDevice)
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test(expected = InvalidState::class)
    fun state_send_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val destinationDevice = createDevice(context)
        val state = State(null, evidence, StateContract.SEND_TO_SERVER, Date(), user, device, destinationDevice)

        state.save(context)
    }

    @Test
    fun state_send_to_server_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val destinationDevice = createDevice(context)
        val state = State(null, evidence, StateContract.SEND_TO_SERVER, Date(), user, device, destinationDevice, true)
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test(expected = InvalidState::class)
    fun state_send_to_server_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val destinationDevice = createDevice(context)
        val state = State(null, evidence, StateContract.SEND, Date(), user, device, destinationDevice, false)

        state.save(context)
    }

    @Test
    fun state_modified_save_ok() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        val expectedId = 1L

        state.save(context)

        assertEquals(expectedId, state.id)
    }

    @Test(expected = InvalidState::class)
    fun state_modified_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, StateContract.ACQUIRED, Date(), user, device, "field", "old", "new")

        state.save(context)
    }

    @Test(expected = InvalidState::class)
    fun state_save_throw_exception() {
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state = State(null, evidence, 9999, Date(), user, device, "field", "old", "new")

        state.save(context)
    }

    @Test
    fun delete_some_ok_delete_some() {
        val stateDB = StateDB(context)
        val user = createUser(context)
        val device = createDevice(context)
        val evidence = createEvidence(context)
        val state1 = State(null, evidence, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state1.save(context)
        val state2 = State(null, evidence, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state2.save(context)
        val state3 = State(null, evidence, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state3.save(context)
        val idList = listOf(state1.id!!, state3.id!!, 5L)
        val expectedAll = false

        val all = State.deleteSome(context, idList)

        assertEquals(expectedAll, all)
        val storedState1 = stateDB.getByID(state1.id!!)
        assertNull(storedState1)
        val storedState2 = stateDB.getByID(state2.id!!)
        assertNotNull(storedState2)
        val storedState3 = stateDB.getByID(state3.id!!)
        assertNull(storedState3)
    }

    @Test
    fun delete_by_evidence_ok() {
        val stateDB = StateDB(context)
        val user = createUser(context)
        val device = createDevice(context)
        val evidence1 = createEvidence(context)
        val evidence2 = createEvidence(context)
        val state1 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state1.save(context)
        val state2 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state2.save(context)
        val state3 = State(null, evidence2, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state3.save(context)
        val expectedDeleted = true

        val deleted = State.deleteByEvidence(context, evidence1)

        assertEquals(expectedDeleted, deleted)
        val storedState1 = stateDB.getByID(state1.id!!)
        assertNull(storedState1)
        val storedState2 = stateDB.getByID(state2.id!!)
        assertNull(storedState2)
        val storedState3 = stateDB.getByID(state3.id!!)
        assertNotNull(storedState3)
    }

    @Test
    fun delete_all_ok() {
        val stateDB = StateDB(context)
        val user = createUser(context)
        val device = createDevice(context)
        val evidence1 = createEvidence(context)
        val evidence2 = createEvidence(context)
        val state1 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state1.save(context)
        val state2 = State(null, evidence1, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state2.save(context)
        val state3 = State(null, evidence2, StateContract.MODIFIED, Date(), user, device, "field", "old", "new")
        state3.save(context)
        val expectedAll = true

        val all = State.deleteAll(context)

        assertEquals(expectedAll, all)
        val storedState1 = stateDB.getByID(state1.id!!)
        assertNull(storedState1)
        val storedState2 = stateDB.getByID(state2.id!!)
        assertNull(storedState2)
        val storedState3 = stateDB.getByID(state3.id!!)
        assertNull(storedState3)
    }
}