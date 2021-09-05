package io.pulse.domain.repositoty;

import io.pulse.domain.entity.SourceCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceustomerRepository extends JpaRepository<SourceCustomer, Long> {

    /**
     * Finds all customers Ids (cheap mode).
     *
     * @return a {@link List} of ids.
     */
    @Query(nativeQuery = true, value = "SELECT cust.id FROM SOURCE_CUSTOMER cust")
    List<Long> findAllIds();

    @Query("SELECT DISTINCT cust FROM SourceCustomer cust WHERE cust.id IN (:ids)")
    List<SourceCustomer> findAllByIdFetch(Iterable<Long> ids);
}
