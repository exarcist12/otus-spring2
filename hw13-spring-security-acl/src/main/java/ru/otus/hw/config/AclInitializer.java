package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JdbcBookRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AclInitializer implements CommandLineRunner {

    private final JdbcBookRepository bookRepository;

    private final MutableAclService aclService;

    @Override
    public void run(String... args) {
        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());

            try {
                aclService.readAclById(oid);
            } catch (NotFoundException e) {
                if (book.getAuthor() == null || book.getAuthor().getUser() == null) {
                    continue;
                }

                Sid ownerSid = new PrincipalSid(book.getAuthor().getUser().getUsername());
                Sid adminSid = new GrantedAuthoritySid("ROLE_ADMIN");
                MutableAcl acl = aclService.createAcl(oid);
                acl.insertAce(acl.getEntries().size(), BasePermission.READ, ownerSid, true);
                acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, ownerSid, true);
                acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, ownerSid, true);
                acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);
                aclService.updateAcl(acl);
            }
        }
    }
}