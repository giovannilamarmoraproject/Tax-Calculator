package io.github.giovannilamarmora.tax_calculator.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class CustomTableHeaderEvent extends PdfPageEventHelper {
  private final PdfPTable table;
  private final float tableHeight;

  public CustomTableHeaderEvent(PdfPTable table) {
    this.table = table;
    this.table.setTotalWidth(
        PageSize.A4.rotate().getWidth() - 80); // adattare alla larghezza della pagina
    this.tableHeight = table.getTotalHeight();
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    PdfContentByte cb = writer.getDirectContent();
    table.writeSelectedRows(
        0, -1, 40, document.top() + ((document.topMargin() + tableHeight) / 2), cb);
  }
}
