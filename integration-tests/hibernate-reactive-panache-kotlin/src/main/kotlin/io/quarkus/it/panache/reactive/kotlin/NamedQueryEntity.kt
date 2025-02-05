package io.quarkus.it.panache.reactive.kotlin

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import javax.persistence.Entity
import javax.persistence.NamedQuery

@Entity
@NamedQuery(name = "NamedQueryEntity.getAll", query = "from NamedQueryEntity")
class NamedQueryEntity : NamedQueryMappedSuperClass() {
    lateinit var test: String

    companion object : PanacheCompanion<NamedQueryEntity>
}
