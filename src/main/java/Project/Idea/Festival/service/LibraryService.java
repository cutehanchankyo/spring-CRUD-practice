package Project.Idea.Festival.service;

import Project.Idea.Festival.Request.AuthorCreationRequest;
import Project.Idea.Festival.Request.BookCreationRequest;
import Project.Idea.Festival.Request.BookLendRequest;
import Project.Idea.Festival.Request.MemberCreationRequest;
import Project.Idea.Festival.domain.*;
import Project.Idea.Festival.repository.AuthorRepository;
import Project.Idea.Festival.repository.BookRepository;
import Project.Idea.Festival.repository.LendRepository;
import Project.Idea.Festival.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Member;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;
    private final LendRepository lendRepository;
    private  final BookRepository bookRepository;

    public Book readBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()) {
            return book.get();
        }
        throw new EntityNotFoundException("지정된 ID에서 책을 찾을 수 없습니다.");
    }

    public Optional<Book> readBooks(){
        return bookRepository.findAll();
    }

    public Book readBook(String isbn){
       Optional<Book> book = bookRepository.findByIsbn(isbn);
       if(book.isPresent()){
           return book.get();
       }
       throw new EntityNotFoundException("지정된 ISBN에서 책을 찾을 수 없습니다.");
    }
    
    public Book createBook(BookCreationRequest book){
        Optional<Author> author = authorRepository.findById(book.getAuthorId());
        if(!author.isPresent()){
            throw new EntityNotFoundException("작성자를 찾을 수 없음");
        }

        Book bookToCreate = new Book();
        BeanUtils.copyProperties(book, bookToCreate);
        bookToCreate.setAuthor(author.get());
        return bookRepository.save(bookToCreate);
    }

    public static void deleteBook(Long id){
        bookRepository.deleteById(id);
    }

    public Member createMember(MemberCreationRequest request){
        Member member = new Member();
        BeanUtils.copyProperties(request, member);
        return memberRepository.save(member);
    }

    public Member updateMember (Long id, MemberCreationRequest request){
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()){
            throw new EntityNotFoundException("구성원이 데이터베이스에 없습니다.");
        }
        Member member = optionalMember.get();
        member.setLastName(request.getLastName());
        member.setFirstName(request.getFirstName());
        return memberRepository.save(member);
    }

    public Author createAuthor (AuthorCreationRequest request) {
        Author author = new Author();
        BeanUtils.copyProperties(request, author);
        return authorRepository.save(author);
    }

    public List<String> lendABook (List<BookLendRequest> list) {
        List<String> booksApprovedToBurrow = new ArrayList<>();
        list.forEach(bookLendRequest -> {
            Optional<Book> bookForId =
                    bookRepository.findById(bookLendRequest.getBookId());
            if (!bookForId.isPresent()) {
                throw new EntityNotFoundException(
                        "Cant find any book under given ID");
            }

            Optional<Member> memberForId =
                    memberRepository.findById(bookLendRequest.getMemberId());
            if (!memberForId.isPresent()) {
                throw new EntityNotFoundException(
                        "Member not present in the database");
            }

            Member member = memberForId.get();
            if (member != MemberStatus.ACTIVE) {
                throw new RuntimeException(
                        "User is not active to proceed a lending.");
            }

            Optional<Lend> burrowedBook =
                    lendRepository.findByBookAndStatus(
                            bookForId.get(), LendStatus.BURROWED);

            if (!burrowedBook.isPresent()) {
                booksApprovedToBurrow.add(bookForId.get().getName());
                Lend lend = new Lend();
                lend.setMember(memberForId.get());
                lend.setBook(bookForId.get());
                lend.setStatus(LendStatus.BURROWED);
                lend.setStartOn(Instant.now());
                lend.setDueOn(Instant.now().plus(30, ChronoUnit.DAYS));
                lendRepository.save(lend);
            }
        });

        return booksApprovedToBurrow;
    }
}
