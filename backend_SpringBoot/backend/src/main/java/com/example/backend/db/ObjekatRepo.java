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
                Promocija p = new Promocija();
                p.setId(rs.getInt("id"));
                p.setNaziv(rs.getString("naziv"));
                p.setNazivObjekta(rs.getString("objekat_naziv"));
                if (rs.getDate("vazi_od") != null) p.setVaziOd(rs.getDate("vazi_od").toLocalDate());
                if (rs.getDate("vazi_do") != null) p.setVaziDo(rs.getDate("vazi_do").toLocalDate());
                p.setPopust(rs.getString("popust"));
                promocije.add(p);
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

    public List<com.example.backend.models.ZaposleniObjekatDTO> dohvatiObjekteZaZaposlenog(String korisnickoIme) {
        List<com.example.backend.models.ZaposleniObjekatDTO> rezultati = new ArrayList<>();
        String sql = "SELECT o.id, o.naziv, o.grad " +
                     "FROM objekti o " +
                     "JOIN zaposleni_objekat zo ON o.id = zo.objekat_id " +
                     "WHERE zo.korisnicko_ime = ?";
                     
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, korisnickoIme);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                com.example.backend.models.ZaposleniObjekatDTO dto = new com.example.backend.models.ZaposleniObjekatDTO();
                dto.setId(rs.getInt("id"));
                dto.setNaziv(rs.getString("naziv"));
                dto.setGrad(rs.getString("grad"));
                
                List<String> sportovi = new ArrayList<>();
                String sqlSportovi = "SELECT s.naziv FROM sportovi s JOIN objekat_sport os ON s.id = os.sport_id WHERE os.objekat_id = ?";
                try (PreparedStatement stSport = conn.prepareStatement(sqlSportovi)) {
                    stSport.setInt(1, dto.getId());
                    ResultSet rsSport = stSport.executeQuery();
                    while (rsSport.next()) {
                        sportovi.add(rsSport.getString("naziv"));
                    }
                }
                dto.setVrsteSportova(sportovi);
                
                List<String> tereni = new ArrayList<>();
                String sqlTereni = "SELECT naziv, tip, kapacitet FROM tereni WHERE objekat_id = ?";
                try (PreparedStatement stTeren = conn.prepareStatement(sqlTereni)) {
                    stTeren.setInt(1, dto.getId());
                    ResultSet rsTeren = stTeren.executeQuery();
                    while (rsTeren.next()) {
                        tereni.add(rsTeren.getString("naziv") + " (" + rsTeren.getString("tip") + ", Kapacitet: " + rsTeren.getInt("kapacitet") + ")");
                    }
                }
                dto.setElementi(tereni);
                
                rezultati.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultati;
    }

    public int dodajObjekat(com.example.backend.models.ObjekatCreateDTO dto, String korisnickoIme) {
        String sql = "INSERT INTO objekti (naziv, grad, adresa, maticni_broj, pib, cena_po_satu, radno_vreme, dozvoljeni_minusi, lajkovi, dislajkovi, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 'ODOBREN')";
        int objekatId = -1;
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dto.getNaziv());
            stmt.setString(2, dto.getGrad());
            stmt.setString(3, dto.getAdresa());
            stmt.setString(4, dto.getMaticniBroj());
            stmt.setString(5, dto.getPib());
            stmt.setDouble(6, dto.getCenaPoSatu());
            stmt.setString(7, dto.getRadnoVreme());
            stmt.setInt(8, dto.getDozvoljeniMinusi());
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                objekatId = rs.getInt(1);
            }
            
            if (objekatId != -1 && korisnickoIme != null && !korisnickoIme.isEmpty()) {
                String linkSql = "INSERT INTO zaposleni_objekat (korisnicko_ime, objekat_id) VALUES (?, ?)";
                try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
                    linkStmt.setString(1, korisnickoIme);
                    linkStmt.setInt(2, objekatId);
                    linkStmt.executeUpdate();
                }
            }

            if (objekatId != -1 && dto.getVrsteSportova() != null) {
                String sqlSport = "INSERT INTO objekat_sport (objekat_id, sport_id) VALUES (?, (SELECT id FROM sportovi WHERE naziv = ?))";
                try (PreparedStatement stmtSport = conn.prepareStatement(sqlSport)) {
                    for (String sport : dto.getVrsteSportova()) {
                        stmtSport.setInt(1, objekatId);
                        stmtSport.setString(2, sport);
                        stmtSport.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return objekatId;
    }

    public boolean azurirajObjekat(int id, com.example.backend.models.ObjekatCreateDTO dto) {
        String sql = "UPDATE objekti SET naziv = ?, grad = ?, adresa = ?, maticni_broj = ?, pib = ?, cena_po_satu = ?, radno_vreme = ?, dozvoljeni_minusi = ? WHERE id = ?";
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.getNaziv());
            stmt.setString(2, dto.getGrad());
            stmt.setString(3, dto.getAdresa());
            stmt.setString(4, dto.getMaticniBroj());
            stmt.setString(5, dto.getPib());
            stmt.setDouble(6, dto.getCenaPoSatu());
            stmt.setString(7, dto.getRadnoVreme());
            stmt.setInt(8, dto.getDozvoljeniMinusi());
            stmt.setInt(9, id);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                // Obriši stare sportove
                String delSport = "DELETE FROM objekat_sport WHERE objekat_id = ?";
                try (PreparedStatement stmtDel = conn.prepareStatement(delSport)) {
                    stmtDel.setInt(1, id);
                    stmtDel.executeUpdate();
                }

                // Unesi nove
                if (dto.getVrsteSportova() != null) {
                    String sqlSport = "INSERT INTO objekat_sport (objekat_id, sport_id) VALUES (?, (SELECT id FROM sportovi WHERE naziv = ?))";
                    try (PreparedStatement stmtSport = conn.prepareStatement(sqlSport)) {
                        for (String sport : dto.getVrsteSportova()) {
                            stmtSport.setInt(1, id);
                            stmtSport.setString(2, sport);
                            stmtSport.executeUpdate();
                        }
                    }
                }
            }
            
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

