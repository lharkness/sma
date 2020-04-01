package com.leeharkness.sma.actions;

import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import org.beryx.textio.TextIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("unused")
class ExitActionTest {

    @Mock
    ApplicationContext mockApplicationContext;
    @Mock
    TextIO mockTextIO;

    @InjectMocks
    private ExitAction target;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testWeExitFromThisAction() {
        SMAExitStatus result = target.execute(mockTextIO, new String[1]);

        assertThat(result, is(SMAExitStatus.SUCCESS));
        assertThat(result.getValue(), is(0));
        assertTrue(target.shouldTerminate());
    }

    @Test
    public void testThatWeHaveAtLeastOneReasonableWayToInvokeThisAction() {
        assertTrue(target.invocationStrings().contains("q"));
    }

}