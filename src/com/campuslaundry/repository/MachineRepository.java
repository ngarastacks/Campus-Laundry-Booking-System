package com.campuslaundry.repository;

import com.campuslaundry.model.Machine;
import java.util.List;
import java.util.Optional;

/** Storage contract for machines. */
public interface MachineRepository {
    Optional<Machine> findById(int id);
    List<Machine> findAll();
    Machine save(Machine machine);
}