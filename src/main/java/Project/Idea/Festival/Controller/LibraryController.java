package Project.Idea.Festival.Controller;

import Project.Idea.Festival.Request.BookCreationRequest;
import Project.Idea.Festival.Request.BookLendRequest;
import Project.Idea.Festival.Request.MemberCreationRequest;
import Project.Idea.Festival.domain.Book;
import Project.Idea.Festival.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.List;

@RestController
@RequestMapping(value = "api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping("/book")
    public ResponseEntity readBooks(@RequestParam(required = false) String isbn) {
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks());
        }
        return ResponseEntity.ok(libraryService.readBook(isbn));
    }

    @GetMapping("book/{bookId}")
    public ResponseEntity<Book> readBook (@PathVariable Long bookId){
        return ResponseEntity.ok(libraryService.readBook(bookId));
    }

    @PostMapping("book")
    public ResponseEntity<Book> createBook (@RequestBody BookCreationRequest request){
        return ResponseEntity.ok(libraryService.createBook(request));
    }

    @DeleteMapping("book/{bookId}")
    public ResponseEntity<Void> deleteBook (@PathVariable Long bookId){
        LibraryService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("member")
    public ResponseEntity<Member> createMember(@RequestBody MemberCreationRequest request){
        return ResponseEntity.ok(libraryService.createMember(request));
    }

    @PatchMapping("member/{memberId}")
    public ResponseEntity<Member> updateMember (@RequestBody MemberCreationRequest request, @PathVariable Long memberId){
        return ResponseEntity.ok(libraryService.updateMember(memberId, request));
    }

    @PostMapping("book/lend")
    public ResponseEntity<List<String>> lendABook(@RequestBody BookLendRequest bookLendRequest){
        return ResponseEntity.ok(libraryService.lendABook(bookLendRequest));
    }
}
