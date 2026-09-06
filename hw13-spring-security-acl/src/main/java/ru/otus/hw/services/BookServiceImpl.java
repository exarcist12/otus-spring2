package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.JdbcAuthorRepository;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcGenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final JdbcBookRepository bookRepository;

    private final JdbcAuthorRepository authorRepository;

    private final JdbcGenreRepository genreRepository;

    private final MutableAclService aclService;

    private final SecurityService securityService;


    @PostFilter("@securityService.hasReadPermissionOnBook(filterObject.id)")
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
//                .filter(book -> securityService.hasReadPermissionOnBook(book.getId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostAuthorize("@securityService.hasReadPermissionOnBook(returnObject.id)")
    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

//        if (!securityService.hasReadPermissionOnBook(book.getId())) {
//            throw new AccessDeniedException("Access denied for this book");
//        }
        return toDto(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public BookDto insert(BookCreateDto createDto) {
        Author author = authorRepository.findById(createDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        List<Genre> genres = new ArrayList<>();
        if (createDto.getGenreIds() != null) {
            for (Long genreId : createDto.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + genreId));
                genres.add(genre);
            }
        }

        Book book = new Book();
        book.setTitle(createDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);

        Book saved = bookRepository.save(book);
        grantPermissionsForBook(saved);
        return toDto(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public BookDto update(long id, BookUpdateDto updateDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        book.setTitle(updateDto.getTitle());

        if (updateDto.getAuthorId() != null) {
            Author author = authorRepository.findById(updateDto.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Author not found"));
            book.setAuthor(author);
        }

        if (updateDto.getGenreIds() != null) {
            List<Genre> genres = new ArrayList<>();
            for (Long genreId : updateDto.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));
                genres.add(genre);
            }
            book.setGenres(genres);
        }

        return toDto(bookRepository.save(book));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(long id) {

        ObjectIdentity oid = new ObjectIdentityImpl(Book.class, id);
        try {
            aclService.deleteAcl(oid, false);
        } catch (NotFoundException e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.debug("ACL not found for book id {}, skipping deletion", id);
        }
        bookRepository.deleteById(id);
    }

    private void grantPermissionsForBook(Book book) {
        User owner = book.getAuthor().getUser();

        if (owner == null) {
            grantAdminOnlyPermissions(book);
            return;
        }

        ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());
        Sid ownerSid = new PrincipalSid(owner.getUsername());
        Sid adminSid = new GrantedAuthoritySid("ROLE_ADMIN");

        MutableAcl acl = aclService.createAcl(oid);

        acl.insertAce(acl.getEntries().size(), BasePermission.READ, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, ownerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, ownerSid, true);

        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);

        aclService.updateAcl(acl);
    }

    private void grantAdminOnlyPermissions(Book book) {
        ObjectIdentity oid = new ObjectIdentityImpl(Book.class, book.getId());
        Sid adminSid = new GrantedAuthoritySid("ROLE_ADMIN");

        MutableAcl acl = aclService.createAcl(oid);

        acl.insertAce(acl.getEntries().size(), BasePermission.READ, adminSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, adminSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, adminSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, adminSid, true);

        aclService.updateAcl(acl);
    }



    private BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());

        if (book.getAuthor() != null) {
            dto.setAuthorId(book.getAuthor().getId());
            dto.setAuthorName(book.getAuthor().getFullName());
        }

        if (book.getGenres() != null) {
            List<BookDto.GenreDto> genreDtos = book.getGenres().stream()
                    .map(genre -> new BookDto.GenreDto(genre.getId(), genre.getName()))
                    .collect(Collectors.toList());
            dto.setGenres(genreDtos);
        }

        return dto;
    }
}