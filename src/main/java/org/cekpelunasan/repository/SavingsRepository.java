package org.cekpelunasan.repository;

import org.cekpelunasan.entity.Savings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long>, JpaSpecificationExecutor<Savings> {

	@Modifying
	@Query("DELETE FROM Savings")
	void deleteAllFast();

	Page<Savings> findByNameContainingIgnoreCaseAndBranch(String name, String branch, Pageable pageable);


	Optional<Savings> findByTabId(String tabId);

	@Query("SELECT DISTINCT b.branch FROM Savings b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	List<String> findAllByNameContainingIgnoreCase(@Param("name") String name);


}
