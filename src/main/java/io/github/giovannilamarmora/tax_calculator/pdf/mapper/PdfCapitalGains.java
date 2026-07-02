package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoTaxes;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.springframework.util.ObjectUtils;

public class PdfCapitalGains {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addCapitalGainsAndIncomeSummary(Document document, CryptoTaxes cryptoTaxes)
      throws DocumentException {
    document.newPage();
    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);

    // Sezione Riepilogo Plusvalenze
    PdfPTable leftTable =
        PdfUtils.createSummaryTable(
            "Riepilogo plusvalenze",
            "Riepilogo dei tuoi profitti e perdite da cessioni/vendite/scambi di criptovalute.");

    capitalGainsSummary(leftTable, cryptoTaxes);
    PdfUtils.addToOuterTable(outerTable, leftTable, 0, 5);
    PdfUtils.addToOuterTable(outerTable, new PdfPTable(1), 0, 5); // Empty right column
    PdfUtils.addToDocument(document, outerTable);

    // Sezione Futures su nuova pagina
    document.newPage();
    PdfPTable futuresTableOuter = new PdfPTable(2);
    futuresTableOuter.setWidthPercentage(100);

    marginOperationSummary(futuresTableOuter, cryptoTaxes);

    PdfUtils.addToDocument(document, futuresTableOuter);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void capitalGainsSummary(PdfPTable table, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    PdfUtils.addSummaryTableRow(
        table,
        "Numero di cessioni",
        String.valueOf(results.getCapital_gains().getDisposals()),
        "Numero di eventi imponibili durante l'anno: questo numero potrebbe essere\n"
            + "superiore al numero effettivo di operazioni a causa di vendite parziali");
    PdfUtils.addSummaryTableRow(
        table,
        "Il ricavato delle vendite",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getCapital_gains().getProceeds())),
        "Questo è l'importo che hai ricevuto da tutte le tue cessioni. Questa cifra può "
            + "essere più alta del previsto se hai anche fatto operazioni con i ricavi delle"
            + "transazioni precedenti");
    PdfUtils.addSummaryTableRow(
        table,
        "Costi di acquisizione",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getCapital_gains().getCosts())),
        "Importo che hai pagato per acquistare gli attivi, comprese le commissioni\n"
            + "delle operazioni");
    PdfUtils.addSummaryTableRow(
        table,
        "Profitti, prima delle perdite",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getCapital_gains().getProfit())),
        "Importo del profitto che hai realizzato dalle tue operazioni dopo aver dedotto\n"
            + "eventuali costi");
    PdfUtils.addSummaryTableRow(
        table,
        "Perdite",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getCapital_gains().getLoss())),
        "Importo delle perdite che hai realizzato dalle tue operazioni dopo aver\n"
            + "dedotto eventuali costi");
    PdfUtils.addSummaryTableRow(
        table,
        "Ricavi netti",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getCapital_gains().getNet())),
        "Guadagno totale dalle tue operazioni, questo è il profitto meno le perdite");
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void marginOperationSummary(PdfPTable futuresTableOuter, CryptoTaxes cryptoTaxes) {
    CryptoTaxes.Results results =
        ObjectUtils.isEmpty(cryptoTaxes.getResults())
            ? cryptoTaxes.getPrevious().getResults()
            : cryptoTaxes.getResults();

    // Futures - Colonna Sinistra (Sommario Principale)
    PdfPTable leftFuturesTable =
        PdfUtils.createSummaryTable(
            "Riepilogo dei futures",
            "Riepilogo dei tuoi guadagni derivanti dal trading di futures/derivati. Questi guadagni NON sono stati inclusi nelle tue plusvalenze e potresti invece volerli dichiarare separatamente");

    PdfUtils.addSummaryTableRow(
        leftFuturesTable,
        "Profitti e perdite realizzati",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getExternal_gains().getNet())),
        " ");
    PdfUtils.addSummaryTableRow(leftFuturesTable, "Commissioni sui futures", "€0.00", " ");
    PdfUtils.addSummaryTableRow(leftFuturesTable, "Commissioni di finanziamento", "€0.00", " ");
    PdfUtils.addSummaryTableRow(
        leftFuturesTable,
        "Profitto netto",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getExternal_gains().getNet())),
        " ");

    PdfUtils.addToOuterTable(futuresTableOuter, leftFuturesTable, 0, 5);

    // Futures - Colonna Destra (Dettagli)
    PdfPTable rightFuturesTable = new PdfPTable(1);
    rightFuturesTable.setWidthPercentage(100);

    // 1. Profitti e perdite realizzati
    PdfPTable pnlTable = PdfUtils.createSummaryTable("Profitti e perdite realizzati", null);
    PdfUtils.addEmptyLine(pnlTable, 1);
    PdfUtils.addSummaryTableRow(
        pnlTable,
        "Profitto",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getExternal_gains().getProfit())),
        null);
    PdfUtils.addSummaryTableRow(
        pnlTable,
        "Perdita",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getExternal_gains().getLoss())),
        null);
    PdfUtils.addSummaryTableRow(
        pnlTable,
        "Profitto netto",
        "€" + PdfUtils.formatCurrency(Double.parseDouble(results.getExternal_gains().getNet())),
        null);

    PdfUtils.addEmptyLine(pnlTable, 1);

    PdfUtils.addToOuterTable(rightFuturesTable, pnlTable, 0, 0);

    // 2. Commissioni sui futures
    PdfPTable commFuturesTable = PdfUtils.createSummaryTable("Commissioni sui futures", null);
    PdfUtils.addEmptyLine(commFuturesTable, 1);
    PdfUtils.addSummaryTableRow(
        commFuturesTable,
        "Commissioni ricevute",
        "€"
            + PdfUtils.formatCurrency(
                Double.parseDouble(results.getExternal_gains().getFutures_commissions_received())),
        null);
    PdfUtils.addSummaryTableRow(
        commFuturesTable,
        "Commissioni pagate",
        "€"
            + PdfUtils.formatCurrency(
                Double.parseDouble(results.getExternal_gains().getFutures_commissions_paid())),
        null);
    double netCommFut =
        Double.parseDouble(results.getExternal_gains().getFutures_commissions_received())
            - Double.parseDouble(results.getExternal_gains().getFutures_commissions_paid());
    PdfUtils.addSummaryTableRow(
        commFuturesTable, "Commissioni nette", "€" + PdfUtils.formatCurrency(netCommFut), null);

    PdfUtils.addEmptyLine(commFuturesTable, 1);

    PdfUtils.addToOuterTable(rightFuturesTable, commFuturesTable, 0, 0);

    // 3. Commissioni di finanziamento
    PdfPTable commFinTable = PdfUtils.createSummaryTable("Commissioni di finanziamento", null);
    PdfUtils.addEmptyLine(commFinTable, 1);
    PdfUtils.addSummaryTableRow(
        commFinTable,
        "Commissioni ricevute",
        "€"
            + PdfUtils.formatCurrency(
                Double.parseDouble(results.getExternal_gains().getFunding_fees_received())),
        null);
    PdfUtils.addSummaryTableRow(
        commFinTable,
        "Commissioni pagate",
        "€"
            + PdfUtils.formatCurrency(
                Double.parseDouble(results.getExternal_gains().getFunding_fees_paid())),
        null);
    double netCommFin =
        Double.parseDouble(results.getExternal_gains().getFunding_fees_received())
            - Double.parseDouble(results.getExternal_gains().getFunding_fees_paid());
    PdfUtils.addSummaryTableRow(
        commFinTable, "Commissioni nette", "€" + PdfUtils.formatCurrency(netCommFin), null);

    PdfUtils.addToOuterTable(rightFuturesTable, commFinTable, 0, 0);

    PdfUtils.addToOuterTable(futuresTableOuter, rightFuturesTable, 5, 0);
  }
}
