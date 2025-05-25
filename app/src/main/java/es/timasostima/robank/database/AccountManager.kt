package es.timasostima.robank.enterApp

import android.app.Activity
import android.net.Uri
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.timasostima.robank.BuildConfig
import es.timasostima.robank.api.RetrofitClient
import es.timasostima.robank.api.RobankUser
import es.timasostima.robank.database.LogInResult
import es.timasostima.robank.database.SignUpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AccountManager(private val activity: Activity) {
    private val credentialManager = CredentialManager.create(activity)
    private val auth: FirebaseAuth = Firebase.auth
    fun getCurrentUser() = auth.currentUser

    suspend fun signUp(
        username: String,
        password: String,
        name: String
    ): SignUpResult {
        val isBackendAvailable = try {
            val response = RetrofitClient.apiService.pingServer()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AccountManager", "Backend not available", e)
            return SignUpResult.Failure
        }

        if (!isBackendAvailable) {
            return SignUpResult.Failure
        }

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

            // Update Firebase user profile with the name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)?.await()

            auth.currentUser?.sendEmailVerification()?.await()

            try {
                val backendUser = RobankUser(
                    uid = auth.currentUser!!.uid,
                    email = username,
                    name = name
                )
                val response = RetrofitClient.apiService.registerUser(backendUser)
                if (!response.isSuccessful) {
                    Log.e(
                        "AccountManager",
                        "Backend registration failed: ${response.errorBody()?.string()}"
                    )
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

    suspend fun logInCredentialManager(): LogInResult {
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
        } catch (e: Exception) {
            e.printStackTrace()
            LogInResult.Failure
        }
    }

    suspend fun logIn(email: String, password: String): LogInResult {
        val isBackendAvailable = try {
            val response = RetrofitClient.apiService.pingServer()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AccountManager", "Backend not available", e)
            return LogInResult.Failure
        }

        if (!isBackendAvailable) {
            return LogInResult.Failure
        }
        return try {
            var result: LogInResult? = null
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) result = LogInResult.Failure
                }.await()
            if (result != null) return result!! //means it is not successful

            if (!checkEmailVerification()) {
                return LogInResult.EmailNotVerified
            }
            auth.currentUser?.let { LogInResult.Success(it) } ?: LogInResult.Failure
        } catch (e: Exception) {
            e.printStackTrace()
            LogInResult.Failure
        }
    }

//    suspend fun signInGoogle(): LogInResult {
//        // Check if backend is available
//        val isBackendAvailable = try {
//            val response = RetrofitClient.apiService.pingServer()
//            response.isSuccessful
//        } catch (e: Exception) {
//            Log.e("AccountManager", "Backend not available", e)
//            return LogInResult.Failure
//        }
//
//        if (!isBackendAvailable) {
//            return LogInResult.Failure
//        }
//
//        return try {
//            // Generate secure nonce for Google authentication
//            val ranNonce = UUID.randomUUID().toString()
//            val bytes = ranNonce.toByteArray()
//            val md = MessageDigest.getInstance("SHA-256")
//            val digest = md.digest(bytes)
//            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
//
//            // Configure Google Sign-In options
//            val googleIdOption = GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(false)
//                .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
//                .setNonce(hashedNonce)
//                .build()
//
//            val request = GetCredentialRequest.Builder()
//                .addCredentialOption(googleIdOption)
//                .build()
//
//            // Get Google credentials
//            val result = credentialManager.getCredential(activity, request)
//            val credential = result.credential
//
//            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
//
//                // THIS IS THE MISSING STEP - Actually sign in with Firebase using the credential
//                val authResult = auth.signInWithCredential(authCredential).await()
//                val currentUser = authResult.user ?: return LogInResult.Failure
//
//                try {
//                    val response = RetrofitClient.apiService.checkNewUser(currentUser.uid)
//                    if (response.isSuccessful) {
//                        val backendUser = RobankUser(
//                            uid = currentUser.uid,
//                            email = currentUser.email ?: "",
//                            name = currentUser.displayName ?: "User",
//                            pictureUrl = currentUser.photoUrl?.toString()
//                        )
//                        val registerResponse = RetrofitClient.apiService.registerUser(backendUser)
//                        if (!registerResponse.isSuccessful) {
//                            Log.e("AccountManager", "Backend registration failed: ${registerResponse.errorBody()?.string()}")
//                            return LogInResult.Failure
//                        }
//                    }
//                    return LogInResult.Success(currentUser)
//                } catch (e: Exception) {
//                    Log.e("AccountManager", "Error communicating with backend", e)
//                    return LogInResult.Failure
//                }
//            } else {
//                Log.e("AccountManager", "Invalid credential type received")
//                return LogInResult.Failure
//            }
//        } catch (e: Exception) {
//            Log.e("AccountManager", "Google sign-in failed", e)
//            e.printStackTrace()
//            LogInResult.Failure
//        }
//    }

    private suspend fun checkEmailVerification(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            val verified = auth.currentUser?.isEmailVerified == true
            verified
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    private val apiService = RetrofitClient.apiService

    suspend fun getProfileImage(): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserProfileImage()
                val cacheDir = File(activity.cacheDir, "profile_images")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }

                val file = File(cacheDir, "profile_${auth.currentUser?.uid}.jpg")
                file.outputStream().use { fileOutput ->
                    response.byteStream().use { input ->
                        input.copyTo(fileOutput)
                    }
                }

                Uri.fromFile(file)
            } catch (e: Exception) {
                Log.e("AccountManager", "Failed to get profile image", e)
                null
            }
        }
    }

    suspend fun uploadProfileImage(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    activity.contentResolver.openInputStream(uri) ?: return@withContext false
                val byteArray = inputStream.readBytes()
                inputStream.close()

                val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile)

                val response = apiService.uploadProfileImage(body)
                return@withContext response.isSuccessful
            } catch (e: Exception) {
                Log.e("AccountManager", "Failed to upload profile image", e)
                false
            }
        }
    }

    suspend fun uploadProfileImageFromUrl(imageUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = mapOf("imageUrl" to imageUrl)
                val response = apiService.uploadProfileImageFromUrl(requestBody)
                return@withContext response.isSuccessful
            } catch (e: Exception) {
                Log.e("AccountManager", "Failed to upload profile image from URL", e)
                false
            }
        }
    }
}