package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoTaxes;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.math.MathService;
import org.springframework.util.ObjectUtils;

public class PdfIncomingGains {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void incomingAndCostSummary(Document document, CryptoTaxes cryptoTaxes)
      throws DocumentException {
    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);
    outerTable.setWidths(new int[] {1, 1});

    PdfPTable leftTable =
        PdfUtils.createSummaryTable(
            "Riepilogo delle entrate",
            "Riassunto di qualsiasi reddito che puoi aver guadagnato da vari eventi cripto durante l'anno fiscale.");

    incomingGainsSummary(leftTable, cryptoTaxes);

    PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);

    PdfPTable rightTable =
        PdfUtils.createSummaryTable(
            "Riepilogo spese",
            "Questi sono costi deducibili che non sono stati inclusi nelle tue plusvalenze, è possibile essere in grado di dedurli altrove nella tua dichiarazione dei redditi.");
    costSummary(rightTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, rightTable, 5, 0);

    document.add(outerTable);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void otherAndGiftSummary(Document document, CryptoTaxes cryptoTaxes)
      throws DocumentException {
    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);
    outerTable.setWidths(new int[] {1, 1});

    PdfPTable leftTable =
        PdfUtils.createSummaryTable(
            "Varie riepilogo",
            "Questa sezione mostra il valore di misc. transazioni contrassegnate.");

    otherGainsSummary(leftTable, cryptoTaxes);

    PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);

    PdfPTable rightTable =
        PdfUtils.createSummaryTable(
            "Regali, donazioni e attivi perduti",
            "Questa sezione mostra il valore di eventuali regali, donazioni o monete perse. Non si realizzano guadagni per queste transazioni.");
    giftSummary(rightTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, rightTable, 5, 0);

    document.add(outerTable);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void incomingGainsSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    PdfUtils.addSummaryTableRow(
        table,
        "Airdrop",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getAirdrop()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Fork",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getFork()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Mining",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getMining()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Reward",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getReward()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Salary",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getSalary()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Lending interest",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getLending_interest()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Other Income",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getOther_income()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€" + MathService.round(Double.parseDouble(results.getIncome().getTotal()), 2),
        null);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void otherGainsSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    PdfUtils.addSummaryTableRow(
        table,
        "Cashback",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getCashback()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Fee refund",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getFee_refund()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Tax",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getTax()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Loan",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getLoan()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Loan repayment",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getLoan_repayment()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Margin loan",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getMargin_loan()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Margin repayment",
        "€" + MathService.round(Double.parseDouble(results.getMisc().getMargin_repayment()), 2),
        null);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void costSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    PdfUtils.addSummaryTableRow(
        table,
        "Cost",
        "€" + MathService.round(Double.parseDouble(results.getExpenses().getCost()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Margin fee",
        "€" + MathService.round(Double.parseDouble(results.getExpenses().getMargin_fee()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Loan fee",
        "€" + MathService.round(Double.parseDouble(results.getExpenses().getLoan_fee()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Transfer fees",
        "€" + MathService.round(Double.parseDouble(results.getExpenses().getTransfer_fees()), 2),
        null);
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€" + MathService.round(Double.parseDouble(results.getExpenses().getTotal()), 2),
        null);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void giftSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    PdfUtils.addSummaryTableRow(
        table,
        "Gift",
        "€" + MathService.round(Double.parseDouble(results.getSpecial().getGift()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Lost",
        "€" + MathService.round(Double.parseDouble(results.getSpecial().getLost()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Donation",
        "€" + MathService.round(Double.parseDouble(results.getSpecial().getDonation()), 2),
        " ");
    PdfUtils.addSummaryTableRow(
        table,
        "Total",
        "€" + MathService.round(Double.parseDouble(results.getSpecial().getTotal()), 2),
        " ");
  }
}
