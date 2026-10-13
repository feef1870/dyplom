package org.zlenko.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zlenko.backend.entities.PriceTag;

public interface PriceTagRepository extends JpaRepository<PriceTag, Long> {

}
