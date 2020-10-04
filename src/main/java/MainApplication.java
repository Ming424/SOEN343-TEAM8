import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import service.DatabaseService;
import org.flywaydb.core.Flyway;

import java.sql.SQLException;

public class MainApplication
{


    public static void main(String[] args) throws ManagedProcessException, SQLException
    {
        SetupDatabase();
        JavaFXController.main(args);
    }

    /**
     * Sets up the DB for this program and migrates all tables automatically from resources/db/migration.
     * Data directory is on the drive's root in a folder named mariaDB
     * @throws ManagedProcessException
     */
    private static void SetupDatabase() throws ManagedProcessException, SQLException
    {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(3306);
        config.setDataDir("/mariaDB/backup");
        DB db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("smartHome");

        final String databaseUrl = db.getConfiguration().getURL("smartHome");
        DatabaseService.SetupDBController(databaseUrl);

        Flyway flyway = Flyway.configure().dataSource(databaseUrl, "root", "").load();
        flyway.migrate();
    }
}
