package com.example.backend.db;

import com.example.backend.models.Korisnik;
import com.example.backend.models.Objekat;
import com.example.backend.models.Sport;
import com.example.backend.models.Trener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminRepo {

    // --- Korisnici CRUD ---
    public List<Korisnik> getSviKorisnici() {
        List<Korisnik> lista = new ArrayList<>();
        String sql = "SELECT * FROM korisnici";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Korisnik k = new Korisnik();
                k.setKorisnickoIme(rs.getString("korisnicko_ime"));
                k.setIme(rs.getString("ime"));
                k.setPrezime(rs.getString("prezime"));
                k.setTelefon(rs.getString("telefon"));
                k.setEmail(rs.getString("email"));
                k.setUloga(rs.getString("uloga"));
                k.setStatus(rs.getString("status"));
                lista.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean updateKorisnikAdmin(Korisnik k) {
        String sql = "UPDATE korisnici SET ime=?, prezime=?, telefon=?, email=?, uloga=?, status=? WHERE korisnicko_ime=?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, k.getIme());
            stmt.setString(2, k.getPrezime());
            stmt.setString(3, k.getTelefon());
            stmt.setString(4, k.getEmail());
            stmt.setString(5, k.getUloga());
            stmt.setString(6, k.getStatus());
            stmt.setString(7, k.getKorisnickoIme());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean obrisiKorisnika(String korisnickoIme) {
        String sql = "DELETE FROM korisnici WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Zahtevi za registraciju ---
    public List<Korisnik> getZahteviZaRegistraciju() {
        List<Korisnik> lista = new ArrayList<>();
        String sql = "SELECT * FROM korisnici WHERE status = 'NA_CEKANJU'";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Korisnik k = new Korisnik();
                k.setKorisnickoIme(rs.getString("korisnicko_ime"));
                k.setIme(rs.getString("ime"));
                k.setPrezime(rs.getString("prezime"));
                k.setEmail(rs.getString("email"));
                k.setUloga(rs.getString("uloga"));
                lista.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean odluciOZahtevuKorisnika(String korisnickoIme, String odluka) {
        String sql = "UPDATE korisnici SET status = ? WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, odluka);
            stmt.setString(2, korisnickoIme);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Zahtevi za objekte ---
    public List<Objekat> getZahteviZaObjekte() {
        List<Objekat> lista = new ArrayList<>();
        String sql = "SELECT * FROM objekti WHERE status = 'NA_CEKANJU'";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Objekat o = new Objekat();
                o.setId(rs.getInt("id"));
                o.setNaziv(rs.getString("naziv"));
                o.setGrad(rs.getString("grad"));
                o.setAdresa(rs.getString("adresa"));
                o.setMaticniBroj(rs.getString("maticni_broj"));
                o.setPib(rs.getString("pib"));
                lista.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean odluciOZahtevuObjekta(int id, String odluka) {
        String sql = "UPDATE objekti SET status = ? WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, odluka);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Treneri ---
    public List<Trener> getSviTreneri() {
        List<Trener> lista = new ArrayList<>();
        String sql = "SELECT t.*, k.ime, k.prezime FROM treneri t JOIN korisnici k ON t.korisnicko_ime = k.korisnicko_ime";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Trener t = new Trener();
                t.setKorisnickoIme(rs.getString("korisnicko_ime"));
                t.setIme(rs.getString("ime"));
                t.setPrezime(rs.getString("prezime"));
                t.setSport(rs.getString("sport"));
                t.setSpecijalizacija(rs.getString("specijalizacija"));
                lista.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean deaktivirajTrenera(String korisnickoIme) {
        String sql = "DELETE FROM treneri WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Sportovi ---
    public boolean dodajSport(String naziv, boolean timski) {
        String sql = "INSERT INTO sportovi (naziv, timski) VALUES (?, ?)";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, naziv);
            stmt.setBoolean(2, timski);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
