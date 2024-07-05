package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.math.MathService;
import org.springframework.util.ObjectUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class PdfLoss {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addTable(Document document, List<Transaction> transactions)
      throws DocumentException {
    document.newPage();
    Paragraph preface = new Paragraph();
    preface.add(
        new Paragraph("Regali, donazioni e attivi perduti", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(7);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    table.setWidths(new float[] {3, 1.5f, 1.5f, 1.5f, 1.5f, 2, 3}); // Ensure these values are valid

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(7); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    PdfUtils.addTableHeader(
        table,
        List.of(
            "Data",
            "Attivo",
            "Importo",
            "Valore (EUR)",
            "Tipo",
            "Descrizione",
            "Nome del portafoglio"));

    // Aggiungi righe di transazioni filtrate
    List<Transaction> crypto_lost =
        transactions.stream()
            .filter(
                transaction ->
                    !ObjectUtils.isEmpty(transaction.getLabel())
                        && transaction.getLabel().equalsIgnoreCase("lost"))
            .toList();
    addLostTransactionRows(table, crypto_lost);

    PdfUtils.addToDocument(document, table);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void addLostTransactionRows(PdfPTable table, List<Transaction> transactions) {
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
                          transaction.getDate().atZone(ZoneId.systemDefault()).format(formatter),
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
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Simbolo valuta

              cell =
                  new PdfPCell(
                      new Phrase(transaction.getFrom().getAmount(), PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          ObjectUtils.isEmpty(transaction.getLabel())
                              ? " "
                              : PdfUtils.upperCamelCase(transaction.getLabel()),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
              table.addCell(cell); // Descrizione

              cell =
                  new PdfPCell(
                      new Phrase(
                          transaction.getFrom().getWallet().getName(),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              cell.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
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
              lineCell.setColspan(7);
              table.addCell(lineCell); // Linea tratteggiata
            });
  }
}
