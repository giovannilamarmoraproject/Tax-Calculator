package io.github.giovannilamarmora.tax_calculator.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.app.model.TaxRequest;
import io.github.giovannilamarmora.tax_calculator.pdf.mapper.PdfCapitalGains;
import io.github.giovannilamarmora.tax_calculator.pdf.mapper.PdfIncomingGains;
import io.github.giovannilamarmora.tax_calculator.pdf.mapper.PdfOverview;
import io.github.giovannilamarmora.tax_calculator.pdf.mapper.PdfUtils;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import io.github.giovannilamarmora.utils.math.MathService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PdfService {

  public static byte[] generatePdf(TaxRequest taxRequest) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Document document = new Document();

    try {
      PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
      writer.setPageEvent(new PdfFooterPageEvent(taxRequest.getTax().getResults().getYear()));
      // TransactionHeader event = new TransactionHeader();
      // writer.setPageEvent(event);
      document.open();
      // Prima pagina
      PdfOverview.setFirstPage(document, taxRequest);

      // Nuova pagina in landscape
      document.setPageSize(PageSize.A4.rotate());
      document.newPage();

      // Aggiungi il riepilogo plusvalenze e le entrate
      PdfCapitalGains.addCapitalGainsAndIncomeSummary(document, taxRequest.getTax());

      PdfIncomingGains.incomingAndCostSummary(document, taxRequest.getTax());
      document.newPage();

      addCapitalGainsTransactions(document, taxRequest.getTransactions());
      document.newPage();

      addInnerTransactionsTable(document, taxRequest.getTransactions());

      document.close();
      System.out.println("PDF creato con successo!");

    } catch (DocumentException e) {
      e.printStackTrace();
    }

    return byteArrayOutputStream.toByteArray();
  }

  /*
   * Fourth Page
   */

  // Metodo per aggiungere la tabella delle transazioni
  private static void addInnerTransactionsTable(Document document, List<Transaction> transactions) {
    Paragraph preface = new Paragraph();
    preface.add(new Paragraph("Transazioni entrate", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    try {
      table.setWidths(new int[] {3, 2, 3, 2, 2, 3}); // Ensure these values are valid
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(6); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    addTableHeader(table, List.of("Data", "Attivo", "Importo", "Valore (EUR)", "Tipo", "Note"));

    // Aggiungi righe di transazioni filtrate
    List<Transaction> crypto_deposit =
        transactions.stream()
            .filter(
                transaction ->
                    transaction.getType().equalsIgnoreCase("crypto_deposit")
                        && transaction.getLabel() != null)
            .toList();
    addInnerTransactionRows(table, crypto_deposit);

    PdfUtils.addToDocument(document, table);
  }

  // Metodo per aggiungere la tabella delle transazioni
  private static void addCapitalGainsTransactions(
      Document document, List<Transaction> transactions) {
    Paragraph preface = new Paragraph();
    preface.add(new Paragraph("Transazioni di plusvalenze", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(8);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    try {
      // table.setWidths(new int[] {3, 3, 2, 3, 2, 2, 2, 3}); // Ensure these values are valid
      table.setWidths(new float[] {2, 2, 1, 2, 1.5f, 1.5f, 2, 2}); // Ensure these values are valid
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(8); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    addTableHeader(
        table,
        List.of(
            "Data vendita",
            "Data acquisto",
            "Attivo",
            "Importo",
            "Costo (EUR)",
            "Ricavi (EUR)",
            "Guadagno/perdita",
            "Note"));

    // Aggiungi righe di transazioni filtrate
    List<Transaction> crypto_withdrawal =
        transactions.stream()
            .filter(
                transaction ->
                    transaction.getType().equalsIgnoreCase("crypto_withdrawal")
                        || transaction.getType().equalsIgnoreCase("exchange"))
            .toList();
    addGainTransactionRows(table, crypto_withdrawal);

    PdfUtils.addToDocument(document, table);
  }

  // Metodo per aggiungere l'intestazione della tabella
  private static void addTableHeader(PdfPTable table, List<String> headers) {
    headers.forEach(
        columnTitle -> {
          PdfPCell header = new PdfPCell();
          header.setBorder(PdfPCell.BOTTOM);
          header.setPaddingBottom(10);
          header.setBorderColor(BaseColor.LIGHT_GRAY);
          header.setBorderWidth(1);

          // Se è l'ultimo elemento, posizionalo a destra
          if (headers.indexOf(columnTitle) == headers.size() - 1) {
            header.setHorizontalAlignment(Element.ALIGN_RIGHT);
          } else {
            // Altrimenti, posizionalo a sinistra
            header.setHorizontalAlignment(Element.ALIGN_LEFT);
          }

          header.setPhrase(new Phrase(columnTitle, PdfFont.SMALL_BOLD.getFont()));
          table.addCell(header);
        });
  }

  private static void addGainTransactionRows(PdfPTable table, List<Transaction> transactions) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);

    // Crea uno stream dalla lista, inverti l'ordine degli elementi e itera
    IntStream.range(0, transactions.size())
        .mapToObj(i -> transactions.get(transactions.size() - 1 - i))
        .forEach(
            transaction -> {
              PdfPCell cell =
                  new PdfPCell(
                      new Phrase(
                          ZonedDateTime.parse(transaction.getDate())
                              .withZoneSameInstant(ZoneId.systemDefault())
                              .format(formatter),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Data

              cell =
                  new PdfPCell(
                      new Phrase(
                          ZonedDateTime.parse(transaction.getDate())
                              .withZoneSameInstant(ZoneId.systemDefault())
                              .format(formatter),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Data

              cell =
                  new PdfPCell(
                      new Phrase(
                          transaction.getFrom().getCurrency().getSymbol(),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Simbolo valuta

              cell =
                  new PdfPCell(
                      new Phrase(transaction.getFrom().getAmount(), PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Importo

              cell =
                  new PdfPCell(
                      new Phrase(
                          String.valueOf(
                              MathService.round(
                                  Double.parseDouble(transaction.getFrom().getCost_basis()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_CENTER);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          String.valueOf(
                              MathService.round(Double.parseDouble(transaction.getNet_value()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_CENTER);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          String.valueOf(
                              MathService.round(Double.parseDouble(transaction.getGain()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_CENTER);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(transaction.getDescription(), PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              cell.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
              cell.setColspan(6); // Numero di colonne della tabella
              table.addCell(cell); // Descrizione

              // Aggiungi la linea tratteggiata tra le righe
              DottedLineSeparator dashedLine = new DottedLineSeparator();
              dashedLine.setGap(5f);
              dashedLine.setLineWidth(1f);
              dashedLine.setLineColor(BaseColor.GRAY);
              dashedLine.setAlignment(Element.ALIGN_CENTER);
              dashedLine.setOffset(-2);
              PdfPCell lineCell = new PdfPCell();
              lineCell.addElement(dashedLine);
              lineCell.setBorder(PdfPCell.NO_BORDER);
              lineCell.setColspan(8);
              table.addCell(lineCell); // Linea tratteggiata
            });
  }

  private static void addInnerTransactionRows(PdfPTable table, List<Transaction> transactions) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);

    // Crea uno stream dalla lista, inverti l'ordine degli elementi e itera
    IntStream.range(0, transactions.size())
        .mapToObj(i -> transactions.get(transactions.size() - 1 - i))
        .forEach(
            transaction -> {
              PdfPCell cell =
                  new PdfPCell(
                      new Phrase(
                          ZonedDateTime.parse(transaction.getDate())
                              .withZoneSameInstant(ZoneId.systemDefault())
                              .format(formatter),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Data

              cell =
                  new PdfPCell(
                      new Phrase(
                          transaction.getTo().getCurrency().getSymbol(),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Simbolo valuta

              cell =
                  new PdfPCell(
                      new Phrase(transaction.getTo().getAmount(), PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Importo

              cell =
                  new PdfPCell(
                      new Phrase(
                          String.valueOf(
                              MathService.round(Double.parseDouble(transaction.getNet_value()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          ObjectUtils.isEmpty(transaction.getLabel())
                              ? " "
                              : transaction.getLabel().toUpperCase(),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_LEFT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Etichetta

              cell =
                  new PdfPCell(
                      new Phrase(transaction.getDescription(), PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              cell.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
              cell.setColspan(6); // Numero di colonne della tabella
              table.addCell(cell); // Descrizione

              // Aggiungi la linea tratteggiata tra le righe
              DottedLineSeparator dashedLine = new DottedLineSeparator();
              dashedLine.setGap(5f);
              dashedLine.setLineWidth(1f);
              dashedLine.setLineColor(BaseColor.GRAY);
              dashedLine.setAlignment(Element.ALIGN_CENTER);
              dashedLine.setOffset(-2);
              PdfPCell lineCell = new PdfPCell();
              lineCell.addElement(dashedLine);
              lineCell.setBorder(PdfPCell.NO_BORDER);
              lineCell.setColspan(6);
              table.addCell(lineCell); // Linea tratteggiata
            });
  }
}
