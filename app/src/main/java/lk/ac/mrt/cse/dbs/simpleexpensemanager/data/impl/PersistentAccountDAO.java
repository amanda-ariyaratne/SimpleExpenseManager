package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private SQLiteOpenHelper dbHelper;

    public PersistentAccountDAO(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();

        String selectQuery = "SELECT  accountNo FROM account_table ;";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                accountNumbers.add(cursor.getString(cursor.getColumnIndex("accountNo")));
            } while (cursor.moveToNext());
        }

        // close db connection and cursor
        db.close();
        cursor.close();

        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();

        String selectQuery = "SELECT  * FROM account_table ;";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
                String bankName = cursor.getString(cursor.getColumnIndex("bankName"));
                String accountHolderName = cursor.getString(cursor.getColumnIndex("accountHolderName"));
                double balance = cursor.getDouble(cursor.getColumnIndex("balance"));

                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accounts.add(account);

            } while (cursor.moveToNext());
        }

        // close db connection and cursor
        db.close();
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        String selectQuery = "SELECT  * FROM account_table WHERE accountNo = '" + accountNo + "';";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int size = cursor.getCount();
        if (size == 0 || size > 1) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        if (cursor.moveToFirst()) {
            Account account;

            String bankName = cursor.getString(cursor.getColumnIndex("bankName"));
            String accountHolderName = cursor.getString(cursor.getColumnIndex("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndex("balance"));

            account = new Account(accountNo, bankName, accountHolderName, balance);

            // close db connection and cursor
            db.close();
            cursor.close();

            return account;

        }

        return null;

    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accountNo", account.getAccountNo());
        values.put("bankName", account.getBankName());
        values.put("accountHolderName", account.getAccountHolderName());
        values.put("balance", account.getBalance());
        db.insert("account_table", null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String selectQuery = "SELECT  * FROM account_table WHERE accountNo = '" + accountNo + "';";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        db = dbHelper.getWritableDatabase();
        db.delete("account_table", "accountNo" + " = ?",
                new String[]{accountNo});
        db.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        String selectQuery = "SELECT  * FROM account_table WHERE accountNo = '" + accountNo + "';";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        if (cursor.moveToFirst()) {
            double amountBeforeUpdate = cursor.getDouble(cursor.getColumnIndex("balance"));
            double newBalance = amountBeforeUpdate;
            switch (expenseType) {
                case EXPENSE:
                    newBalance -= amount;
                    break;
                case INCOME:
                    newBalance += amount;
                    break;
            }

            ContentValues values = new ContentValues();
            values.put("balance", newBalance);
            db.update("account_table", values, "accountNo" + " = ?", new String[]{accountNo});

        }

    }
}
