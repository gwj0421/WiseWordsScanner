package com.example.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoleTypeTest {
    @Test
    void Should_searchRoleTypeAndMatch_When_givenRole() {
        // given
        String roleTarget = "ROLE_USER";

        // when
        RoleType expectedRoleType = RoleType.getRoleTypeByRole(roleTarget);
        boolean isMatch = RoleType.isMatch(RoleType.USER, roleTarget);

        // then
        assertThat(expectedRoleType).isEqualTo(RoleType.USER);
        assertThat(isMatch).isTrue();
    }
}