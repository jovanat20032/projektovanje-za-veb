package com.example.backend.db;

import com.example.backend.models.Korisnik;
import com.example.backend.models.RezervacijaDTO;
import com.example.backend.models.Sport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean azurirajKorisnika(Korisnik korisnik) {
        String sql = "UPDATE korisnici SET ime = ?, prezime = ?, telefon = ?, email = ?, profilna_slika = ? WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, korisnik.getIme());
            st.setString(2, korisnik.getPrezime());
            st.setString(3, korisnik.getTelefon());
            st.setString(4, korisnik.getEmail());
            st.setString(5, korisnik.getProfilnaSlika());
            st.setString(6, korisnik.getKorisnickoIme());
            
            int rows = st.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Sport> dohvatiSveSportove() {
        List<Sport> sportovi = new ArrayList<>();
        String sql = "SELECT id, naziv, timski FROM sportovi";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                sportovi.add(new Sport(rs.getInt("id"), rs.getString("naziv"), rs.getBoolean("timski")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sportovi;
    }

    @Override
    public List<String> dohvatiOmiljeneSportove(String korisnickoIme) {
        List<String> sportovi = new ArrayList<>();
        String sql = "SELECT sport FROM omiljeni_sportovi WHERE korisnicko_ime = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, korisnickoIme);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                sportovi.add(rs.getString("sport"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sportovi;
    }

    @Override
    public boolean azurirajOmiljeneSportove(String korisnickoIme, List<String> sportovi) {
        String sqlDelete = "DELETE FROM omiljeni_sportovi WHERE korisnicko_ime = ?";
        String sqlInsert = "INSERT INTO omiljeni_sportovi (korisnicko_ime, sport) VALUES (?, ?)";
        
        try (Connection conn = DB.source().getConnection()) {
            try (PreparedStatement stDel = conn.prepareStatement(sqlDelete)) {
                stDel.setString(1, korisnickoIme);
                stDel.executeUpdate();
            }
            try (PreparedStatement stIns = conn.prepareStatement(sqlInsert)) {
                for (String sport : sportovi) {
                    stIns.setString(1, korisnickoIme);
                    stIns.setString(2, sport);
                    stIns.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<RezervacijaDTO> dohvatiRezervacijeKorisnika(String korisnickoIme) {
        List<RezervacijaDTO> lista = new ArrayList<>();
        
        String sql = "SELECT r.id, o.naziv AS objekat, o.grad, t.naziv AS teren, r.sport, " +
                    "r.vreme_od, r.vreme_do, r.status " +
                    "FROM rezervacije r " +
                    "JOIN tereni t ON r.teren_id = t.id " +
                    "JOIN objekti o ON t.objekat_id = o.id " +
                    "WHERE r.korisnicko_ime = ? ORDER BY r.vreme_od DESC";
                    
        try (Connection conn = DB.source().getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, korisnickoIme);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                lista.add(new RezervacijaDTO(
                    rs.getInt("id"),
                    rs.getString("objekat"),
                    rs.getString("grad"),
                    rs.getString("teren"),
                    rs.getString("sport"), // Sada čitamo direktno iz 'rezervacije' tabele
                    rs.getTimestamp("vreme_od").toLocalDateTime(),
                    rs.getTimestamp("vreme_do").toLocalDateTime(),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return lista;
    }
    @Override
    public boolean otkaziRezervaciju(int id) {

        String sql = "UPDATE rezervacije SET status = 'OTKAZANA' WHERE id = ?";
        
        try (Connection conn = DB.source().getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {
            
            st.setInt(1, id);
            
            int rows = st.executeUpdate();
            return rows > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}