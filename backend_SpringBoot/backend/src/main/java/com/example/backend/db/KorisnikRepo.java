package com.example.backend.db;

import com.example.backend.models.Korisnik;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KorisnikRepo implements KorisnikRepoInterface {

    @Override
    public Korisnik findByKorisnickoIme(String korisnickoIme) {
        String sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ?";
        

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, korisnickoIme);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Korisnik(
                    rs.getString("korisnicko_ime"),
                    rs.getString("lozinka"),
                    rs.getString("ime"),
                    rs.getString("prezime"),
                    rs.getString("telefon"),
                    rs.getString("email"),
                    rs.getString("profilna_slika"),
                    rs.getString("uloga"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Greska pri trazenju korisnika: " + e.getMessage());
            e.printStackTrace();
        }

        return null; 
    }

    @Override
    public boolean save(Korisnik korisnik) {
        String sql = "INSERT INTO korisnici (korisnicko_ime, lozinka, ime, prezime, telefon, email, profilna_slika, uloga, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, korisnik.getKorisnickoIme());
            stmt.setString(2, korisnik.getLozinka());
            stmt.setString(3, korisnik.getIme());
            stmt.setString(4, korisnik.getPrezime());
            stmt.setString(5, korisnik.getTelefon());
            stmt.setString(6, korisnik.getEmail());
            
            String slika = (korisnik.getProfilnaSlika() != null && !korisnik.getProfilnaSlika().isEmpty()) 
                            ? korisnik.getProfilnaSlika() 
                            : "default_avatar.png";
            stmt.setString(7, slika);
            
            stmt.setString(8, korisnik.getUloga());
            stmt.setString(9, korisnik.getStatus());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.out.println("Greska pri cuvanju korisnika: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public Korisnik findByKorisnickoImeIliEmail(String unos) {
        String sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ? OR email = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, unos);
            stmt.setString(2, unos);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Korisnik k = new Korisnik();
                k.setKorisnickoIme(rs.getString("korisnicko_ime"));
                k.setLozinka(rs.getString("lozinka"));
                k.setEmail(rs.getString("email"));
                k.setUloga(rs.getString("uloga"));
                k.setStatus(rs.getString("status"));
                return k;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean azurirajLozinku(String korisnickoIme, String novaEnkriptovanaLozinka) {
        String sql = "UPDATE korisnici SET lozinka = ? WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novaEnkriptovanaLozinka);
            stmt.setString(2, korisnickoIme);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;    }
}