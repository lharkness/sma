package com.leeharkness.sma.actions;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import com.leeharkness.sma.aws.CognitoStub;
import com.leeharkness.sma.localstores.MessageStore;
import com.leeharkness.sma.localstores.UserStore;
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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class LoginActionTest {

    @Mock
    private TextIO mockTextIO;
    @Mock
    private ApplicationContext mockApplicationContext;
    @Mock
    private CognitoStub mockCognitoStub;
    @Mock
    private MessageStore mockMessageStore;
    @Mock
    private UserStore mockUserStore;
    @Mock
    Credentials mockCredentials;
    @Mock
    private StringInputReader mockStringInputReader;
    @SuppressWarnings("rawtypes")
    @Mock
    private TextTerminal mockTextTerminal;

    @InjectMocks
    LoginAction target;

    @BeforeEach
    public void setup() {
        initMocks(this);

        //noinspection unchecked
        when(mockTextIO.getTextTerminal()).thenReturn(mockTextTerminal);
        when(mockTextIO.newStringInputReader()).thenReturn(mockStringInputReader);
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

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.CANCELLED));
    }

    @Test
    public void testThatWeAttemptToLogOnAUser() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");

        //noinspection OptionalGetWithoutIsPresent
        when(mockCognitoStub.login("username", ActionUtils.generatePasswordHash("password").get()))
                .thenReturn(Optional.of(mockCredentials));

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.SUCCESS));

        verify(mockApplicationContext).setCredentials(anyObject());
        verify(mockMessageStore).sync();
        verify(mockUserStore).sync();
    }

    @Test
    public void testThatWeReportLoginFailure() {
        when(mockStringInputReader.read("Username: ")).thenReturn("username");
        when(mockStringInputReader.read("Password: ")).thenReturn("password");

        //noinspection OptionalGetWithoutIsPresent
        when(mockCognitoStub.login("username", ActionUtils.generatePasswordHash("password").get()))
                .thenReturn(Optional.empty());

        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.LOGIN_FAILURE));

        verify(mockMessageStore, never()).sync();
        verify(mockUserStore, never()).sync();
    }

    @Test
    public void testThatWeAttemptToLogOnAUsernameFromCommandLine() {
        when(mockStringInputReader.read("Password: ")).thenReturn("password");

        //noinspection OptionalGetWithoutIsPresent
        when(mockCognitoStub.login("username", ActionUtils.generatePasswordHash("password").get()))
                .thenReturn(Optional.of(mockCredentials));

        String[] args = {"login", "username"};
        SMAExitStatus result = target.execute(mockTextIO, args);

        assertThat(result, is(SMAExitStatus.SUCCESS));

        verify(mockApplicationContext).setCredentials(anyObject());
        verify(mockMessageStore).sync();
        verify(mockUserStore).sync();
    }


}