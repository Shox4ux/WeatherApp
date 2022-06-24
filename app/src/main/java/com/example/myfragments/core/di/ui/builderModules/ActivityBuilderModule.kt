package com.example.myfragments.core.di.ui.builderModules

import com.example.myfragments.core.di.ui.vm.MainVMModule
import com.example.myfragments.ui.activity.Main2Activity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [MainVMModule::class,FragmentBuilderModule::class])
    fun contributeMainActivity(): Main2Activity
}