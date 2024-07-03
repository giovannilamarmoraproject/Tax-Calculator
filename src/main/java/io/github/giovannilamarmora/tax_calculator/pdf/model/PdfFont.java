package io.github.giovannilamarmora.tax_calculator.pdf.model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import lombok.Getter;

@Getter
public enum PdfFont {
  TITLE(new Font(FontFamily.HELVETICA, 18, Font.BOLD)),
  TITLE_NORMAL(new Font(FontFamily.HELVETICA, 18, Font.NORMAL)),
  SUBTITLE(new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY)),
  NORMAL(new Font(FontFamily.HELVETICA, 12, Font.NORMAL)),
  BOLD(new Font(FontFamily.HELVETICA, 12, Font.BOLD)),
  SMALL(new Font(FontFamily.HELVETICA, 10, Font.NORMAL)),
  SMALL_BOLD(new Font(FontFamily.HELVETICA, 10, Font.BOLD)),
  VERY_SMALL(new Font(FontFamily.HELVETICA, 8, Font.NORMAL)),
  SMALL_GREY(new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY)),
  COLORED(new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY));

  private final Font font;

  PdfFont(Font font) {
    this.font = font;
  }
}
