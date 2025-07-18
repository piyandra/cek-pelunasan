package org.cekpelunasan.repository;

import org.cekpelunasan.entity.Repayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

	@Modifying
	@Query("delete FROM repayment ")
	void deleteAllFast();

	Page<Repayment> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Boolean existsByNameIsLikeIgnoreCase(String name);
}
