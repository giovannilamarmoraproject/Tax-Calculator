package io.github.giovannilamarmora.tax_calculator.pdf;

import io.github.giovannilamarmora.tax_calculator.exception.ExceptionMap;
import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class PDFException extends UtilsException {

  private static final ExceptionCode DEFAULT_CODE = ExceptionMap.ERR_PDF_001;

  public PDFException(String message) {
    super(DEFAULT_CODE, message);
  }

  public PDFException(ExceptionCode exceptionCode, String message) {
    super(exceptionCode, message);
  }
}
