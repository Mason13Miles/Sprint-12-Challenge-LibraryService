package com.bloomtech.library.services;

import com.bloomtech.library.datastore.Datastore;
import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.models.checkableTypes.Media;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.models.CheckableAmount;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    //TODO: Implement behavior described by the unit tests in tst.com.bloomtech.library.services.LibraryService

    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private CheckableService checkableService;

    public List<Library> getLibraries() {
        List<Library> libraries = libraryRepository.findAll();

        return libraries;
    }

    public Library getLibraryByName(String name) {
        Optional<Library> libraryOptional = libraryRepository.findByName(name);

        if (libraryOptional.isPresent()) {
            return libraryOptional.get();
        } else {
            throw new LibraryNotFoundException("Library with name: " + name + " not found in repository");
        }
    }
    public void save(Library library) {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.stream().filter(p->p.getName().equals(library.getName())).findFirst().isPresent()) {
            throw new ResourceExistsException("Library with name: " + library.getName() + " already exists!");
        }
        libraryRepository.save(library);
    }

    public CheckableAmount getCheckableAmount(String libraryName, String checkableIsbn) {
        Library library = libraryRepository.findByName(libraryName)
                .orElseThrow(() -> new LibraryNotFoundException("Library not found: " + libraryName));

        Checkable checkable = checkableService.getByIsbn(checkableIsbn);

        for (CheckableAmount checkableAmount : library.getCheckables()) {
            if (checkableAmount.getCheckable().getIsbn().equals(checkableIsbn)) {
                return checkableAmount;
            }
        }

        return new CheckableAmount(checkable, 0);
    }

    public List<LibraryAvailableCheckouts> getLibrariesWithAvailableCheckout(String isbn) {
        List<LibraryAvailableCheckouts> available = new ArrayList<>();

        Checkable checkable = checkableService.getByIsbn(isbn);

        List<Library> libraries = libraryRepository.findAll();

        for (Library library : libraries) {
            for (CheckableAmount checkableAmount : library.getCheckables()) {
                if (checkableAmount.getCheckable().getIsbn().equals(isbn) && checkableAmount.getAmount() > 0) {
                    available.add(new LibraryAvailableCheckouts(checkableAmount.getAmount(), library.getName()));
                }
            }
        }
        System.out.println(available);
        return available;
    }

    public List<OverdueCheckout> getOverdueCheckouts(String libraryName) {
        List<OverdueCheckout> overdueCheckouts = new ArrayList<>();

        Library library = libraryRepository.findByName(libraryName)
                .orElseThrow(() -> new LibraryNotFoundException("Library not found: " + libraryName));

        for (LibraryCard libraryCard : library.getLibraryCards()) {
            for (Checkout checkout : libraryCard.getCheckouts()) {
                if (checkout.getDueDate().isBefore(LocalDateTime.now())) {
                    overdueCheckouts.add(new OverdueCheckout(libraryCard.getPatron(), checkout));
                }
            }
        }

        return overdueCheckouts;
    }

}
