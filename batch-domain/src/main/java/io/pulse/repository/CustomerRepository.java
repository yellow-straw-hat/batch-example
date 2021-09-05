package io.pulse.repository;

import io.pulse.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

/**
 * Repository used for CRUD operations
 *
 * @author yellowstrawhatter
 */
public interface CustomerRepository extends CrudRepository<Customer, String> {

    /**
     * Prepare a {@link String} value to be given as parameter in a SQL {@literal LIKE} expression.
     * <ul>
     * <li>To avoid issues with starting or trailing blanks: {@link String#trim()};</li>
     * <li>To be lenient with separators: any non-letter between words is replaced by an SQL wildcard.</li>
     * </ul>
     * Case is handled by the SQL query.
     *
     * @param field String value.
     * @return A transformed String.
     */
    static String fuzzy(String field) {
        return field.trim().replaceAll("\\b[^\\w]\\b", "_");
    }

    /**
     * Selects the minimum refresh date from Customer database table.
     *
     * @return Oldest refresh date.
     */
    @Query("SELECT min(refreshDate) FROM Customer")
    LocalDateTime findOldestRefreshDate();

    /**
     * Selects the maximum refresh date from Customer database table.
     *
     * @return Latest refresh date.
     */
    @Query("SELECT max(refreshDate) FROM Customer")
    LocalDateTime findLatestRefreshDate();

    /**
     * Finds a Customer with given criteria.
     *
     * @param firstname Customer firstname.
     * @param lastname  Customer lastname.
     * @param pageable  Page size and sort option for queries.
     * @return A sublist of {@link Customer}s.
     */
    @Query("SELECT c FROM Customer c WHERE UPPER(c.id.firstname) LIKE UPPER(:firstname) AND UPPER(c.id.lastname) LIKE UPPER(:lastname)")
    Page<Customer> findByIdLike(String firstname, String lastname, Pageable pageable);

    /**
     * A {@link #fuzzy(String)} way to {@link #findByIdLike(String, String, Pageable)}.
     *
     * @param firstname Customer firstname.
     * @param lastname  Customer lastname.
     * @param pageable  Page size and sort option for queries.
     * @return A sublist of {@link Customer}s.
     */
    default Page<Customer> findByIdLikeFuzzy(String firstname, String lastname, Pageable pageable) {
        return findByIdLike(
                fuzzy(firstname),
                fuzzy(lastname),
                pageable);
    }
}
