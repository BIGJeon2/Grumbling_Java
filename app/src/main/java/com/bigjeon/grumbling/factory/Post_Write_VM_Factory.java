package com.bigjeon.grumbling.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

public class Post_Write_VM_Factory implements ViewModelProvider.Factory {
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        try{
            return modelClass.newInstance();
        }catch (IllegalAccessException e){
            e.printStackTrace();
            throw new RuntimeException("Factory_RunTime_ERROR_CLASS");
        }catch (InstantiationException e){
            e.printStackTrace();
            throw new RuntimeException("Factory_RunTime_ERROR_INSTANCE");
        }
    }
}
