package com.buyback.eve.service;

import java.util.Optional;

import com.buyback.eve.domain.User;
import com.buyback.eve.repository.AuthorityRepository;
import com.buyback.eve.repository.UserRepository;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private AuthorityRepository authRepo = mock(AuthorityRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private UserService sut = new UserService(userRepo, authRepo);

    @Test
    public void createUser() throws Exception {
        when(userRepo.save(any(User.class))).thenReturn(null);

        User user = sut.createUser("login", 1L);

        assertEquals("login", user.getLogin());
        assertEquals(1L, user.getCharacterId().longValue());
        assertTrue(user.getActivated());
        assertFalse(user.getAuthorities().isEmpty());
    }

    @Test
    public void updateUser() throws Exception {
    }

    @Test
    public void deleteUser() throws Exception {
        User user = new User();
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.of(user));

        sut.deleteUser("");

        verify(userRepo).delete(user);
    }

    @Test
    public void deleteUser_withoutResult() throws Exception {
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.empty());

        sut.deleteUser("");

        verify(userRepo, never()).delete(any(User.class));
    }

    @Test
    public void getAllManagedUsers() throws Exception {
    }

    @Test
    public void getUserWithAuthoritiesByLogin() throws Exception {
    }

    @Test
    public void getUserWithAuthorities() throws Exception {
    }

    @Test
    public void getUserWithAuthorities1() throws Exception {
    }

    @Test
    public void getAuthorities() throws Exception {
    }
}
