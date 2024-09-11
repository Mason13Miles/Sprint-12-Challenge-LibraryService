package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Library;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.repositories.CheckableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CheckableServiceTest {

    @Autowired
    private CheckableService checkableService;
    @MockBean
    private CheckableRepository checkableRepository;

    private List<Checkable> checkables;

    @BeforeEach
    void init() {
        //Initialize test data
        checkables = new ArrayList<>();

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
    }

    //TODO: Write Unit Tests for all CheckableService methods and possible Exceptions

    // test getAll()
    @Test
    void testGetAll() {
        when(checkableRepository.findAll()).thenReturn(checkables);

        List<Checkable> result = checkableService.getAll();

        assertEquals(checkables.size(), result.size());
        assertEquals(checkables.get(0).getIsbn(), result.get(0).getIsbn());
        verify(checkableRepository, times(1)).findAll();
    }
    //test getByISBN
    @Test
    void testGetByIsbn() {
        Checkable checkable = checkables.get(0);
        when(checkableRepository.findByIsbn(checkable.getIsbn())).thenReturn(Optional.of(checkable));

        Checkable result = checkableService.getByIsbn(checkable.getIsbn());

        assertEquals(checkable.getIsbn(), result.getIsbn());
        verify(checkableRepository, times(1)).findByIsbn(checkable.getIsbn());
    }

    //test getByISBN throws exception
    @Test
    void testGetByIsbnThrowsException() {
        String isbn = "non-existent-isbn";
        when(checkableRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, () -> checkableService.getByIsbn(isbn));
        verify(checkableRepository, times(1)).findByIsbn(isbn);
    }

    //test getByType
    @Test
    void testGetByType() {
        Class<?> type = Media.class;
        Checkable checkable = checkables.get(0);
        when(checkableRepository.findByType(type)).thenReturn(Optional.of(checkable));

        Checkable result = checkableService.getByType(type);

        assertEquals(checkable.getClass(), result.getClass());
        verify(checkableRepository, times(1)).findByType(type);
    }

    //test getByType thorws excpetion
    @Test
    void testGetByTypeThrowsException() {
        Class<?> type = ScienceKit.class;
        when(checkableRepository.findByType(type)).thenReturn(Optional.empty());

        assertThrows(CheckableNotFoundException.class, () -> checkableService.getByType(type));
        verify(checkableRepository, times(1)).findByType(type);
    }

    //test save()
    @Test
    void testSave() {
        Checkable newCheckable = new Media("1-5", "New Media Title", "Author X", MediaType.BOOK);

        when(checkableRepository.findAll()).thenReturn(checkables);
        checkableService.save(newCheckable);

        verify(checkableRepository, times(1)).save(newCheckable);
    }

    //test save() throws exception
    @Test
    void testSaveThrowsException() {
        Checkable duplicateCheckable = checkables.get(0);  // An existing checkable in the list

        when(checkableRepository.findAll()).thenReturn(checkables);

        assertThrows(ResourceExistsException.class, () -> checkableService.save(duplicateCheckable));
        verify(checkableRepository, times(0)).save(duplicateCheckable);
    }

}