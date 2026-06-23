package com.example.backend.db;

import com.example.backend.models.Oglas;
import com.example.backend.models.ZahtevOglasa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OglasRepo implements OglasRepoInterface {

    @Override
    public List<Oglas> getAllAktivniOglasi() {
        List<Oglas> oglasi = new ArrayList<>();
        String sql = "SELECT * FROM oglasi WHERE status = 'AKTIVAN'";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                oglasi.add(mapirajOglas(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oglasi;
    }

    @Override
    public List<Oglas> getOglasiByKorisnik(String korisnickoIme) {
        List<Oglas> oglasi = new ArrayList<>();
        String sql = "SELECT * FROM oglasi WHERE korisnicko_ime = ? ORDER BY id DESC";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                oglasi.add(mapirajOglas(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oglasi;
    }

    @Override
    public boolean kreirajOglas(Oglas o) {
        String sql = "INSERT INTO oglasi (korisnicko_ime, sport, grad, datum_vreme, nedostaje_igraca, status) VALUES (?, ?, ?, ?, ?, 'AKTIVAN')";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, o.getKorisnickoIme());
            stmt.setString(2, o.getSport());
            stmt.setString(3, o.getGrad());
            stmt.setTimestamp(4, Timestamp.valueOf(o.getDatumVreme()));
            stmt.setInt(5, o.getNedostajeIgraca());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean zatvoriOglas(int oglasId) {
        String sql = "UPDATE oglasi SET status = 'ZATVOREN' WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, oglasId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean prijaviSeNaOglas(int oglasId, String korisnickoIme) {
        String checkSql = "SELECT * FROM zahtevi_oglasi WHERE oglas_id = ? AND korisnicko_ime = ?";
        String insertSql = "INSERT INTO zahtevi_oglasi (oglas_id, korisnicko_ime, status) VALUES (?, ?, 'NA_CEKANJU')";
        try (Connection conn = DB.source().getConnection()) {
            // Provera da li se korisnik vec prijavio
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, oglasId);
                checkStmt.setString(2, korisnickoIme);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) return false; // Vec postoji prijava
            }
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, oglasId);
                stmt.setString(2, korisnickoIme);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ZahtevOglasa> getZahteviZaOglas(int oglasId) {
        List<ZahtevOglasa> zahtevi = new ArrayList<>();
        String sql = "SELECT * FROM zahtevi_oglasi WHERE oglas_id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, oglasId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ZahtevOglasa z = new ZahtevOglasa();
                z.setId(rs.getInt("id"));
                z.setOglasId(rs.getInt("oglas_id"));
                z.setKorisnickoIme(rs.getString("korisnicko_ime"));
                z.setStatus(rs.getString("status"));
                zahtevi.add(z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zahtevi;
    }

    @Override
    public boolean azurirajStatusZahteva(int zahtevId, String status) {
        String sql = "UPDATE zahtevi_oglasi SET status = ? WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, zahtevId);
            int affected = stmt.executeUpdate();
            
            if (affected > 0 && "ODOBREN".equals(status)) {
                // Smanji broj nedostajucih igraca
                String findOglasSql = "SELECT oglas_id FROM zahtevi_oglasi WHERE id = ?";
                int oglasId = -1;
                try (PreparedStatement findStmt = conn.prepareStatement(findOglasSql)) {
                    findStmt.setInt(1, zahtevId);
                    ResultSet rs = findStmt.executeQuery();
                    if (rs.next()) oglasId = rs.getInt("oglas_id");
                }
                
                if (oglasId != -1) {
                    String updateOglas = "UPDATE oglasi SET nedostaje_igraca = nedostaje_igraca - 1 WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateOglas)) {
                        updateStmt.setInt(1, oglasId);
                        updateStmt.executeUpdate();
                    }
                    
                    // Proveri da li je doslo do 0, pa zatvori oglas
                    String checkOglas = "SELECT nedostaje_igraca FROM oglasi WHERE id = ?";
                    int nedostaje = -1;
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkOglas)) {
                        checkStmt.setInt(1, oglasId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next()) nedostaje = rs.getInt("nedostaje_igraca");
                    }
                    
                    if (nedostaje <= 0) {
                        zatvoriOglas(oglasId);
                    }
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Oglas mapirajOglas(ResultSet rs) throws SQLException {
        Oglas o = new Oglas();
        o.setId(rs.getInt("id"));
        o.setKorisnickoIme(rs.getString("korisnicko_ime"));
        o.setSport(rs.getString("sport"));
        o.setGrad(rs.getString("grad"));
        if (rs.getTimestamp("datum_vreme") != null) {
            o.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
        }
        o.setNedostajeIgraca(rs.getInt("nedostaje_igraca"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}
