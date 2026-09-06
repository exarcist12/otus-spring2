package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JdbcBookRepository;

import java.util.List;

@Slf4j
@Component
@Profile("!test")
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class AclInitializer implements CommandLineRunner {

    private final JdbcBookRepository bookRepository;

    private final MutableAclService aclService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("========== AclInitializer STARTED ==========");

        Authentication systemAuth = new AnonymousAuthenticationToken(
                "system",
                "system",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(systemAuth);

        try {
            List<Book> books = bookRepository.findAll();
            log.info("AclInitializer: found {} books", books.size());

            for (Book book : books) {
                ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());

                try {
                    aclService.readAclById(oid);
                    log.info("AclInitializer: ACL exists for book id={}", book.getId());
                    continue;
                } catch (NotFoundException e) {
                    createAclForBook(book);
                } catch (Exception ex) {
                    log.error("AclInitializer: error for book id={}", book.getId(), ex);
                }
            }

            log.info("========== AclInitializer FINISHED ==========");
        } finally {

            SecurityContextHolder.clearContext();
        }
    }

    private void createAclForBook(Book book) {
        log.info("AclInitializer: creating ACL for book id={}", book.getId());

        if (book.getAuthor() == null || book.getAuthor().getUser() == null) {
            log.warn("AclInitializer: no owner for book id={}, granting admin only", book.getId());
            grantAdminOnlyPermissions(book);
            return;
        }

        String ownerUsername = book.getAuthor().getUser().getUsername();
        log.info("AclInitializer: owner='{}' for book id={}", ownerUsername, book.getId());

        ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());
        Sid ownerSid = new PrincipalSid(ownerUsername);
        Sid adminSid = new GrantedAuthoritySid("ROLE_ADMIN");

        MutableAcl acl = aclService.createAcl(oid);

        acl.insertAce(acl.getEntries().size(), BasePermission.READ, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);

        aclService.updateAcl(acl);
        log.info("AclInitializer: ACL created for book id={}", book.getId());
    }

    private void grantAdminOnlyPermissions(Book book) {
        try {
            ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());
            Sid adminSid = new GrantedAuthoritySid("ROLE_ADMIN");

            MutableAcl acl = aclService.createAcl(oid);
            acl.insertAce(acl.getEntries().size(), BasePermission.READ, adminSid, true);
            acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, adminSid, true);
            acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, adminSid, true);
            acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);
            aclService.updateAcl(acl);
        } catch (Exception ex) {
            log.error("AclInitializer: error granting admin permissions for book id={}", book.getId(), ex);
        }
    }
}