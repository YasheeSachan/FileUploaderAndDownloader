package com.Yashee.FileUploader_RP

import com.Yashee.FileUploader_RP.Exception.FileNotSelectedException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.*
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.naming.SizeLimitExceededException
@Service
class FileService {
    val basePath = Paths.get("./src/main/resources/Uploads/")
    fun single(filePartMono: FilePart, size: Long): Mono<ResponseEntity<String>> {
        return Mono.just(filePartMono).flatMap { fp: FilePart ->
            when {
                (size <= 172L) -> Mono.error(FileNotSelectedException())
                (size > DataSize.ofKilobytes(5000)
                    .toBytes()) -> Mono.error(SizeLimitExceededException())
                fp.filename().substring(fp.filename().lastIndexOf('.') +1) !in setOf("jpeg","pdf","jpg","doc","txt","png") -> Mono.error(
                    UnsupportedMediaTypeException("File format not supported"))
                else -> {
                    println("Received File : " + fp.filename())
                    return@flatMap fp.transferTo(basePath.resolve(fp.filename())).then(
                        Mono.just(ResponseEntity.ok(UriComponentsBuilder.fromPath("/download/").path(fp.filename()).toUriString())))
                }
            }
        }
    }
    fun download( filename: String): Mono<ResponseEntity<Resource>> {
        val filePath: Path = basePath.toAbsolutePath().normalize().resolve(filename)
        return Mono.fromCallable {
            when {
                !Files.exists(filePath) -> throw FileNotFoundException()
                else -> {
                    val resource: Resource = UrlResource(filePath.toUri())
                    val httpHeaders = HttpHeaders()
                    httpHeaders.add("File-Name", filename)
                    httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.filename)
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .headers(httpHeaders).body(resource)
                }
            }
        }
    }
}