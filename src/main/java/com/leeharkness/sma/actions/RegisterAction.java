package com.leeharkness.sma.actions;

import com.google.common.annotations.VisibleForTesting;
import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import com.leeharkness.sma.aws.CognitoStub;
import com.leeharkness.sma.aws.DynamoStub;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import javax.inject.Inject;
import java.security.*;
import java.util.*;

/**
 * Handles user registration
 */
@Slf4j
public class RegisterAction extends BaseUnauthenticatedAction {

    private DynamoStub dynamoStub;
    private CognitoStub cognitoStub;
    private ApplicationContext applicationContext;
    private boolean inTest;

    /**
     * Initialization ctor
     * @param dynamoStub Interface to Dynamo
     * @param cognitoStub Interface to Cognito
     * @param applicationContext Application Context
     */
    @Inject
    public RegisterAction(final @NonNull DynamoStub dynamoStub, final @NonNull CognitoStub cognitoStub,
                          final @NonNull ApplicationContext applicationContext) {
        super(applicationContext);
        this.cognitoStub = cognitoStub;
        this.dynamoStub = dynamoStub;
        this.applicationContext = applicationContext;
        this.inTest = false;
    }

    @Override
    public SMAExitStatus execute(final @NonNull TextIO textIO, final @NonNull String[] args) {
        final TextTerminal<?> textTerminal = textIO.getTextTerminal();
        // Get valid inputs
        final Optional<String> optionalUserName = getUserName(args, textIO);
        if (optionalUserName.isEmpty()) {
            printActionCancelledMessage(textTerminal);
            return SMAExitStatus.CANCELLED;
        }
        final Optional<String> optionalPasswordHash = getPasswordHash(textIO);
        if (optionalPasswordHash.isEmpty()) {
            printActionCancelledMessage(textTerminal);
            return SMAExitStatus.CANCELLED;
        }
        final Optional<String> optionalEmail = getEmail(args, textIO);
        if (optionalEmail.isEmpty()) {
            printActionCancelledMessage(textTerminal);
            return SMAExitStatus.CANCELLED;
        }

        String publicKey;
        // Skip generating a public/private key for most tests
        if (inTest) {
            publicKey = "publicKey";
        }
        else {
            try {
                publicKey = generatePublicKey(optionalPasswordHash.get(), applicationContext);
            } catch (NoSuchAlgorithmException nsae) {
                log.error("Could not generate public/private key", nsae);
                return SMAExitStatus.FAILURE;
            }
        }

        final boolean signedUp = cognitoStub.signUpUser(optionalUserName.get(), optionalPasswordHash.get(),
                optionalEmail.get(), publicKey);

        if (signedUp) {
            String confirmationCode = "";
            while (!confirmationCode.equalsIgnoreCase("q")) {
                confirmationCode = textIO.newStringInputReader().read("Enter confirmation code: ");
                if (!confirmationCode.equalsIgnoreCase("q")) {
                    if (cognitoStub.confirmUser(optionalUserName.get(), confirmationCode)) {
                        textTerminal.println("User " + optionalUserName.get() + " registration successful");
                        return SMAExitStatus.SUCCESS;
                    }
                }
            }
            return SMAExitStatus.CANCELLED;
        }
        return SMAExitStatus.FAILURE;
    }

    @Override
    public Set<String> invocationStrings() {
        return Set.of("register", "r");
    }

    /**
     * Gets a valid user name
     * @param args Any arguments to the action (the first one is user name)
     * @param textIO the IO facility
     * @return A valid Optional user name, Optional.empty if the user cancels
     */
    private Optional<String> getUserName(final @NonNull String[] args, final @NonNull TextIO textIO) {
        final TextTerminal<?> textTerminal = textIO.getTextTerminal();
        boolean keepGoing = true;
        boolean firstTime = true;
        String userName = "";
        while(keepGoing) {
            if (args.length > 1 && firstTime) {
                userName = args[1];
                firstTime = false;
            } else {
                textTerminal.println("Enter 'q' to quit");
                userName = textIO.newStringInputReader().read("Username: ");
                if (userName.equalsIgnoreCase("q")) {
                    return Optional.empty();
                }
            }
            if (dynamoStub.lookupUserName(userName).isPresent()) {
                textTerminal.println("Username [" + userName + "] already exists");
                keepGoing = true;
            } else keepGoing = false;
        }
        return Optional.of(userName);
    }

    /**
     * Gets a valid password
     * @param textIO the IO facility
     * @return An Optional hash of a valid password, Optional.empty if the user cancels
     */
    private Optional<String> getPasswordHash(final @NonNull TextIO textIO) {
        final TextTerminal<?> textTerminal = textIO.getTextTerminal();
        boolean keepGoing = true;
        String passwordHash = "";
        while(keepGoing) {
            textTerminal.println("Enter 'q' to quit");
            final String password = textIO.newStringInputReader().read("Password: ");
            if (password.equalsIgnoreCase("q")) {
                return Optional.empty();
            }
            final List<String> validationErrorList = validatePassword(password);
            if (validationErrorList.size() != 0) {
                textTerminal.println("Password is invalid: " + validationErrorList);
                keepGoing = true;
            }
            else {
                try {
                    final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                    final byte[] passwordHashBytes = messageDigest.digest(password.getBytes());
                    final Base64.Encoder base64Encoder = Base64.getEncoder();
                    passwordHash = base64Encoder.encodeToString(passwordHashBytes);
                    keepGoing = false;
                }
                catch (final NoSuchAlgorithmException nsae) {
                    log.error("No MD5 algorithm found", nsae);
                    return Optional.empty();
                }
            }
        }
        return Optional.of(passwordHash);
    }

    /**
     * Gets a valid email
     * @param args Any arguments to the action (the second one is email)
     * @param textIO the IO facility
     * @return A valid Optional email, Optional.empty if the user cancels
     */
    private Optional<String> getEmail(final @NonNull String[] args, final @NonNull TextIO textIO) {
        final TextTerminal<?> textTerminal = textIO.getTextTerminal();
        boolean keepGoing = true;
        boolean firstTime = true;
        String email = "";
        while(keepGoing) {
            if (args.length > 2 && firstTime) {
                email = args[2];
                firstTime = false;
            } else {
                textTerminal.println("Enter 'q' to quit");
                email = textIO.newStringInputReader().read("Email: ");
                if (email.equalsIgnoreCase("q")) {
                    return Optional.empty();
                }
            }
            if (dynamoStub.lookupUserEmail(email).isPresent()) {
                textTerminal.println("Email [" + email + "] already exists");
                keepGoing = true;
            } else keepGoing = false;
        }
        return Optional.of(email);
    }

    /**
     * Used to generate a public key and save the private key
     * @param passwordHash the password to encrypt the private key with
     * @param applicationContext the application context to store the public key in
     * @return the public key
     * @throws NoSuchAlgorithmException if we have a crypto problem
     */
    private String generatePublicKey(final @NonNull String passwordHash,
                                     final @NonNull ApplicationContext applicationContext)
            throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        applicationContext.setPublicKey(keyPair.getPublic());
        savePublicKey(keyPair.getPublic());
        savePrivateKey(keyPair.getPrivate(), passwordHash);
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * Saves the encrypted private key
     * @param privateKey the private key
     * @param passwordHash the password has to encrypt this with
     */
    private void savePrivateKey(final @NonNull PrivateKey privateKey, final @NonNull String passwordHash) {
        // Encrypt this thing and save it to disk
    }

    /**
     * Saves the public key
     * @param publicKey the public key
     */
    private void savePublicKey(final @NonNull PublicKey publicKey) {
        // save it to disk
    }

    /**
     * Enforces password validation rules
     * @param password the password to validate
     * @return a List of validation errors; empty list if valid
     */
    private List<String> validatePassword(final @NonNull String password) {
        return new ArrayList<>();
    }

    /**
     * Validates an email address
     * @param email the email address to validate
     * @return a List of validation errors; empty list if valid
     */
    private List<String> validateEmail(String email) {
        return new ArrayList<>();
    }

    /**
     * Prints the message that the user cancelled this action to the output facility
     * @param textTerminal the output facility
     */
    private void printActionCancelledMessage(final @NonNull TextTerminal<?> textTerminal) {
        textTerminal.println("Registration Cancelled");
    }

    @VisibleForTesting
    protected void setTesting(boolean inTest) {
        this.inTest = inTest;
    }
}
