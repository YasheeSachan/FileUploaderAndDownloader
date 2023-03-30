package com.Yashee.FileUploader_RP

import com.Yashee.FileUploader_RP.Exception.FileNotSelectedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.io.FileNotFoundException
import java.nio.file.Path
import javax.naming.SizeLimitExceededException


@ExtendWith(MockitoExtension::class)
class FileTest {

    @InjectMocks
    private lateinit var service: FileService

    @Test
    fun uploadTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("abc.txt")

        Mockito.`when`(fp.transferTo(Mockito.any(Path::class.java)))
            .thenReturn(Mono.empty())

        var response:Mono<ResponseEntity<String>> = service.single(fp,DataSize.ofKilobytes(500).toBytes())

        StepVerifier.create(response).expectNextMatches{ob -> ob.statusCode.is2xxSuccessful}
            .verifyComplete()
    }

    @Test
    fun fileLimitExceedTest(){
        val fp:FilePart= mock(FilePart::class.java)

        var response:Mono<ResponseEntity<String>> = service.single(fp, DataSize.ofKilobytes(50000).toBytes())

        StepVerifier.create(response)
            .verifyError(SizeLimitExceededException::class.java)
    }


    @Test
    fun fileNotSelectedTest(){
        val fp:FilePart= mock(FilePart::class.java)
        var response:Mono<ResponseEntity<String>> = service.single(fp,172L)

        StepVerifier.create(response).verifyError(FileNotSelectedException::class.java)
    }

    @Test
    fun fileFormatTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("abc.csv")

        var response:Mono<ResponseEntity<String>> = service.single(fp, DataSize.ofKilobytes(500).toBytes())

        StepVerifier.create(response)
            .verifyError(UnsupportedMediaTypeException::class.java)
    }

    @Test
    fun downloadTest(){

    }


    @Test
    fun hello(){
        val ob=FileController()
        var res=ob.check()
        assertEquals("hello",res)
    }

}