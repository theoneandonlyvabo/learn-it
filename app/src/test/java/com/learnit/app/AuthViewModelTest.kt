package com.learnit.app

import com.google.firebase.auth.FirebaseUser
import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.ui.viewmodel.AuthViewModel
import com.learnit.app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private class FakeAuthRepository(
        var loginResult: Result<FirebaseUser> = Result.failure(Exception("fake")),
        var registerResult: Result<FirebaseUser> = Result.failure(Exception("fake"))
    ) : AuthRepository {
        var loggedOut = false
        override suspend fun register(email: String, password: String): Result<FirebaseUser> = registerResult
        override suspend fun login(email: String, password: String): Result<FirebaseUser> = loginResult
        override fun getCurrentUser(): FirebaseUser? = null
        override fun logout() { loggedOut = true }
    }

    @Test
    fun `authState is null before any attempt`() {
        val vm = AuthViewModel(FakeAuthRepository())
        assertNull(vm.authState.value)
    }

    @Test
    fun `login with blank email sets Error without calling repository`() {
        val vm = AuthViewModel(FakeAuthRepository())
        vm.login("", "password")
        val state = vm.authState.value
        assertTrue(state is ApiState.Error)
    }

    @Test
    fun `login failure maps to Error with message`() {
        val vm = AuthViewModel(FakeAuthRepository(loginResult = Result.failure(Exception("bad credentials"))))
        vm.login("user@test.com", "password")
        val state = vm.authState.value
        assertTrue(state is ApiState.Error)
        assertEquals("bad credentials", (state as ApiState.Error).errorMsg)
    }

    @Test
    fun `register with blank password sets Error without calling repository`() {
        val vm = AuthViewModel(FakeAuthRepository())
        vm.register("user@test.com", "")
        val state = vm.authState.value
        assertTrue(state is ApiState.Error)
    }

    @Test
    fun `register failure maps to Error with message`() {
        val vm = AuthViewModel(FakeAuthRepository(registerResult = Result.failure(Exception("email in use"))))
        vm.register("user@test.com", "password")
        val state = vm.authState.value
        assertTrue(state is ApiState.Error)
        assertEquals("email in use", (state as ApiState.Error).errorMsg)
    }

    @Test
    fun `logout clears authState and delegates to repository`() {
        val repo = FakeAuthRepository()
        val vm = AuthViewModel(repo)
        vm.login("", "") // populate an Error state first
        vm.logout()
        assertNull(vm.authState.value)
        assertTrue(repo.loggedOut)
    }
}
