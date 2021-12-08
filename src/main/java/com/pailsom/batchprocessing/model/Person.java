package com.pailsom.batchprocessing.model;


import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PEOPLE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "firstName", "lastName" })
@XmlRootElement(name = "Contact")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_PERSON")
    @SequenceGenerator(sequenceName = "SEQ_PERSON", allocationSize = 1, name = "SEQ_PERSON")
    @XmlTransient
    private long Id;

    @XmlElement(name = "lastName", required = true)
    @Column(name = "lastname")
    private String lastName;

    @XmlElement(name = "FirstName", required = true)
    @Column(name = "firstname")
    private String firstName;

    @Override
    public String toString() {
        return firstName+","+lastName;
    }
}
