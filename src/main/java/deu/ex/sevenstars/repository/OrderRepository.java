package deu.ex.sevenstars.repository;

import deu.ex.sevenstars.entity.Orders;
import deu.ex.sevenstars.repository.search.OrderSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders,Long>, OrderSearch {

    Optional<Orders> findByEmail(String email);
}
