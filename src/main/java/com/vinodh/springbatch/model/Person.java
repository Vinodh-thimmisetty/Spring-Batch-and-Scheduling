package com.vinodh.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    private String lastName;
    private String firstName;
    
/*    DROP TABLE people IF EXISTS;

    CREATE TABLE people  (
        person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
        first_name VARCHAR(20),
        last_name VARCHAR(20)
    );
*/ 
}