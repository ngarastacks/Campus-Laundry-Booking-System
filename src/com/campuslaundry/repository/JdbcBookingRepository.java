package com.campuslaundry.repository;

import com.campuslaundry.model.Booking;
import com.campuslaundry.model.BookingStatus;
import com.campuslaundry.model.Machine;
import com.campuslaundry.model.ServiceType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BookingRepository that stores bookings in the MySQL "bookings" table.
 * Same interface as InMemoryBookingRepository, so BookingService
 * works with either one unchanged.
 */
public class JdbcBookingRepository implements BookingRepository {
    private static final String SELECT =
            "SELECT id, student_id, machine_id, service_type, start_time, end_time, status FROM bookings";

    private final MachineRepository machineRepo;

    public JdbcBookingRepository(MachineRepository machineRepo) {
        this.machineRepo = machineRepo;
    }

    @Override
    public Booking save(Booking b) {
        String sql = "INSERT INTO bookings (id, student_id, machine_id, service_type, start_time, end_time, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE status = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getId());
            ps.setString(2, b.getStudentId());
            ps.setInt(3, b.getMachine().getId());
            ps.setString(4, b.getServiceType().name());
            ps.setTimestamp(5, Timestamp.valueOf(b.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(b.getEndTime()));
            ps.setString(7, b.getStatus().name());
            ps.setString(8, b.getStatus().name());
            ps.executeUpdate();
            return b;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save booking: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Booking> findById(int id) {
        List<Booking> found = query(SELECT + " WHERE id = ?", id);
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    @Override
    public List<Booking> findByStudentId(String studentId) {
        return query(SELECT + " WHERE student_id = ? ORDER BY start_time", studentId);
    }

    @Override
    public List<Booking> findByMachineId(int machineId) {
        return query(SELECT + " WHERE machine_id = ? ORDER BY start_time", machineId);
    }

    @Override
    public List<Booking> findAll() {
        return query(SELECT + " ORDER BY id");
    }

    /** Next free id. BookingService is synchronized, so this is safe for our use. */
    @Override
    public int nextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM bookings";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not get next booking id: " + e.getMessage(), e);
        }
    }

    /** Runs a SELECT with the given parameters and turns each row into a Booking. */
    private List<Booking> query(String sql, Object... params) {
        List<Booking> result = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load bookings: " + e.getMessage(), e);
        }
    }

    private Booking map(ResultSet rs) throws SQLException {
        Machine machine = machineRepo.findById(rs.getInt("machine_id"))
                .orElseThrow(() -> new IllegalStateException("Booking refers to a missing machine."));
        return new Booking(
                rs.getInt("id"),
                rs.getString("student_id"),
                machine,
                ServiceType.valueOf(rs.getString("service_type")),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                BookingStatus.valueOf(rs.getString("status")));
    }
}