package com.Yashee.FileUploader_RP

import com.Yashee.FileUploader_RP.Dto.FileUploadResponse
import com.Yashee.FileUploader_RP.ExceptionClasses.FileNotSelectedException
import com.Yashee.FileUploader_RP.ExceptionClasses.InvalidFileNameException
import com.Yashee.FileUploader_RP.ExceptionClasses.InvalidUserIdException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Path
import javax.naming.SizeLimitExceededException


@ExtendWith(MockitoExtension::class)
class FileTest {


    @InjectMocks
    private lateinit var service: FileService


    @Test
    fun uploadTest(){
        //creating mock file
        val fp:FilePart= mock(FilePart::class.java)

        Mockito.`when`(fp.filename()).thenReturn("abc.csv")
//        Mockito.`when`(Paths.get("/abc"))
        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"

        Mockito.`when`(fp.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())

        var response:Mono<ResponseEntity<FileUploadResponse>> = service.singlefileupload(Flux.just(fp),DataSize.ofKilobytes(500).toBytes(),id)

        StepVerifier.create(response).expectNextMatches{ob -> ob.statusCode.is2xxSuccessful}
            .verifyComplete()
    }



    @Test
    fun fileLimitExceededTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("abc.csv")
        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"

        var response:Mono<ResponseEntity<FileUploadResponse>> = service.singlefileupload(Flux.just(fp), DataSize.ofKilobytes(50000).toBytes(),id)

        StepVerifier.create(response)
            .verifyError(SizeLimitExceededException::class.java)
    }


    @Test
    fun fileNotSelectedTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("")
        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"
        var response:Mono<ResponseEntity<FileUploadResponse>> = service.singlefileupload(Flux.just(fp),172L,id)

        StepVerifier.create(response).verifyError(FileNotSelectedException::class.java)
    }

    @Test
    fun fileFormatTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("abc.txt")

        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"
        var response:Mono<ResponseEntity<FileUploadResponse>> = service.singlefileupload(Flux.just(fp), DataSize.ofKilobytes(500).toBytes(),id)

        StepVerifier.create(response)
            .verifyError(UnsupportedMediaTypeException::class.java)
    }

    @Test
    fun downloadTest(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("download.jpg")

        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"
        var response:Mono<ResponseEntity<Resource>> = service.downloadFile(id,fp.filename())

        StepVerifier.create(response)
            .expectNextMatches{ob -> ob.statusCode.is2xxSuccessful}
            .verifyComplete()
    }

    @Test
    fun downloadTestInvalidFileName(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("Dog.jpg")
        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383eac9"
        var response:Mono<ResponseEntity<Resource>> = service.downloadFile(id,fp.filename())

        StepVerifier.create(response)
            .verifyError(InvalidFileNameException::class.java)
    }

    @Test
    fun downloadTestInvalidUserId(){
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("Dog.jpg")
        var id="f5ee9bb2-38e4-4cd1-aeef-a354e383dsjfeac9"
        var response:Mono<ResponseEntity<Resource>> = service.downloadFile(id,fp.filename())

        StepVerifier.create(response)
            .verifyError(InvalidUserIdException::class.java)
    }

}

