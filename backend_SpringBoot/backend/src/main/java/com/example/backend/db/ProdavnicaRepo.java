package com.example.backend.db;

import com.example.backend.models.Oprema;
import com.example.backend.models.Porudzbina;
import com.example.backend.models.StavkaPorudzbine;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProdavnicaRepo {

    public List<Oprema> getOpremaBySport(String sport) {
        List<Oprema> lista = new ArrayList<>();
        String sql = "SELECT * FROM oprema WHERE zaliha > 0";
        if (sport != null && !sport.isEmpty() && !sport.equals("SVI")) {
            sql += " AND sport = ?";
        }
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (sport != null && !sport.isEmpty() && !sport.equals("SVI")) {
                stmt.setString(1, sport);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Oprema o = new Oprema();
                o.setId(rs.getInt("id"));
                o.setSport(rs.getString("sport"));
                o.setNaziv(rs.getString("naziv"));
                o.setCena(rs.getDouble("cena"));
                o.setZaliha(rs.getInt("zaliha"));
                o.setSlika(rs.getString("slika"));
                lista.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean naruci(Porudzbina p) {
        Connection conn = null;
        try {
            conn = DB.source().getConnection();
            conn.setAutoCommit(false); // Transakcija


            for (StavkaPorudzbine stavka : p.getStavke()) {
                String checkSql = "SELECT zaliha FROM oprema WHERE id = ? FOR UPDATE";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, stavka.getOpremaId());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        int zaliha = rs.getInt("zaliha");
                        if (zaliha < stavka.getKolicina()) {
                            conn.rollback();
                            return false; // Nema dovoljno na stanju
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

                String updateSql = "UPDATE oprema SET zaliha = zaliha - ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, stavka.getKolicina());
                    updateStmt.setInt(2, stavka.getOpremaId());
                    updateStmt.executeUpdate();
                }
            }


            String insertPorudzbina = "INSERT INTO porudzbine (korisnicko_ime, datum_vreme, status, ukupna_cena) VALUES (?, ?, 'NARUCENO', ?)";
            int porudzbinaId = -1;
            try (PreparedStatement stmtP = conn.prepareStatement(insertPorudzbina, Statement.RETURN_GENERATED_KEYS)) {
                stmtP.setString(1, p.getKorisnickoIme());
                stmtP.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmtP.setDouble(3, p.getUkupnaCena());
                stmtP.executeUpdate();
                ResultSet rsP = stmtP.getGeneratedKeys();
                if (rsP.next()) {
                    porudzbinaId = rsP.getInt(1);
                }
            }


            String insertStavka = "INSERT INTO stavke_porudzbine (porudzbina_id, oprema_id, kolicina, cena_po_komadu) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmtS = conn.prepareStatement(insertStavka)) {
                for (StavkaPorudzbine s : p.getStavke()) {
                    stmtS.setInt(1, porudzbinaId);
                    stmtS.setInt(2, s.getOpremaId());
                    stmtS.setInt(3, s.getKolicina());
                    stmtS.setDouble(4, s.getCenaPoKomadu());
                    stmtS.addBatch();
                }
                stmtS.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {}
            }
        }
    }

    public List<Porudzbina> getMojePorudzbine(String korisnickoIme) {
        List<Porudzbina> porudzbine = new ArrayList<>();
        String sql = "SELECT * FROM porudzbine WHERE korisnicko_ime = ? ORDER BY datum_vreme DESC";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Porudzbina p = new Porudzbina();
                p.setId(rs.getInt("id"));
                p.setKorisnickoIme(rs.getString("korisnicko_ime"));
                p.setDatumVreme(rs.getTimestamp("datum_vreme").toLocalDateTime());
                p.setStatus(rs.getString("status"));
                p.setUkupnaCena(rs.getDouble("ukupna_cena"));
                p.setStavke(getStavkeZaPorudzbinu(p.getId()));
                porudzbine.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return porudzbine;
    }

    private List<StavkaPorudzbine> getStavkeZaPorudzbinu(int porudzbinaId) {
        List<StavkaPorudzbine> stavke = new ArrayList<>();
        String sql = "SELECT sp.*, o.naziv FROM stavke_porudzbine sp JOIN oprema o ON sp.oprema_id = o.id WHERE sp.porudzbina_id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, porudzbinaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StavkaPorudzbine s = new StavkaPorudzbine();
                s.setId(rs.getInt("id"));
                s.setPorudzbinaId(rs.getInt("porudzbina_id"));
                s.setOpremaId(rs.getInt("oprema_id"));
                s.setKolicina(rs.getInt("kolicina"));
                s.setCenaPoKomadu(rs.getDouble("cena_po_komadu"));
                s.setOpremaNaziv(rs.getString("naziv"));
                stavke.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stavke;
    }

    public boolean otkaziPorudzbinu(int porudzbinaId) {
        Connection conn = null;
        try {
            conn = DB.source().getConnection();
            conn.setAutoCommit(false);


            String checkSql = "SELECT status FROM porudzbine WHERE id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, porudzbinaId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    if (!"NARUCENO".equals(rs.getString("status"))) {
                        conn.rollback();
                        return false;
                    }
                } else {
                    conn.rollback();
                    return false;
                }
            }


            List<StavkaPorudzbine> stavke = getStavkeZaPorudzbinu(porudzbinaId);
            String updateSql = "UPDATE oprema SET zaliha = zaliha + ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                for (StavkaPorudzbine s : stavke) {
                    updateStmt.setInt(1, s.getKolicina());
                    updateStmt.setInt(2, s.getOpremaId());
                    updateStmt.executeUpdate();
                }
            }


            String cancelSql = "UPDATE porudzbine SET status = 'OTKAZANO' WHERE id = ?";
            try (PreparedStatement cancelStmt = conn.prepareStatement(cancelSql)) {
                cancelStmt.setInt(1, porudzbinaId);
                cancelStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            return false;
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
