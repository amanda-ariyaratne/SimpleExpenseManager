package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private SQLiteOpenHelper dbHelper;

    public PersistentTransactionDAO(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", dateToString(date));
        values.put("accountNo", accountNo);
        values.put("expenseType", expenseType.name());
        values.put("amount", amount);
        db.insert("transaction_table", null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new LinkedList<>();

        String selectQuery = "SELECT  * FROM transaction_table ;";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Date date;
                date = stringToDate(cursor.getString(cursor.getColumnIndex("date")));

                String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
                double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

                ExpenseType expenseType;
                if(cursor.getString(cursor.getColumnIndex("expenseType")).equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        // close db connection and cursor
        db.close();
        cursor.close();

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactions = new LinkedList<>();

        String countQuery = "SELECT  * FROM transaction_table ORDER BY id DESC LIMIT ?;";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{Integer.toString(limit)});
        int size = cursor.getCount();
        if (size <= limit) {
            limit = size;
        }
        int i = 0;
        if (cursor.moveToFirst()) {

            do {
                Date date;
                date = stringToDate(cursor.getString(cursor.getColumnIndex("date")));

                String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
                double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

                ExpenseType expenseType;
                if(cursor.getString(cursor.getColumnIndex("expenseType")).equals("EXPENSE")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);

                i++;

            } while ((i < limit) && cursor.moveToNext());
        }

        // close db connection and cursor
        db.close();
        cursor.close();

        return transactions;
    }

    private DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date stringToDate(String strDate){
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private String dateToString(Date date){
        return sdf.format(date);
    }
}
