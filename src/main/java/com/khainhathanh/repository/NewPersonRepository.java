package com.khainhathanh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khainhathanh.entity.PersonEntity;

public interface NewPersonRepository extends JpaRepository<PersonEntity, Long> {
	List<PersonEntity> findByLastName(String name);
}
