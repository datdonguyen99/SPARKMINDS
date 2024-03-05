package net.sparkminds.librarymanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.service.MaintenanceModeService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MaintenanceModeFilter extends OncePerRequestFilter {
    private final MaintenanceModeService maintenanceModeService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (maintenanceModeService.isEnable()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && isUser(authentication)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

                final Map<String, Object> body = new HashMap<>();
                final ObjectMapper mapper = new ObjectMapper();

                body.put("message", "Maintenance mode is enable");
                body.put("messageCode", "system.maintenance-mode.enable");
                body.put("path", request.getServletPath());

                mapper.writeValue(response.getOutputStream(), body);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
    }
}
