package com.Yashee.FileUploader_RP.ExceptionClasses

import org.springframework.http.ZeroCopyHttpOutputMessage

class MultipleFileSelectedException(override var message: String?):Exception() {
}