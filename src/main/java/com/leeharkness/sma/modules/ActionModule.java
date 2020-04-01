package com.leeharkness.sma.modules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.leeharkness.sma.actions.ExitAction;
import com.leeharkness.sma.actions.RegisterAction;
import com.leeharkness.sma.actions.SMAAction;

/**
 * Module containing all known Actions
 */
public class ActionModule extends AbstractModule {
    /**
     * Define a multibinder with all the actions SMA knows about
     */
    protected void configure() {
        Multibinder<SMAAction> multibinder = Multibinder.newSetBinder(binder(), SMAAction.class);
        multibinder.addBinding().to(RegisterAction.class);
        multibinder.addBinding().to(ExitAction.class);
    }
}
