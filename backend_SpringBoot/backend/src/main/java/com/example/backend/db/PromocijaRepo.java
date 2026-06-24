package com.example.backend.db;

import com.example.backend.models.Promocija;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromocijaRepo {

    public List<Promocija> getPromocijeZaZaposlenog(String zaposleniKorisnickoIme) {
        List<Promocija> lista = new ArrayList<>();
        String sql = "SELECT p.*, o.naziv as naziv_objekta FROM promocije p " +
                     "JOIN objekti o ON p.objekat_id = o.id " +
                     "JOIN zaposleni_objekat zo ON o.id = zo.objekat_id " +
                     "WHERE zo.korisnicko_ime = ? " +
                     "ORDER BY p.vazi_do DESC";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zaposleniKorisnickoIme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Promocija p = new Promocija();
                p.setId(rs.getInt("id"));
                p.setNaziv(rs.getString("naziv"));
                p.setObjekatId(rs.getInt("objekat_id"));
                p.setVaziOd(rs.getDate("vazi_od").toLocalDate());
                p.setVaziDo(rs.getDate("vazi_do").toLocalDate());
                p.setPopust(rs.getString("popust"));
                p.setSport(rs.getString("sport"));
                p.setNazivObjekta(rs.getString("naziv_objekta"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean dodajPromociju(Promocija p) {
        String sql = "INSERT INTO promocije (naziv, objekat_id, vazi_od, vazi_do, popust, sport) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNaziv());
            stmt.setInt(2, p.getObjekatId());
            stmt.setDate(3, Date.valueOf(p.getVaziOd()));
            stmt.setDate(4, Date.valueOf(p.getVaziDo()));
            stmt.setString(5, p.getPopust());
            stmt.setString(6, p.getSport());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean azurirajPromociju(Promocija p) {
        String sql = "UPDATE promocije SET naziv=?, vazi_od=?, vazi_do=?, popust=?, sport=? WHERE id=?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNaziv());
            stmt.setDate(2, Date.valueOf(p.getVaziOd()));
            stmt.setDate(3, Date.valueOf(p.getVaziDo()));
            stmt.setString(4, p.getPopust());
            stmt.setString(5, p.getSport());
            stmt.setInt(6, p.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
