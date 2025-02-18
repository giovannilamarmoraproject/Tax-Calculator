package io.github.giovannilamarmora.tax_calculator.config;

import static io.github.giovannilamarmora.utils.exception.UtilsException.getExceptionResponse;

import io.github.giovannilamarmora.tax_calculator.authentication.AuthException;
import io.github.giovannilamarmora.tax_calculator.authentication.AuthService;
import io.github.giovannilamarmora.tax_calculator.authentication.dto.UserData;
import io.github.giovannilamarmora.tax_calculator.exception.ExceptionMap;
import io.github.giovannilamarmora.utils.context.TraceUtils;
import io.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import io.github.giovannilamarmora.utils.utilities.Mapper;
import io.github.giovannilamarmora.utils.web.CookieManager;
import io.github.giovannilamarmora.utils.web.HeaderManager;
import io.github.giovannilamarmora.utils.web.RequestManager;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AppInterceptor implements WebFilter {
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private final UserData userInfo;

  @Value(value = "${app.shouldNotFilter}")
  private List<String> shouldNotFilter;

  @Autowired private AuthService authService;

  private static boolean isEmpty(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (shouldNotFilter(exchange.getRequest())) return chain.filter(exchange);
    LOG.debug("Starting Filter Authentication");

    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    String authToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    String sessionId = RequestManager.getCookieOrHeaderData("Session-ID", request);
    ExceptionResponse exceptionResponse = new ExceptionResponse();

    if (isEmpty(authToken) || isEmpty(sessionId)) {
      LOG.error(
          "Not Found Data: Auth-Token is {} and Session ID is {}",
          isEmpty(authToken) ? "[EMPTY]" : "[NOT_EMPTY]",
          isEmpty(sessionId) ? "[EMPTY]" : "[NOT_EMPTY]");
      return errorResponse(request, response, exceptionResponse, ExceptionMap.ERR_AUTH_TAX_401);
    }

    return authService
        .authorize(authToken, sessionId)
        .flatMap(
            userInfoResponse -> {
              // Imposta i dati dell'utente nel contesto
              setUserInContext(userInfoResponse);

              // Imposta il nuovo token nell'header
              response.getHeaders().set(HttpHeaders.AUTHORIZATION, authToken);
              setSessionIDInResponse(sessionId, response);
              settingTracing(request, response);

              LOG.debug("Ending Filter Authentication");
              return chain.filter(exchange);
            })
        .onErrorResume(
            throwable -> {
              if (throwable instanceof AuthException error) {
                return errorResponse(
                    request, response, exceptionResponse, (ExceptionMap) error.getExceptionCode());
              }
              return errorResponse(
                  request, response, exceptionResponse, ExceptionMap.ERR_AUTH_TAX_401);
            });
  }

  private void setSessionIDInResponse(String sessionId, ServerHttpResponse response) {
    CookieManager.setCookieInResponse("Session-ID", sessionId, "giovannilamarmora.com", response);
    HeaderManager.addHeaderInResponse("Session-ID", sessionId, response);
  }

  private void settingTracing(ServerHttpRequest request, ServerHttpResponse response) {
    // Leggi gli header dalla richiesta
    String spanId = request.getHeaders().getFirst("Span-ID");
    String traceId = request.getHeaders().getFirst("Trace-ID");
    String parentId = request.getHeaders().getFirst("Parent-ID");

    // Imposta gli stessi header nella risposta
    if (spanId != null) {
      response.getHeaders().set("Span-ID", spanId);
    }
    if (traceId != null) {
      response.getHeaders().set("Trace-ID", traceId);
    }
    if (parentId != null) {
      response.getHeaders().set("Parent-ID", parentId);
    }
  }

  protected boolean shouldNotFilter(ServerHttpRequest request) {
    String method = request.getMethod().name();
    if (method.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
      LOG.debug("CORS Preflight request detected");
      return true; // Ignora la richiesta OPTIONS
    }
    String path = request.getPath().value();
    if (shouldNotFilter.stream()
        .noneMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path)))
      LOG.debug("Filtering Authentication on {}", path);
    return shouldNotFilter.stream()
        .anyMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path));
  }

  private void setUserInContext(UserData user) {
    BeanUtils.copyProperties(user, this.userInfo);
  }

  private Mono<Void> errorResponse(
      ServerHttpRequest request,
      ServerHttpResponse response,
      ExceptionResponse exceptionResponse,
      ExceptionMap exception) {

    exceptionResponse =
        getExceptionResponse(
            new AuthException(exception, exception.getMessage()), request, exception);
    exceptionResponse.setSpanId(TraceUtils.getSpanID());
    exceptionResponse.getError().setStackTrace(null);
    exceptionResponse.getError().setMessage(exceptionResponse.getError().getExceptionMessage());
    exceptionResponse.getError().setExceptionMessage(null);

    // Aggiungi header CORS
    response
        .getHeaders()
        .set("Access-Control-Allow-Origin", request.getHeaders().getFirst("origin"));
    response.getHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.getHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type");
    response.getHeaders().set("Access-Control-Allow-Credentials", "true");

    response.setStatusCode(HttpStatus.FORBIDDEN);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    DataBuffer responseBuffer =
        new DefaultDataBufferFactory()
            .wrap(Mapper.writeObjectToString(exceptionResponse).getBytes());
    return response.writeWith(Mono.just(responseBuffer));
  }
}
