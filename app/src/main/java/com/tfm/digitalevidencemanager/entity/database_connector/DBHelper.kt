package com.tfm.digitalevidencemanager.entity.database_connector

import android.content.Context
import com.tfm.digitalevidencemanager.Local
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class DBHelper : SQLiteOpenHelper {

    constructor(context: Context) : super(context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {
        createDatabase(context, DATABASE_NAME)
    }

    constructor(context: Context, testing: Boolean) : super(context,
        TEST_DATABASE_NAME, null,
        DATABASE_VERSION
    ) {
        var database_name = DATABASE_NAME
        if(testing){
            database_name = TEST_DATABASE_NAME
        }
        createDatabase(context, database_name)
    }

    private fun createDatabase(context: Context, database_name: String) {
        SQLiteDatabase.loadLibs(context)
        val dbFile = context.getDatabasePath(database_name)
        if (!dbFile.exists()) {
            dbFile.mkdirs()
            dbFile.delete()
        }

        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, Local.db_password, null)
        db.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DeviceDB.SQL_CREATE_TABLE)
        db.execSQL(UserDB.SQL_CREATE_TABLE)
        db.execSQL(EvidenceDB.SQL_CREATE_TABLE)
        db.execSQL(StateDB.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO: update values, do not delete and recreate database tables
        deleteTables(db)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun cleanAndRecreate(context: Context) {
        clean(context)

        val dbFile = context.getDatabasePath(TEST_DATABASE_NAME)
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, Local.db_password, null)
        db.close()
    }

    fun clean(context: Context) {
        context.deleteDatabase(TEST_DATABASE_NAME)
    }

    private fun deleteTables(db: SQLiteDatabase) {
        db.execSQL(DeviceDB.SQL_DELETE_TABLE)
        db.execSQL(UserDB.SQL_DELETE_TABLE)
        db.execSQL(EvidenceDB.SQL_DELETE_TABLE)
        db.execSQL(StateDB.SQL_DELETE_TABLE)
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "test_digital_evidence.db"
        const val TEST_DATABASE_NAME = "test_digital_evidence.db"
    }
}