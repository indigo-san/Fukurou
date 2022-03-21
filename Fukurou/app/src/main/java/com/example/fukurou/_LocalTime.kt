package com.example.fukurou

import java.time.LocalTime

inline val LocalTime.totalMinute: Float get(){
    return this.hour * 60f + this.minute
}

inline val LocalTime.totalHour: Float get(){
    return this.hour + (this.minute / 60f)
}