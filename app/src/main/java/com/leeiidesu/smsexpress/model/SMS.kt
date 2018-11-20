package com.leeiidesu.smsexpress.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class SMS(
        @Id var id: Long = 0,
        var address: String? = null,
        var person: String? = null,
        var date: Long = 0,
        var type: Int = 0,
        var body: String? = null,
        var subject: String? = null,
        var code: String? = null,
        var express: String? = null,
        var got: Int = 0)