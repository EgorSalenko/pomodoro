package io.esalenko.pomadoro


import android.content.Context
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import io.esalenko.pomadoro.domain.LocalRoomDatabase
import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.domain.model.Task
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    lateinit var db: LocalRoomDatabase
    lateinit var taskDao : TaskDao

    @Before
    fun init(){
        val ctx : Context = InstrumentationRegistry.getContext()
        db = Room.inMemoryDatabaseBuilder(ctx.applicationContext, LocalRoomDatabase::class.java).build()
        taskDao = db.taskDao
    }

    @After
    fun after(){
        db.close()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("io.esalenko.pomadoro", appContext.packageName)
    }

    @Test
    fun insertion_into_database() {

        val expected = Task(
            0,
            "Test description",
            TimeUnit.MINUTES.toMillis(1),
            Date().time
        )

        taskDao.insert(expected)

        val actual = taskDao.get(0)

        assertEquals(expected, actual)
    }
}
