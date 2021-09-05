package io.pulse.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "cartes")
@Entity
@Table(name = "SOURCE_CUSTOMER")
public class SourceCustomer {

    @Id
    @Column(name = "CUST_NUM")
    private long id;

    @Column(name = "CUST_FIRSTNAME")
    private String firstname;

    @Column(name = "CUST_LASTNAME")
    private String lastname;
}
