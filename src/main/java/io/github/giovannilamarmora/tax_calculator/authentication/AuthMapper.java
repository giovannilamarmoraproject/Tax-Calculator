package io.github.giovannilamarmora.tax_calculator.authentication;

import io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.UserInfoResponse;
import io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.shared.JWTData;
import io.github.giovannilamarmora.tax_calculator.api.accessSphere.dto.shared.User;
import io.github.giovannilamarmora.tax_calculator.authentication.dto.UserData;
import io.github.giovannilamarmora.tax_calculator.config.AppRole;
import io.github.giovannilamarmora.tax_calculator.exception.ExceptionMap;
import io.github.giovannilamarmora.tax_calculator.utils.Utils;
import io.github.giovannilamarmora.utils.generic.Response;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.utilities.Mapper;
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthMapper {

  private static final Logger LOG = LoggerFactory.getLogger(AuthMapper.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static UserData mapAccessSphereUserToUserData(User user) {
    UserData userData = new UserData();
    BeanUtils.copyProperties(user, userData);
    return userData;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static Mono<UserData> verifyAndMapAccessSphereResponse(
      ResponseEntity<Response> responseEntity) {
    if (responseEntity.getStatusCode().isError()
        || ObjectToolkit.areNullOrEmptyCast(
            responseEntity,
            (ResponseEntity<Response> re) -> re.getBody(),
            (Response body) -> body.getData())) {
      throw new AuthException(
          ExceptionMap.ERR_AUTH_TAX_401, ExceptionMap.ERR_AUTH_TAX_401.getMessage());
    }
    UserInfoResponse userInfoResponse =
        Mapper.convertObject(responseEntity.getBody().getData(), UserInfoResponse.class);
    if (ObjectToolkit.areNotNullOrEmptyCast(
            userInfoResponse,
            (UserInfoResponse info) -> info.getUserInfo(), // Specifica il tipo per info
            (JWTData data) -> data.getRoles()) // Specifica il tipo per data
        && userInfoResponse.getUserInfo().getRoles().stream()
            .noneMatch(string -> Utils.isEnumValue(string, AppRole.class))) {
      LOG.error(
          "The current user role {} not match with the app role",
          userInfoResponse.getUserInfo().getRoles());
      throw new AuthException(
          ExceptionMap.ERR_AUTH_TAX_403,
          "You cannot make this request cause you don't have the right roles");
    }
    return Mono.just(mapAccessSphereUserToUserData(userInfoResponse.getUser()));
  }
}
