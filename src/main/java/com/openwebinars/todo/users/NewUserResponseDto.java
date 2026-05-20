package com.openwebinars.todo.users;

public record NewUserResponseDto(
        Long id,
        String username,
        String email
) {
    public static NewUserResponseDto of(User user) {
        return new NewUserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
