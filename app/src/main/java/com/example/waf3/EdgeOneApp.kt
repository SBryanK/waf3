package com.example.waf3

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.waf3.core.db.AppDatabase
import com.example.waf3.core.quota.QuotaManager
import com.example.waf3.core.repo.TemplateRepository
import com.example.waf3.core.repo.HistoryRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class EdgeOneApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "edgeone.db").build()

        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { applicationContext.preferencesDataStoreFile("quota.preferences_pb") }
        )

        val quota = QuotaManager(dataStore)
        val templateRepo = TemplateRepository(this)

        container = AppContainer(
            okHttpClient = okHttp,
            database = db,
            quotaManager = quota,
            templateRepository = templateRepo,
            historyRepository = HistoryRepository(db)
        )
    }
}

data class AppContainer(
    val okHttpClient: OkHttpClient,
    val database: AppDatabase,
    val quotaManager: QuotaManager,
    val templateRepository: TemplateRepository,
    val historyRepository: HistoryRepository
)


