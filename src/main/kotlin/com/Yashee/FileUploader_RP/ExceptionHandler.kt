package com.Yashee.FileUploader_RP.ExceptionClasses

import com.Yashee.FileUploader_RP.Dto.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import javax.naming.SizeLimitExceededException

@ControllerAdvice
class ExceptionHandler {


    //No file was sent for uploading
    @ExceptionHandler(FileNotSelectedException::class)
    fun fileNotSelected(ex:Exception):ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response(ex.message))
    }

    @ExceptionHandler(InvalidUserIdException::class)
    fun invalidUserId(ex: Exception):ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response(ex.message))
    }

    @ExceptionHandler(InvalidFileNameException::class)
    fun invalidFileName(ex: Exception):ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response(ex.message))
    }


    //File size exceeded
    @ExceptionHandler(SizeLimitExceededException::class)
    fun largeFile(ex: Exception): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response(ex.message))
    }

    //File does not belong to allowed media type
    @ExceptionHandler(UnsupportedMediaTypeException::class)
    fun format(ex: Exception): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response(ex.message))
    }

    @ExceptionHandler(MultipleFileSelectedException::class)
    fun numberOfFiles(ex:Exception): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response(ex.message))
    }
    @ExceptionHandler(Exception::class)
    fun parentHandler(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File Not Found"))
    }

}