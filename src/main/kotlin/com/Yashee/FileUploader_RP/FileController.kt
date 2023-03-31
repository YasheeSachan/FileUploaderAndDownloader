package com.Yashee.FileUploader_RP
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
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

    //Download
    @GetMapping("/download/{fileName}")
    @Throws(IOException::class)
    fun downloadFile(@PathVariable fileName: String): Mono<ResponseEntity<Resource>>
    {
            return service.download(fileName)

    }

}

