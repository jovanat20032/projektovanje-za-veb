package com.example.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.models.Rezervacija;

public class RezervacijaRepo implements RezervacijaRepoInterface {

    @Override
    public List<Rezervacija> getRezervacijeZaTeren(int terenId) {
        List<Rezervacija> rezervacije = new ArrayList<>();
        String sql = "SELECT * FROM rezervacije WHERE teren_id = ? AND status IN ('AKTIVNA', 'POTVRDJENA')";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, terenId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rezervacija r = new Rezervacija();
                r.setId(rs.getInt("id"));
                r.setTerenId(rs.getInt("teren_id"));
                r.setKorisnickoIme(rs.getString("korisnicko_ime"));
                r.setSport(rs.getString("sport"));
                r.setVremeOd(rs.getTimestamp("vreme_od").toLocalDateTime());
                r.setVremeDo(rs.getTimestamp("vreme_do").toLocalDateTime());
                r.setStatus(rs.getString("status"));
                rezervacije.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezervacije;
    }

    @Override
    public boolean dodajRezervaciju(Rezervacija r) {
        String proveraSql = "SELECT COUNT(*) as cnt FROM rezervacije WHERE teren_id = ? AND status IN ('AKTIVNA', 'POTVRDJENA') AND ((vreme_od < ? AND vreme_do > ?) OR (vreme_od < ? AND vreme_do > ?))";
        String insertSql = "INSERT INTO rezervacije (teren_id, korisnicko_ime, sport, vreme_od, vreme_do, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DB.source().getConnection()) {
            // Provera preklapanja
            try (PreparedStatement stmt = conn.prepareStatement(proveraSql)) {
                stmt.setInt(1, r.getTerenId());
                stmt.setTimestamp(2, Timestamp.valueOf(r.getVremeDo()));
                stmt.setTimestamp(3, Timestamp.valueOf(r.getVremeOd()));
                stmt.setTimestamp(4, Timestamp.valueOf(r.getVremeDo()));
                stmt.setTimestamp(5, Timestamp.valueOf(r.getVremeOd()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt("cnt") > 0) {
                    return false; // Vec postoji rezervacija
                }
            }

            // Insert
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, r.getTerenId());
                stmt.setString(2, r.getKorisnickoIme());
                stmt.setString(3, r.getSport());
                stmt.setTimestamp(4, Timestamp.valueOf(r.getVremeOd()));
                stmt.setTimestamp(5, Timestamp.valueOf(r.getVremeDo()));
                stmt.setString(6, r.getStatus() != null ? r.getStatus() : "AKTIVNA");
                int affected = stmt.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
