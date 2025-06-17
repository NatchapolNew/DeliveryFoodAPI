package in.natchapol.deliveryfoodapi.config;

import in.natchapol.deliveryfoodapi.filters.JwtAuthenFilter;
import in.natchapol.deliveryfoodapi.service.AppUserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

//Config=บอกว่าคลาสนี้ใช้สร้างbeanเพื่อให้คลาสอื่นๆเรียกใช้bean
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SercurityConfig {

  private final AppUserDetailService userDetailsService;
    private final JwtAuthenFilter jwtAuthenFilter;


    @Bean
    public SecurityFilterChain sercurityFilterChain(HttpSecurity http) throws Exception {
        //cors(Cross-Origin Resource Sharing)
        http.cors(Customizer.withDefaults())
                //csrf(Cross-Site Request Forgery)
                .csrf(AbstractHttpConfigurer::disable)
                //อนุญาตให้URLต่อไปนี้ไม่ต้องLogin
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/register", "/api/login","/api/foods/**").permitAll().anyRequest().authenticated())
                //ไม่ใช้sessionในการจดจำผู้ใช้
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsCofigurationSource());
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsCofigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTION", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;


    }

    @Bean
    public AuthenticationManager authenticationManager(){
        //DataAccessObject
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);

    }

}
