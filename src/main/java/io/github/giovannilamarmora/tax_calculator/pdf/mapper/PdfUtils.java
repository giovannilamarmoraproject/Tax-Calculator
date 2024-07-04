package io.github.giovannilamarmora.tax_calculator.pdf.mapper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import io.github.giovannilamarmora.tax_calculator.pdf.model.PdfFont;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class PdfUtils {

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addToDocument(Document document, Object data) {
    try {
      document.add((Element) data);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static PdfPTable createBorderedCell(Paragraph paragraph) {
    PdfPCell cell = new PdfPCell(paragraph);
    cell.setBorder(Rectangle.BOX);
    cell.setPadding(10);

    PdfPTable table = new PdfPTable(1);
    table.addCell(cell);
    table.setWidthPercentage(100);

    return table;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addSummaryTableRow(
      PdfPTable table, String title, String value, String subtitle) {
    PdfPCell titleCell = new PdfPCell(new Phrase(title, PdfFont.BOLD.getFont()));
    titleCell.setBorder(Rectangle.NO_BORDER);
    titleCell.setPaddingBottom(12);
    titleCell.setColspan(1);
    table.addCell(titleCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, PdfFont.NORMAL.getFont()));
    valueCell.setBorder(Rectangle.NO_BORDER);
    valueCell.setPaddingBottom(12);
    valueCell.setColspan(1);
    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(valueCell);

    PdfPCell lineCell = new PdfPCell();
    lineCell.setBorder(Rectangle.NO_BORDER);
    lineCell.setPaddingBottom(4);
    lineCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    lineCell.setColspan(2);
    DottedLineSeparator dashedLine = new DottedLineSeparator();
    dashedLine.setGap(5f);
    dashedLine.setLineWidth(2f);
    dashedLine.setLineColor(BaseColor.GRAY);
    dashedLine.setAlignment(Element.ALIGN_CENTER);
    dashedLine.setOffset(-2);
    // new LineSeparator(0.5f, 100, BaseColor.GRAY, Element.ALIGN_CENTER, -2)
    lineCell.addElement(dashedLine);
    table.addCell(lineCell);

    if (!ObjectUtils.isEmpty(subtitle)) {
      PdfPCell subtitleCell = new PdfPCell(new Phrase(subtitle, PdfFont.SMALL_GREY.getFont()));
      subtitleCell.setBorder(Rectangle.NO_BORDER);
      subtitleCell.setPaddingBottom(10);
      subtitleCell.setColspan(2);
      table.addCell(subtitleCell);
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static PdfPTable createSummaryTable(String title, String subtitle) {
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);

    PdfPCell cell = createTitleCell(title);
    table.addCell(cell);

    cell = createSubtitleCell(subtitle);
    table.addCell(cell);

    return table;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static PdfPCell createTitleCell(String title) {
    PdfPCell cell = new PdfPCell(new Phrase(title, PdfFont.TITLE_NORMAL.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(0);
    cell.setColspan(2);
    return cell;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static PdfPCell createSubtitleCell(String subtitle) {
    PdfPCell cell = new PdfPCell(new Phrase(subtitle, PdfFont.SUBTITLE.getFont()));
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingBottom(10);
    cell.setColspan(2);
    return cell;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addToOuterTable(
      PdfPTable outerTable, PdfPTable innerTable, int left, int right) {
    PdfPCell cell = new PdfPCell(innerTable);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPaddingLeft(left);
    cell.setPaddingRight(right);
    outerTable.addCell(cell);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static void addTableHeader(PdfPTable table, List<String> headers) {
    headers.forEach(
        columnTitle -> {
          PdfPCell header = new PdfPCell();
          header.setBorder(PdfPCell.BOTTOM);
          header.setPaddingBottom(10);
          header.setBorderColor(BaseColor.LIGHT_GRAY);
          header.setBorderWidth(1);

          // Se è l'ultimo elemento, posizionalo a destra
          if (headers.indexOf(columnTitle) == headers.size() - 1) {
            header.setHorizontalAlignment(Element.ALIGN_RIGHT);
          } else {
            // Altrimenti, posizionalo a sinistra
            header.setHorizontalAlignment(Element.ALIGN_LEFT);
          }

          header.setPhrase(new Phrase(columnTitle, PdfFont.SMALL_BOLD.getFont()));
          table.addCell(header);
        });
  }
}
