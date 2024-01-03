package de.mydomain.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;

import java.util.function.Consumer;


public class MyRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "phone_number_provider_id";

    private static final String PHONE_NUMBER_FIELD = "phone_number";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext requiredActionContext) {
        requiredActionContext.challenge(createForm(requiredActionContext, null));
    }

    private Response createForm(RequiredActionContext requiredActionContext, Consumer<LoginFormsProvider> formConsumer) {

        LoginFormsProvider loginFormsProvider = requiredActionContext.form();

        // für die Ausgabe des Usernames im Formular, z.B. "Hallo Maxmustermann"
        loginFormsProvider.setAttribute("username", requiredActionContext.getUser().getUsername());

        // Wenn die User schon eine Telefonnummer hat, dann sie auch in der Maske entsprechend ausgeben
        String phoneNumber = requiredActionContext.getUser().getFirstAttribute(PHONE_NUMBER_FIELD);
        loginFormsProvider.setAttribute(PHONE_NUMBER_FIELD, phoneNumber == null ? "" : phoneNumber);

        if (formConsumer != null) {
            formConsumer.accept(loginFormsProvider);
        }

        return loginFormsProvider.createForm("my_required_action_template.ftl");
    }

    @Override
    public void processAction(RequiredActionContext context) {

        UserModel user = context.getUser();

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        String phoneNumber = formData.getFirst(PHONE_NUMBER_FIELD);

        user.setSingleAttribute(PHONE_NUMBER_FIELD, phoneNumber);

        user.removeRequiredAction(PROVIDER_ID);

        context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);

        context.success();
    }

    @Override
    public void close() {

    }

}
