package com.nuvio.tv.core.provisioning

import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.AppOnboardingDataStore
import com.nuvio.tv.data.local.ExperienceModeDataStore
import com.nuvio.tv.data.local.LayoutPreferenceDataStore
import com.nuvio.tv.data.local.StreamReadyProvisioningDataStore
import com.nuvio.tv.domain.model.ExperienceMode
import com.nuvio.tv.domain.model.HomeLayout
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamReadyProvisioningService @Inject constructor(
    private val appOnboardingDataStore: AppOnboardingDataStore,
    private val layoutPreferenceDataStore: LayoutPreferenceDataStore,
    private val experienceModeDataStore: ExperienceModeDataStore,
    private val profileManager: ProfileManager,
    private val provisioningDataStore: StreamReadyProvisioningDataStore
) {
    /**
     * Makes the primary local profile immediately usable without requiring a
     * Nuvio account. Existing user choices are preserved; only missing
     * first-run values are filled in.
     */
    suspend fun ensureProvisioned() {
        profileManager.setActiveProfile(PRIMARY_PROFILE_ID)

        // StreamReady uses the local primary profile as the normal startup path.
        appOnboardingDataStore.setHasSeenAuthQrOnFirstLaunch(true)

        if (!layoutPreferenceDataStore.hasChosenLayout.first()) {
            layoutPreferenceDataStore.setLayout(HomeLayout.MODERN)
        }

        if (experienceModeDataStore.mode.first() == null) {
            experienceModeDataStore.setMode(ExperienceMode.ADVANCED)
        }

        if (!provisioningDataStore.initialized.first()) {
            provisioningDataStore.markInitialized()
        }
    }

    companion object {
        const val PRIMARY_PROFILE_ID = 1
    }
}
