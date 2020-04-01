package com.leeharkness.sma.actions;

import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import com.leeharkness.sma.aws.CognitoStub;
import com.leeharkness.sma.aws.DynamoStub;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RegisterActionTest {

    @Mock
    private TextIO mockTextIO;
    @Mock
    private ApplicationContext mockApplicationContext;
    @Mock
    private DynamoStub mockDynamoStub;
    @Mock
    private CognitoStub mockCognitoStub;
    @Mock
    private StringInputReader mockStringInputReader;
    @Mock
    private TextTerminal mockTextTerminal;

    @InjectMocks
    RegisterAction target;

    @BeforeEach
    public void setup() {
        initMocks(this);

        when(mockTextIO.getTextTerminal()).thenReturn(mockTextTerminal);
        when(mockTextIO.newStringInputReader()).thenReturn(mockStringInputReader);

        target.setTesting(true);
    }

    @Test
    public void testThatWeCanCancelOnUserName() {
        when(mockStringInputReader.read("Username: ")).thenReturn("q");

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.CANCELLED));
    }

    @Test
    public void testThatWeCancelOnPassword() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("q");

        // Indicate that we don't have a duplicate username
        when(mockDynamoStub.lookupUserName("username")).thenReturn(Optional.empty());

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.CANCELLED));
    }

    @Test
    public void testThatWeCancelOnEmail() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");
        when(mockStringInputReader.read("Email: ")).thenReturn("q");

        // Indicate that we don't have a duplicate username
        when(mockDynamoStub.lookupUserName("username")).thenReturn(Optional.empty());

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.CANCELLED));
    }

    @Test
    public void testThatWeCanSuccessfullySignUpAUser() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");
        when(mockStringInputReader.read("Email: ")).thenReturn("email");

        // Indicate that we don't have a duplicate username or email
        when(mockDynamoStub.lookupUserName("username")).thenReturn(Optional.empty());
        when(mockDynamoStub.lookupUserEmail("email")).thenReturn(Optional.empty());

        when(mockCognitoStub.signUpUser(eq("username"), anyString(), eq("email"), anyString()))
                .thenReturn(true);
        when(mockCognitoStub.confirmUser("username", "confirmationcode")).thenReturn(true);

        when(mockStringInputReader.read("Enter confirmation code: ")).thenReturn("confirmationcode");

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.SUCCESS));
    }

    @Test
    public void testThatWeReportProblemsSigningUpUser() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");
        when(mockStringInputReader.read("Email: ")).thenReturn("email");

        // Indicate that we don't have a duplicate username or email
        when(mockDynamoStub.lookupUserName("username")).thenReturn(Optional.empty());
        when(mockDynamoStub.lookupUserEmail("email")).thenReturn(Optional.empty());

        when(mockCognitoStub.signUpUser(eq("username"), anyString(), eq("email"), anyString()))
                .thenReturn(false);

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.FAILURE));
    }

    @Test
    public void testThatWeCanCancelConfirmationCode() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");
        when(mockStringInputReader.read("Email: ")).thenReturn("email");

        // Indicate that we don't have a duplicate username or email
        when(mockDynamoStub.lookupUserName("username")).thenReturn(Optional.empty());
        when(mockDynamoStub.lookupUserEmail("email")).thenReturn(Optional.empty());

        when(mockCognitoStub.signUpUser(eq("username"), anyString(), eq("email"), anyString()))
                .thenReturn(true);

        when(mockStringInputReader.read("Enter confirmation code: ")).thenReturn("q");

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.CANCELLED));
    }

}