package me.whiteship.demospringsecurityform.common;

import me.whiteship.demospringsecurityform.account.Account;
import me.whiteship.demospringsecurityform.account.AccountService;
import me.whiteship.demospringsecurityform.book.Book;
import me.whiteship.demospringsecurityform.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultDataGenerator implements ApplicationRunner {
    @Autowired
    AccountService accountService;

    @Autowired
    BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // keesun - spring
        // whiteship - hibernate
        Account keesun = createUser("keesun");
        Account whiteship = createUser("whiteship");

        createBook("spring", keesun);
        createBook("hibernate", whiteship);
    }

    private void createBook(String title, Account author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        bookRepository.save(book);
    }

    private Account createUser(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("123");
        account.setRole("USER");
        return accountService.createNew(account);
    }
}
