FROM quay.io/keycloak/keycloak:latest

ARG KEITHS_THEME_CONTAINER_DIR=/opt/keycloak/themes/keithstheme

ARG KEITHS_THEME_LOCAL_DIR=themes/keithstheme

ARG USER_PERSISTENCE_REGISTRATION_AUTHENTICATOR=user-persistence-registration-authenticator.jar

ARG PROVIDERS_USER_PERSISTENCE_REGISTRATION_AUTHENTICATOR=/opt/keycloak/providers/user-persistence-registration-authenticator.jar 

ARG PASSWORD_REGISTRATION_AUTHENTICATOR=password-registration-authenticator.jar

ARG PROVIDERS_PASSWORD_REGISTRATION_AUTHENTICATOR=/opt/keycloak/providers/password-registration-authenticator.jar

ARG EMAIL_MOBILE_NUMBER_REGISTRATION_AUTHENTICATOR=email-mobile-number-registration-authenticator.jar

ARG PROVIDERS_EMAIL_MOBILE_NUMBER_REGISTRATION_AUTHENTICATOR=/opt/keycloak/providers/email-mobile-number-registration-authenticator.jar

ARG NAMES_REGISTRATION_AUTHENTICATOR=names-registration-authenticator.jar

ARG CONTAINER_NAMES_REGISTRATION_AUTHENTICATOR=/opt/keycloak/providers/names-registration-authenticator.jar

ARG VERIFY_MOBILE_NUMBER_RA=verify-mobile-code-ra-V1.5.jar

ARG CONTAINER_VERIFY_MOBILE_NUMBER_RA=/opt/keycloak/providers/verify-mobile-code-ra-V1.5.jar

ARG SEND_AND_VERIFY_EMAIL=send-and-verify-email-authenticator.jar

ARG CONTAINER_SEND_AND_VERIFY_EMAIL=/opt/keycloak/providers/send-and-verify-email-authenticator.jar

ARG VERIFY_MOBILE_CODE_AUTHENTICATOR=verify-mobile-code-authenticator.jar

ARG CONTAINER_VERIFY_MOBILE_CODE_AUTHENTICATOR=/opt/keycloak/providers/verify-mobile-code-authenticator.jar

ARG FRIENDS_LIST_QUERY_PROVIDER=friends-list-query-provider-V1.1.jar

ARG CONTAINER_FRIENDS_LIST_QUERY_PROVIDER=/opt/keycloak/providers/friends-list-query-provider-V1.1.jar

ARG KEYCLOAK_SSL_P12=keycloak_server.p12

ARG CONTAINER_KEYCLOAK_SSL_P12=/opt/keycloak/conf/truststores/keycloak_server.p12

ARG LOGIN_EVENT_LISTENER=login-event-listener.jar

ARG CONTAINER_LOGIN_EVENT_LISTENER=/opt/keycloak/providers/event-listener.jar

ARG USER_DELETION_LISTENER=delete-user-event-listener.jar

ARG CONTAINER_USER_DELETION_LISTENER=/opt/keycloak/providers/delete-user-event-listener.jar

RUN mkdir ${KEITHS_THEME_CONTAINER_DIR}

COPY ${KEITHS_THEME_LOCAL_DIR} ${KEITHS_THEME_CONTAINER_DIR}
COPY ${USER_PERSISTENCE_REGISTRATION_AUTHENTICATOR} ${PROVIDERS_USER_PERSISTENCE_REGISTRATION_AUTHENTICATOR} 
COPY ${PASSWORD_REGISTRATION_AUTHENTICATOR} ${PROVIDERS_PASSWORD_REGISTRATION_AUTHENTICATOR} 
COPY ${EMAIL_MOBILE_NUMBER_REGISTRATION_AUTHENTICATOR} ${PROVIDERS_EMAIL_MOBILE_NUMBER_REGISTRATION_AUTHENTICATOR} 
COPY ${NAMES_REGISTRATION_AUTHENTICATOR} ${CONTAINER_NAMES_REGISTRATION_AUTHENTICATOR}
COPY ${VERIFY_MOBILE_NUMBER_RA} ${CONTAINER_VERIFY_MOBILE_NUMBER_RA}
COPY ${SEND_AND_VERIFY_EMAIL} ${CONTAINER_SEND_AND_VERIFY_EMAIL}
COPY ${VERIFY_MOBILE_CODE_AUTHENTICATOR} ${CONTAINER_VERIFY_MOBILE_CODE_AUTHENTICATOR}
COPY ${FRIENDS_LIST_QUERY_PROVIDER} ${CONTAINER_FRIENDS_LIST_QUERY_PROVIDER}
COPY ${KEYCLOAK_SSL_P12} ${CONTAINER_KEYCLOAK_SSL_P12}
COPY ${LOGIN_EVENT_LISTENER} ${CONTAINER_LOGIN_EVENT_LISTENER} 
COPY ${USER_DELETION_LISTENER} ${CONTAINER_USER_DELETION_LISTENER}
# # Switch to root user to modify the cacerts
USER root

# # Adds RootCA.crt to trust store of Keycloak: 
COPY rootCA.crt /opt/keycloak/rootCA.crt

# # # Install keytool if not already available
# RUN apt-get update && apt-get install -y openjdk-17-jre-headless && apt-get clean

# # Import the root certificate into the Java cacerts trust store
RUN keytool -import -trustcacerts -alias backend -file /opt/keycloak/rootCA.crt \
    -keystore /usr/lib/jvm/java-21-openjdk-21.0.5.0.11-2.el9.x86_64/lib/security/cacerts -storepass changeit -noprompt 
                            # ^ change path to latest path per latest version of Keycloak container 

# # Switch to root user to modify the cacerts
USER keycloak 

CMD ["start-dev"] 