package com.itau.insurance.infrastructure.persistence.entity

import com.itau.insurance.domain.enums.CategoryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "quotations")
data class QuotationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "policy_id", nullable = true)
    var policyId: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: UUID = UUID.randomUUID(),

    @Column(name = "offer_id", nullable = false)
    val offerId: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: CategoryType = CategoryType.HOME,

    @Column(name = "total_monthly_premium_amount", nullable = false)
    val totalMonthlyPremiumAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_coverage_amount", nullable = false)
    val totalCoverageAmount: BigDecimal = BigDecimal.ZERO,

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "coverage_key")
    @Column(name = "coverage_value")
    @CollectionTable(name = "quotations_coverages", joinColumns = [JoinColumn(name = "quotation_id")])
    val coverages: Map<String, BigDecimal> = emptyMap(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quotations_assistances", joinColumns = [JoinColumn(name = "quotation_id")])
    @Column(name = "assistance")
    val assistances: List<String> = emptyList(),

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: CustomerEntity = CustomerEntity(),

    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime?=LocalDateTime.now(),

    @Column(name = "update_at", nullable = false)
    var updateAt: LocalDateTime?=LocalDateTime.now()
) {
    constructor() : this(
        id = null,
        policyId = null,
        productId = UUID.randomUUID(),
        offerId = UUID.randomUUID(),
        category = CategoryType.HOME,
        totalMonthlyPremiumAmount = BigDecimal.ZERO,
        totalCoverageAmount = BigDecimal.ZERO,
        coverages = emptyMap(),
        assistances = emptyList(),
        customer = CustomerEntity(),
        createAt = LocalDateTime.now(),
        updateAt = LocalDateTime.now()
    )
}
