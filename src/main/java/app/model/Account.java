package app.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Account {
    private String accountNumber;
    private User owner;
    private double balance;
    private Bank bank;
    private LocalDateTime createdAt;
    private boolean isActive;
    private String accountType;

    public Account() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.accountType = "SAVINGS";
    }

    public Account(String accountNumber, User owner, Bank bank, double initialBalance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.bank = bank;
        this.balance = Math.max(0, initialBalance); // Ensure non-negative balance
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.accountType = "SAVINGS";
    }

    // Getter and Setter

    // 1- accountNumber
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    // 2-Owner
    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    // 3-Bank
    public Bank getBank() {
        return this.bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    // 4- Balance
    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.max(0, balance);
    }

    // 5- Additional properties
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    // Business logic methods
    public boolean deposit(double amount) {
        if (amount > 0 && this.isActive) {
            this.balance += amount;
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance && this.isActive) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public boolean canWithdraw(double amount) {
        return this.isActive && amount > 0 && this.balance >= amount;
    }

    // Utility methods for API
    public String getOwnerUserId() {
        return this.owner != null ? this.owner.getUserId() : null;
    }

    public String getBankIfsc() {
        return this.bank != null ? this.bank.getIfscCode() : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Account account = (Account) obj;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + (owner != null ? owner.getName() : "N/A") + '\'' +
                ", balance=" + balance +
                ", bankName='" + (bank != null ? bank.getBankName() : "N/A") + '\'' +
                ", accountType='" + accountType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
