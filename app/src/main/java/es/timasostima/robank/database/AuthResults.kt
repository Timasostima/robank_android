package es.timasostima.robank.database

import com.google.firebase.auth.FirebaseUser

sealed interface SignUpResult {
    data object Success: SignUpResult
    data object Cancelled : SignUpResult
    data object AlreadyRegistered: SignUpResult
    data object Failure : SignUpResult
}

sealed interface LogInResult {
    data class Success(val user: FirebaseUser) : LogInResult
    data object Cancelled : LogInResult
    data object Failure : LogInResult
    data object DoesNotExist : LogInResult
    data object EmailNotVerified : LogInResult
}