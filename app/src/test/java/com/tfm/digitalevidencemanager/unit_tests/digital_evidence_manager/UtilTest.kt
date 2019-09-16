package com.tfm.digitalevidencemanager.unit_tests.digital_evidence_manager

import com.tfm.digitalevidencemanager.api.Utils
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UtilTest {
    @Test
    fun get_file_type_from_file() {
        val file =  File("path/new_file.txt")
        val expectedType = "txt"

        val type = Utils.getFileType(file)

        assertEquals(expectedType, type)
    }

    @Test
    fun get_file_type_without_extension() {
        val file = File("path/new_file")
        val expectedType = "file"

        val type = Utils.getFileType(file)

        assertEquals(expectedType, type)
    }

//    @Test
//    fun get_md5_hash_from_content_file() {
////        PowerMockito.mockStatic(Files::class.java)
//        val mockFile = Mockito.mock(File::class.java)
//        val fileContent = "example text".toByteArray()
//        Mockito.doReturn(fileContent).`when`(mockFile).readBytes()
//        val  file =  File("new_file.txt")
//        val expectedHash = "F81E29AE988B19699ABD92C59906D0EE"
//
//        val hash = Utils.getHashFromFile(file)
//
//        assertEquals(expectedHash, hash)
//    }
//
//    @Test
//    fun get_md5_hash_from_content_file() {
//        val mockFile = mockk<File>()
//        every {
//            mockFile.readBytes()
//        } returns "example text".toByteArray()
//        val expectedHash = "F81E29AE988B19699ABD92C59906D0EE"
//
//        val hash = Utils.getHashFromFile(mockFile)
//
//        assertEquals(expectedHash, hash)
//    }
}
