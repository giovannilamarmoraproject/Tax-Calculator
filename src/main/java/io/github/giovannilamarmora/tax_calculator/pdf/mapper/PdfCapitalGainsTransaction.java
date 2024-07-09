package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.pdf.PdfService;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoInvestment;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.math.MathService;
import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class PdfCapitalGainsTransaction {

  private static final Logger LOG = LoggerFilter.getLogger(PdfService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  // Metodo per aggiungere la tabella delle transazioni
  public static void addTable(Document document, List<Transaction> transactions)
      throws DocumentException {
    document.newPage();
    Paragraph preface = new Paragraph("Transazioni di plusvalenze", PdfFont.TITLE_NORMAL.getFont());
    preface.setPaddingTop(-10);
    // PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    // Aggiungi righe di transazioni filtrate
    List<Transaction> crypto_withdrawal =
        transactions.stream()
            .filter(
                transaction ->
                    transaction.getType().equalsIgnoreCase("crypto_withdrawal")
                        || transaction.getType().equalsIgnoreCase("exchange")
                        || transaction.getType().equalsIgnoreCase("sell"))
            .toList();

    if (PdfUtils.isEmptyData(crypto_withdrawal, document)) return;

    PdfPTable table = new PdfPTable(9);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    table.setWidths(
        new float[] {2, 2, 1, 2, 1.5f, 1.5f, 2, 1.1f, 3}); // Ensure these values are valid

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

    addGainTransactionRows(table, crypto_withdrawal);

    PdfUtils.addToDocument(document, table);
  }

  // Metodo per aggiungere l'intestazione della tabella

  private static void addGainTransactionRows(PdfPTable table, List<Transaction> transactions) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);

    // Crea uno stream dalla lista, inverti l'ordine degli elementi e itera
    IntStream.range(0, transactions.size())
        .mapToObj(i -> transactions.get(transactions.size() - 1 - i))
        .forEach(
            transaction -> {
              if (!ObjectUtils.isEmpty(transaction.getInvestments())) {
                addFeeTransactionsRows(table, transaction.getInvestments(), transaction);
                return;
              }

              if (!ObjectUtils.isEmpty(transaction.getFee())
                  && transaction.getType().equalsIgnoreCase("exchange")) {
                LOG.info("Transaction with id {} ignored", transaction.getId());
                return;
              }

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
                          transaction.getDate().atZone(ZoneId.systemDefault()).format(formatter),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
                          String.valueOf(
                              MathService.round(Double.parseDouble(transaction.getNet_value()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          String.valueOf(
                              MathService.round(Double.parseDouble(transaction.getGain()), 2)),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
              table.addCell(cell); // Valore netto

              cell =
                  new PdfPCell(
                      new Phrase(
                          PdfUtils.cutStringToMaxLength(transaction.getDescription(), 11),
                          PdfFont.VERY_SMALL.getFont()));
              cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
              cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
              cell.setBorder(PdfPCell.NO_BORDER);
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
              lineCell.setColspan(9);
              table.addCell(lineCell); // Linea tratteggiata
            });
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void addFeeTransactionsRows(
      PdfPTable table, List<CryptoInvestment> investments, Transaction transaction) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    if (ObjectUtils.isEmpty(investments)) return;

    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);
    investments.sort(
        Comparator.comparingDouble(
            investment -> Math.abs(Double.parseDouble(investment.getAmount()))));

    investments.forEach(
        cryptoInvestment -> {
          if (ObjectUtils.isEmpty(cryptoInvestment.getFrom_date())) return;
          PdfPCell cell =
              new PdfPCell(
                  new Phrase(
                      ObjectUtils.isEmpty(cryptoInvestment.getDate())
                          ? "-"
                          : cryptoInvestment
                              .getDate()
                              .atZone(ZoneId.systemDefault())
                              .format(formatter),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Data

          cell =
              new PdfPCell(
                  new Phrase(
                      ObjectUtils.isEmpty(cryptoInvestment.getFrom_date())
                          ? "-"
                          : cryptoInvestment
                              .getFrom_date()
                              .atZone(ZoneId.systemDefault())
                              .format(formatter),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Data

          cell =
              new PdfPCell(
                  new Phrase(
                      cryptoInvestment.getCurrency().getSymbol(), PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Simbolo valuta

          BigDecimal amount = new BigDecimal(cryptoInvestment.getAmount());
          cell =
              new PdfPCell(new Phrase(amount.abs().toPlainString(), PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Importo

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(
                          MathService.round(Double.parseDouble(cryptoInvestment.getValue()), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Valore netto

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(
                          MathService.round(
                              Double.parseDouble(cryptoInvestment.getValue())
                                  + Double.parseDouble(cryptoInvestment.getGain()),
                              2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Valore netto

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(
                          MathService.round(Double.parseDouble(cryptoInvestment.getGain()), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Valore netto

          cell = new PdfPCell(new Phrase(cryptoInvestment.getInfo(), PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Descrizione

          cell =
              new PdfPCell(
                  new Phrase(
                      transaction.getFrom().getWallet().getName(), PdfFont.VERY_SMALL.getFont()));
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
          lineCell.setColspan(9);
          table.addCell(lineCell); // Linea tratteggiata
        });
  }
}
