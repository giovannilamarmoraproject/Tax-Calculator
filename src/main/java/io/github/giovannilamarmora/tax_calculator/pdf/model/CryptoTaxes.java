package io.github.giovannilamarmora.tax_calculator.pdf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoTaxes {
  private String id;
  private String status;
  private Results results;

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Results {
    private String from;
    private String to;
    private int year;
    private Transactions transactions;
    private CapitalGains capital_gains;
    private Income income;
    private Expenses expenses;
    private Special special;
    private Misc misc;
    private ExternalGains external_gains;
    private String zero_cost_gains_total;
    private String cost_basis_method;
    private boolean gains_blocked;
    private boolean txn_limit_exceeded;
    private int plan_limit;
    private int billable_txn_count;
    private int billable_txns_in_year;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Transactions {
    private int total;
    private int deposits;
    private int withdrawals;
    private int trades;
    private int transfers;
    private int errors;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CapitalGains {
    private int disposals;
    private String profit;
    private String loss;
    private String net;
    private String costs;
    private String proceeds;
    private int failed;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Income {
    private String airdrop;
    private String fork;
    private String mining;
    private String reward;
    private String salary;
    private String lending_interest;
    private String other_income;
    private String total;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Expenses {
    private String margin_fee;
    private String loan_fee;
    private String cost;
    private String transfer_fees;
    private String total;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Special {
    private String gift;
    private String donation;
    private String lost;
    private String total;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Misc {
    private String cashback;
    private String fee_refund;
    private String tax;
    private String loan;
    private String loan_repayment;
    private String margin_loan;
    private String margin_repayment;
  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExternalGains {
    private int count;
    private String profit;
    private String loss;
    private String net;
  }
}
