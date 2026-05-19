package com.openwebinars.todo.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data @Builder
public class UserResponseDto {
private Long id;
private String username;
private String role;
}
