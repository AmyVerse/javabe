package app.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private String transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED, PENDING
    private String description;
    private String transactionType; // TRANSFER, DEPOSIT, WITHDRAWAL

    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.transactionType = "TRANSFER";
    }

    public Transaction(String transactionId, String fromAccountNumber, String toAccountNumber,
            double amount, LocalDateTime timestamp, String status) {
        this.transactionId = transactionId;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.status = status != null ? status : "PENDING";
        this.transactionType = "TRANSFER";
    }

    // getter and setter

    // 1-fromAccountNumber
    public String getFromAccountNumber() {
        return this.fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    // 2- toAccountNumber
    public String getToAccountNumber() {
        return this.toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    // 3- transactionId
    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // 4- Amount
    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // 5- timestamp
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // 6- Status
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 7- Description
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 8- TransactionType
    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    // Utility methods
    public boolean isSuccessful() {
        return "SUCCESS".equals(this.status);
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    public void markAsSuccessful() {
        this.status = "SUCCESS";
    }

    public void markAsFailed(String reason) {
        this.status = "FAILED";
        this.description = reason;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Transaction transaction = (Transaction) obj;
        return Objects.equals(transactionId, transaction.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", from='" + fromAccountNumber + '\'' +
                ", to='" + toAccountNumber + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", type='" + transactionType + '\'' +
                '}';
    }
}
