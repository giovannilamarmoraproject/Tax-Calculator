package io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.shared.JWTData;
import io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.shared.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
  private JWTData userInfo;
  private User user;
}
