package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import io.github.giovannilamarmora.tax_calculator.pdf.PdfService;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoWallet;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;

public class PdfDataSource {

  private static final Logger LOG = LoggerFilter.getLogger(PdfService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  // Metodo per aggiungere la tabella delle transazioni
  public static void addTable(Document document, List<CryptoWallet> wallets)
      throws DocumentException {

    document.newPage();
    Paragraph preface = new Paragraph("Fonti di dati", PdfFont.TITLE_NORMAL.getFont());
    preface.setPaddingTop(-10);
    PdfUtils.addToDocument(document, preface);

    Paragraph noData = new Paragraph();
    noData.add(
        new Paragraph(
            "Le fonti di dati utilizzate per generare questo rapporto sono elencate di seguito.",
            PdfFont.SMALL_GREY.getFont()));
    PdfUtils.addEmptyLine(noData, 1);
    PdfUtils.addToDocument(document, noData);

    Collections.reverse(wallets);

    // Aggiungiamo un elenco numerato di Wallet
    com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
    for (CryptoWallet wallet : wallets) {
      list.add(new ListItem(wallet.getName()));
    }
    PdfUtils.addToDocument(document, list);
  }
}
