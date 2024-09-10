package com.itau.insurance.infrastructure.persistence.repository


import com.itau.insurance.infrastructure.persistence.entity.QuotationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuotationRepository : JpaRepository<QuotationEntity, Long>
