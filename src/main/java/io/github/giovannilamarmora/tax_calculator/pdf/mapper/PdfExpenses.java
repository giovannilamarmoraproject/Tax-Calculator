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
import org.springframework.util.ObjectUtils;

import java.util.List;

public class PdfExpenses {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addTable(Document document, List<Transaction> transactions)
      throws DocumentException {
    document.newPage();
    Paragraph preface = new Paragraph();
    preface.add(new Paragraph("Spese", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    // Aggiungi righe di transazioni filtrate
    List<Transaction> expenses =
        transactions.stream()
            .filter(
                transaction ->
                    !ObjectUtils.isEmpty(transaction.getLabel())
                        && transaction.getLabel().equalsIgnoreCase("expenses"))
            .toList();

    if (PdfUtils.isEmptyData(expenses, document)) return;

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

    // addLostTransactionRows(table, crypto_lost);

    PdfUtils.addToDocument(document, table);
  }
}
