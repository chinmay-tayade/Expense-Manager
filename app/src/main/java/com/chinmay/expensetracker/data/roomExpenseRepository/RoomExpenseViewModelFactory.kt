package com.chinmay.expensetracker.data.roomExpenseRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel

class RoomExpenseViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomExpenseViewModel(
                RoomExpenseRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}