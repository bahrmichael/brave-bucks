package com.bravebucks.eve.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A user.
 */

@Document(collection = "jhi_user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private Integer allianceId;

    @NotNull
    @Indexed
    private String login;

    @NotNull
    private Long characterId;

    private boolean activated = false;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    @JsonIgnore
    private Map<Integer, String> walletReadRefreshTokens = new HashMap<>();

    @Deprecated
    public Map<Integer, String> getWalletReadRefreshTokens() {
        return walletReadRefreshTokens;
    }

    @Deprecated
    public void setWalletReadRefreshTokens(final Map<Integer, String> walletReadRefreshTokens) {
        this.walletReadRefreshTokens = walletReadRefreshTokens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Long characterId) {
        this.characterId = characterId;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return !(user.getId() == null || getId() == null) && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", activated='" + activated + '\'' +
            "}";
    }

    public Integer getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(final Integer allianceId) {
        this.allianceId = allianceId;
    }
}
