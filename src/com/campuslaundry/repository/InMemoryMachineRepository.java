package com.campuslaundry.repository;

import com.campuslaundry.model.Machine;
import com.campuslaundry.model.MachineType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Keeps machines in memory, pre-loaded with a few sample machines. */
public class InMemoryMachineRepository implements MachineRepository {
    private final Map<Integer, Machine> store = new LinkedHashMap<>();

    public InMemoryMachineRepository() {
        save(new Machine(1, "Washer 1", MachineType.WASHER));
        save(new Machine(2, "Washer 2", MachineType.WASHER));
        save(new Machine(3, "Dryer 1", MachineType.DRYER));
        save(new Machine(4, "Dryer 2", MachineType.DRYER));
    }

    @Override
    public Optional<Machine> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Machine> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Machine save(Machine machine) {
        store.put(machine.getId(), machine);
        return machine;
    }
}