package com.tfm.digitalevidencemanager.unit_tests.digital_evidence_manager

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class AcquireTest {

//    @Test
//    fun get_acquire_ok() {
//
//        val mockFile = Mockito.mock(File::class.java)
//        Mockito.`when`(mockFile.exists()).thenReturn(true)
//        val mockContext= Mockito.mock(Context::class.java)
//        val mockUser= Mockito.mock(User::class.java)
//        val mockDevice= Mockito.mock(Device::class.java)
//        val dem = DigitalEvidenceManager()
//        val expected = false
//
//        val ok = dem.acquire(
//            mockContext,
//            "testName",
//            null,
//            null,
//            mockFile,
//            "testPath",
//            mockUser,
//            mockDevice
//        )
//
//        assertEquals(expected, ok)
//
////        val mockFile = mockk<File>()
////        every {
////            mockFile.exists()
////        } returns true
////        every{
////            mockFile.copyTo(any())
////        } returns mockFile
////
////        val mockContext = mockk<Context>()
////        val mockUser = mockk<User>()
////        val mockDevice = mockk<Device>()
////        mockkObject(Utils)
////        every {
////            Utils.getFileType(mockFile)
////        } returns "jejej"
////        val dem = DigitalEvidenceManager()
////
////        dem.acquire(
////            mockContext,
////            "testName",
////            null,
////            null,
////            mockFile,
////            "testPath",
////            mockUser,
////            mockDevice
////        )
//    }

//    @Test(expected = FileNotFoundException::class)
//    fun get_acquire_original_file_not_exists_throw_exception() {
//        val mockFile = Mockito.mock(File::class.java)
//        Mockito.`when`(mockFile.exists()).thenReturn(false)
//        val mockContext= Mockito.mock(Context::class.java)
//        val mockUser= Mockito.mock(User::class.java)
//        val mockDevice= Mockito.mock(Device::class.java)
//        val dem = DigitalEvidenceManager()
//        val expected = false
//
//        val ok = dem.acquire(
//            mockContext,
//            "testName",
//            null,
//            null,
//            mockFile,
//            "testPath",
//            mockUser,
//            mockDevice
//        )
//
//        assertEquals(expected, ok)
//    }
//
//    @Test
//    fun get_file_type_without_extension() {
//        val file =  File("path/new_file")
//        val expectedType = "file"
//
//        val type = Utils.getFileType(file)
//
//        assertEquals(expectedType, type)
//    }
//
//    @Test
//    fun get_test() {
//        val mockUtils = Mockito.mock(User::class.java)
//        Mockito.`when`(mockUtils.dni).thenReturn("hola")
//
//        val u = User(2L, "12", "12", "12")
//
//        assertEquals(u.dni, "hola")
//    }

//    @Test
//    fun get_md5_hash_from_content_file() {
//        val mockFileReadBytes = Mockito.mock(File::class.java)
//        val fileContent = "example text".toByteArray()
//
//        MockK
//        mockFileReadBytes.readBytes()
//        Mockito.verify(mockFileReadBytes).readBytes()
//
//        val  file =  File("new_file.txt")
//        val expectedHash = "F81E29AE988B19699ABD92C59906D0EE"
//
//        val hash = Utils.getHashFromFile(file)
//
//        assertEquals(expectedHash, hash)
//    }

}
