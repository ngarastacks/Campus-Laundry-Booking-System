package com.campuslaundry.repository;

import com.campuslaundry.model.Machine;
import com.campuslaundry.model.MachineType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** MachineRepository that stores machines in the MySQL "machines" table. */
public class JdbcMachineRepository implements MachineRepository {

    @Override
    public Optional<Machine> findById(int id) {
        String sql = "SELECT id, name, type, in_service FROM machines WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load machine " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Machine> findAll() {
        String sql = "SELECT id, name, type, in_service FROM machines ORDER BY id";
        List<Machine> result = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load machines: " + e.getMessage(), e);
        }
    }

    @Override
    public Machine save(Machine machine) {
        String sql = "INSERT INTO machines (id, name, type, in_service) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = ?, type = ?, in_service = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, machine.getId());
            ps.setString(2, machine.getName());
            ps.setString(3, machine.getType().name());
            ps.setBoolean(4, machine.isInService());
            ps.setString(5, machine.getName());
            ps.setString(6, machine.getType().name());
            ps.setBoolean(7, machine.isInService());
            ps.executeUpdate();
            return machine;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save machine: " + e.getMessage(), e);
        }
    }

    private Machine map(ResultSet rs) throws SQLException {
        Machine m = new Machine(rs.getInt("id"), rs.getString("name"),
                MachineType.valueOf(rs.getString("type")));
        m.setInService(rs.getBoolean("in_service"));
        return m;
    }
}