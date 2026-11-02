package dev.dmitriy.job_x_hunter.security;

import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.enums.Role;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionDefinition;

import java.util.UUID;

@Service
public class CurrentUserService {
    private final UserRepository users;
    private final TransactionTemplate insertTransaction;

    public CurrentUserService(UserRepository users, PlatformTransactionManager transactionManager) {
        this.users = users;
        this.insertTransaction = new TransactionTemplate(transactionManager);
        this.insertTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public User getOrProvision(Jwt jwt) {
        String issuer = jwt.getIssuer() == null ? null : jwt.getIssuer().toString();
        String subject = jwt.getSubject();
        if (issuer == null || issuer.isBlank() || subject == null || subject.isBlank()) {
            throw new InvalidIdentityClaimsException();
        }
        String email = jwt.getClaimAsString("email");
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                || !Boolean.TRUE.equals(jwt.getClaim("email_verified"))) {
            throw new InvalidIdentityClaimsException();
        }
        Object realmAccess = jwt.getClaim("realm_access");
        boolean appRole = realmAccess instanceof java.util.Map<?, ?> realm
                && realm.get("roles") instanceof java.util.List<?> roles
                && roles.stream().anyMatch(role -> "USER".equals(role) || "ADMIN".equals(role));
        if (!appRole) {
            throw new AccessDeniedException("An application role is required.");
        }

        return users.findByAuthIssuerAndAuthSubject(issuer, subject).orElseGet(() -> provision(issuer, subject, jwt));
    }

    private User provision(String issuer, String subject, Jwt jwt) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail(jwt.getClaimAsString("email"));
        user.setUserRole(Role.USER);
        user.setAuthIssuer(issuer);
        user.setAuthSubject(subject);
        try {
            return insertTransaction.execute(status -> users.saveAndFlush(user));
        } catch (DataIntegrityViolationException ex) {
            // The failed insert transaction has rolled back; this read uses a valid transaction.
            return users.findByAuthIssuerAndAuthSubject(issuer, subject).orElseThrow(() -> ex);
        }
    }
}
