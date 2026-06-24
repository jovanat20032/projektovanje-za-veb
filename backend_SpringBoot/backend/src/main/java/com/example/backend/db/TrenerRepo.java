package com.example.backend.db;

import com.example.backend.models.Trener;
import com.example.backend.models.TreningRezervacija;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TrenerRepo {

    public List<Trener> getTreneriByObjekatAndSport(int objekatId, String sport) {
        List<Trener> treneri = new ArrayList<>();
        String sql = "SELECT t.*, k.ime, k.prezime " +
                     "FROM treneri t " +
                     "JOIN korisnici k ON t.korisnicko_ime = k.korisnicko_ime " +
                     "JOIN zaposleni_objekat zo ON t.korisnicko_ime = zo.korisnicko_ime " +
                     "WHERE zo.objekat_id = ? AND t.sport = ?";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            stmt.setString(2, sport);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Trener t = new Trener();
                t.setKorisnickoIme(rs.getString("korisnicko_ime"));
                t.setIme(rs.getString("ime"));
                t.setPrezime(rs.getString("prezime"));
                t.setSport(rs.getString("sport"));
                t.setSpecijalizacija(rs.getString("specijalizacija"));
                t.setProsecnaOcena(rs.getDouble("prosecna_ocena"));
                t.setCenaPoSatu(rs.getInt("cena_po_satu"));
                treneri.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treneri;
    }

    public boolean zakaziTrening(TreningRezervacija r) {
        String sql = "INSERT INTO trening_rezervacije (trener_korisnicko_ime, sportista_korisnicko_ime, datum_vreme, status, teren_id) VALUES (?, ?, ?, 'ZAKAZAN', ?)";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getTrenerKorisnickoIme());
            stmt.setString(2, r.getSportistaKorisnickoIme());
            stmt.setTimestamp(3, Timestamp.valueOf(r.getDatumVreme()));
            stmt.setInt(4, r.getTerenId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TreningRezervacija> getTreninziZaSportistu(String korisnickoIme) {
        List<TreningRezervacija> treninzi = new ArrayList<>();
        String sql = "SELECT tr.*, k.ime, k.prezime, t.sport " +
                     "FROM trening_rezervacije tr " +
                     "JOIN treneri t ON tr.trener_korisnicko_ime = t.korisnicko_ime " +
                     "JOIN korisnici k ON t.korisnicko_ime = k.korisnicko_ime " +
                     "WHERE tr.sportista_korisnicko_ime = ? ORDER BY tr.datum_vreme DESC";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TreningRezervacija r = new TreningRezervacija();
                r.setId(rs.getInt("id"));
                r.setTrenerKorisnickoIme(rs.getString("trener_korisnicko_ime"));
                r.setSportistaKorisnickoIme(rs.getString("sportista_korisnicko_ime"));
                r.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
                r.setStatus(rs.getString("status"));
                r.setTerenId(rs.getInt("teren_id"));
                r.setTrenerIme(rs.getString("ime") + " " + rs.getString("prezime"));
                r.setSport(rs.getString("sport"));
                treninzi.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treninzi;
    }

    public List<TreningRezervacija> getTreninziZaZaposlenog(String zaposleniKorisnickoIme) {
        List<TreningRezervacija> treninzi = new ArrayList<>();
        String sql = "SELECT DISTINCT tr.*, ks.ime, ks.prezime, t.sport " +
                     "FROM trening_rezervacije tr " +
                     "JOIN treneri t ON tr.trener_korisnicko_ime = t.korisnicko_ime " +
                     "JOIN korisnici ks ON tr.sportista_korisnicko_ime = ks.korisnicko_ime " +
                     "JOIN zaposleni_objekat zo_trener ON t.korisnicko_ime = zo_trener.korisnicko_ime " +
                     "JOIN zaposleni_objekat zo_zaposleni ON zo_trener.objekat_id = zo_zaposleni.objekat_id " +
                     "WHERE zo_zaposleni.korisnicko_ime = ? " +
                     "ORDER BY tr.datum_vreme DESC";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zaposleniKorisnickoIme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TreningRezervacija r = new TreningRezervacija();
                r.setId(rs.getInt("id"));
                r.setTrenerKorisnickoIme(rs.getString("trener_korisnicko_ime"));
                r.setSportistaKorisnickoIme(rs.getString("sportista_korisnicko_ime"));
                r.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
                r.setStatus(rs.getString("status"));
                r.setTerenId(rs.getInt("teren_id"));
                r.setTrenerIme(rs.getString("ime") + " " + rs.getString("prezime"));
                r.setSport(rs.getString("sport"));
                treninzi.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treninzi;
    }

    public boolean azurirajStatusTreninga(int id, String status) {
        String sql = "UPDATE trening_rezervacije SET status = ? WHERE id = ?";
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

    public List<TreningRezervacija> getTreninziZaTeren(int terenId) {
        List<TreningRezervacija> treninzi = new ArrayList<>();
        String sql = "SELECT tr.*, k.ime, k.prezime, t.sport " +
                     "FROM trening_rezervacije tr " +
                     "JOIN treneri t ON tr.trener_korisnicko_ime = t.korisnicko_ime " +
                     "JOIN korisnici k ON t.korisnicko_ime = k.korisnicko_ime " +
                     "WHERE tr.teren_id = ? AND tr.status IN ('ZAKAZAN', 'POTVRDJEN')";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, terenId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TreningRezervacija r = new TreningRezervacija();
                r.setId(rs.getInt("id"));
                r.setTrenerKorisnickoIme(rs.getString("trener_korisnicko_ime"));
                r.setSportistaKorisnickoIme(rs.getString("sportista_korisnicko_ime"));
                r.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
                r.setStatus(rs.getString("status"));
                r.setTerenId(rs.getInt("teren_id"));
                r.setTrenerIme(rs.getString("ime") + " " + rs.getString("prezime"));
                r.setSport(rs.getString("sport"));
                treninzi.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treninzi;
    }

    public boolean pomeriTrening(int id, java.time.LocalDateTime novoVreme) {
        String sql = "UPDATE trening_rezervacije SET datum_vreme = ? WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(novoVreme));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
