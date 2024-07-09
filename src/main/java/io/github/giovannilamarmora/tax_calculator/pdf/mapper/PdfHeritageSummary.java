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
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;

public class PdfHeritageSummary {

  private static final Logger LOG = LoggerFilter.getLogger(PdfService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  // Metodo per aggiungere la tabella delle transazioni
  public static void addTable(Document document, List<Transaction> transactions)
      throws DocumentException {
    Map<String, Map<String, Double>> heritageMap = heritageMap(transactions);

    document.newPage();
    Paragraph preface = new Paragraph("Riepilogo del patrimonio", PdfFont.TITLE_NORMAL.getFont());
    preface.setPaddingTop(-10);
    // PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    table.setWidths(new float[] {3, 2, 2, 2}); // Ensure these values are valid

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(4); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    PdfUtils.addTableHeader(
        table, List.of("Attivo", "Profitto (EUR)", "Perdita (EUR)", "Netto (EUR)"));

    addHeritageTransactionRows(table, heritageMap);

    PdfUtils.addToDocument(document, table);
  }

  // Metodo per aggiungere l'intestazione della tabella

  private static void addHeritageTransactionRows(
      PdfPTable table, Map<String, Map<String, Double>> heritageMap) {
    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);

    heritageMap.forEach(
        (symbol, values) -> {
          PdfPCell cell = new PdfPCell(new Phrase(symbol, PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Data

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(values.get("profit"), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Data

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(values.get("lost"), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Simbolo valuta

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(values.get("net"), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Importo

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

  public static Map<String, Map<String, Double>> heritageMap(List<Transaction> transactions) {
    // Filtrare le transazioni secondo le regole specificate
    List<Transaction> filteredTransactions =
        transactions.stream()
            .filter(
                transaction ->
                    transaction.getType().equalsIgnoreCase("crypto_withdrawal")
                        || transaction.getType().equalsIgnoreCase("exchange")
                        || transaction.getType().equalsIgnoreCase("sell")
                        || (transaction.getType().equalsIgnoreCase("crypto_deposit")
                            && transaction.getLabel() != null)
                        || (!ObjectUtils.isEmpty(transaction.getLabel())
                            && transaction.getLabel().equalsIgnoreCase("lost"))
                        || (!ObjectUtils.isEmpty(transaction.getLabel())
                            && transaction.getLabel().equalsIgnoreCase("expenses")))
            .toList();

    // Raggruppare per symbol e calcolare i valori richiesti
    Map<String, Map<String, Double>> groupedBySymbol =
        filteredTransactions.stream()
            .filter(
                transaction ->
                    transaction.getInvestments()
                        != null) // Verificare che gli investimenti non siano null
            .flatMap(
                transaction ->
                    transaction
                        .getInvestments()
                        .stream()) // Accedere agli investimenti di ogni transazione
            .collect(
                Collectors.groupingBy(
                    investment ->
                        investment
                            .getCurrency()
                            .getSymbol(), // Raggruppare per simbolo della valuta
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        investments -> {
                          double profit =
                              investments.stream()
                                  .mapToDouble(
                                      investment -> Double.parseDouble(investment.getGain()))
                                  .filter(gain -> gain > 0)
                                  .sum();

                          double lost =
                              investments.stream()
                                  .mapToDouble(
                                      investment -> Double.parseDouble(investment.getGain()))
                                  .filter(gain -> gain < 0)
                                  .map(Math::abs)
                                  .sum();

                          double net =
                              investments.stream()
                                  .mapToDouble(
                                      investment -> Double.parseDouble(investment.getGain()))
                                  .sum();

                          Map<String, Double> resultMap = new HashMap<>();
                          resultMap.put("profit", profit);
                          resultMap.put("lost", lost);
                          resultMap.put("net", net);

                          return resultMap;
                        })));

    // Ordinare per valore di 'net' e ritornare una mappa ordinata
    return groupedBySymbol.entrySet().stream()
        .sorted((e1, e2) -> Double.compare(e2.getValue().get("net"), e1.getValue().get("net")))
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) ->
                    e1, // Merge function per gestire i duplicati (non dovrebbe essere necessaria)
                LinkedHashMap::new // Usare LinkedHashMap per mantenere l'ordine
                ));
  }
}
