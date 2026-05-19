package com.openwebinars.todo.users;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequestDto {
private String username;
private String password;
private String email;
private String fullname;
}
