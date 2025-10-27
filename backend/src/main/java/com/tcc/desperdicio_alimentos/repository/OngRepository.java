package com.tcc.desperdicio_alimentos.repository;



import com.tcc.desperdicio_alimentos.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OngRepository extends JpaRepository<Ong, Long> {
}

