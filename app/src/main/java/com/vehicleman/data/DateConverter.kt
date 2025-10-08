// ΥΠΟΘΕΤΙΚΟ DateConverter
// 📍 Path: app/src/main/java/com/vehicleman/data/DateConverter.kt
import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}