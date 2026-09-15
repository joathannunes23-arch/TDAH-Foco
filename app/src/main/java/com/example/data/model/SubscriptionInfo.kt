package com.example.data.model

enum class SubscriptionPlanStatus {
    FREE_TRIAL,
    PRO_ACTIVE,
    EXPIRED,
    FREE_TIER
}

data class SubscriptionInfo(
    val isPro: Boolean = true,
    val status: SubscriptionPlanStatus = SubscriptionPlanStatus.FREE_TRIAL,
    val daysLeft: Int = 7,
    val priceFormatted: String = "R$ 9,90",
    val period: String = "mês",
    val trialDaysTotal: Int = 7
)
