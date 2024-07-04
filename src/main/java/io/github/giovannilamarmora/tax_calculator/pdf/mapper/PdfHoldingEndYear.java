package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoHolding;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.math.MathService;
import org.springframework.util.ObjectUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class PdfHoldingEndYear {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addHoldingEndOfYearTable(Document document, CryptoHolding holding)
      throws DocumentException {
    document.newPage();
    Paragraph preface = new Paragraph();
    preface.add(new Paragraph("Saldi di fine anno", PdfFont.TITLE_NORMAL.getFont()));
    PdfUtils.addEmptyLine(preface, 1);
    PdfUtils.addToDocument(document, preface);

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setPaddingTop(5);
    table.setWidths(new int[] {4, 2, 2, 2, 2}); // Ensure these values are valid

    // Rimuovi i bordi della tabella
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // Aggiungi la linea grigia sopra al contenuto
    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(PdfPCell.NO_BORDER);
    lineCell.setFixedHeight(1f); // Altezza della linea grigia
    lineCell.setColspan(5); // Numero di colonne della tabella
    table.addCell(lineCell);

    // Aggiungi intestazione della tabella
    PdfUtils.addTableHeader(
        table, List.of("Attivo", "Quantità", "Costo (EUR)", "Valore (EUR)", "Descrizione"));

    addHoldingTransactionRows(table, holding);

    PdfUtils.addToDocument(document, table);
  }

  private static void addHoldingTransactionRows(PdfPTable table, CryptoHolding holding) {

    // Imposta lo spazio prima della tabella per aumentare lo spazio tra le righe
    table.setSpacingBefore(10f);

    List<CryptoHolding.Holding> holdings =
        ObjectUtils.isEmpty(holding.getPrevious())
            ? holding.getResults().getHoldings().stream()
                .filter(
                    holding1 ->
                        Double.parseDouble(holding1.getAmount()) > 0
                            && holding1.getCurrency().isCrypto())
                .toList()
            : holding.getPrevious().getResults().getHoldings().stream()
                .filter(
                    holding1 ->
                        Double.parseDouble(holding1.getAmount()) > 0
                            && holding1.getCurrency().isCrypto())
                .toList();
    // Crea uno stream dalla lista, inverti l'ordine degli elementi e itera
    holdings.forEach(
        hold -> {
          PdfPCell cell =
              new PdfPCell(
                  new Phrase(
                      hold.getCurrency().getSymbol() + " (" + hold.getCurrency().getName() + ")",
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Data

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(Double.parseDouble(hold.getAmount()), 8)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Simbolo valuta

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(Double.parseDouble(hold.getCost()), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Importo

          cell =
              new PdfPCell(
                  new Phrase(
                      String.valueOf(MathService.round(Double.parseDouble(hold.getValue()), 2)),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_LEFT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          table.addCell(cell); // Valore netto

          cell =
              new PdfPCell(
                  new Phrase(
                      ObjectUtils.isEmpty(hold.getCurrency().getMarket().getPrice())
                          ? " "
                          : ("@ USD "
                              + MathService.round(
                                  Double.parseDouble(hold.getCurrency().getMarket().getPrice()), 2)
                              + " per "
                              + hold.getCurrency().getSymbol()),
                      PdfFont.VERY_SMALL.getFont()));
          cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
          cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
          cell.setBorder(PdfPCell.NO_BORDER);
          cell.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
          table.addCell(cell); // Etichetta

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
          lineCell.setColspan(5);
          table.addCell(lineCell); // Linea tratteggiata
        });

    PdfPCell total = new PdfPCell(new Phrase("-"));
    total.setBorder(PdfPCell.NO_BORDER);
    table.addCell(total); // Data

    total = new PdfPCell(new Phrase("-"));
    total.setBorder(PdfPCell.NO_BORDER);
    table.addCell(total); // Data

    total =
        new PdfPCell(
            new Phrase(
                String.valueOf(
                    MathService.round(
                        Double.parseDouble(
                            ObjectUtils.isEmpty(holding.getPrevious())
                                ? holding.getResults().getTotal_cost()
                                : holding.getPrevious().getResults().getTotal_cost()),
                        2)),
                PdfFont.VERY_SMALL.getFont()));
    total.setHorizontalAlignment(Element.ALIGN_LEFT);
    total.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
    total.setBorder(PdfPCell.NO_BORDER);
    table.addCell(total); // Importo

    total =
        new PdfPCell(
            new Phrase(
                String.valueOf(
                    MathService.round(
                        Double.parseDouble(
                            ObjectUtils.isEmpty(holding.getPrevious())
                                ? holding.getResults().getTotal_value()
                                : holding.getPrevious().getResults().getTotal_value()),
                        2)),
                PdfFont.VERY_SMALL.getFont()));
    total.setHorizontalAlignment(Element.ALIGN_LEFT);
    total.setVerticalAlignment(Element.ALIGN_MIDDLE); // Centra verticalmente il testo
    total.setBorder(PdfPCell.NO_BORDER);
    table.addCell(total); // Valore netto

    total = new PdfPCell(new Phrase("-"));
    total.setVerticalAlignment(Element.ALIGN_RIGHT);
    total.setBorder(PdfPCell.NO_BORDER);
    total.setFixedHeight(17f); // Altezza maggiore per la riga della descrizione
    table.addCell(total); // Etichetta
  }
}
