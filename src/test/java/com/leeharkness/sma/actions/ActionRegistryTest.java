package com.leeharkness.sma.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class ActionRegistryTest {

    @Mock
    SMAAction mockFirstAction;
    @Mock
    SMAAction mockSecondAction;
    @Mock
    SMAAction mockDuplicateAction;

    private Set<SMAAction> actionSet = new HashSet<>();
    private Set<String> firstActionStrings = Set.of("a");
    private Set<String> secondActionStrings = Set.of("b");
    private Set<String> duplicateActionStrings = Set.of("a");

    private ActionRegistry target;

    @BeforeEach
    public void setup() {
        initMocks(this);
        when(mockFirstAction.invocationStrings()).thenReturn(firstActionStrings);
        when(mockSecondAction.invocationStrings()).thenReturn(secondActionStrings);
        when(mockDuplicateAction.invocationStrings()).thenReturn(duplicateActionStrings);

        actionSet.add(mockFirstAction);
        actionSet.add(mockSecondAction);

        target = new ActionRegistry(actionSet);
    }

    @Test
    public void testThatAnEmptySetReturnsOptionalEmpty() {
        ActionRegistry localTarget = new ActionRegistry(new HashSet<>());

        Optional<SMAAction> result = localTarget.getActionFor("string");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void testThatANonHandledStringReturnsOptionalEmpty() {
        Optional<SMAAction> result = target.getActionFor("bogus");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void testThatWeFindWhatWeAreLookingFor() {
        Optional<SMAAction> result =
                target.getActionFor(mockFirstAction.invocationStrings().stream().findFirst().get());

        assertThat(result.get(), is(mockFirstAction));
    }

    @Test
    public void testThatWeFindOneOfTheActionsWhenDuplicatesArePresent() {
        Set<SMAAction> actionSet = new HashSet<>();
        actionSet.add(mockFirstAction);
        actionSet.add(mockSecondAction);
        actionSet.add(mockDuplicateAction);

        ActionRegistry targetWithDuplicates = new ActionRegistry(actionSet);

        Optional<SMAAction> result = targetWithDuplicates.getActionFor("a");

        assertTrue(result.get().invocationStrings().contains("a"));
    }



}