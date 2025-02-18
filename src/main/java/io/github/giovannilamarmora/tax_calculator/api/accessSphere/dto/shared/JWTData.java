package io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTData {
  private String identifier;
  private String aud;
  private String azp;
  private long exp;
  private long iat;
  private String iss;
  private String sub;
  private String name;
  private String email;
  private String picture;
  private String given_name;
  private String family_name;
  private String at_hash;
  private boolean email_verified;
  private List<String> roles;
  private OAuthType type;
  private String client_id;
  private Map<String, Object> attributes;
}
