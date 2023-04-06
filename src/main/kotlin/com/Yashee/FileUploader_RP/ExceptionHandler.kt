package com.Yashee.FileUploader_RP.Exception

import com.Yashee.FileUploader_RP.FileNotSelectedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import java.io.FileNotFoundException
import javax.naming.SizeLimitExceededException

@ControllerAdvice
class ExceptionController {

//    @ExceptionHandler(Exception::class)
//    fun notFound1(): ResponseEntity<Response> {
//        return ResponseEntity.badRequest().body(Response("File Not Found",404))
//    }
    //File was not called during download
    @ExceptionHandler(FileNotFoundException::class)
    fun notFound(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File Not Found",404))
    }

    //No file was sent for uploading
    @ExceptionHandler(FileNotSelectedException::class)
    fun notSelected():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("File Not Selected",416))
    }

    @ExceptionHandler(InvalidUserIdException::class)
    fun invalidUserId():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("Invalid User Id ",404))
    }

    @ExceptionHandler(InvalidUserIdOrFileNameException::class)
    fun invalidUserIdOrFile():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("File not found-Invalid UserId or filename ",404))
    }


    //File size exceeded
    @ExceptionHandler(SizeLimitExceededException::class)
    fun largeFile(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File size too large",413))
    }

    //File does not belong to allowed media type
    @ExceptionHandler(UnsupportedMediaTypeException::class)
    fun format(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File format not supported",415))
    }

    @ExceptionHandler(IndexOutOfBoundsException::class)
    fun numberOfFiles(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("Only single file allowed", 406))
    }
    data class Response(var message:String?, var errorCode: Int)
}