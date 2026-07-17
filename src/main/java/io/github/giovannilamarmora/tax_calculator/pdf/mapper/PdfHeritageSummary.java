package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.pdf.PdfService;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoTaxes;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.math.MathService;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;

public class PdfHeritageSummary {

  private static final Logger LOG = LoggerFilter.getLogger(PdfService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  // Metodo per aggiungere la tabella delle transazioni
  public static void addTable(Document document, List<Transaction> transactions, CryptoTaxes tax)
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

    addHeritageTransactionRows(table, heritageMap, tax);

    PdfUtils.addToDocument(document, table);
  }

  // Metodo per aggiungere l'intestazione della tabella

  private static void addHeritageTransactionRows(
      PdfPTable table, Map<String, Map<String, Double>> heritageMap, CryptoTaxes tax) {
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
          lineCell.setColspan(4); // Fissato da 9 a 4 colonne
          table.addCell(lineCell); // Linea tratteggiata
        });

    // Aggiungi la riga dei totali dal JSON (con fallback di sicurezza)
    CryptoTaxes.Results results = null;
    if (tax != null) {
      results =
          ObjectUtils.isEmpty(tax.getResults())
              ? (tax.getPrevious() != null ? tax.getPrevious().getResults() : null)
              : tax.getResults();
    }

    double totalProfit = 0.0;
    double totalLost = 0.0;
    double totalNet = 0.0;

    double summedProfit = heritageMap.values().stream().mapToDouble(v -> v.get("profit")).sum();
    double summedLost =
        heritageMap.values().stream().mapToDouble(v -> Math.abs(v.get("lost"))).sum();
    double summedNet = heritageMap.values().stream().mapToDouble(v -> v.get("net")).sum();

    if (results != null && results.getCapital_gains() != null) {
      totalProfit = Double.parseDouble(results.getCapital_gains().getProfit());
      totalLost = Math.abs(Double.parseDouble(results.getCapital_gains().getLoss()));
      totalNet = Double.parseDouble(results.getCapital_gains().getNet());

      // Calcolo discrepanza (Koinly taglia alcuni lotti di investimento dall'API)
      double missingProfit = totalProfit - summedProfit;
      double missingLost = totalLost - summedLost;
      double missingNet = missingProfit - missingLost; // approssimazione

      // Se manca più di 1 Euro, aggiungiamo una riga correttiva
      if (missingProfit > 1.0 || missingLost > 1.0) {
        PdfPCell cell =
            new PdfPCell(new Phrase("Plus/minusvalenze compensate", PdfFont.VERY_SMALL.getFont()));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell =
            new PdfPCell(
                new Phrase(
                    String.valueOf(MathService.round(missingProfit, 2)),
                    PdfFont.VERY_SMALL.getFont()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell =
            new PdfPCell(
                new Phrase(
                    String.valueOf(MathService.round(-missingLost, 2)),
                    PdfFont.VERY_SMALL.getFont()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell =
            new PdfPCell(
                new Phrase(
                    String.valueOf(MathService.round(missingNet, 2)),
                    PdfFont.VERY_SMALL.getFont()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(17f);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        DottedLineSeparator dashedLine = new DottedLineSeparator();
        dashedLine.setGap(5f);
        dashedLine.setLineWidth(1f);
        dashedLine.setLineColor(BaseColor.GRAY);
        dashedLine.setAlignment(Element.ALIGN_CENTER);
        dashedLine.setOffset(-2);
        PdfPCell lineCell = new PdfPCell();
        lineCell.addElement(dashedLine);
        lineCell.setBorder(PdfPCell.NO_BORDER);
        lineCell.setColspan(4);
        table.addCell(lineCell);
      }
    } else {
      totalProfit = summedProfit;
      totalLost = summedLost;
      totalNet = summedNet;
    }

    PdfPCell cell = new PdfPCell(new Phrase("Totale", PdfFont.SMALL_BOLD.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(PdfUtils.formatCurrency(totalProfit), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(new Phrase(PdfUtils.formatCurrency(totalLost), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(new Phrase(PdfUtils.formatCurrency(totalNet), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setFixedHeight(17f);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);
  }

  public static Map<String, Map<String, Double>> heritageMap(List<Transaction> transactions) {
    Map<String, Map<String, BigDecimal>> groupedBySymbol =
        transactions.stream()
            .filter(transaction -> transaction.getInvestments() != null)
            .flatMap(transaction -> transaction.getInvestments().stream())
            .filter(investment -> investment.getGain() != null)
            // FILTRO INTELLIGENTE:
            // Se è un trasferimento interno ("own_transfer"), prendi solo il lato di uscita
            // (withdrawal).
            // Se è una transazione normale (es. trade/scambio/sell), prendila sempre!
            .filter(
                investment ->
                    !"own_transfer".equalsIgnoreCase(investment.getSubtype())
                        || investment.isWithdrawal())
            .collect(
                Collectors.groupingBy(
                    investment -> investment.getCurrency().getSymbol(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        investments -> {
                          BigDecimal profit = BigDecimal.ZERO;
                          BigDecimal lost = BigDecimal.ZERO;
                          BigDecimal net = BigDecimal.ZERO;

                          for (var inv : investments) {
                            BigDecimal gainVal = new BigDecimal(inv.getGain());
                            net = net.add(gainVal);
                            if (gainVal.compareTo(BigDecimal.ZERO) >= 0) {
                              profit = profit.add(gainVal);
                            } else {
                              lost = lost.add(gainVal);
                            }
                          }

                          Map<String, BigDecimal> map = new java.util.HashMap<>();
                          map.put("profit", profit);
                          map.put("lost", lost);
                          map.put("net", net);
                          return map;
                        })));

    // Passata 2: Per tutte le transazioni, calcoliamo il root_gain.
    // Se c'è una differenza tra il root_gain e la somma degli investments,
    // l'aggiungiamo alla valuta 'from' della transazione.
    for (Transaction t : transactions) {
      if (t.getGain() == null || "0.0".equals(t.getGain()) || "0".equals(t.getGain())) {
        continue;
      }

      double rootGain = Double.parseDouble(t.getGain());
      double sumInvestments = 0.0;
      if (t.getInvestments() != null) {
        sumInvestments =
            t.getInvestments().stream()
                .filter(
                    inv ->
                        inv.getGain() != null
                            && (!"own_transfer".equalsIgnoreCase(inv.getSubtype())
                                || inv.isWithdrawal()))
                .mapToDouble(inv -> Double.parseDouble(inv.getGain()))
                .sum();
      }

      double missingGain = rootGain - sumInvestments;

      if (Math.abs(missingGain) > 0.01) {
        String symbol = "Altro";
        if (t.getFrom() != null
            && t.getFrom().getCurrency() != null
            && t.getFrom().getCurrency().getSymbol() != null) {
          symbol = t.getFrom().getCurrency().getSymbol();
        } else if (t.getTo() != null
            && t.getTo().getCurrency() != null
            && t.getTo().getCurrency().getSymbol() != null) {
          symbol = t.getTo().getCurrency().getSymbol();
        }

        Map<String, BigDecimal> map =
            groupedBySymbol.computeIfAbsent(
                symbol,
                k -> {
                  Map<String, BigDecimal> m = new java.util.HashMap<>();
                  m.put("profit", BigDecimal.ZERO);
                  m.put("lost", BigDecimal.ZERO);
                  m.put("net", BigDecimal.ZERO);
                  return m;
                });

        BigDecimal missingVal = new BigDecimal(String.valueOf(missingGain));
        map.put("net", map.get("net").add(missingVal));
        if (missingGain >= 0) {
          map.put("profit", map.get("profit").add(missingVal));
        } else {
          map.put("lost", map.get("lost").add(missingVal));
        }
      }
    }

    // Riconvertiamo in Double per mantenere compatibile la firma del tuo metodo
    Map<String, Map<String, Double>> finalMap = new LinkedHashMap<>();

    groupedBySymbol.entrySet().stream()
        .sorted((e1, e2) -> e2.getValue().get("net").compareTo(e1.getValue().get("net")))
        .forEach(
            e -> {
              Map<String, Double> doubleValues = new HashMap<>();
              doubleValues.put("profit", e.getValue().get("profit").doubleValue());
              doubleValues.put("lost", e.getValue().get("lost").doubleValue());
              doubleValues.put("net", e.getValue().get("net").doubleValue());
              finalMap.put(e.getKey(), doubleValues);
            });

    return finalMap;
  }

  @Deprecated
  private static void addHeritageTransactionRowsOld(
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

    // Aggiungi la riga dei totali
    double totalProfit = heritageMap.values().stream().mapToDouble(v -> v.get("profit")).sum();
    double totalLost = heritageMap.values().stream().mapToDouble(v -> v.get("lost")).sum();
    double totalNet = heritageMap.values().stream().mapToDouble(v -> v.get("net")).sum();

    PdfPCell cell = new PdfPCell(new Phrase("Totale", PdfFont.SMALL_BOLD.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(PdfUtils.formatCurrency(totalProfit), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(new Phrase(PdfUtils.formatCurrency(totalLost), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);

    cell =
        new PdfPCell(new Phrase(PdfUtils.formatCurrency(totalNet), PdfFont.SMALL_GREY.getFont()));
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setFixedHeight(17f);
    cell.setBorder(PdfPCell.NO_BORDER);
    table.addCell(cell);
  }

  @Deprecated
  public static Map<String, Map<String, Double>> heritageMapOld(List<Transaction> transactions) {
    List<Transaction> filteredTransactions =
        transactions.stream().filter(transaction -> transaction.getInvestments() != null).toList();

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
