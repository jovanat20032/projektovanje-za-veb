package com.example.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IzvestajRepo {

    public List<Map<String, Object>> getPopunjenostTerena(int mesec, int godina, int objekatId) {
        List<Map<String, Object>> rezultati = new ArrayList<>();
        Map<Integer, Map<String, Object>> tereniMap = new HashMap<>();

        String sqlRez = "SELECT t.id, t.naziv AS naziv_terena, " +
                        "COALESCE(SUM(TIMESTAMPDIFF(MINUTE, r.vreme_od, r.vreme_do) / 60.0), 0) AS sati_rezervacija " +
                        "FROM tereni t " +
                        "LEFT JOIN rezervacije r ON t.id = r.teren_id " +
                        "    AND MONTH(r.vreme_od) = ? AND YEAR(r.vreme_od) = ? AND r.status IN ('AKTIVNA', 'POTVRDJENA') " +
                        "WHERE t.objekat_id = ? " +
                        "GROUP BY t.id, t.naziv";

        String sqlTrening = "SELECT t.id, " +
                            "COALESCE(COUNT(tr.id) * 1.0, 0) AS sati_trening " +
                            "FROM tereni t " +
                            "LEFT JOIN trening_rezervacije tr ON t.id = tr.teren_id " +
                            "    AND MONTH(tr.datum_vreme) = ? AND YEAR(tr.datum_vreme) = ? AND tr.status IN ('ZAKAZAN', 'ODRZAN') " +
                            "WHERE t.objekat_id = ? " +
                            "GROUP BY t.id";

        try (Connection conn = DB.source().getConnection()) {
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlRez)) {
                stmt.setInt(1, mesec);
                stmt.setInt(2, godina);
                stmt.setInt(3, objekatId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> tMap = new HashMap<>();
                    tMap.put("id", rs.getInt("id"));
                    tMap.put("naziv", rs.getString("naziv_terena"));
                    tMap.put("satiRezervacija", rs.getDouble("sati_rezervacija"));
                    tMap.put("satiTrening", 0.0);
                    tereniMap.put(rs.getInt("id"), tMap);
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlTrening)) {
                stmt.setInt(1, mesec);
                stmt.setInt(2, godina);
                stmt.setInt(3, objekatId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (tereniMap.containsKey(id)) {
                        tereniMap.get(id).put("satiTrening", rs.getDouble("sati_trening"));
                    } else {
                        Map<String, Object> tMap = new HashMap<>();
                        tMap.put("id", id);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        java.time.YearMonth yearMonthObject = java.time.YearMonth.of(godina, mesec);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        double ukupanBrojSatiUMesecu = daysInMonth * 14.0;

        for (Map<String, Object> map : tereniMap.values()) {
            double rez = (double) map.get("satiRezervacija");
            double tren = (double) map.get("satiTrening");
            double ukupnoSati = rez + tren;
            double procenat = (ukupnoSati / ukupanBrojSatiUMesecu) * 100.0;
            
            map.put("ukupnoSati", ukupnoSati);
            map.put("ukupnoMogucihSati", ukupanBrojSatiUMesecu);
            map.put("procenatPopunjenosti", procenat);
            rezultati.add(map);
        }

        return rezultati;
    }

    public List<Map<String, Object>> getPrometOpreme(int mesec, int godina) {
        List<Map<String, Object>> rezultati = new ArrayList<>();

        String sql = "SELECT o.naziv, " +
                     "SUM(sp.kolicina) AS ukupno_kolicina, " +
                     "SUM(sp.kolicina * sp.cena_po_komadu) AS ukupna_zarada " +
                     "FROM oprema o " +
                     "JOIN stavke_porudzbine sp ON o.id = sp.oprema_id " +
                     "JOIN porudzbine p ON sp.porudzbina_id = p.id " +
                     "WHERE MONTH(p.datum_vreme) = ? AND YEAR(p.datum_vreme) = ? AND p.status != 'OTKAZANO' " +
                     "GROUP BY o.id, o.naziv";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mesec);
            stmt.setInt(2, godina);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("naziv", rs.getString("naziv"));
                map.put("ukupnoKolicina", rs.getInt("ukupno_kolicina"));
                map.put("ukupnaZarada", rs.getDouble("ukupna_zarada"));
                rezultati.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rezultati;
    }
}
