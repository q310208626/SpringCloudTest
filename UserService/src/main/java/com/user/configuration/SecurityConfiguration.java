package com.user.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.ITUserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain securityFilterChain(HttpSecurity http, MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/token").permitAll().anyRequest().authenticated());
        http.formLogin(withDefaults());
        http.addFilterAt(myUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.csrf((t) -> { t.ignoringRequestMatchers("/token"); });
        http.securityContext(context -> context.securityContextRepository(new HttpSessionSecurityContextRepository()));

        // 验证失败，返回403，不做页面跳转，供前后端分离
        http.exceptionHandling(t->t.authenticationEntryPoint(new Http403ForbiddenEntryPoint()));
        return http.build();
    }

    @Bean
    public MyUsernamePasswordAuthenticationFilter UsernamePasswordAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) {
        try {
            MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter = new MyUsernamePasswordAuthenticationFilter();
            myUsernamePasswordAuthenticationFilter.setFilterProcessesUrl("/user/login");
            myUsernamePasswordAuthenticationFilter.setUsernameParameter("userName");
            myUsernamePasswordAuthenticationFilter.setPasswordParameter("password");
            myUsernamePasswordAuthenticationFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

            // 验证成功时，返回200，不做页面跳转，供前后端分离项目
            myUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());


            // 增加策略，登录成功后更新csrftoken跟SessionId
            List<SessionAuthenticationStrategy> sessionAuthenticationStrategies = new ArrayList<>();

            // 下面这个策略是移除session中的老csrfToken,生成新的放到request的Attribute中
            sessionAuthenticationStrategies.add(new CsrfAuthenticationStrategy(new HttpSessionCsrfTokenRepository()));

            // 下面这个是重新生成一个Session，把原来的数据放到新Session中
            sessionAuthenticationStrategies.add(new SessionFixationProtectionStrategy());

            // 下面这个是我自定义的，把新的csrfToken从request的Atribute中取出，放到Response的Header中
            sessionAuthenticationStrategies.add(new CsrfSaveAuthenticationStrategy());
            myUsernamePasswordAuthenticationFilter.setSessionAuthenticationStrategy(new CompositeSessionAuthenticationStrategy(sessionAuthenticationStrategies));

            myUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
            return myUsernamePasswordAuthenticationFilter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setStatus(HttpStatus.OK.value());
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
            AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
            response.setStatus(HttpStatus.OK.value());
        }
    }

    @Getter
    @Setter
    class CsrfSaveAuthenticationStrategy implements SessionAuthenticationStrategy {

        @Override
        public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            String token = csrfToken.getToken();
            String headerName = csrfToken.getHeaderName();
            response.addHeader(headerName, token);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
        encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
        encoders.put("pbkdf2@SpringSecurity_v5_8", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
        encoders.put("scrypt@SpringSecurity_v5_8", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256",
                new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2());
        encoders.put("argon2@SpringSecurity_v5_8", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put(null, NoOpPasswordEncoder.getInstance());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    @Bean
    public UserDetailsService userDetailsService(ITUserService userService) {
        UserDetailsService userDetailsService = new MyUserDetailsService(userService);
        return userDetailsService;
    }

    /**
     * 自定义UserDetailsService，适配自己的用户查询逻辑
     */
    class MyUserDetailsService implements UserDetailsService {

        private ITUserService userService;

        public MyUserDetailsService(ITUserService userService) {
            this.userService = userService;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return userService.loadUserByUsername(username);
        }
    }

    /**
     * 自定义requestWrapper，把请求中的输入保存到byte[]中，便于后续多次读取
     */
    class RequestBodyCopyServletRequestWrapper extends HttpServletRequestWrapper {

        private byte[] copyBody = null;

        public RequestBodyCopyServletRequestWrapper(HttpServletRequest request) {
            super(request);
            try {
                copyBody = StreamUtils.copyToByteArray(request.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(copyBody);
            return new ServletInputStream() {

                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {

                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }

    /**
     * 自定义UsernamePasswordAuthenticationFilter，用于从InputStream中读取用户名密码，适用于RestFul接口
     */
    class MyUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            ServletRequestWrapper requestWrapper = null;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new RequestBodyCopyServletRequestWrapper((HttpServletRequest) request);
                super.doFilter(requestWrapper, response, chain);
            } else {
                super.doFilter(request, response, chain);
            }
        }

        @Override
        protected String obtainPassword(HttpServletRequest request) {
            try {
                Map requestMap = objectMapper.readValue(request.getInputStream(), Map.class);
                Object o = requestMap.get(getPasswordParameter());
                if (o != null) {
                    return String.valueOf(o);
                } else {
                    return "";
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected String obtainUsername(HttpServletRequest request) {
            try {
                Map requestMap = objectMapper.readValue(request.getInputStream(), Map.class);
                Object o = requestMap.get(getUsernameParameter());
                if (o != null) {
                    return String.valueOf(o);
                } else {
                    return "";
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
