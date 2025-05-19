package es.timasostima.robank.enterApp

import android.app.Activity
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.timasostima.robank.BuildConfig
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.api.RobankUser
import es.timasostima.robank.database.LogInResult
import es.timasostima.robank.database.SignUpResult
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AccountManager (private val activity: Activity)
{
    private val credentialManager = CredentialManager.create(activity)
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun signUp(
        username: String,
        password: String
    ): SignUpResult
    {
        return try {
            try {
                credentialManager.createCredential(
                    context = activity,
                    request = CreatePasswordRequest(
                        id = username,
                        password = password
                    )
                )
            } catch (e: CreateCredentialCancellationException) {
                return SignUpResult.Cancelled
            } catch (e: CreateCredentialException) {
                Log.w("AccountManager", "Credential Manager error: ${e.message}")
            }

            auth.createUserWithEmailAndPassword(username, password).await()
            auth.currentUser?.sendEmailVerification()?.await()

            try {
                val backendUser = RobankUser(
                    uid = auth.currentUser!!.uid,
                    email = username,
                    name = username.substringBefore('@')
                )
                val response = RetrofitClient.apiService.registerUser(backendUser)
                if (!response.isSuccessful) {
                    Log.e("AccountManager", "Backend registration failed: ${response.errorBody()?.string()}")
                    return SignUpResult.Failure
                }
            } catch (e: Exception) {
                Log.e("AccountManager", "Error with backend registration", e)
                return SignUpResult.Failure
            }
            SignUpResult.Success
        } catch (e: FirebaseAuthUserCollisionException) {
            e.printStackTrace()
            SignUpResult.AlreadyRegistered
        } catch (e: Exception) {
            e.printStackTrace()
            SignUpResult.Failure
        }
    }

    suspend fun logInCredentialManager(): LogInResult
    {
        return try {
            val credentialResponse = credentialManager.getCredential(
                context = activity,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )

            val credential = credentialResponse.credential as? PasswordCredential
                ?: return LogInResult.DoesNotExist
            logIn(credential.id, credential.password)
        } catch (e: Exception){
            e.printStackTrace()
            LogInResult.Failure
        }
    }

    suspend fun logIn(email: String, password: String): LogInResult
    {
        return try {
            var result: LogInResult? = null
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) result = LogInResult.Failure
            }.await()
            if (result != null) return result!! //means it is not successful

            if (!checkEmailVerification()){
                return LogInResult.EmailNotVerified
            }
            auth.currentUser?.let { LogInResult.Success(it) }  ?: LogInResult.Failure
        } catch (e: Exception){
            e.printStackTrace()
            LogInResult.Failure
        }
    }

    suspend fun signInGoogle(): LogInResult
    {
        return try {
            val ranNonce: String = UUID.randomUUID().toString()
            val bytes: ByteArray = ranNonce.toByteArray()
            val md: MessageDigest = MessageDigest.getInstance("SHA-256")
            val digest: ByteArray = md.digest(bytes)
            val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
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

                val a = auth.signInWithCredential(authCredential).await()
            }
            else{
                println("Error")
            }

            auth.currentUser?.let { LogInResult.Success(it) }  ?: LogInResult.Failure
        } catch (e: Exception){
            e.printStackTrace()
            LogInResult.Failure
        }
    }

    private suspend fun checkEmailVerification(): Boolean
    {
        return try {
            auth.currentUser?.reload()?.await()
            val verified = auth.currentUser?.isEmailVerified == true
            verified
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun resetPassword(email: String){
        auth.sendPasswordResetEmail(email).await()
    }
}