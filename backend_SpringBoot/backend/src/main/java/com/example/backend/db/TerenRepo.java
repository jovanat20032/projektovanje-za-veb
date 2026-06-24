package com.example.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.models.Teren;

public class TerenRepo implements TerenRepoInterface{
    public List<Teren> getTereniZaObjekat(int objekatId) {
        List<Teren> tereni = new ArrayList<>();
        String sql = "SELECT * FROM tereni WHERE objekat_id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tereni.add(new Teren(
                    rs.getInt("id"), rs.getInt("objekat_id"), rs.getString("naziv"),
                    rs.getString("tip"), rs.getInt("kapacitet"), rs.getString("opis_opreme")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tereni;
    }

    public void dodajTeren(Teren t, int objekatId) {
        String sql = "INSERT INTO tereni (objekat_id, naziv, tip, kapacitet, opis_opreme) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            stmt.setString(2, t.getNaziv());
            stmt.setString(3, t.getTip());
            stmt.setInt(4, t.getKapacitet());
            stmt.setString(5, t.getOpisOpreme());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obrisiTereneZaObjekat(int objekatId) {
        String sql = "DELETE FROM tereni WHERE objekat_id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
