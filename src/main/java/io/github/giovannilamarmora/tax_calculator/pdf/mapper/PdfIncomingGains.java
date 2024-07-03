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

public class PdfIncomingGains {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void incomingAndCostSummary(Document document, CryptoTaxes cryptoTaxes)
      throws DocumentException {
    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);
    outerTable.setWidths(new int[] {1, 1});

    // Sezione sinistra
    PdfPTable leftTable = new PdfPTable(2);
    leftTable.setWidthPercentage(100);

    PdfPCell cell =
        new PdfPCell(new Phrase("Riepilogo delle entrate", PdfFont.TITLE_NORMAL.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(0);
    cell.setColspan(2);
    leftTable.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(
                "Riassunto di qualsiasi reddito che puoi aver guadagnato da vari"
                    + "eventi cripto durante l'anno fiscale.",
                PdfFont.SUBTITLE.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(10);
    cell.setColspan(2);
    leftTable.addCell(cell);

    incomingGainsSummary(leftTable, cryptoTaxes);
    //PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);

    cell = new PdfPCell(new Phrase("Riepilogo spese", PdfFont.TITLE_NORMAL.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(0);
    cell.setPaddingTop(30);
    cell.setColspan(2);
    leftTable.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(
                "Questi sono costi deducibili che non sono stati inclusi nelle tue"
                    + "plusvalenze, è possibile essere in grado di dedurli altrove nella tua"
                    + "dichiarazione dei redditi.",
                PdfFont.SUBTITLE.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(10);
    cell.setColspan(2);
    leftTable.addCell(cell);

    costSummary(leftTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);

    /*PdfPCell leftCell = new PdfPCell(leftTable);
    leftCell.setBorder(Rectangle.NO_BORDER);
    leftCell.setPaddingRight(5);
    outerTable.addCell(leftCell);*/

    // Sezione destra
    /*PdfPTable rightTable = new PdfPTable(2);
    rightTable.setWidthPercentage(100);

    cell =
        new PdfPCell(
            new Phrase("Regali, donazioni e attivi perduti", PdfFont.TITLE_NORMAL.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(0);
    cell.setColspan(2);
    rightTable.addCell(cell);

    cell =
        new PdfPCell(
            new Phrase(
                "Questa sezione mostra il valore di eventuali regali, donazioni o"
                    + "monete perse. Non si realizzano guadagni per queste transazioni.",
                PdfFont.SUBTITLE.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(10);
    cell.setColspan(2);
    rightTable.addCell(cell);*/
    PdfPTable rightTable =
        PdfUtils.createSummaryTable(
            "Regali, donazioni e attivi perduti",
            "Questa sezione mostra il valore di eventuali regali, donazioni o monete perse. Non si realizzano guadagni per queste transazioni.");
    giftSummary(rightTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, rightTable, 5, 0);

    /*PdfPCell rightCell = new PdfPCell(rightTable);
    rightCell.setBorder(Rectangle.NO_BORDER);
    rightCell.setPaddingLeft(5);
    outerTable.addCell(rightCell);*/

    document.add(outerTable);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void incomingGainsSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    PdfUtils.addSummaryTableRow(
        table,
        "Airdrop",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getAirdrop()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Fork",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getFork()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Mining",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getMining()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Reward",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getReward()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Loan Interest",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getLending_interest()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Other Income",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getOther_income()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getIncome().getTotal()), 2),
        null);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void costSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    PdfUtils.addSummaryTableRow(
        table,
        "Cost",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExpenses().getCost()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Margin trade fee",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExpenses().getMargin_fee()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Margin interest fee",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExpenses().getLoan_fee()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Transfer fees",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExpenses().getTransfer_fees()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getExpenses().getTotal()), 2),
        null);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void giftSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    PdfUtils.addSummaryTableRow(
        table,
        "Gift",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getSpecial().getGift()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Lost",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getSpecial().getLost()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Donation",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getSpecial().getDonation()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€"
            + MathService.round(
                Double.parseDouble(cryptoTaxes.getResults().getSpecial().getTotal()), 2),
        " ");
  }
}
