package com.catface996.auth.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command for user login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCommand {

    private String identifier;  // username or email
    private String password;
    private boolean rememberMe;
    private String ipAddress;
    private String userAgent;
}
