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
            // Provera забране
            int objekatId = -1;
            try (PreparedStatement stmtObjekat = conn.prepareStatement("SELECT objekat_id FROM tereni WHERE id = ?")) {
                stmtObjekat.setInt(1, r.getTerenId());
                ResultSet rsObj = stmtObjekat.executeQuery();
                if (rsObj.next()) {
                    objekatId = rsObj.getInt("objekat_id");
                }
            }

            if (objekatId != -1 && !proveriZabranu(r.getKorisnickoIme(), objekatId, conn)) {
                return false; // Zabranjeno zbog previše nedolazaka
            }

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

    public List<Rezervacija> getRezervacijeZaZaposlenog(String zaposleniKorisnickoIme) {
        List<Rezervacija> rezervacije = new ArrayList<>();
        String sql = "SELECT r.*, k.ime, k.prezime, t.naziv as naziv_terena, o.naziv as naziv_objekta " +
                     "FROM rezervacije r " +
                     "JOIN korisnici k ON r.korisnicko_ime = k.korisnicko_ime " +
                     "JOIN tereni t ON r.teren_id = t.id " +
                     "JOIN objekti o ON t.objekat_id = o.id " +
                     "JOIN zaposleni_objekat zo ON o.id = zo.objekat_id " +
                     "WHERE zo.korisnicko_ime = ? " +
                     "ORDER BY r.vreme_od DESC";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zaposleniKorisnickoIme);
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
                r.setKorisnikIme(rs.getString("ime") + " " + rs.getString("prezime"));
                r.setNazivTerena(rs.getString("naziv_terena"));
                r.setNazivObjekta(rs.getString("naziv_objekta"));
                rezervacije.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezervacije;
    }

    public boolean azurirajStatus(int id, String status) {
        String sql = "UPDATE rezervacije SET status = ? WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean pomeriRezervaciju(int id, java.time.LocalDateTime novoVremeOd, java.time.LocalDateTime novoVremeDo) {
        String proveraSql = "SELECT COUNT(*) as cnt FROM rezervacije r1 " +
                            "WHERE r1.teren_id = (SELECT r2.teren_id FROM rezervacije r2 WHERE r2.id = ?) " +
                            "AND r1.id != ? AND r1.status IN ('AKTIVNA', 'POTVRDJENA') " +
                            "AND ((r1.vreme_od < ? AND r1.vreme_do > ?) OR (r1.vreme_od < ? AND r1.vreme_do > ?))";

        String updateSql = "UPDATE rezervacije SET vreme_od = ?, vreme_do = ? WHERE id = ?";

        try (Connection conn = DB.source().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(proveraSql)) {
                stmt.setInt(1, id);
                stmt.setInt(2, id);
                stmt.setTimestamp(3, Timestamp.valueOf(novoVremeDo));
                stmt.setTimestamp(4, Timestamp.valueOf(novoVremeOd));
                stmt.setTimestamp(5, Timestamp.valueOf(novoVremeDo));
                stmt.setTimestamp(6, Timestamp.valueOf(novoVremeOd));
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt("cnt") > 0) {
                    return false;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setTimestamp(1, Timestamp.valueOf(novoVremeOd));
                stmt.setTimestamp(2, Timestamp.valueOf(novoVremeDo));
                stmt.setInt(3, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean proveriZabranu(String korisnickoIme, int objekatId, Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) as nedolasci FROM rezervacije r " +
                          "JOIN tereni t ON r.teren_id = t.id " +
                          "WHERE r.korisnicko_ime = ? AND t.objekat_id = ? AND r.status = 'NIJE_DOSAO'";
        String limitSql = "SELECT dozvoljeni_minusi FROM objekti WHERE id = ?";

        int nedolasci = 0;
        int limit = 3;

        try (PreparedStatement stmtCount = conn.prepareStatement(countSql)) {
            stmtCount.setString(1, korisnickoIme);
            stmtCount.setInt(2, objekatId);
            ResultSet rs = stmtCount.executeQuery();
            if (rs.next()) {
                nedolasci = rs.getInt("nedolasci");
            }
        }

        try (PreparedStatement stmtLimit = conn.prepareStatement(limitSql)) {
            stmtLimit.setInt(1, objekatId);
            ResultSet rs = stmtLimit.executeQuery();
            if (rs.next()) {
                limit = rs.getInt("dozvoljeni_minusi");
            }
        }

        return nedolasci < limit; // true ako ima pravo da rezervise
    }
}
