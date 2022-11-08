package me.lullaby.study.restdocstest;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryController {

    @GetMapping("/books")
    public Book getBooks() {
        return new Book("Circuit Theory", "Professor", new Category("Electronics"));
    }

    public static record Category(@NonNull String name){}
    public static record Book(@NonNull String title, String writer, Category category){}
}
