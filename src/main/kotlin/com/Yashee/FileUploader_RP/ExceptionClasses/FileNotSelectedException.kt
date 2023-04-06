package com.Yashee.FileUploader_RP.ExceptionClasses

import org.springframework.http.ZeroCopyHttpOutputMessage

class FileNotSelectedException(override var message: String?):Exception() {
}