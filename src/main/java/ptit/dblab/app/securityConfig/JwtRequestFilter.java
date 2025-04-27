package ptit.dblab.app.securityConfig;

import ptit.dblab.shared.enumerate.TypeToken;
import ptit.dblab.shared.securityConfig.UserDetailCustom;
import ptit.dblab.shared.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailServiceCustom userDetailsService;

    private final JwtUtil jwtUtil;


    private final String[] PUBLIC_URL = {
            "/user/auth/**",
            "/question",
            "/question/*",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/ws/**",
            "/public/**",
            "/executor/webhook/result",
            "/stats/export/user-submit",
            "/media/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,@NotNull FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        log.info("************* REQUEST ***********");
        log.info(request.getRequestURI());
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(jwt, TypeToken.ACCESS_TOKEN)) {
                UserDetailCustom userDetails = (UserDetailCustom) this.userDetailsService.loadUserByUsername(username);
                log.info("**** ROLE: {}", userDetails.getRole());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI().substring(request.getContextPath().length());
//        return Arrays.asList(PUBLIC_URL).contains(path);
//    }
}
