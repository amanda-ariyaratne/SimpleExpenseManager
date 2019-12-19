package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "170043A";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS account_table (" +
                "accountNo TEXT PRIMARY KEY NOT NULL," +
                "bankName TEXT NOT NULL," +
                "accountHolderName TEXT NOT NULL," +
                "balance REAL NOT NULL" +
                ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS expense_type_table (" +
                "name TEXT PRIMARY KEY NOT NULL" +
                ");");
        sqLiteDatabase.execSQL("" +
                "INSERT INTO expense_type_table(name) VALUES('EXPENSE');"
                );
        sqLiteDatabase.execSQL("" +
                "INSERT INTO expense_type_table(name) VALUES('INCOME');"
                );
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS transaction_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT NOT NULL," +
                "accountNo TEXT NOT NULL," +
                "expenseType TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "FOREIGN KEY (accountNo) REFERENCES account_table (accountNo)," +
                "FOREIGN KEY (expenseType) REFERENCES expense_type_table (name)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account_table;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS expense_type_table;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transaction_table;");

        // Create tables again
        onCreate(sqLiteDatabase);
    }
}
