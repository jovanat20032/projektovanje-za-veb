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
}
