package io.github.giovannilamarmora.tax_calculator.app;

import io.github.giovannilamarmora.tax_calculator.app.model.TaxRequest;
import io.github.giovannilamarmora.utils.generic.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired private AppService appService;

  @PostMapping(value = "/generate-pdf")
  public Mono<ResponseEntity<byte[]>> generatePdf(@RequestPart("file") Mono<FilePart> filePartMono) {
    return appService.getPdfData(filePartMono);
  }
}
