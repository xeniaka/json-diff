package com.ainexka.jsondiff.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "json_data")
@Getter
@Setter
public class JsonData implements Serializable {
    private static final long serialVersionUID = 5265664959764269082L;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "object_identifier", nullable = false)
    private String identifier;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataPosition position;

    @Column(nullable = false)
    private String value;
}