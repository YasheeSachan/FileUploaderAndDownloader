package com.Yashee.FileUploader_RP
import com.Yashee.FileUploader_RP.Dto.FileUploadResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
@RestController
class FileController {
    @Autowired
    lateinit var service: FileService


    @PostMapping("single/file/upload")
    fun uploadFile(@RequestPart("file") filePartMono: Flux<FilePart>?, @RequestHeader("Content-Length") size:Long,@RequestHeader("User-Id") userId:String?): ResponseEntity<Mono<FileUploadResponse>> {
        return ResponseEntity.ok(service.singleFileUpload(filePartMono,size,userId))
    }


    @GetMapping("/download/{fileName}")
    fun downloadFile(@RequestHeader("User-Id") userId: String,@PathVariable fileName: String): Mono<ResponseEntity<Resource>>
    {
        return service.downloadFile(userId,fileName)
    }


}

