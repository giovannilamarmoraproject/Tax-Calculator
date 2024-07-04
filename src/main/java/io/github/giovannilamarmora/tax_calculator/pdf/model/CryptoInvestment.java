package io.github.giovannilamarmora.tax_calculator.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoInvestment {
  private String id;
  private String transactionId;
  private int currencyId;
  private LocalDateTime date;
  private String amount;
  private String value;
  private String gain;
  private String subtype;
  private String poolName;
  private boolean longTerm;
  private LocalDateTime fromDate;
  private String info;
  private String notes;
  private boolean withdrawal;
  private Currency currency;

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Currency {
    private int id;
    private String type;
    private String subtype;
    private String symbol;
    private String name;
    private String icon;
    private int iconFileSize;
    private String iconContentType;
    private boolean fiat;
    private boolean crypto;
    private boolean nft;
    private boolean liquidityToken;
    private String tokenAddress;
    private String nftToken;
    private int rank;
  }
}
