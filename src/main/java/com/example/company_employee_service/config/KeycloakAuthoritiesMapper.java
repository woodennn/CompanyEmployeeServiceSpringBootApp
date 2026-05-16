package com.example.company_employee_service.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Component
public class KeycloakAuthoritiesMapper implements GrantedAuthoritiesMapper {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Collection<GrantedAuthority> mappedAuthorities = new ArrayList<>();

        for (GrantedAuthority authority : authorities) {
            mappedAuthorities.add(authority); // додаємо базові ролі

            Map<String, Object> attributes = null;

            // Витягуємо атрибути незалежно від типу авторизації (OIDC чи загальний OAuth2)
            if (authority instanceof OidcUserAuthority oidcAuthority) {
                attributes = oidcAuthority.getAttributes();
            } else if (authority instanceof OAuth2UserAuthority oauth2Authority) {
                attributes = oauth2Authority.getAttributes();
            }

            // Якщо знайшли атрибути та там є блок realm_access — витягуємо ролі
            if (attributes != null && attributes.containsKey("realm_access")) {
                Map<String, Object> realmAccess = (Map<String, Object>) attributes.get("realm_access");
                if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                    for (Object role : roles) {
                        String roleName = "ROLE_" + role.toString().toUpperCase();
                        mappedAuthorities.add(new SimpleGrantedAuthority(roleName));
                    }
                }
            }
        }

        // Цей рядок виведе в консоль розпарсені ролі, щоб ми бачили їх при вході
        System.out.println("=== КІНЦЕВІ РОЛІ В СИСТЕМІ: " + mappedAuthorities);

        return mappedAuthorities;
    }
}