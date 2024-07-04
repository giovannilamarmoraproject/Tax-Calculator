package io.github.giovannilamarmora.tax_calculator.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoHolding {

  private String id;
  private String status;
  private Results results;
  private Previous previous;

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Previous {
    private String id;
    private String status;
    private Results results;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Results {
    private String total_cost;
    private String total_value;
    private List<Holding> holdings;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Holding {
    private String amount;
    private String cost;
    private String value;
    private Currency currency;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Currency {
    private int id;
    private String type;
    private Object subtype;
    private String symbol;
    private String name;
    private String icon;
    private int icon_file_size;
    private String icon_content_type;
    private boolean fiat;
    private boolean crypto;
    private boolean nft;
    private boolean liquidity_token;
    private String token_address;
    private Object nft_token;
    private int rank;
    private Market market;
    private String usd_rate;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Market {
    private int rank;
    private String market_cap;
    private String volume;
    private String circulating_supply;
    private String price;
    private String change1h;
    private String change1d;
    private String change7d;
    private List<String> sparkline;
    private String synced_at;
  }
}
