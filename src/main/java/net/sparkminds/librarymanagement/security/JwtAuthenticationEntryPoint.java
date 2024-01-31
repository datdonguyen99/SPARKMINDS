package net.sparkminds.librarymanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        final ObjectMapper mapper = new ObjectMapper();
        final String expired = (String) request.getAttribute("expired");

        if (expired != null) {
            body.put("message", "token is expired");
            body.put("messageCode", "token.token-expired");
            body.put("path", request.getServletPath());
        } else {
            body.put("message", "Invalid Login details");
            body.put("messageCode", "token.token-invalid");
            body.put("path", request.getServletPath());
        }

        mapper.writeValue(response.getOutputStream(), body);
    }
}
