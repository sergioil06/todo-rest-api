package com.openwebinars.todo.users;

public record NewUserDto(
        String username,
        String email,
        String password
) {
}
