package com.leeiidesu.smsexpress.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Filter(
        @Id var id: Long = 0,
        var alias: String? = null,
        var keywordsRegex: String? = null,
        var codeRegex: String? = null,
        var nominate: String? = null,
        var fixedName: Boolean = false,
        var nameRegex: String? = null,
        var nameStartSplit: Int = 0,
        var nameEndSplit: Int = 0,
        var lastReadTime: Long = 0,
        var startColor: Int = 0,
        var endColor: Int = 0)