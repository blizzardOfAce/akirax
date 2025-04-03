package com.example.akirax.di

import com.example.akirax.data.repository.EventRepository
import com.example.akirax.data.repository.FirebaseAuthRepositoryImpl
import com.example.akirax.data.repository.FirebaseTicketsRepository
import com.example.akirax.data.repository.FirestoreRepository
import com.example.akirax.data.repository.MovieRepository
import com.example.akirax.data.repository.PaymentRepository
import com.example.akirax.data.repository.TicketsRepository
import com.example.akirax.data.sources.RetrofitInstance
import com.example.akirax.domain.repository.AuthRepository
import com.example.akirax.domain.usecase.AuthUseCases
import com.example.akirax.domain.usecase.CheckUserLoggedIn
import com.example.akirax.domain.usecase.LoginUser
import com.example.akirax.domain.usecase.Logout
import com.example.akirax.domain.usecase.RegisterUser
import com.example.akirax.presentation.viewmodel.AuthViewModel
import com.example.akirax.presentation.viewmodel.HomeViewModel
import com.example.akirax.presentation.viewmodel.PaymentViewModel
import com.example.akirax.presentation.viewmodel.ProfileViewModel
import com.example.akirax.presentation.viewmodel.SeeAllViewModel
import com.example.akirax.presentation.viewmodel.TicketsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // FirebaseAuth and Firestore Instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Firestore Repository
    single { FirestoreRepository(get()) }

    // AuthRepository Implementation
    single<AuthRepository> { FirebaseAuthRepositoryImpl(get(), get()) }

    // Use Case Dependencies
    single { RegisterUser(get()) }
    single { LoginUser(get()) }
    single { CheckUserLoggedIn(get()) }
    single { Logout(get()) }

    // Aggregate Use Cases into AuthUseCases
    single {
        AuthUseCases(
            registerUser = get(),
            loginUser = get(),
            checkUserLoggedIn = get(),
            logout = get()
        )
    }

    // Retrofit Instances for APIs
    single { RetrofitInstance.tmdbApi }  // TMDb API Service
    single { RetrofitInstance.ticketmasterApi }  // Ticketmaster API Service
    single { RetrofitInstance.cashfreeApiService } //Cashfree API Service

    // Repositories
    single { MovieRepository(get()) }  // TMDb Movie Repository
    single { EventRepository(get()) }  // Ticketmaster Event Repository
    single { PaymentRepository (get(), get(), get())}
    single<TicketsRepository> { FirebaseTicketsRepository() }

    single { PaymentViewModel(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { TicketsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SeeAllViewModel(get(), get()) }

}



