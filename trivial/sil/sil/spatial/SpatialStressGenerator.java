package com.example.spatial;

import com.example.spatial.domain.*;
import com.example.spatial.domain.Location;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SpatialStressGenerator implements CommandLineRunner {

    private final EntityManager em;

    private static final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

    private static final Random random = new Random(42);

    private static final int TOTAL_PARENTS = 1_000;
    private static final int BATCH_SIZE = 100;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("Starting spatial stress test...");

        long start = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_PARENTS; i++) {

            Parent parent = createParent(i);
            em.persist(parent);

            if (i % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }

            if (i % 100 == 0) {
                System.out.println("Inserted: " + i);
            }
        }

        em.flush();
        em.clear();

        long end = System.currentTimeMillis();
        long duration = end - start;

        printStatistics(duration);
    }

    private Parent createParent(int index) {

        double baseLat = random(-60, 60);
        double baseLon = random(-180, 180);

        Parent parent = Parent.builder()
                .parentNo("P%06d".formatted(index))
                .place("StressPlace-" + index)
                .build();

        ParentVersion version = ParentVersion.builder()
                .type("ACTIVE")
                .point(point(baseLon, baseLat))
                .parent(parent)
                .build();

        parent.setLastVersion(version);

        int closerAngle = (index % 18) * 20;

        generateLocations(version, baseLon, baseLat, closerAngle);
        generateDangerZones(version, baseLon, baseLat, closerAngle);

        return parent;
    }

    private void generateLocations(ParentVersion version,
                                   double lon,
                                   double lat,
                                   int closerAngle) {

        for (int i = 0; i < 3; i++) {

            Point center = project(lon, lat,
                    random(100, 300),
                    random(0, 360));

            Location location = Location.builder()
                    .name("Location-" + i)
                    .street("Street-" + i)
                    .shape(circle(center, 80))
                    .parentVersion(version)
                    .build();

            version.addLocation(location);

            generateDestinations(location, lon, lat, closerAngle);
        }
    }

    private void generateDestinations(Location location,
                                      double lon,
                                      double lat,
                                      int closerAngle) {

        for (int i = 0; i < 5; i++) {

            Point p = project(lon, lat,
                    random(50, 500),
                    random(0, 360));

            Destination d = Destination.builder()
                    .name("Dest-" + i)
                    .no(UUID.randomUUID().toString().substring(0, 6))
                    .point(p)
                    .dangerZoneAngle((double) closerAngle)
                    .location(location)
                    .build();

            location.addDestination(d);
        }
    }

    private void generateDangerZones(ParentVersion version,
                                     double lon,
                                     double lat,
                                     int closerAngle) {

        Point close = project(lon, lat, 300, closerAngle);

        version.addDangerZone(
                DangerZone.builder()
                        .name("Closer")
                        .shape(circle(close, 60))
                        .parentVersion(version)
                        .build()
        );

        for (int i = 0; i < 4; i++) {

            Point far = project(lon, lat,
                    random(700, 1500),
                    random(0, 360));

            version.addDangerZone(
                    DangerZone.builder()
                            .name("Far-" + i)
                            .shape(circle(far, 70))
                            .parentVersion(version)
                            .build()
            );
        }
    }

    private Point project(double lon,
                          double lat,
                          double distance,
                          double azimuth) {

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(lon, lat);
        calc.setDirection(azimuth, distance);

        Point2D dest = calc.getDestinationGeographicPoint();
        return point(dest.getX(), dest.getY());
    }

    private Point point(double lon, double lat) {
        Point p = gf.createPoint(new Coordinate(lon, lat));
        p.setSRID(4326);
        return p;
    }

    private Polygon circle(Point center, double radiusMeters) {
        GeometricShapeFactory factory = new GeometricShapeFactory(gf);
        factory.setCentre(center.getCoordinate());
        factory.setSize(radiusMeters / 111_320d * 2);
        factory.setNumPoints(16);

        Polygon poly = factory.createCircle();
        poly.setSRID(4326);
        return poly;
    }

    private double random(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private void printStatistics(long durationMs) {

        double seconds = durationMs / 1000.0;
        double rows = 100_000 * 25.0; // approx rows per parent
        double rowsPerSec = rows / seconds;

        System.out.println("========== STRESS RESULT ==========");
        System.out.println("Duration: " + seconds + " sec");
        System.out.println("Approx rows inserted: " + (long) rows);
        System.out.println("Throughput: " + (long) rowsPerSec + " rows/sec");
        System.out.println("===================================");
    }
}