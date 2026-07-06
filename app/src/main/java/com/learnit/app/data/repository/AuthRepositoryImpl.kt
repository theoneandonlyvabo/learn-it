package com.learnit.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): Result<FirebaseUser> =
        runCatching {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
                ?: error("Registration succeeded but user is null")
            user.updateProfile(userProfileChangeRequest { this.displayName = displayName }).await()
            user
        }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await().user
                ?: error("Login succeeded but user is null")
        }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override fun logout() = auth.signOut()
}
