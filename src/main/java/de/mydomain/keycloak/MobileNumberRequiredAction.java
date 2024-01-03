package de.mydomain.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import java.util.function.Consumer;


public class MobileNumberRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "mobile_number_provider_id";

    private static final String MOBILE_NUMBER_FIELD = "mobile_number";

    private KeycloakSession keycloakSession;

    public MobileNumberRequiredAction(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

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
        String mobileNumber = requiredActionContext.getUser().getFirstAttribute(MOBILE_NUMBER_FIELD);
        loginFormsProvider.setAttribute(MOBILE_NUMBER_FIELD, mobileNumber == null ? "" : mobileNumber);

        if (formConsumer != null) {
            formConsumer.accept(loginFormsProvider);
        }

        return loginFormsProvider.createForm("my_required_action_template.ftl");
    }

    @Override
    public void processAction(RequiredActionContext context) {

        // submitted form
        UserModel user = context.getUser();

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formData.getFirst(MOBILE_NUMBER_FIELD);

		/*if (Validation.isBlank(mobileNumber) || mobileNumber.length() < 5) {
			context.challenge(createForm(context, form -> form.addError(new FormMessage(MOBILE_NUMBER_FIELD, "Invalid input"))));
			return;
		}*/

        user.setSingleAttribute(MOBILE_NUMBER_FIELD, mobileNumber);
        user.removeRequiredAction(PROVIDER_ID);
        context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);

        context.success();
    }

    @Override
    public void close() {

    }

}
