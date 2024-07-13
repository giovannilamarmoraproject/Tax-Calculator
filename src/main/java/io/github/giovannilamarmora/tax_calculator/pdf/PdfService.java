package io.github.giovannilamarmora.tax_calculator.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import io.github.giovannilamarmora.tax_calculator.app.model.TaxRequest;
import io.github.giovannilamarmora.tax_calculator.pdf.mapper.*;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class PdfService {

  private static final Logger LOG = LoggerFilter.getLogger(PdfService.class);

  public static byte[] generatePdf(TaxRequest taxRequest) {
    LOG.info("Analyzing {} Transactions", taxRequest.getTransactions().size());
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Document document = new Document();

    try {
      PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
      int year =
          ObjectUtils.isEmpty(taxRequest.getTax().getResults())
              ? taxRequest.getTax().getPrevious().getResults().getYear()
              : taxRequest.getTax().getResults().getYear();
      writer.setPageEvent(new PdfFooterPageEvent(year));
      // TransactionHeader event = new TransactionHeader();
      // writer.setPageEvent(event);
      document.open();
      document.addTitle("Tax-Report_" + LocalDateTime.now());
      // Prima pagina
      PdfOverview.setFirstPage(document, year);

      // Nuova pagina in landscape
      document.setPageSize(PageSize.A4.rotate());

      // Aggiungi il riepilogo plusvalenze e le entrate
      PdfCapitalGains.addCapitalGainsAndIncomeSummary(document, taxRequest.getTax());

      PdfIncomingGains.incomingAndCostSummary(document, taxRequest.getTax());

      PdfIncomingGains.otherAndGiftSummary(document, taxRequest.getTax());

      PdfHeritageSummary.addTable(document, taxRequest.getTransactions());

      PdfHoldingEndYear.addHoldingEndOfYearTable(document, taxRequest.getHoldings());

      PdfCapitalGainsTransaction.addTable(document, taxRequest.getTransactions());

      PdfInnerTransaction.addTable(document, taxRequest.getTransactions());

      PdfLoss.addTable(document, taxRequest.getTransactions());

      PdfExpenses.addTable(document, taxRequest.getTransactions());

      PdfDataSource.addTable(document, taxRequest.getWallets());

      // PdfOtherTransactions.addTable(document, taxRequest.getTransactions());
      document.close();
      LOG.info("PDF creato con successo!");

    } catch (DocumentException e) {
      LOG.error("Exception message is {}", e.getMessage());
      throw new PDFException(e.getMessage());
    }

    return byteArrayOutputStream.toByteArray();
  }
}
