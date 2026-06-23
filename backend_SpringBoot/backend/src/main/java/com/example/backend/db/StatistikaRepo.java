package com.example.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatistikaRepo {


    public Map<String, Integer> getRezervacijePoSportu() {
        Map<String, Integer> rezultat = new LinkedHashMap<>();
        String sql = "SELECT sport, COUNT(*) as broj FROM rezervacije GROUP BY sport ORDER BY broj DESC";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rezultat.put(rs.getString("sport"), rs.getInt("broj"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }


    public Map<String, Integer> getMesecnaAktivnost() {
        Map<String, Integer> rezultat = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(vreme_od, '%Y-%m') as mesec, COUNT(*) as broj FROM rezervacije GROUP BY mesec ORDER BY mesec ASC";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rezultat.put(rs.getString("mesec"), rs.getInt("broj"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }


    public Map<String, Double> getPotrosnjaPoSportisti() {
        Map<String, Double> rezultat = new LinkedHashMap<>();
        String sql = "SELECT korisnicko_ime, SUM(ukupna_cena) as ukupno FROM porudzbine WHERE status != 'OTKAZANO' GROUP BY korisnicko_ime ORDER BY ukupno DESC";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rezultat.put(rs.getString("korisnicko_ime"), rs.getDouble("ukupno"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }
}
