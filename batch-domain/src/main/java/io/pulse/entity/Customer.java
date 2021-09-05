package io.pulse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

/**
 * Class for persisting and reloading customers into/from a database
 *
 * @author yellowstrawhatter
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "seqCustomer", sequenceName = "customer_id_seq")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqCustomer")
    private String subscriptionId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime refreshDate = now();

    private String firstname;

    private String lastname;

    private String fullname;

}
