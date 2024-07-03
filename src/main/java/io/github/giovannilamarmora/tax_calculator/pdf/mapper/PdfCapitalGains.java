package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoTaxes;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.math.MathService;

public class PdfCapitalGains {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addCapitalGainsAndIncomeSummary(Document document, CryptoTaxes cryptoTaxes)
      throws DocumentException {
    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);
    outerTable.setWidths(new int[] {1, 1});

    // Sezione sinistra
    PdfPTable leftTable =
        PdfUtils.createSummaryTable(
            "Riepilogo plusvalenze",
            "Riepilogo dei tuoi profitti e perdite da cessioni/vendite/scambi di criptovalute.");
    /*PdfPTable leftTable = new PdfPTable(2);
     leftTable.setWidthPercentage(100);

    PdfPCell cell =
         new PdfPCell(new Phrase("Riepilogo plusvalenze", PdfFont.TITLE_NORMAL.getFont()));
     cell.setBorder(Rectangle.NO_BORDER);
     cell.setPaddingBottom(0);
     cell.setColspan(2);
     leftTable.addCell(cell);

     cell =
         new PdfPCell(
             new Phrase(
                 "Riepilogo dei tuoi profitti e perdite da cessioni/vendite/scambi di criptovalute.",
                 PdfFont.SUBTITLE.getFont()));
     cell.setBorder(Rectangle.NO_BORDER);
     cell.setPaddingBottom(10);
     cell.setColspan(2);
     leftTable.addCell(cell);*/

    capitalGainsSummary(leftTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);

    /*PdfPCell leftCell = new PdfPCell(leftTable);
    leftCell.setBorder(Rectangle.NO_BORDER);
    leftCell.setPaddingRight(5);
    outerTable.addCell(leftCell);*/

    // Sezione destra
    /*PdfPTable rightTable = new PdfPTable(2);
    rightTable.setWidthPercentage(100);

    cell =
        new PdfPCell(new Phrase("Riassunto operazioni di margine", PdfFont.TITLE_NORMAL.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(0);
    cell.setColspan(2);
    rightTable.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(
                "I guadagni da operazioni con CFD, futures e margini sono riassunti di"
                    + "seguito. Questi guadagni NON sono stati inclusi nelle tue plusvalenze"
                    + "e potresti invece volerli dichiarare separatamente",
                PdfFont.SUBTITLE.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(10);
    cell.setColspan(2);
    rightTable.addCell(cell);*/
    PdfPTable rightTable =
        PdfUtils.createSummaryTable(
            "Riassunto operazioni di margine",
            "I guadagni da operazioni con CFD, futures e margini sono riassunti di seguito. Questi guadagni NON sono stati inclusi nelle tue plusvalenze e potresti invece volerli dichiarare separatamente");
    marginOperationSummary(rightTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, rightTable, 5, 0);
    /*PdfPCell rightCell = new PdfPCell(rightTable);
    rightCell.setBorder(Rectangle.NO_BORDER);
    rightCell.setPaddingLeft(5);
    outerTable.addCell(rightCell);*/

    PdfUtils.addToDocument(document, outerTable);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void capitalGainsSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    PdfUtils.addSummaryTableRow(
        table,
        "Numero di cessioni",
        String.valueOf(cryptoTaxes.getResults().getCapital_gains().getDisposals()),
        "Numero di eventi imponibili durante l'anno: questo numero potrebbe essere\n"
            + "superiore al numero effettivo di operazioni a causa di vendite parziali");
    PdfUtils.addSummaryTableRow(
        table,
        "Il ricavato delle vendite",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getCapital_gains().getProceeds()), 2),
        "Questo è l'importo che hai ricevuto da tutte le tue cessioni. Questa cifra può"
            + "essere più alta del previsto se hai anche fatto operazioni con i ricavi delle"
            + "transazioni precedenti");
    PdfUtils.addSummaryTableRow(
        table,
        "Costi di acquisizione",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getCapital_gains().getCosts()), 2),
        "Importo che hai pagato per acquistare gli attivi, comprese le commissioni\n"
            + "delle operazioni");
    PdfUtils.addSummaryTableRow(
        table,
        "Profitti, prima delle perdite",
        "€"
            + MathService.round(
                Double.parseDouble((cryptoTaxes.getResults().getCapital_gains().getProfit())), 2),
        "Importo del profitto che hai realizzato dalle tue operazioni dopo aver dedotto\n"
            + "eventuali costi");
    PdfUtils.addSummaryTableRow(
        table,
        "Perdite",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getCapital_gains().getLoss()), 2),
        "Importo delle perdite che hai realizzato dalle tue operazioni dopo aver\n"
            + "dedotto eventuali costi");
    PdfUtils.addSummaryTableRow(
        table,
        "Ricavi netti",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getCapital_gains().getNet()), 2),
        "Guadagno totale dalle tue operazioni, questo è il profitto meno le perdite");
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void marginOperationSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    PdfUtils.addSummaryTableRow(
        table,
        "Numero di operazioni",
        String.valueOf(cryptoTaxes.getResults().getExternal_gains().getCount()),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Profitti di margine",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExternal_gains().getProfit()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Perdite di margine",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExternal_gains().getLoss()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Guadagno netto",
        "€"
            + MathService.round(
                Double.parseDouble((cryptoTaxes.getResults().getExternal_gains().getNet())), 2),
        " ");
  }
}
