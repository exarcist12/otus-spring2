package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.JdbcAuthorRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final JdbcAuthorRepository authorRepository;

    private final MutableAclService aclService;

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isAuthorBelongsToUser(Long authorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Author author = authorRepository.findById(authorId).orElse(null);
        return author != null && author.getUser() != null &&
                author.getUser().getId().equals(currentUser.getId());
    }

    public boolean hasReadPermissionOnBook(Long bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        try {
            ObjectIdentity oid = new ObjectIdentityImpl(Book.class, bookId);
            Acl acl = aclService.readAclById(oid);

            List<Sid> sids = new ArrayList<>();

            sids.add(new PrincipalSid(auth));

            auth.getAuthorities().forEach(authority ->
                    sids.add(new GrantedAuthoritySid(authority))
            );

            return acl.isGranted(List.of(BasePermission.READ), sids, false);
        } catch (NotFoundException e) {
            return false;
        }
    }

    public boolean canCreateBook(Long authorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || isAuthorBelongsToUser(authorId);
    }
}