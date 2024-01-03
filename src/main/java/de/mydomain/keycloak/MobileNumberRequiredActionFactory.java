package de.mydomain.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class MobileNumberRequiredActionFactory implements RequiredActionFactory {

    @Override
    public String getId() {
        return MobileNumberRequiredAction.PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return "Update Mobile";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession keycloakSession) {
        return new MobileNumberRequiredAction(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }
}
