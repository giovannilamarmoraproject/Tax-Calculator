package io.github.giovannilamarmora.tax_calculator.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import io.github.giovannilamarmora.tax_calculator.app.model.TaxRequest;
import io.github.giovannilamarmora.tax_calculator.pdf.PdfService;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.utilities.MapperUtils;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Service
public class AppService {

  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Mono<ResponseEntity<byte[]>> getPdfData(@RequestPart("file") Mono<FilePart> filePartMono) {
    return filePartMono
        .flatMap(
            filePart -> {
              // Leggi il contenuto del file
              return DataBufferUtils.join(filePart.content())
                  .map(
                      dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        return bytes;
                      });
            })
        .map(
            bytes -> {
              // Converti i bytes in una stringa JSON (assumendo che il file sia un JSON)
              String jsonString = new String(bytes, StandardCharsets.UTF_8);
              TaxRequest request =
                  parseJsonToTaxRequest(
                      jsonString); // Implementa questa funzione per convertire la stringa JSON in
              // un oggetto TaxRequest

              if (ObjectUtils.isEmpty(request)) {
                throw new IllegalArgumentException("Invalid request data");
              }

              return ResponseEntity.ok()
                  .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=crypto_taxes.pdf")
                  .contentType(MediaType.APPLICATION_PDF)
                  .body(PdfService.generatePdf(request));
            });
  }

  // Metodo di utilità per convertire una stringa JSON in un oggetto TaxRequest
  private TaxRequest parseJsonToTaxRequest(String jsonString) {
    ObjectMapper objectMapper =
        MapperUtils.mapper().failOnUnknownProprieties().enableJavaTime().build();
    try {
      return objectMapper.readValue(jsonString, TaxRequest.class);
    } catch (JsonProcessingException e) {
      // return objectMapper.convertValue(jsonString, TaxRequest.class);
      throw new IllegalArgumentException("Invalid JSON data", e);
    }
  }
}
