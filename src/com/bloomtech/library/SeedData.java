package com.bloomtech.library;

import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.services.CheckableService;
import com.bloomtech.library.services.LibraryCardService;
import com.bloomtech.library.services.LibraryService;
import com.bloomtech.library.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private PatronService patronService;

    @Autowired
    private LibraryCardService libraryCardService;

    @Autowired
    private CheckableService checkableService;

    @Override
    public void run(String... args) {
        List<Checkable> checkables = new ArrayList<>();

        checkables.addAll(
                Arrays.asList(
                        new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK),
                        new Media("1-1", "The Sorcerer's Quest", "Ana T", MediaType.BOOK),
                        new Media("1-2", "When You're Gone", "Complaining at the Disco", MediaType.MUSIC),
                        new Media("1-3", "Nature Around the World", "DocuSpecialists", MediaType.VIDEO),
                        new ScienceKit("2-0", "Anatomy Model"),
                        new ScienceKit("2-1", "Robotics Kit"),
                        new Ticket("3-0", "Science Museum Tickets"),
                        new Ticket("3-1", "National Park Day Pass")
                )
        );

        Patron p1 = new Patron("Aung", 24);
        Patron p2 = new Patron("Darya", 32);

        Library l1 = new Library("Brightmore Public Library");
        Library l2 = new Library("Corte Madera Library");

        l1.getCheckables().add(new CheckableAmount(checkables.get(0), 2));
        l1.getCheckables().add(new CheckableAmount(checkables.get(1), 1));
        l1.getCheckables().add(new CheckableAmount(checkables.get(4), 1));
        l1.getCheckables().add(new CheckableAmount(checkables.get(7), 4));

        l2.getCheckables().add(new CheckableAmount(checkables.get(0), 1));
        l2.getCheckables().add(new CheckableAmount(checkables.get(1), 3));
        l2.getCheckables().add(new CheckableAmount(checkables.get(2), 2));
        l2.getCheckables().add(new CheckableAmount(checkables.get(3), 4));
        l2.getCheckables().add(new CheckableAmount(checkables.get(4), 3));
        l2.getCheckables().add(new CheckableAmount(checkables.get(5), 5));
        l2.getCheckables().add(new CheckableAmount(checkables.get(6), 8));
        l2.getCheckables().add(new CheckableAmount(checkables.get(7), 6));


        LibraryCard lc1_1 = new LibraryCard();
        lc1_1.setPatron(p1);
        lc1_1.setLibrary(l1);
        p1.getLibraryCards().add(lc1_1);
        l1.getLibraryCards().add(lc1_1);

        LibraryCard lc1_2 = new LibraryCard();
        lc1_2.setPatron(p1);
        lc1_2.setLibrary(l2);
        p1.getLibraryCards().add(lc1_2);
        l2.getLibraryCards().add(lc1_2);

        LibraryCard lc2_2 = new LibraryCard();
        lc2_2.setPatron(p2);
        lc2_2.setLibrary(l2);
        p2.getLibraryCards().add(lc2_2);
        l2.getLibraryCards().add(lc2_2);


        lc1_1.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(0), LocalDateTime.now().plusDays(5)),
                        new Checkout(checkables.get(1), LocalDateTime.now().minusDays(1)),
                        new Checkout(checkables.get(4), LocalDateTime.now().minusDays(3))
                )
        );

        lc1_2.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(2), LocalDateTime.now().plusDays(15)),
                        new Checkout(checkables.get(3), LocalDateTime.now().minusDays(3)),
                        new Checkout(checkables.get(5), LocalDateTime.now().plusDays(4))
                )
        );

        lc2_2.getCheckouts().addAll(
                Arrays.asList(
                        new Checkout(checkables.get(2), LocalDateTime.now().minusDays(15)),
                        new Checkout(checkables.get(3), LocalDateTime.now().minusDays(3)),
                        new Checkout(checkables.get(4), LocalDateTime.now().minusDays(4))
                )
        );

        checkables.forEach(c -> checkableService.save(c));

        patronService.save(p1);
        patronService.save(p2);

        libraryService.save(l1);
        libraryService.save(l2);

        libraryCardService.save(lc1_1);
        libraryCardService.save(lc1_2);
        libraryCardService.save(lc2_2);
    }
}
