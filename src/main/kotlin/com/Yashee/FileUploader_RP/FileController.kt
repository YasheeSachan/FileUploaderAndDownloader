package com.Yashee.FileUploader_RP

//import com.Yashee.FileUploader_RP.Exception.FileNotSelectedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.FileNotFoundException
import java.io.IOException

@RestController
class FileController {

    @Autowired
    lateinit var service: FileService

    @GetMapping("hello")
    fun check():String
    {
        return "hello"
    }

    @PostMapping("single/upload")
    fun uploadFile(@RequestPart("file") filePartMono: FilePart, @RequestHeader("Content-Length") size:Long): Mono<ResponseEntity<String>> {

        return service.single(filePartMono,size)


        }




    //multiple file upload
    @PostMapping("multi/upload")
    fun uploadMultipleFiles(@RequestPart("files") partFlux: Flux<FilePart>): Mono<Void>
    {
        return partFlux
            .doOnNext { fp: FilePart ->
                println(fp.filename()) }
            .flatMap { fp: FilePart ->
                fp.transferTo(service.basePath.resolve(fp.filename())) }
            .then()
    }

    //Download
    @GetMapping("/download/{fileName}")
    @Throws(IOException::class)
    fun downloadFile(@PathVariable fileName: String): Mono<ResponseEntity<Resource>>
    {
            return service.download(fileName)

    }

}

