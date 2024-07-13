package io.github.giovannilamarmora.tax_calculator.exception;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum ExceptionMap implements ExceptionCode {
  ERR_PDF_001("ERROR-PDF-EXCEPTION", HttpStatus.BAD_REQUEST, "Error on generating PDF");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  ExceptionMap(String exceptionName, HttpStatus status, String message) {
    this.exceptionName = exceptionName;
    this.status = status;
    this.message = message;
  }

  @Override
  public String exception() {
    return exceptionName;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public HttpStatus getStatus() {
    return status;
  }
}
