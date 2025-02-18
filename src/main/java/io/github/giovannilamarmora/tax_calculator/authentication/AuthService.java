package io.github.giovannilamarmora.tax_calculator.authentication;

import io.github.giovannilamarmora.tax_calculator.api.accessSphere.AccessSphereClient;
import io.github.giovannilamarmora.tax_calculator.authentication.dto.UserData;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Logged
@RequiredArgsConstructor
public class AuthService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired private AccessSphereClient accessSphereClient;

  /**
   * Authorize request to Access Sphere
   *
   * @param access_token to be validated
   * @param sessionId of the request
   * @return User Info
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Mono<UserData> authorize(String access_token, String sessionId) {
    return accessSphereClient
        .getUserInfo(access_token, sessionId, true)
        .flatMap(AuthMapper::verifyAndMapAccessSphereResponse);
  }
}
