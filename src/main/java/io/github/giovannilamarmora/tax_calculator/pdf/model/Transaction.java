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
public class Transaction {
  private String id;
  private String type;
  private TransactionDetail from; // Cambiato da String a TransactionDetail
  private ToDetails to;
  private Fee fee; // Può essere null
  private String net_value;
  private String fee_value; // Può essere null
  private NetWorth net_worth;
  private FeeWorth fee_worth; // Può essere null
  private String gain;
  private String date;
  private String label; // Può essere null
  private String description; // Può essere null
  private boolean synced;
  private boolean manual;
  private String txhash; // Può essere null
  private String txsrc; // Può essere null
  private String txdest; // Può essere null
  private String txurl; // Può essere null
  private String contract_address; // Può essere null
  private String method_name; // Può essere null
  private Boolean negative_balances; // Può essere null
  private boolean missing_rates;
  private String missing_cost_basis; // Può essere null
  private boolean margin;
  private String group_name; // Può essere null
  private String group_count; // Può essere null
  private String from_source; // Può essere null
  private String to_source;
  private boolean ignored;
  private boolean imported;
  private String synced_to_accounting_at; // Può essere null
  private String rates_updated_at;
  private String net_value_source; // Può essere null
  private String fee_value_source; // Può essere null
  private String cost_basis_method;
  private String ignored_reason; // Può essere null
  private String disposal_missing_acq_value_at; // Può essere null
  private List<CryptoInvestment> investments;

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Fee {
    private String amount;
    private CurrencyDetails currency;
    private WalletDetails wallet;
    private String ledger_id;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TransactionDetail {
    private String amount;
    private CurrencyDetails currency;
    private WalletDetails wallet;
    private String cost_basis;
    private String ledger_id;
    private String source;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ToDetails {
    private String amount;
    private CurrencyDetails currency;
    private WalletDetails wallet;
    private String cost_basis;
    private String ledger_id;
    private String source;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrencyDetails {
    private int id;
    private String type;
    private String subtype; // Può essere null
    private String symbol;
    private String name;
    private String icon;
    private int icon_file_size;
    private String icon_content_type;
    private boolean fiat;
    private boolean crypto;
    private boolean nft;
    private boolean liquidity_token;
    private String token_address; // Può essere null
    private Object nft_token; // Può essere null
    private Integer rank; // Può essere null
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WalletDetails {
    private String id;
    private String name;
    private String last_txn_at;
    private String usd_value;
    private String usd_value_updated_at;
    private int txn_count;
    private boolean syncing;
    private String updated_at;
    private String created_at;
    private WalletServiceDetails wallet_service;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WalletServiceDetails {
    private int id;
    private String name;
    private String tag;
    private String type;
    private boolean shutdown;
    private boolean api_beta;
    private List<String> api_required_fields;
    private String api_oauth_url; // Può essere null
    private List<String> api_optional_fields;
    private List<String> api_notes;
    private String instructions_url;
    private boolean include_api_warning_text;
    private boolean include_csv_warning_text;
    private String updated_at;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NetWorth {
    private String amount;
    private CurrencyDetails currency;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FeeWorth {
    private String amount;
    private CurrencyDetails currency;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Meta {
    private Page page;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Page {
    private int current_page;
    private int per_page;
    private int total_pages;
    private int total_items;
  }
}
