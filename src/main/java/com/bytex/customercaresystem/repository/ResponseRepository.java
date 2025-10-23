package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Response;
import com.bytex.customercaresystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}