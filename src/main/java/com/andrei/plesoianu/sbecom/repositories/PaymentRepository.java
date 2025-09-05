package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
