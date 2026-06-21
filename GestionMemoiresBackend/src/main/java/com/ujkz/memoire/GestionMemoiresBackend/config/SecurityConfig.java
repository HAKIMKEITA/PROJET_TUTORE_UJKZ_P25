package com.ujkz.memoire.GestionMemoiresBackend.config;

import com.ujkz.memoire.GestionMemoiresBackend.security.CustomUserDetailsService;
import com.ujkz.memoire.GestionMemoiresBackend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                // ============================================
                // 1. PUBLIC ENDPOINTS - Accessibles sans authentification
                // ============================================
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                
                // Sujets - accès public en lecture
                .requestMatchers("/subjects").permitAll()
                .requestMatchers("/subjects/{id}").permitAll()
                .requestMatchers("/subjects/teacher/{teacherId}").permitAll()
                
                // ============================================
                // 2. ETUDIANT ENDPOINTS - Réservés aux étudiants
                // ============================================
                .requestMatchers("/applications/apply").hasRole("ETUDIANT")
                
                // ============================================
                // 3. ENSEIGNANT ENDPOINTS - Réservés aux enseignants
                // ============================================
                .requestMatchers("/applications/subject/**").hasRole("ENSEIGNANT")
                .requestMatchers("/applications/**/accept").hasRole("ENSEIGNANT")
                .requestMatchers("/applications/**/reject").hasRole("ENSEIGNANT")
                
                // ============================================
                // 4. MEMOIRE ENDPOINTS
                // ============================================
                // Accès aux mémoires - authentifié
                .requestMatchers("/memoires").authenticated()
                .requestMatchers("/memoires/student/**").authenticated()
                .requestMatchers("/memoires/subject/**").authenticated()
                .requestMatchers("/memoires/soutenables").hasAnyRole("RESPONSABLE_MASTER", "ENSEIGNANT", "ADMINISTRATEUR")
                
                // Mise à jour de l'avancement - authentifié
                .requestMatchers("/memoires/**/avancement").authenticated()
                
                // Validation de la soutenabilité - réservé aux enseignants
                .requestMatchers("/memoires/**/soutenabilite").hasRole("ENSEIGNANT")
                
                // Création d'un mémoire à partir d'une candidature - enseignants et admin
                .requestMatchers("/memoires/from-application/**").hasAnyRole("ENSEIGNANT", "ADMINISTRATEUR")
                
                // Statistiques des mémoires - responsables et admin
                .requestMatchers("/memoires/stats").hasAnyRole("RESPONSABLE_MASTER", "ADMINISTRATEUR")
                
                // ============================================
                // 5. DOCUMENTS ENDPOINTS
                // ============================================
                .requestMatchers("/documents/**").authenticated()
                
                // ============================================
                // 6. MILESTONES ENDPOINTS
                // ============================================
                .requestMatchers("/milestones/**").authenticated()
                
                // ============================================
                // 7. OBSERVATIONS ENDPOINTS
                // ============================================
                .requestMatchers("/observations/**").authenticated()
                
                // ============================================
                // 8. CAMPAGNES ENDPOINTS - Réservés aux administrateurs et responsables
                // ============================================
                .requestMatchers("/campagnes/**").hasAnyRole("ADMINISTRATEUR", "RESPONSABLE_MASTER")
                
                // ============================================
                // 9. DEFENSE SESSIONS ENDPOINTS
                // ============================================
                // Gestion des sessions - réservé aux responsables et admin
                .requestMatchers("/defense-sessions/**").hasAnyRole("RESPONSABLE_MASTER", "ADMINISTRATEUR")
                
                // Soutenances - accès aux responsables, enseignants et admin
                .requestMatchers("/defenses/**").hasAnyRole("RESPONSABLE_MASTER", "ENSEIGNANT", "ADMINISTRATEUR")
                
                // ============================================
                // 10. JURY ENDPOINTS - Réservés aux responsables et admin
                // ============================================
                .requestMatchers("/juries/**").hasAnyRole("RESPONSABLE_MASTER", "ADMINISTRATEUR")
                
                // ============================================
                // 11. GRADES ENDPOINTS - Réservés aux responsables et admin
                // ============================================
                .requestMatchers("/grades/**").hasAnyRole("RESPONSABLE_MASTER", "ADMINISTRATEUR")
                
                // ============================================
                // 12. USERS ENDPOINTS - Réservés aux administrateurs
                // ============================================
                .requestMatchers("/users/**").hasRole("ADMINISTRATEUR")
                
                // ============================================
                // 13. ALL OTHER ENDPOINTS - Doivent être authentifiés
                // ============================================
                .anyRequest().authenticated()
            .and()
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}