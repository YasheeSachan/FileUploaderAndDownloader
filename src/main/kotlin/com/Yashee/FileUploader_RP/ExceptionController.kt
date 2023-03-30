package com.Yashee.FileUploader_RP.Exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import reactor.core.publisher.Mono
import java.io.FileNotFoundException
import javax.naming.SizeLimitExceededException

@ControllerAdvice
class ExceptionController {

    @ExceptionHandler(FileNotFoundException::class)
    fun notFound(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File Not Found",HttpStatus.NOT_FOUND))
    }

    @ExceptionHandler(FileNotSelectedException::class)
    fun notSelected():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("File Not Selected",HttpStatus.NOT_ACCEPTABLE))
    }


    @ExceptionHandler(SizeLimitExceededException::class)
    fun largeFile(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File size too large",HttpStatus.PAYLOAD_TOO_LARGE))
    }

    @ExceptionHandler(UnsupportedMediaTypeException::class)
    fun format(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File format not supported",HttpStatus.UNSUPPORTED_MEDIA_TYPE))
    }
    data class Response(var message:String?, var errorCode:HttpStatus)
}