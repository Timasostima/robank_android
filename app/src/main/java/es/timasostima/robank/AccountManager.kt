package es.timasostima.robank

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

sealed interface SignUpResult {
    data class Success(val username: String) : SignUpResult
    data object Cancelled : SignUpResult
    data object Failure : SignUpResult
}

sealed interface SignInResult {
    data object Success : SignInResult
    data object Cancelled : SignInResult
    data object Failure : SignInResult
    data object DoesNotExist : SignInResult
    data object EmailNotVerified : SignInResult
}

class AccountManager (
    private val activity: Activity
)
{
    private val credentialManager = CredentialManager.create(activity)
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun signUp(
        username: String,
        password: String
    ): SignUpResult
    {
        return try {
            credentialManager.createCredential(
                context = activity,
                request = CreatePasswordRequest(
                    id = username,
                    password = password
                )
            )

            auth.createUserWithEmailAndPassword(username, password).await()
            auth.currentUser?.sendEmailVerification()?.await()

            return SignUpResult.Success(username)

        } catch (e: CreateCredentialCancellationException){
            e.printStackTrace()
            SignUpResult.Cancelled
        } catch (e: CreateCredentialException){
            e.printStackTrace()
            SignUpResult.Failure
        }
    }

    suspend fun signInCredentialManager(): SignInResult
    {
        return try {
            val credentialResponse = credentialManager.getCredential(
                context = activity,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )

            val credential = credentialResponse.credential as? PasswordCredential
                ?: return SignInResult.DoesNotExist

            signIn(credential.id, credential.password)
        } catch (e: Exception){
            e.printStackTrace()
            SignInResult.Failure
        }
    }

    suspend fun signIn(email: String, password: String): SignInResult
    {
        return try {
            var result: SignInResult? = null
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) result = SignInResult.Failure
            }.await()
            if (result != null) return result!!

            if (!checkEmailVerification()){
                return SignInResult.EmailNotVerified
            }
            SignInResult.Success
        } catch (e: Exception){
            e.printStackTrace()
            SignInResult.Failure
        }
    }

    suspend fun signInGoogle(): SignInResult
    {
        return try {
            val ranNonce: String = UUID.randomUUID().toString()
            val bytes: ByteArray = ranNonce.toByteArray()
            val md: MessageDigest = MessageDigest.getInstance("SHA-256")
            val digest: ByteArray = md.digest(bytes)
            val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

            // Set up Google ID option
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("1055631948325-q2kq5l4u1primbrhbegva97gs3h1ekau.apps.googleusercontent.com")
                .setNonce(hashedNonce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                if (auth.currentUser?.isEmailVerified == false){
                    throw Exception("Email not verified")
                }
                val a = auth.signInWithCredential(authCredential).await()

            }
            else{
                println("Error")
            }

            SignInResult.Success
        } catch (e: Exception){
            e.printStackTrace()
            SignInResult.Failure
        }
    }

    suspend fun checkEmailVerification(): Boolean
    {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser?.isEmailVerified == true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}