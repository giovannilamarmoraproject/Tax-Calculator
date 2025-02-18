package io.github.giovannilamarmora.tax_calculator.pdf.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoWallet {
    private String id;
    private String name;
    private int wallet_service_id;
    private Map<String, String> icon;
    private String synced_at;
    private String archived_at;
    private String last_error;
    private String last_txn_at;
    private boolean api_connected;
    private boolean auth_failed;
    private Map<String, Object> balance_diff;
    private String display_address;
    private String friendly_error;
    private int txn_count;
    private boolean pool;
    private String issues_updated_at;
    private boolean needs_review;
    private boolean syncing;
    private String sync_started_at;
    private Map<String, Object> reported_balances;
    private String unknown_reported_balances;
    private String assume_zero_balance_if_missing;
    private boolean ignore_reported_balances;
    private String send_email_on_first_import;
    private String usd_value;
    private String usd_value_updated_at;
    private Map<String, Object> api_options;
    private String parent_wallet;
    private Wallet wallet_service;
    private String updated_at;
    private String created_at;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wallet {
        private int id;
        private String name;
        private String tag;
        private Map<String, String> icon;
        private String type;
        private boolean shutdown;
        private Boolean api_active;
        private boolean api_beta;
        private List<String> api_required_fields;
        private String api_oauth_url;
        private List<String> api_optional_fields;
        private List<String> api_notes;
        private String instructions_url;
        private String recommended_input_source;
        private String api_instructions;
        private String api_warning_text;
        private boolean include_api_warning_text;
        private String csv_instructions;
        private String csv_warning_text;
        private boolean include_csv_warning_text;
        private String updated_at;
        private Integer sync_eta_low;
        private Integer sync_eta_high;
        private boolean allow_subwallets;
        private boolean evm;

        // Metodi getters e setters vengono omessi come richiesto
    }
}
