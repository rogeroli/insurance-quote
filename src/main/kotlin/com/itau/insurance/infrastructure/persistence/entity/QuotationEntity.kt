package com.itau.insurance.infrastructure.persistence.entity

import com.itau.insurance.domain.enums.CategoryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "quotations")
data class QuotationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(name = "offer_id", nullable = false)
    val offerId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: CategoryType,

    @Column(name = "total_monthly_premium_amount", nullable = false)
    val totalMonthlyPremiumAmount: BigDecimal,

    @Column(name = "total_coverage_amount", nullable = false)
    val totalCoverageAmount: BigDecimal,

    @ElementCollection
    @MapKeyColumn(name = "coverage_key")
    @Column(name = "coverage_value")
    @CollectionTable(name = "quotations_coverages", joinColumns = [JoinColumn(name = "quotation_id")])
    val coverages: Map<String, BigDecimal>,

    @ElementCollection
    @CollectionTable(name = "quotations_assistances", joinColumns = [JoinColumn(name = "quotation_id")])
    @Column(name = "assistance")
    val assistances: List<String>,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: CustomerEntity
)
