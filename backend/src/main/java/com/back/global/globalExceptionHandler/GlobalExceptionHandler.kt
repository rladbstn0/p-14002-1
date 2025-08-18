package com.back.global.globalExceptionHandler

import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handle(ex: NoSuchElementException): ResponseEntity<RsData<Void>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(RsData("404-1", "해당 데이터가 존재하지 않습니다."))

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(ex: ConstraintViolationException): ResponseEntity<RsData<Void>> {
        val message = ex.constraintViolations
            .asSequence()
            .map { v ->
                val field = v.propertyPath.toString().split('.', limit = 2).getOrNull(1) ?: v.propertyPath.toString()
                val bits = v.messageTemplate.split('.')
                val code = bits.getOrNull(bits.size - 2) ?: "Invalid"
                val msg = v.message
                "$field-$code-$msg"
            }
            .sorted()
            .joinToString("\n")
        return ResponseEntity.badRequest().body(RsData("400-1", message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<RsData<Void>> {
        val message = ex.bindingResult
            .allErrors
            .asSequence()
            .filterIsInstance<FieldError>()
            .map { "${it.field}-${it.code}-${it.defaultMessage}" }
            .sorted()
            .joinToString("\n")
        return ResponseEntity.badRequest().body(RsData("400-1", message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException): ResponseEntity<RsData<Void>> =
        ResponseEntity.badRequest().body(RsData("400-1", "요청 본문이 올바르지 않습니다."))

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(ex: MissingRequestHeaderException): ResponseEntity<RsData<Void>> {
        val message = "${ex.headerName}-NotBlank-${ex.localizedMessage}"
        return ResponseEntity.badRequest().body(RsData("400-1", message))
    }

    @ExceptionHandler(ServiceException::class)
    fun handle(ex: ServiceException): ResponseEntity<RsData<Void>> {
        val rsData = ex.rsData
        return ResponseEntity.status(rsData.statusCode).body(rsData)
    }
}