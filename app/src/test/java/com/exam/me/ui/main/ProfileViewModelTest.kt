package com.exam.me.ui.main

import android.app.Application
import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.exam.me.model.PopulatedUser
import com.exam.me.model.UserProfile
import com.exam.me.repository.UserRepository
import com.exam.me.util.ImageUploader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var imageUploader: ImageUploader

    private lateinit var profileViewModel: ProfileViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        profileViewModel = ProfileViewModel(application, imageUploader)
        val repositoryField = profileViewModel::class.java.getDeclaredField("userRepository")
        repositoryField.isAccessible = true
        repositoryField.set(profileViewModel, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile success updates profileState`() = runTest {
        val user = PopulatedUser("test@test.com", "Test User")
        val profile = UserProfile("1", user, "Test User", "12345", emptyList(), null)
        coEvery { userRepository.getMyProfile() } returns profile

        profileViewModel.loadProfile()

        val result = profileViewModel.profileState.value
        assert(result is ProfileState.Success)
        assertEquals(profile, (result as ProfileState.Success).profile)
        assertEquals(profile.nombre, profileViewModel.formState.value.nombre)
        assertEquals(profile.user.email, profileViewModel.formState.value.email)
    }

    @Test
    fun `updateProfile success refreshes profile`() = runTest {
        val user = PopulatedUser("test@test.com", "Test User")
        val initialProfile = UserProfile("1", user, "Test User", "12345", emptyList(), null)
        coEvery { userRepository.getMyProfile() } returns initialProfile

        val updatedName = "Updated Name"
        val updatedProfile = initialProfile.copy(nombre = updatedName)
        coEvery { userRepository.updateMyProfile(any()) } returns updatedProfile

        profileViewModel.onNameChange(updatedName)
        profileViewModel.updateProfile()

        coVerify { userRepository.updateMyProfile(any()) }
        coVerify(exactly = 2) { userRepository.getMyProfile() } // Called on init and after update
    }

    @Test
    fun `uploadProfileImage success refreshes profile`() = runTest {
        val bitmap = mockk<Bitmap>(relaxed = true)
        val imagePart = mockk<MultipartBody.Part>()
        val profile = mockk<UserProfile>(relaxed = true)

        coEvery { imageUploader.bitmapToMultipart(bitmap, "avatar") } returns imagePart
        coEvery { userRepository.uploadProfileAvatar(imagePart) } returns mockk()
        coEvery { userRepository.getMyProfile() } returns profile

        profileViewModel.uploadProfileImage(bitmap)

        coVerify { imageUploader.bitmapToMultipart(bitmap, "avatar") }
        coVerify { userRepository.uploadProfileAvatar(imagePart) }
        coVerify { userRepository.getMyProfile() }
    }
}