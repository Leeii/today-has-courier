package com.leeiidesu.smsexpress.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Filter(
        @Id var id: Long = 0,
        var keywordsRegex: String? = null,
        var codeRegex: String? = null,
        var startColor: Int = 0,
        var endColor: Int = 0)