package com.joker.module_order.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.joker.module_order.di.module.GosDeviceReadyModule;

import com.jess.arms.di.scope.ActivityScope;
import com.joker.module_order.mvp.view.activity.GosDeviceReadyActivity;

@ActivityScope
@Component(modules = GosDeviceReadyModule.class, dependencies = AppComponent.class)
public interface GosDeviceReadyComponent {
    void inject(GosDeviceReadyActivity activity);
}