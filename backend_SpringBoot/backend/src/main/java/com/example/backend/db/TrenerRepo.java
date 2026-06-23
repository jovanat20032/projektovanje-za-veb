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
        String sql = "INSERT INTO trening_rezervacije (trener_korisnicko_ime, sportista_korisnicko_ime, datum_vreme, status) VALUES (?, ?, ?, 'ZAKAZAN')";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getTrenerKorisnickoIme());
            stmt.setString(2, r.getSportistaKorisnickoIme());
            stmt.setTimestamp(3, Timestamp.valueOf(r.getDatumVreme()));
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
                r.setTrenerIme(rs.getString("ime") + " " + rs.getString("prezime"));
                r.setSport(rs.getString("sport"));
                treninzi.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treninzi;
    }
}
