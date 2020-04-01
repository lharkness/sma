package com.leeharkness.sma.ui;

import com.github.lalyos.jfiglet.FigletFont;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leeharkness.sma.ApplicationContext;
import com.leeharkness.sma.SMAExitStatus;
import com.leeharkness.sma.actions.ActionRegistry;
import com.leeharkness.sma.actions.SMAAction;
import com.leeharkness.sma.modules.ActionModule;
import lombok.NonNull;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.web.RunnerData;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Main application for the terminal version of SMA
 */
public class TerminalApp implements BiConsumer<TextIO, RunnerData> {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String BANNER = "SMA";

    private final ActionRegistry actionRegistry;

    private final ApplicationContext applicationContext;

    /**
     * Initialization ctor
     * @param actionRegistry the action registry to use
     * @param applicationContext the singleton application context
     */
    @Inject
    public TerminalApp(ActionRegistry actionRegistry, ApplicationContext applicationContext) {
        this.actionRegistry = actionRegistry;
        this.applicationContext = applicationContext;
    }

    /**
     * Main program entry point
     * @param args command-line arguments
     */
    public static void main(final @NonNull String[] args) {
        final Injector injector = Guice.createInjector(new ActionModule());
        final TerminalApp app = injector.getInstance(TerminalApp.class);
        final TextIO textIO = TextIoFactory.getTextIO();

        textIO.getTextTerminal().println(FigletFont.convertOneLine(BANNER));
        textIO.getTextTerminal().println("version 0.1");
        app.accept(textIO, null);
    }

    /**
     * Main program logic
     * @param textIO the IO facility
     * @param runnerData no idea
     */
    @Override
    public void accept(final @NonNull TextIO textIO, final RunnerData runnerData) {
        final TextTerminal<?> terminal = textIO.getTextTerminal();

        // I have no idea how this works/what this does
        String initData = (runnerData == null) ? null : runnerData.getInitData();

        String prompt = "Enter 'q' to quit \n> ";
        boolean keepGoing = true;
        SMAExitStatus exitStatus = SMAExitStatus.SUCCESS;

        while (keepGoing) {
            final String requestString = textIO.newStringInputReader().read(prompt);
            final String[] args = requestString.split(" ");
            final Optional<SMAAction> optionalAction = actionRegistry.getActionFor(args[0]);
            if (optionalAction.isEmpty()) {
                terminal.println("Unrecognized command [" + requestString + "]");
                continue;
            }
            exitStatus = optionalAction.get().execute(textIO, args);
            if (optionalAction.get().shouldTerminate() ) {
                keepGoing = false;
            }
        }

        System.exit(exitStatus.getValue());

    }
}
