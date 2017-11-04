package com.bravebucks.eve.service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bravebucks.eve.domain.Authority;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.service.dto.UserDTO;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class UserMapperTest {

    private static final String DEFAULT_ID = "id1";
    private static final String DEFAULT_LOGIN = "johndoe";

    private UserMapper sut = new UserMapper();

    @Test
    public void usersToUserDTOs() {
        final User user = new User();
        user.setId("id");
        user.setLogin("login");
        user.setActivated(true);
        final Set<Authority> authSet = new HashSet<>();
        authSet.add(new Authority());
        user.setAuthorities(authSet);
        final List<User> users = new ArrayList<>();
        users.add(null);
        users.add(user);

        List<UserDTO> userDTOS = sut.usersToUserDTOs(users);
        assertEquals(1, userDTOS.size());
        UserDTO dto = userDTOS.get(0);
        assertNotNull(dto);
        assertEquals("id", dto.getId());
        assertEquals("login", dto.getLogin());
        assertTrue(dto.isActivated());
        assertFalse(dto.getAuthorities().isEmpty());
    }

    @Test
    public void testUserDTOtoUser() {
        UserDTO userDTO = new UserDTO(
            DEFAULT_ID,
            DEFAULT_LOGIN,
            true,
            DEFAULT_LOGIN,
            null,
            DEFAULT_LOGIN,
            null,
            Stream.of(AuthoritiesConstants.USER).collect(Collectors.toSet()));
        User user = sut.userDTOToUser(userDTO);
        assertThat(user.getId()).isEqualTo(DEFAULT_ID);
        assertThat(user.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(user.getActivated()).isEqualTo(true);
        assertThat(user.getCreatedBy()).isNull();
        assertThat(user.getCreatedDate()).isNotNull();
        assertThat(user.getLastModifiedBy()).isNull();
        assertThat(user.getLastModifiedDate()).isNotNull();
        assertThat(user.getAuthorities()).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }


    @Test
    public void testUserFromId() {
        Assertions.assertThat(sut.userFromId(DEFAULT_ID).getId()).isEqualTo(DEFAULT_ID);
        Assertions.assertThat(sut.userFromId(null)).isNull();
    }

    @Test
    public void testUserToUserDTO() {
        User user = new User();
        user.setId(DEFAULT_ID);
        user.setActivated(true);
        user.setLogin(DEFAULT_LOGIN);
        user.setCreatedBy(DEFAULT_LOGIN);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy(DEFAULT_LOGIN);
        user.setLastModifiedDate(Instant.now());

        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);
        user.setAuthorities(authorities);

        UserDTO userDTO = sut.userToUserDTO(user);

        assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userDTO.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.isActivated()).isEqualTo(true);
        assertThat(userDTO.getCreatedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getCreatedDate()).isEqualTo(user.getCreatedDate());
        assertThat(userDTO.getLastModifiedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
        assertThat(userDTO.getAuthorities()).containsExactly(AuthoritiesConstants.USER);
        assertThat(userDTO.toString()).isNotNull();
    }

    @Test
    public void testAuthorityEquals() throws Exception {
        Authority authorityA = new Authority();
        assertThat(authorityA).isEqualTo(authorityA);
        assertThat(authorityA).isNotEqualTo(null);
        assertThat(authorityA).isNotEqualTo(new Object());
        assertThat(authorityA.hashCode()).isEqualTo(0);
        assertThat(authorityA.toString()).isNotNull();

        Authority authorityB = new Authority();
        assertThat(authorityA).isEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityA.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isEqualTo(authorityB);
        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode());
    }

    @Test
    public void userFromId_withNull_returnsNull() throws Exception {
        assertNull(sut.userFromId(null));
    }

    @Test
    public void userDTOToUser_withNull_returnsNull() throws Exception {
        assertNull(sut.userDTOToUser(null));
    }
}
