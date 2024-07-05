package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;

import java.util.List;

public class PdfCapitalGainsTransaction {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void addCapitalGainsTransactions(Document document, List<Transaction> transactions)
      throws DocumentException {
    document.newPage();
    Paragraph preface = new Paragraph();
    preface.add(new Paragraph("Transazioni di plusvalenze", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(9);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    table.setWidths(new float[] {2, 2, 1, 2, 1.5f, 1.5f, 2, 1, 3}); // Ensure these values are valid

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(9); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    PdfUtils.addTableHeader(
        table,
        List.of(
            "Data vendita",
            "Data acquisto",
            "Attivo",
            "Importo",
            "Costo (EUR)",
            "Ricavi (EUR)",
            "Guadagno/perdita",
            "Note",
            "Nome del portafoglio"));

    // Aggiungi righe di transazioni filtrate
    List<Transaction> crypto_withdrawal =
        transactions.stream()
            .filter(
                transaction ->
                    transaction.getType().equalsIgnoreCase("crypto_withdrawal")
                        || transaction.getType().equalsIgnoreCase("exchange"))
            .toList();
    //addGainTransactionRows(table, crypto_withdrawal);

    PdfUtils.addToDocument(document, table);
  }
}
