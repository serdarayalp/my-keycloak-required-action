package de.mydomain.keycloak;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.InitiatedActionSupport;
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
    public void requiredActionChallenge(RequiredActionContext context) {
        context.challenge(createForm(context, null));
    }

    private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
        LoginFormsProvider form = context.form();
        form.setAttribute("username", context.getUser().getUsername());

        String mobileNumber = context.getUser().getFirstAttribute(MOBILE_NUMBER_FIELD);
        form.setAttribute(MOBILE_NUMBER_FIELD, mobileNumber == null ? "" : mobileNumber);

        if (formConsumer != null) {
            formConsumer.accept(form);
        }

        return form.createForm("my_required_action_template.ftl");
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
