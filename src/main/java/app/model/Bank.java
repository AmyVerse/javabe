package app.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Bank {
    private String bankName;
    private String ifscCode;
    private String branchName;
    private String address;
    private LocalDateTime establishedDate;
    private boolean isActive;
    // Note: Removed accounts list to avoid circular dependency - use repository to
    // get accounts

    public Bank() {
        this.establishedDate = LocalDateTime.now();
        this.isActive = true;
    }

    public Bank(String bankName, String ifscCode) {
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.establishedDate = LocalDateTime.now();
        this.isActive = true;
    }

    public Bank(String bankName, String ifscCode, String branchName, String address) {
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.branchName = branchName;
        this.address = address;
        this.establishedDate = LocalDateTime.now();
        this.isActive = true;
    }

    // Getter and Setter

    // 1- bank name
    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    // 2-ifsc Code
    public String getIfscCode() {
        return this.ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    // 3- BranchName
    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    // 4- Address
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // 5- EstablishedDate
    public LocalDateTime getEstablishedDate() {
        return this.establishedDate;
    }

    public void setEstablishedDate(LocalDateTime establishedDate) {
        this.establishedDate = establishedDate;
    }

    // 6- IsActive
    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Utility methods for validation
    public boolean isValidIfscCode() {
        return this.ifscCode != null && this.ifscCode.length() >= 4;
    }

    public String getDisplayName() {
        return this.bankName + (this.branchName != null ? " - " + this.branchName : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Bank bank = (Bank) obj;
        return Objects.equals(ifscCode, bank.ifscCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ifscCode);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "bankName='" + bankName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
