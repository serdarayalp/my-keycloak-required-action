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

    /**
     * Wird jedes Mal aufgerufen, wenn sich ein Benutzer authentifiziert.
     * Dabei wird geprüft, ob die erforderliche Aktion ausgelöst werden soll.
     *
     * @param context
     */
    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        /*
        if (context.getUser().getFirstAttribute(PHONE_NUMBER_FIELD) == null) {
            context.getUser().addRequiredAction(PROVIDER_ID);
            context.getAuthenticationSession().addRequiredAction(PROVIDER_ID);
        }
        */
    }

    /**
     * Wenn der Benutzer eine RequiredAction hat, wird diese Methode der erste Aufruf sein,
     * um zu erfahren, was dem Browser des Benutzers angezeigt werden soll.
     *
     * @param requiredActionContext
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext requiredActionContext) {
        requiredActionContext.challenge(createForm(requiredActionContext, null));
    }

    private Response createForm(RequiredActionContext requiredActionContext, Consumer<LoginFormsProvider> formConsumer) {

        LoginFormsProvider form = requiredActionContext.form();

        // für die Ausgabe des Usernames im Formular, z.B. "Hallo Maxmustermann"
        form.setAttribute("username", requiredActionContext.getUser().getUsername());

        // Wenn die User schon eine Telefonnummer hat, dann sie ist auch in der Maske entsprechend auszugeben
        String phoneNumber = requiredActionContext.getUser().getFirstAttribute(PHONE_NUMBER_FIELD);
        form.setAttribute(PHONE_NUMBER_FIELD, phoneNumber == null ? "" : phoneNumber);

        if (formConsumer != null) {
            formConsumer.accept(form);
        }

        return form.createForm("my_required_action_template.ftl");
    }

    /**
     * Wird aufgerufen, wenn eine RequiredAction Formulareingaben hat,
     * die man verarbeiten möchte.
     *
     * @param requiredActionContext
     */
    @Override
    public void processAction(RequiredActionContext requiredActionContext) {

        UserModel user = requiredActionContext.getUser();

        MultivaluedMap<String, String> formData = requiredActionContext.getHttpRequest().getDecodedFormParameters();

        String phoneNumber = formData.getFirst(PHONE_NUMBER_FIELD);

        user.setSingleAttribute(PHONE_NUMBER_FIELD, phoneNumber);

        user.removeRequiredAction(PROVIDER_ID);

        requiredActionContext.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);

        requiredActionContext.success();
    }

    @Override
    public void close() {

    }

}
