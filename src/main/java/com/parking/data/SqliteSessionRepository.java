
package com.parking.data;

import com.parking.model.ParkingSession;
import com.parking.model.ParkingSpot;
import com.parking.model.SpotSize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSessionRepository implements SessionRepository {

    // Initialize database and create tables
    @Override
    public void initDatabase() {
        try {
            DatabaseManager.enableWAL();

            try (Connection connection = DatabaseManager.getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute("""
                    CREATE TABLE IF NOT EXISTS spots (
                        spot_id TEXT PRIMARY KEY,
                        floor INTEGER NOT NULL,
                        size TEXT NOT NULL
                    )
                """);

                statement.execute("""
                    CREATE TABLE IF NOT EXISTS sessions (
                        session_id INTEGER PRIMARY KEY,
                        license_plate TEXT NOT NULL,
                        vehicle_type TEXT NOT NULL,
                        spot_id TEXT NOT NULL,
                        entry_time TEXT NOT NULL,
                        exit_time TEXT,
                        fee INTEGER,
                        FOREIGN KEY (spot_id) REFERENCES spots(spot_id)
                    )
                """);

                statement.execute("""
                    CREATE UNIQUE INDEX IF NOT EXISTS idx_active_spot
                    ON sessions(spot_id)
                    WHERE exit_time IS NULL
                """);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot initialize database", e);
        }
    }

    // Insert 60 default parking spots if the table is empty
    @Override
    public void seedDefaultSpotsIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM spots";

        String insertSql = """
            INSERT INTO spots (spot_id, floor, size)
            VALUES (?, ?, ?)
        """;

        try (Connection connection = DatabaseManager.getConnection();
             Statement countStatement = connection.createStatement();
             ResultSet rs = countStatement.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

            try (PreparedStatement ps =
                         connection.prepareStatement(insertSql)) {

                // Floor 1: 20 SMALL spots
                for (int i = 1; i <= 20; i++) {
                    ps.setString(1, String.format("F1-%02d", i));
                    ps.setInt(2, 1);
                    ps.setString(3, SpotSize.SMALL.name());
                    ps.addBatch();
                }

                // Floor 2: 25 MEDIUM spots
                for (int i = 1; i <= 25; i++) {
                    ps.setString(1, String.format("F2-%02d", i));
                    ps.setInt(2, 2);
                    ps.setString(3, SpotSize.MEDIUM.name());
                    ps.addBatch();
                }

                // Floor 3: 15 LARGE spots
                for (int i = 1; i <= 15; i++) {
                    ps.setString(1, String.format("F3-%02d", i));
                    ps.setInt(2, 3);
                    ps.setString(3, SpotSize.LARGE.name());
                    ps.addBatch();
                }

                ps.executeBatch();
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot seed default parking spots", e);
        }
    }

    // Load all parking spots from database
    @Override
    public List<ParkingSpot> loadAllSpots() {
        List<ParkingSpot> spots = new ArrayList<>();

        String sql = """
            SELECT spot_id, floor, size
            FROM spots
            ORDER BY floor, spot_id
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ParkingSpot spot = new ParkingSpot(
                        rs.getString("spot_id"),
                        rs.getInt("floor"),
                        SpotSize.valueOf(rs.getString("size"))
                );

                spots.add(spot);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot load parking spots", e);
        }

        return spots;
    }

    // Save a new parking session
    @Override
    public void save(ParkingSession session) {
        String sql = """
            INSERT INTO sessions (
                session_id,
                license_plate,
                vehicle_type,
                spot_id,
                entry_time,
                exit_time,
                fee
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, session.getSessionId());
            ps.setString(2, session.getLicensePlate());
            ps.setString(3, session.getVehicleType());
            ps.setString(4, session.getSpotId());
            ps.setString(5, session.getEntryTime().toString());
            ps.setString(6, null);
            ps.setObject(7, null);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot save parking session", e);
        }
    }

    // Update a session when the vehicle leaves
    @Override
    public void update(ParkingSession session) {
        if (session.isActive()
                || session.getExitTime() == null
                || session.getFee() == null) {
            throw new IllegalArgumentException(
                    "Session must be completed before updating");
        }

        String sql = """
            UPDATE sessions
            SET exit_time = ?, fee = ?
            WHERE session_id = ?
              AND exit_time IS NULL
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, session.getExitTime().toString());
            ps.setLong(2, session.getFee());
            ps.setLong(3, session.getSessionId());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new IllegalStateException(
                        "No active session found with ID "
                                + session.getSessionId());
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot update parking session", e);
        }
    }

    // Find active session by parking spot ID
    @Override
    public Optional<ParkingSession> findActiveSessionBySpotId(
            String spotId) {

        String sql = """
            SELECT *
            FROM sessions
            WHERE spot_id = ?
              AND exit_time IS NULL
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, spotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSession(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot find active session by spot ID", e);
        }

        return Optional.empty();
    }

    // Find active session by license plate
    @Override
    public Optional<ParkingSession> findActiveSessionByLicensePlate(
            String licensePlate) {

        String sql = """
            SELECT *
            FROM sessions
            WHERE license_plate = ?
              AND exit_time IS NULL
        """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, licensePlate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSession(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot find active session by license plate", e);
        }

        return Optional.empty();
    }

    // Load all active sessions
    @Override
    public List<ParkingSession> findAllActiveSessions() {
        String sql = """
            SELECT *
            FROM sessions
            WHERE exit_time IS NULL
            ORDER BY entry_time
        """;

        return executeSessionQuery(sql);
    }

    // Load parking history with a maximum number of records
    @Override
    public List<ParkingSession> findHistorySessions(int limit) {
        if (limit <= 0) {
            return new ArrayList<>();
        }

        String sql = """
            SELECT *
            FROM sessions
            WHERE exit_time IS NOT NULL
            ORDER BY exit_time DESC
            LIMIT ?
        """;

        List<ParkingSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapSession(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot load parking history", e);
        }

        return sessions;
    }

    // Execute a query that returns a list of sessions
    private List<ParkingSession> executeSessionQuery(String sql) {
        List<ParkingSession> sessions = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapSession(rs));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Cannot execute session query", e);
        }

        return sessions;
    }

    // Convert a database row into a ParkingSession object
    private ParkingSession mapSession(ResultSet rs)
            throws SQLException {

        String exitTimeValue = rs.getString("exit_time");

        LocalDateTime exitTime = exitTimeValue == null
                ? null
                : LocalDateTime.parse(exitTimeValue);

        long feeValue = rs.getLong("fee");
        Long fee = rs.wasNull() ? null : feeValue;

        return new ParkingSession(
                rs.getLong("session_id"),
                rs.getString("license_plate"),
                rs.getString("vehicle_type"),
                rs.getString("spot_id"),
                LocalDateTime.parse(rs.getString("entry_time")),
                exitTime,
                fee
        );
    }
}
