package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import io.github.giovannilamarmora.tax_calculator.app.model.TaxRequest;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfOverview {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void setFirstPage(Document document, TaxRequest taxRequest) {
    // Aggiungi il titolo
    addTitle(document, "Resoconto fiscale " + taxRequest.getTax().getResults().getYear());

    // Aggiungi la data e il periodo
    addMetaData(document, String.valueOf(taxRequest.getTax().getResults().getYear()));

    // Aggiungi il sommario
    addSummary(document);

    // Aggiungi le note
    addNotes(document);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  private static void addTitle(Document document, String title) {
    Paragraph preface = new Paragraph(title, PdfFont.TITLE.getFont());
    preface.setAlignment(Element.ALIGN_CENTER);
    PdfUtils.addEmptyLine(preface, 2);
    PdfUtils.addToDocument(document, preface);
  }

  private static void addMetaData(Document document, String year) {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String formattedDateTime = now.format(formatter);

    Paragraph metaData = new Paragraph();
    metaData.setAlignment(Element.ALIGN_LEFT);
    metaData.add(new Paragraph("Data: " + formattedDateTime, PdfFont.COLORED.getFont()));
    metaData.add(
        new Paragraph(
            "Periodo: 1 gen " + year + " al 31 dic " + year,
            PdfFont.COLORED.getFont())); // Aggiungi il periodo come esempio
    PdfUtils.addEmptyLine(metaData, 2);
    PdfUtils.addToDocument(document, metaData);
  }

  private static void addSummary(Document document) {
    Paragraph summary = new Paragraph();
    summary.setAlignment(Element.ALIGN_LEFT);
    summary.add(new Paragraph("Contenuto", PdfFont.BOLD.getFont()));
    PdfUtils.addEmptyLine(summary, 1);

    String[] contents = {
      "1. Riepilogo plusvalenze",
      "2. Riepilogo delle entrate",
      "3. Riepilogo del patrimonio",
      "4. Saldi di fine anno",
      "5. Transazioni di plusvalenze",
      "6. Transazioni entrate",
      "7. Regali, donazioni e attivi perduti",
      "8. Spese",
      "9. Fonti di dati"
    };

    for (String item : contents) {
      summary.add(new Paragraph(item, PdfFont.NORMAL.getFont()));
    }

    PdfUtils.addEmptyLine(summary, 2);
    PdfUtils.addToDocument(document, summary);
  }

  private static void addNotes(Document document) {
    String text =
        "Tutti i valori e i prezzi fiat sono in EUR a meno che non sia indicato diversamente. Le plusvalenze sono state "
            + "calcolate utilizzando il FIFO metodo contabile (lotto fiscale universale per ogni valuta). Le operazioni tra criptovalute "
            + "sono trattate come un evento tassabile. I prezzi di mercato sono determinati utilizzando il valore medio di mercato al "
            + "momento della vendita - a meno che il prezzo non sia fornito dalla piattaforma di trading o sovrascritto manualmente. "
            + "Tutte le date e gli orari sono nel Europe/Rome fuso orario."
            + "Questo rapporto può essere usato per scopi fiscali dopo essere stato controllato per l'accuratezza e la completezza "
            + "da te o dal tuo consulente fiscale.\n\n";

    Paragraph notes = new Paragraph(text, PdfFont.SMALL.getFont());
    notes.setAlignment(Element.ALIGN_LEFT);

    PdfUtils.addToDocument(document, PdfUtils.createBorderedCell(notes));
  }
}
