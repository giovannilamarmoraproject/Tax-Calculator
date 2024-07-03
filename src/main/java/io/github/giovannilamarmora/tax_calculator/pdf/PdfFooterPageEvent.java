package io.github.giovannilamarmora.tax_calculator.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;


public class PdfFooterPageEvent extends PdfPageEventHelper {

  private final int year;

  public PdfFooterPageEvent(int year) {
    this.year = year;
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContent();
    Phrase footer = new Phrase("Generato da Giovanni", PdfFont.SMALL_GREY.getFont());
    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footer, document.leftMargin(), document.bottom() - 10, 0);
  }

  @Override
  public void onStartPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContent();
    int currentPageNumber = writer.getPageNumber();
    if (currentPageNumber > 1) {
      Phrase header = new Phrase("ANNO FISCALE " + year, PdfFont.SMALL_GREY.getFont());
      ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, document.right(), document.top() + 10, 0);
    }
  }
}