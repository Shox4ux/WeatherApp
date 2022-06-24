package com.example.myfragments.core.di.ui.builderModules

import com.example.myfragments.ui.fragments.EndFragment
import com.example.myfragments.ui.fragments.StartFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBuilderModule {

    @ContributesAndroidInjector
    fun contributeStartFragment():StartFragment

    @ContributesAndroidInjector
    fun contributeEndFragment():EndFragment


}