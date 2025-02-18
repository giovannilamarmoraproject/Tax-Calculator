package io.github.giovannilamarmora.tax_calculator.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoHolding;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoTaxes;
import io.github.giovannilamarmora.tax_calculator.pdf.model.CryptoWallet;
import io.github.giovannilamarmora.tax_calculator.pdf.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {
    private String year;
    private String baseCurrency;
    private CryptoTaxes tax;
    private List<Transaction> transactions;
    private CryptoHolding holdings;
    private List<CryptoWallet> wallets;
}
