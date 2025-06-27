package com.codeloop.storeviewapp.features.utils.zone

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ZoneTimer {

    fun timeStampToMs(time: String?) : Long {
        return try {
            Instant.parse(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        catch (_: Exception){
            0
        }
    }

    fun formatByYearTimePattern(millis: Long,pattern: String="yyyy-MM-dd"):String {
        val format = formatZoneDateTime(millis)
        return DateTimeFormatter.ofPattern(pattern).format(format)
    }

    private fun formatUtcToPattern(timeStamp: String,pattern: String = "yyyy-MM-dd"): String {
        val utc = Instant.parse(timeStamp).atZone(ZoneId.systemDefault()).toInstant()
        val format = formatZoneDateTime(utc.toEpochMilli())
        return DateTimeFormatter.ofPattern(pattern).format(format)
    }

    private fun formatZoneDateTime(millis:Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.systemDefault()
        )
    }

    fun formatAfterTimeAdded(hour:Long,minutes:Long): String {
        val currentInstant = Instant.now()
        var currentDateTime = ZonedDateTime.ofInstant(currentInstant, ZoneOffset.UTC)
        currentDateTime = currentDateTime.plusHours(hour).plusMinutes(minutes)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    fun convertCurrentZoneToUtc(time:String, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val localDateTime = LocalDateTime.parse(time, formatter)
        val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
        val utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
        return utcDateTime.format(formatter)
    }

    fun LocalDate.endOfDay(zone: ZoneId = ZoneId.systemDefault()): Instant {
        return this.atTime(LocalTime.of(23,59,59))
            .atZone(zone).toInstant()
    }

    fun LocalDate.startOfDay(zone: ZoneId = ZoneId.systemDefault()): Instant {
        return this.atTime(LocalTime.MIDNIGHT).atZone(zone)
            .toInstant()
    }

    fun LocalDate.midOfDay(zone: ZoneId = ZoneId.systemDefault()): Instant {
        return this.atTime(LocalTime.NOON).atZone(zone)
            .toInstant()
    }
}