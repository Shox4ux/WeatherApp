package com.example.myfragments.core.di.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfragments.core.di.annotation.ViewModelKey
import com.example.myfragments.ui.activity.MainVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.example.myfragments.core.util.ViewModelProviderFactory
@Module
interface MainVMModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainVM::class)
    fun provideLoginVM(mainVM: MainVM): ViewModel
}