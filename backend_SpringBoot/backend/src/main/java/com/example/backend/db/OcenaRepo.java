package com.example.backend.db;

import com.example.backend.models.KomentarOcena;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OcenaRepo {

    public List<KomentarOcena> dohvatiKomentare(int objekatId) {
        List<KomentarOcena> lista = new ArrayList<>();

        String sql = "SELECT * FROM komentari_ocene WHERE objekat_id = ? ORDER BY datum_vreme DESC LIMIT 5";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                KomentarOcena k = new KomentarOcena();
                k.setId(rs.getInt("id"));
                k.setObjekatId(rs.getInt("objekat_id"));
                k.setKorisnickoIme(rs.getString("korisnicko_ime"));
                k.setTekst(rs.getString("tekst"));
                k.setReakcija(rs.getString("reakcija"));
                k.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
                lista.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean dodajKomentarOcenu(KomentarOcena ko) throws Exception {
        Connection conn = null;
        try {
            conn = DB.source().getConnection();
            conn.setAutoCommit(false); // Započinjemo transakciju


            String sqlRez = "SELECT COUNT(*) FROM rezervacije r JOIN tereni t ON r.teren_id = t.id WHERE t.objekat_id = ? AND r.korisnicko_ime = ? AND r.status = 'POTVRDJENA'";
            int brojRezervacija = 0;
            try (PreparedStatement stmtRez = conn.prepareStatement(sqlRez)) {
                stmtRez.setInt(1, ko.getObjekatId());
                stmtRez.setString(2, ko.getKorisnickoIme());
                ResultSet rsRez = stmtRez.executeQuery();
                if (rsRez.next()) {
                    brojRezervacija = rsRez.getInt(1);
                }
            }

            if (brojRezervacija == 0) {
                conn.rollback();
                throw new Exception("Nemate nijednu potvrđenu rezervaciju u ovom objektu, pa ne možete ostaviti ocenu.");
            }


            String sqlKomentari = "SELECT COUNT(*) FROM komentari_ocene WHERE objekat_id = ? AND korisnicko_ime = ?";
            int brojOstavljenih = 0;
            try (PreparedStatement stmtKom = conn.prepareStatement(sqlKomentari)) {
                stmtKom.setInt(1, ko.getObjekatId());
                stmtKom.setString(2, ko.getKorisnickoIme());
                ResultSet rsKom = stmtKom.executeQuery();
                if (rsKom.next()) {
                    brojOstavljenih = rsKom.getInt(1);
                }
            }

            if (brojOstavljenih >= brojRezervacija) {
                conn.rollback();
                throw new Exception("Iskoristili ste maksimalan broj ocena/komentara s obzirom na broj vaših rezervacija.");
            }


            String insertSql = "INSERT INTO komentari_ocene (objekat_id, korisnicko_ime, tekst, reakcija, datum_vreme) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, ko.getObjekatId());
                insertStmt.setString(2, ko.getKorisnickoIme());
                insertStmt.setString(3, ko.getTekst());
                insertStmt.setString(4, ko.getReakcija());
                insertStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                insertStmt.executeUpdate();
            }


            if (ko.getReakcija() != null) {
                String kolona = ko.getReakcija().equals("LAJK") ? "lajkovi" : "dislajkovi";
                String updateObjekatSql = "UPDATE objekti SET " + kolona + " = " + kolona + " + 1 WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateObjekatSql)) {
                    updateStmt.setInt(1, ko.getObjekatId());
                    updateStmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw e; // Prosleđujemo grešku gore ka kontroleru
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {}
            }
        }
    }
}
