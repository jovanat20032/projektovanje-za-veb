package com.example.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.models.Objekat;
import com.example.backend.models.Promocija;

public class ObjekatRepo implements ObjekatRepoInterface{
    @Override
    public int getUkupanBrojAktivnih() {
        String sql = "SELECT COUNT(*) as total FROM objekti WHERE status = 'ODOBREN'";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Greška pri dohvatanju broja objekata: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Objekat> getTop3Aktivna() {
        List<Objekat> top3 = new ArrayList<>();
        String sql = "SELECT * FROM objekti WHERE status = 'ODOBREN' ORDER BY lajkovi DESC LIMIT 3";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                top3.add(mapirajObjekat(rs));
            }
        } catch (SQLException e) {
            System.out.println("Greška pri dohvatanju TOP 3 objekata: " + e.getMessage());
            e.printStackTrace();
        }
        return top3;
    }

    @Override
    public List<Objekat> pretraga(String naziv, String grad, String sport) {
        List<Objekat> rezultati = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, GROUP_CONCAT(s.naziv SEPARATOR ', ') as sport_lista " +
            "FROM objekti o " +
            "LEFT JOIN objekat_sport os ON o.id = os.objekat_id " +
            "LEFT JOIN sportovi s ON os.sport_id = s.id " +
            "WHERE o.status = 'ODOBREN' "
        );

    if (naziv != null && !naziv.isEmpty()) sql.append(" AND o.naziv LIKE ? ");
    if (grad != null && !grad.isEmpty()) sql.append(" AND o.grad = ? ");
    if (sport != null && !sport.isEmpty()) sql.append(" AND os.sport = ? ");

    sql.append(" GROUP BY o.id");

    try (Connection conn = DB.source().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

        int paramIndex = 1;
        if (naziv != null && !naziv.isEmpty()) stmt.setString(paramIndex++, "%" + naziv + "%");
        if (grad != null && !grad.isEmpty()) stmt.setString(paramIndex++, grad);
        if (sport != null && !sport.isEmpty()) stmt.setString(paramIndex++, sport);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Objekat o = mapirajObjekat(rs);
            o.setSport(rs.getString("sport_lista")); 
            rezultati.add(o);
        }
    } catch (SQLException e) {
        System.out.println("Greška pri pretrazi objekata: " + e.getMessage());
        e.printStackTrace();
    }
    return rezultati;
    }

    @Override
    public List<String> getSviGradovi() {
        List<String> gradovi = new ArrayList<>();
        String sql = "SELECT DISTINCT grad FROM objekti WHERE status = 'ODOBREN'";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                gradovi.add(rs.getString("grad"));
            }
        } catch (SQLException e) {
            System.out.println("Greška pri dohvatanju gradova: " + e.getMessage());
            e.printStackTrace();
        }
        return gradovi;
    }

    private Objekat mapirajObjekat(ResultSet rs) throws SQLException {
        Objekat o = new Objekat(); 
        o.setId(rs.getInt("id"));
        o.setNaziv(rs.getString("naziv"));
        o.setGrad(rs.getString("grad"));
        o.setAdresa(rs.getString("adresa"));
        o.setMaticniBroj(rs.getString("maticni_broj"));
        o.setPib(rs.getString("pib"));
        o.setCenaPoSatu(rs.getDouble("cena_po_satu"));
        o.setRadnoVreme(rs.getString("radno_vreme"));
        o.setDozvoljeniMinusi(rs.getInt("dozvoljeni_minusi"));
        o.setLajkovi(rs.getInt("lajkovi"));
        o.setDislajkovi(rs.getInt("dislajkovi"));
        o.setStatus(rs.getString("status"));
        return o;
    }

    @Override
    public List<Promocija> getTop3Promocije() {
        List<Promocija> promocije = new ArrayList<>();
        String sql = "SELECT p.id, p.naziv, o.naziv as objekat_naziv, p.vazi_od, p.vazi_do, p.popust " +
                     "FROM promocije p " +
                     "JOIN objekti o ON p.objekat_id = o.id " +
                     "WHERE p.vazi_do >= CURRENT_DATE " +
                     "ORDER BY p.vazi_do ASC LIMIT 3";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                promocije.add(new Promocija(
                    rs.getInt("id"),
                    rs.getString("naziv"),
                    rs.getString("objekat_naziv"),
                    rs.getDate("vazi_od"),
                    rs.getDate("vazi_do"),
                    rs.getString("popust")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promocije;
    }

    public Objekat getById(int id) {
        String sql = "SELECT * FROM objekti WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapirajObjekat(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<String> getSlikeZaObjekat(int objekatId) {
        List<String> slike = new ArrayList<>();
        String sql = "SELECT putanja_slike FROM galerija WHERE objekat_id = ?";
        
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                slike.add(rs.getString("putanja_slike"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slike;
    }

    public List<String> getSportoviZaObjekat(int objekatId) {
        List<String> sportovi = new ArrayList<>();
        String sql = "SELECT s.naziv FROM sportovi s JOIN objekat_sport os ON s.id = os.sport_id WHERE os.objekat_id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sportovi.add(rs.getString("naziv"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sportovi;
    }

    @Override
    public List<Objekat> pretragaSportista(String naziv, String grad, String sport, String tipTerena, boolean slobodniDanas) {
        List<Objekat> rezultati = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, GROUP_CONCAT(DISTINCT s.naziv SEPARATOR ', ') as sport_lista " +
            "FROM objekti o " +
            "LEFT JOIN objekat_sport os ON o.id = os.objekat_id " +
            "LEFT JOIN sportovi s ON os.sport_id = s.id " +
            "LEFT JOIN tereni t ON o.id = t.objekat_id " +
            "WHERE o.status = 'ODOBREN' "
        );

        if (naziv != null && !naziv.isEmpty()) sql.append(" AND o.naziv LIKE ? ");
        if (grad != null && !grad.isEmpty()) sql.append(" AND o.grad = ? ");
        if (sport != null && !sport.isEmpty()) sql.append(" AND s.naziv = ? ");
        if (tipTerena != null && !tipTerena.isEmpty()) sql.append(" AND t.tip = ? ");

        sql.append(" GROUP BY o.id");

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (naziv != null && !naziv.isEmpty()) stmt.setString(paramIndex++, "%" + naziv + "%");
            if (grad != null && !grad.isEmpty()) stmt.setString(paramIndex++, grad);
            if (sport != null && !sport.isEmpty()) stmt.setString(paramIndex++, sport);
            if (tipTerena != null && !tipTerena.isEmpty()) stmt.setString(paramIndex++, tipTerena);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Objekat o = mapirajObjekat(rs);
                o.setSport(rs.getString("sport_lista"));
                
                boolean dodaj = true;
                if (slobodniDanas) {
                    // Check if it has free slots today
                    dodaj = imaSlobodnihTerminaDanas(o.getId(), conn);
                }
                
                if (dodaj) {
                    rezultati.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultati;
    }

    private boolean imaSlobodnihTerminaDanas(int objekatId, Connection conn) throws SQLException {
        // Find radno_vreme
        String sqlObjekat = "SELECT radno_vreme FROM objekti WHERE id = ?";
        String radnoVreme = null;
        try (PreparedStatement stmt = conn.prepareStatement(sqlObjekat)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) radnoVreme = rs.getString("radno_vreme");
        }
        if (radnoVreme == null || !radnoVreme.contains("-")) return false;
        
        // Extract hours
        String[] delovi = radnoVreme.split("-");
        int radnoVremeOd = Integer.parseInt(delovi[0].trim().split(":")[0]);
        int radnoVremeDo = Integer.parseInt(delovi[1].trim().split(":")[0]);
        int ukupanBrojSati = radnoVremeDo - radnoVremeOd;

        // Get tereni
        List<Integer> terenIds = new ArrayList<>();
        String sqlTereni = "SELECT id FROM tereni WHERE objekat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlTereni)) {
            stmt.setInt(1, objekatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                terenIds.add(rs.getInt("id"));
            }
        }

        if (terenIds.isEmpty()) return false;

        // For each teren, check if there is at least one free 1-hour slot today
        for (int terenId : terenIds) {
            String sqlRezervacije = "SELECT COUNT(*) as broj_rezervacija_danas FROM rezervacije WHERE teren_id = ? AND status IN ('AKTIVNA', 'POTVRDJENA') AND DATE(vreme_od) = CURRENT_DATE";
            try (PreparedStatement stmt = conn.prepareStatement(sqlRezervacije)) {
                stmt.setInt(1, terenId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int rez = rs.getInt("broj_rezervacija_danas");
                    if (rez < ukupanBrojSati) {
                        return true; // Found at least one free slot!
                    }
                }
            }
        }
        return false;
    }
}

