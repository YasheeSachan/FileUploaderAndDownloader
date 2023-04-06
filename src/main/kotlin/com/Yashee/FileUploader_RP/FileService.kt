package com.Yashee.FileUploader_RP

import com.Yashee.FileUploader_RP.Dto.FileUploadResponse
import com.Yashee.FileUploader_RP.ExceptionClasses.FileNotSelectedException
import com.Yashee.FileUploader_RP.ExceptionClasses.InvalidFileNameException
import com.Yashee.FileUploader_RP.ExceptionClasses.InvalidUserIdException
import com.Yashee.FileUploader_RP.ExceptionClasses.MultipleFileSelectedException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.*
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.naming.SizeLimitExceededException
@Service
class FileService {
    val basePath = Paths.get("./src/main/resources/Uploads/")


    fun singleFileUpload(filePartMono: Flux<FilePart>?, size: Long, userId: String?=null): Mono<FileUploadResponse>
    {
        return when{
            filePartMono==null ->throw (FileNotSelectedException("File not provided.Kindly select a single file"))
             userId!=null && !Files.exists(basePath.resolve(userId)) -> throw (InvalidUserIdException("Invalid User Id"))
            else-> {
                val id = userId ?: UUID.randomUUID().toString()
                val dir = basePath.resolve(id)
                var count = AtomicLong()

                filePartMono.flatMap { fp: FilePart ->
                    when {
                        (count.incrementAndGet() > 1) -> Flux.error(
                            MultipleFileSelectedException
                                ("Multiple files selection is not allowed.Kindly select a single file.")
                        )

                        else -> Flux.just(fp)
                    }
                }.single()
                    .flatMap { fp: FilePart ->
                        when {
                            //validating if file is present
                            (fp.filename()
                                .isBlank()) -> Mono.error(FileNotSelectedException("No file selected for upload.Please select a file."))
                            //validating size<=5M.B
                            (size > DataSize.ofKilobytes(5000)
                                .toBytes()) -> Mono.error(SizeLimitExceededException("File size cannot exceed 5MB"))
                            // Validating allowed content type
                            fp.filename().substring(fp.filename().lastIndexOf('.') + 1) !in
                                    setOf("jpeg", "pdf", "csv", "xls", "jpg", "doc", "png", "zip")
                            -> Mono.error(UnsupportedMediaTypeException("File format not supported"))

                            else -> {
                                println("Received File : " + fp.filename())
                                Files.createDirectories(dir)
                                val path= UriComponentsBuilder.fromPath("/download/").host("localhost").port(8080)
                                .path(fp.filename()).toUriString().replace("//","")
                                //Saving file to the specified folder
                                return@flatMap fp.transferTo(dir.resolve(fp.filename())).then(
                                    //returning path of the saved file
                                    Mono.just(FileUploadResponse(id, path)))
                            }
                        }
                    }
            }
        }
    }


    fun downloadFile(userId: String, filename: String): Mono<ResponseEntity<Resource>> {
        val filePath: Path = basePath.toAbsolutePath().normalize().resolve(userId)
        val dir=filePath.resolve((filename))
        return Mono.fromCallable {
            when {
                //validation
                !Files.exists(filePath)-> throw InvalidUserIdException("Invalid User Id")
                !Files.exists(dir)-> throw InvalidFileNameException("No such file exists")
                else -> {

                    val resource: Resource = UrlResource(dir.toUri())
                    val httpHeaders = HttpHeaders()
                    //assigning filename to the header
                    httpHeaders.add("File-Name", filename)
                    //defining presentation style and providing filename to the downloaded file
                    httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.filename)
                    //Displaying file content
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(dir)))
                        .headers(httpHeaders).body(resource)
                }
            }
        }
    }

}

