package com.mithulram.assetaccess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("!demo")
public class CredentialStartupValidator {

    private static final Set<String> FORBIDDEN_PASSWORDS = Set.of(
            "demo-admin-change-me",
            "demo-operator-change-me",
            "demo-viewer-change-me");

    CredentialStartupValidator(
            @Value("${asset-access.demo.admin-password:}") String adminPassword,
            @Value("${asset-access.demo.operator-password:}") String operatorPassword,
            @Value("${asset-access.demo.viewer-password:}") String viewerPassword) {
        validate("ASSET_ACCESS_ADMIN_PASSWORD", adminPassword);
        validate("ASSET_ACCESS_OPERATOR_PASSWORD", operatorPassword);
        validate("ASSET_ACCESS_VIEWER_PASSWORD", viewerPassword);
    }

    private void validate(String variableName, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    variableName + " must be set when the demo profile is inactive. "
                            + "Activate the demo profile for local portfolio use or provide explicit secrets.");
        }
        if (FORBIDDEN_PASSWORDS.contains(password)) {
            throw new IllegalStateException(
                    variableName + " uses a known demo password outside the demo profile. "
                            + "Choose a local secret or run with spring.profiles.active=demo.");
        }
    }
}
